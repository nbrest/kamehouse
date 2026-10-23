package com.nicobrest.kamehouse.mobile.ztv.lockscreen;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ServiceInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadata;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Foreground service that shows the current ZTV playing item and media controls
 * (previous, play/pause, stop, next, close) in an android lock screen notification.
 *
 * The service polls the kamehouse-vlcrc status api on its own so that the
 * notification keeps working while the phone is locked and the app runs in the
 * background.
 *
 * @author nbrest
 */
public class ZtvLockscreenService extends Service {

  public static final String ACTION_START = "com.nicobrest.kamehouse.mobile.ztv.lockscreen.START";
  public static final String ACTION_STOP = "com.nicobrest.kamehouse.mobile.ztv.lockscreen.STOP";
  public static final String ACTION_PREVIOUS = ZtvLockscreenHelper.ACTION_PREVIOUS;
  public static final String ACTION_PLAY = ZtvLockscreenHelper.ACTION_PLAY;
  public static final String ACTION_NEXT = ZtvLockscreenHelper.ACTION_NEXT;
  public static final String ACTION_CLOSE = ZtvLockscreenHelper.ACTION_CLOSE;

  public static final String EXTRA_URL = "url";
  public static final String EXTRA_USERNAME = "username";
  public static final String EXTRA_PASSWORD = "password";
  public static final String EXTRA_SKIP_SSL_CHECK = "skipSslCheck";
  public static final String EXTRA_HOST = "host";

  private static final String TAG = "ZtvLockscreen";
  private static final String CHANNEL_ID = "kamehouse-ztv-lockscreen";
  private static final String ARTWORK_ASSET_PATH =
      "www/kame-house/img/mobile-lockscreen/mobile-lockscreen-background.jpg";
  private static final int NOTIFICATION_ID = 9821;
  private static final long POLL_INTERVAL_SECONDS = 2;
  private static final int CONNECT_TIMEOUT_MS = 5000;
  private static final int READ_TIMEOUT_MS = 5000;
  private static final String STATE_PLAYING = ZtvLockscreenHelper.STATE_PLAYING;

  private static volatile boolean running = false;

  private String baseUrl = null;
  private String username = null;
  private String password = null;
  private String host = "localhost";
  private boolean skipSslCheck = false;

  private String playbackState = "stopped";
  private String title = null;
  private int currentTime = 0;
  private int totalTime = 0;

  private ScheduledExecutorService scheduler = null;
  private ScheduledFuture<?> pollTask = null;
  private MediaSession mediaSession = null;
  private Bitmap artworkBitmap = null;

  public static boolean isRunning() {
    return running;
  }

  private static void setRunning(boolean value) {
    running = value;
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    setRunning(true);
    createNotificationChannel();
    createMediaSession();
    loadArtworkBitmap();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent == null || intent.getAction() == null) {
      return START_NOT_STICKY;
    }
    String action = intent.getAction();
    Log.d(TAG, "onStartCommand action: " + action);
    if (ACTION_STOP.equals(action)) {
      stopSelf();
      return START_NOT_STICKY;
    }
    if (ACTION_START.equals(action)) {
      readConfig(intent);
      startForegroundCompat(buildNotification());
      startPolling();
      return START_NOT_STICKY;
    }
    handleAction(action);
    return START_NOT_STICKY;
  }

  @Override
  public void onDestroy() {
    Log.d(TAG, "Stopping ZTV lock screen service");
    setRunning(false);
    stopPolling();
    if (mediaSession != null) {
      mediaSession.setActive(false);
      mediaSession.release();
      mediaSession = null;
    }
    super.onDestroy();
  }

  /**
   * Read the backend config and ztv host from the start intent.
   */
  private void readConfig(Intent intent) {
    baseUrl = ZtvLockscreenHelper.trimTrailingSlash(intent.getStringExtra(EXTRA_URL));
    username = intent.getStringExtra(EXTRA_USERNAME);
    password = intent.getStringExtra(EXTRA_PASSWORD);
    skipSslCheck = intent.getBooleanExtra(EXTRA_SKIP_SSL_CHECK, false);
    String intentHost = intent.getStringExtra(EXTRA_HOST);
    if (intentHost != null && !intentHost.isEmpty()) {
      host = intentHost;
    }
  }

  /**
   * Start the notification channel for the media controls.
   */
  private void createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "ZTV Lock Screen",
          NotificationManager.IMPORTANCE_LOW);
      channel.setDescription("ZTV player controls on the lock screen");
      channel.setShowBadge(false);
      channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
      NotificationManager notificationManager = getSystemService(NotificationManager.class);
      if (notificationManager != null) {
        notificationManager.createNotificationChannel(channel);
      }
    }
  }

  /**
   * Create the media session that provides the lock screen media controls.
   */
  private void createMediaSession() {
    mediaSession = new MediaSession(this, TAG);
    mediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS
        | MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
    mediaSession.setCallback(new MediaSession.Callback() {
      @Override
      public void onPlay() {
        execCommand("pl_play");
      }

      @Override
      public void onPause() {
        execCommand("pl_pause");
      }

      @Override
      public void onSkipToNext() {
        execCommand("pl_next");
      }

      @Override
      public void onSkipToPrevious() {
        execCommand("pl_previous");
      }

      @Override
      public void onStop() {
        execCommand("pl_stop");
      }

      @Override
      public void onCustomAction(String action, Bundle extras) {
        handleAction(action);
      }

      @Override
      public void onSeekTo(long pos) {
        final long positionMs = pos;
        runInBackground(() -> sendCommandWithValue("seek", String.valueOf(positionMs / 1000L)));
      }
    });
    PendingIntent sessionActivity = openAppPendingIntent();
    if (sessionActivity != null) {
      mediaSession.setSessionActivity(sessionActivity);
    }
    mediaSession.setActive(true);
  }

  /**
   * Load the kamehouse banner to use as the notification artwork.
   * The banner is read from the bundled www assets so it always reflects the kamehouse-ui
   * source image, falling back to the app icon.
   */
  private void loadArtworkBitmap() {
    artworkBitmap = loadArtworkFromAssets();
    if (artworkBitmap == null) {
      ApplicationInfo appInfo = getApplicationInfo();
      if (appInfo != null && appInfo.icon != 0) {
        artworkBitmap = BitmapFactory.decodeResource(getResources(), appInfo.icon);
      }
    }
  }

  /**
   * Load the kamehouse banner from the bundled assets.
   */
  private Bitmap loadArtworkFromAssets() {
    try (InputStream bannerStream = getAssets().open(ARTWORK_ASSET_PATH)) {
      return BitmapFactory.decodeStream(bannerStream);
    } catch (IOException e) {
      Log.d(TAG, "Error loading kamehouse artwork from assets: " + e.getMessage());
      return null;
    }
  }

  /**
   * Start the service in the foreground with the media controls notification.
   */
  private void startForegroundCompat(Notification notification) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
    } else {
      startForeground(NOTIFICATION_ID, notification);
    }
  }

  /**
   * Start the status polling loop.
   */
  private void startPolling() {
    if (scheduler == null || scheduler.isShutdown()) {
      scheduler = Executors.newSingleThreadScheduledExecutor();
    }
    if (pollTask == null || pollTask.isCancelled()) {
      pollTask = scheduler.scheduleWithFixedDelay(this::pollStatus, 0, POLL_INTERVAL_SECONDS,
          TimeUnit.SECONDS);
    }
  }

  /**
   * Stop the status polling loop.
   */
  private void stopPolling() {
    if (pollTask != null) {
      pollTask.cancel(true);
      pollTask = null;
    }
    if (scheduler != null) {
      scheduler.shutdownNow();
      scheduler = null;
    }
  }

  /**
   * Handle a media control action coming from the notification buttons.
   */
  private void handleAction(String action) {
    if (ACTION_CLOSE.equals(action)) {
      execCloseZtv();
      return;
    }
    String command = ZtvLockscreenHelper.getCommandForAction(action,
        STATE_PLAYING.equals(playbackState));
    if (command != null) {
      execCommand(command);
    }
  }

  /**
   * Execute a ztv command and refresh the status afterwards.
   */
  private void execCommand(final String command) {
    runInBackground(() -> sendCommand(command));
  }

  /**
   * Close the ztv player process, same as the close button on the ztv player page, and refresh afterwards.
   */
  private void execCloseZtv() {
    runInBackground(this::closeZtvProcess);
  }

  /**
   * Run the task in the background, then refresh the notification state.
   */
  private void runInBackground(final Runnable task) {
    if (scheduler == null || scheduler.isShutdown()) {
      scheduler = Executors.newSingleThreadScheduledExecutor();
    }
    scheduler.execute(() -> {
      task.run();
      try {
        Thread.sleep(600);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        Log.d(TAG, "Interrupted while refreshing ztv notification", e);
      }
      pollStatus();
    });
  }

  /**
   * Poll the vlcrc status api and refresh the notification.
   */
  private void pollStatus() {
    if (baseUrl == null || baseUrl.isEmpty()) {
      return;
    }
    try {
      String response = httpGet(ZtvLockscreenHelper.buildStatusUrl(baseUrl, host));
      parseStatus(response);
    } catch (Exception e) {
      Log.d(TAG, "Error polling ztv status: " + e.getMessage());
      playbackState = "stopped";
    }
    updateMediaSession();
    updateNotification();
  }

  /**
   * Parse the vlcrc status json response.
   */
  private void parseStatus(String response) {
    ZtvLockscreenHelper.ZtvStatus status = ZtvLockscreenHelper.parseStatus(response);
    playbackState = status.getPlaybackState();
    title = status.getTitle();
    currentTime = status.getCurrentTime();
    totalTime = status.getTotalTime();
  }

  /**
   * Send a vlcrc command to the backend.
   */
  private void sendCommand(String command) {
    sendCommandWithValue(command, null);
  }

  /**
   * Send a vlcrc command with value to the backend.
   */
  private void sendCommandWithValue(String command, String value) {
    try {
      String body = ZtvLockscreenHelper.buildCommandBody(command, value);
      if (body != null) {
        httpPost(ZtvLockscreenHelper.buildCommandsUrl(baseUrl, host), body);
      }
    } catch (Exception e) {
      Log.d(TAG, "Error sending ztv command " + command + ": " + e.getMessage());
    }
  }

  /**
   * Close the ztv player process through the vlcrc process control api.
   */
  private void closeZtvProcess() {
    if (baseUrl == null || baseUrl.isEmpty()) {
      return;
    }
    try {
      httpDelete(ZtvLockscreenHelper.buildZtvProcessUrl(baseUrl));
    } catch (Exception e) {
      Log.d(TAG, "Error closing ztv process: " + e.getMessage());
    }
  }

  /**
   * Build and display the lock screen notification.
   */
  private void updateNotification() {
    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    if (notificationManager != null) {
      notificationManager.notify(NOTIFICATION_ID, buildNotification());
    }
  }

  /**
   * Update the media session playback state and metadata so the system renders the media controls.
   */
  private void updateMediaSession() {
    if (mediaSession == null) {
      return;
    }
    long actions = PlaybackState.ACTION_PLAY
        | PlaybackState.ACTION_PAUSE
        | PlaybackState.ACTION_PLAY_PAUSE
        | PlaybackState.ACTION_SKIP_TO_PREVIOUS
        | PlaybackState.ACTION_SKIP_TO_NEXT
        | PlaybackState.ACTION_SEEK_TO;
    PlaybackState.Builder stateBuilder = new PlaybackState.Builder();
    stateBuilder.setActions(actions);
    stateBuilder.setState(getPlaybackStateCode(), currentTime * 1000L,
        STATE_PLAYING.equals(playbackState) ? 1.0f : 0.0f, SystemClock.elapsedRealtime());
    stateBuilder.addCustomAction(new PlaybackState.CustomAction.Builder(
        ACTION_CLOSE, "Close ZTV", android.R.drawable.ic_lock_power_off).build());
    mediaSession.setPlaybackState(stateBuilder.build());

    String displayTitle = (title == null || title.isEmpty())
        ? ZtvLockscreenHelper.NO_MEDIA_TITLE : ZtvLockscreenHelper.formatMediaTitle(title);
    MediaMetadata.Builder metaBuilder = new MediaMetadata.Builder();
    metaBuilder.putString(MediaMetadata.METADATA_KEY_TITLE, displayTitle);
    metaBuilder.putLong(MediaMetadata.METADATA_KEY_DURATION, totalTime * 1000L);
    if (artworkBitmap != null) {
      metaBuilder.putBitmap(MediaMetadata.METADATA_KEY_ART, artworkBitmap);
    }
    mediaSession.setMetadata(metaBuilder.build());
  }

  /**
   * Map the ztv playback state to an android playback state.
   */
  private int getPlaybackStateCode() {
    if (STATE_PLAYING.equals(playbackState)) {
      return PlaybackState.STATE_PLAYING;
    }
    if ("paused".equals(playbackState)) {
      return PlaybackState.STATE_PAUSED;
    }
    return PlaybackState.STATE_STOPPED;
  }

  /**
   * Build the lock screen notification with the media controls.
   */
  private Notification buildNotification() {
    String displayTitle = (title == null || title.isEmpty())
        ? ZtvLockscreenHelper.NO_MEDIA_TITLE : ZtvLockscreenHelper.formatMediaTitle(title);
    String displayTime = ZtvLockscreenHelper.formatTime(currentTime) + " / "
        + ZtvLockscreenHelper.formatTime(totalTime);
    boolean isPlaying = STATE_PLAYING.equals(playbackState);

    Notification.Builder builder;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      builder = new Notification.Builder(this, CHANNEL_ID);
    } else {
      builder = new Notification.Builder(this);
    }
    builder.setSmallIcon(com.nicobrest.kamehouse.R.drawable.ic_ztv);
    if (artworkBitmap != null) {
      builder.setLargeIcon(artworkBitmap);
    }
    builder.setContentTitle(displayTitle);
    builder.setContentText(displayTime);
    builder.setOngoing(true);
    builder.setOnlyAlertOnce(true);
    builder.setShowWhen(false);
    builder.setVisibility(Notification.VISIBILITY_PUBLIC);
    builder.setPriority(Notification.PRIORITY_LOW);
    builder.setContentIntent(openAppPendingIntent());
    builder.addAction(new Notification.Action.Builder(
        android.R.drawable.ic_media_previous, "Previous", actionPendingIntent(ACTION_PREVIOUS)).build());
    builder.addAction(new Notification.Action.Builder(
        isPlaying ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play,
        "Play or pause", actionPendingIntent(ACTION_PLAY)).build());
    builder.addAction(new Notification.Action.Builder(
        android.R.drawable.ic_media_next, "Next", actionPendingIntent(ACTION_NEXT)).build());
    builder.addAction(new Notification.Action.Builder(
        android.R.drawable.ic_lock_power_off, "Close ZTV", actionPendingIntent(ACTION_CLOSE)).build());
    if (mediaSession != null) {
      builder.setStyle(new Notification.MediaStyle()
          .setMediaSession(mediaSession.getSessionToken())
          .setShowActionsInCompactView(0, 1, 2));
    }
    return builder.build();
  }

  /**
   * Pending intent to trigger a media control action.
   */
  private PendingIntent actionPendingIntent(String action) {
    Intent intent = new Intent(this, ZtvLockscreenService.class);
    intent.setAction(action);
    int flags = PendingIntent.FLAG_UPDATE_CURRENT;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      flags = flags | PendingIntent.FLAG_IMMUTABLE;
    }
    return PendingIntent.getService(this, action.hashCode(), intent, flags);
  }

  /**
   * Pending intent to open the kamehouse mobile app.
   */
  private PendingIntent openAppPendingIntent() {
    Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
    int flags = PendingIntent.FLAG_UPDATE_CURRENT;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      flags = flags | PendingIntent.FLAG_IMMUTABLE;
    }
    if (intent == null) {
      return null;
    }
    return PendingIntent.getActivity(this, 0, intent, flags);
  }

  /**
   * Execute an http GET request against the backend.
   */
  private String httpGet(String requestUrl) throws IOException {
    HttpURLConnection connection = openConnection(requestUrl);
    connection.setRequestMethod("GET");
    try {
      int responseCode = connection.getResponseCode();
      InputStream inputStream = responseCode >= 200 && responseCode < 300
          ? connection.getInputStream() : connection.getErrorStream();
      return readStream(inputStream);
    } finally {
      connection.disconnect();
    }
  }

  /**
   * Execute an http POST request against the backend.
   */
  private void httpPost(String requestUrl, String body) throws IOException {
    HttpURLConnection connection = openConnection(requestUrl);
    connection.setRequestMethod("POST");
    connection.setDoOutput(true);
    connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
    try {
      OutputStream outputStream = connection.getOutputStream();
      outputStream.write(body.getBytes(StandardCharsets.UTF_8));
      outputStream.flush();
      outputStream.close();
      connection.getResponseCode();
    } finally {
      connection.disconnect();
    }
  }

  /**
   * Execute an http DELETE request against the backend.
   */
  private void httpDelete(String requestUrl) throws IOException {
    HttpURLConnection connection = openConnection(requestUrl);
    connection.setRequestMethod("DELETE");
    try {
      connection.getResponseCode();
    } finally {
      connection.disconnect();
    }
  }

  /**
   * Open an http connection with the configured credentials.
   */
  @SuppressWarnings("java:S6201")
  private HttpURLConnection openConnection(String requestUrl) throws IOException {
    URL url = new URL(requestUrl);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
    connection.setReadTimeout(READ_TIMEOUT_MS);
    connection.setRequestProperty("Accept", "application/json");
    if (username != null && !username.isEmpty()) {
      String credentials = username + ":" + (password == null ? "" : password);
      String basicAuth = "Basic " + Base64.encodeToString(credentials.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);
      connection.setRequestProperty("Authorization", basicAuth);
    }
    if (skipSslCheck && connection instanceof HttpsURLConnection) {
      disableSslCheck((HttpsURLConnection) connection);
    }
    return connection;
  }

  /**
   * Disable ssl verification for self signed certificates.
   */
  private void disableSslCheck(HttpsURLConnection connection) throws IOException {
    TrustManager[] trustAllCerts = {
        new X509TrustManager() {
          @Override
          public void checkClientTrusted(X509Certificate[] chain, String authType) { // NOSONAR
            // Trust all certificates, only used when skipSslCheck is explicitly enabled.
          }

          @Override
          public void checkServerTrusted(X509Certificate[] chain, String authType) { // NOSONAR
            // Trust all certificates, only used when skipSslCheck is explicitly enabled.
          }

          @Override
          public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
          }
        }
    };
    try {
      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, trustAllCerts, new SecureRandom());
      connection.setSSLSocketFactory(sslContext.getSocketFactory());
    } catch (GeneralSecurityException e) {
      throw new IOException("Error disabling ssl check", e);
    }
    connection.setHostnameVerifier((hostname, session) -> true); // NOSONAR
  }

  /**
   * Read an input stream into a string.
   */
  private String readStream(InputStream inputStream) throws IOException {
    if (inputStream == null) {
      return "";
    }
    StringBuilder response = new StringBuilder();
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    try {
      String line;
      while ((line = reader.readLine()) != null) {
        response.append(line);
      }
    } finally {
      reader.close();
    }
    return response.toString();
  }
}

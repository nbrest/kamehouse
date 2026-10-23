package com.nicobrest.kamehouse.mobile.ztv.lockscreen;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Cordova bridge for the KameHouse ZTV lock screen applet.
 *
 * @author nbrest
 */
public class ZtvLockscreenPlugin extends CordovaPlugin {

  private static final int PERMISSION_REQUEST_CODE = 8472;

  private CallbackContext pendingStartCallbackContext = null;
  private JSONObject pendingStartConfig = null;

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    if ("start".equals(action)) {
      start(args.optJSONObject(0), callbackContext);
      return true;
    }
    if ("stop".equals(action)) {
      stop(callbackContext);
      return true;
    }
    if ("isRunning".equals(action)) {
      callbackContext.success(ZtvLockscreenService.isRunning() ? 1 : 0);
      return true;
    }
    return false;
  }

  /**
   * Start the lock screen applet, requesting notification permission if needed.
   */
  private void start(JSONObject config, CallbackContext callbackContext) {
    if (config == null) {
      callbackContext.error("No config provided to start the ZTV lock screen applet");
      return;
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission()) {
      this.pendingStartConfig = config;
      this.pendingStartCallbackContext = callbackContext;
      cordova.requestPermission(this, PERMISSION_REQUEST_CODE, Manifest.permission.POST_NOTIFICATIONS);
      return;
    }
    startService(config);
    callbackContext.success();
  }

  /**
   * Stop the lock screen applet.
   */
  private void stop(CallbackContext callbackContext) {
    Context context = cordova.getActivity().getApplicationContext();
    Intent intent = new Intent(context, ZtvLockscreenService.class);
    intent.setAction(ZtvLockscreenService.ACTION_STOP);
    context.startService(intent);
    callbackContext.success();
  }

  /**
   * Check if the app has the notification permission granted.
   */
  private boolean hasNotificationPermission() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
      return true;
    }
    return cordova.getActivity().checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
        == PackageManager.PERMISSION_GRANTED;
  }

  @Override
  public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
    if (requestCode != PERMISSION_REQUEST_CODE) {
      return;
    }
    JSONObject config = this.pendingStartConfig;
    CallbackContext callbackContext = this.pendingStartCallbackContext;
    this.pendingStartConfig = null;
    this.pendingStartCallbackContext = null;
    if (config == null) {
      return;
    }
    startService(config);
    if (callbackContext != null) {
      callbackContext.success();
    }
  }

  /**
   * Start the foreground service with the backend config.
   */
  private void startService(JSONObject config) {
    Context context = cordova.getActivity().getApplicationContext();
    Intent intent = new Intent(context, ZtvLockscreenService.class);
    intent.setAction(ZtvLockscreenService.ACTION_START);
    intent.putExtra(ZtvLockscreenService.EXTRA_URL, config.optString("url", null));
    intent.putExtra(ZtvLockscreenService.EXTRA_USERNAME, config.optString("username", null));
    intent.putExtra(ZtvLockscreenService.EXTRA_PASSWORD, config.optString("password", null));
    intent.putExtra(ZtvLockscreenService.EXTRA_SKIP_SSL_CHECK, config.optBoolean("skipSslCheck", false));
    intent.putExtra(ZtvLockscreenService.EXTRA_HOST, config.optString("host", "localhost"));
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      context.startForegroundService(intent);
    } else {
      context.startService(intent);
    }
  }
}

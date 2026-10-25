package com.nicobrest.kamehouse.mobile.ztv.lockscreen;

import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Pure java helpers for the ZTV lock screen service. This class has no android dependencies so it
 * can be compiled and unit tested on a plain JVM.
 *
 * @author nbrest
 */
public final class ZtvLockscreenHelper {

  public static final String ACTION_PREVIOUS = "com.nicobrest.kamehouse.mobile.ztv.lockscreen.PREVIOUS";
  public static final String ACTION_PLAY = "com.nicobrest.kamehouse.mobile.ztv.lockscreen.PLAY";
  public static final String ACTION_NEXT = "com.nicobrest.kamehouse.mobile.ztv.lockscreen.NEXT";
  public static final String ACTION_CLOSE = "com.nicobrest.kamehouse.mobile.ztv.lockscreen.CLOSE";

  public static final String STATE_PLAYING = "playing";
  public static final String STATE_PAUSED = "paused";
  public static final String STATE_STOPPED = "stopped";

  public static final String NO_MEDIA_TITLE = "No media playing";

  private static final Logger LOGGER = Logger.getLogger(ZtvLockscreenHelper.class.getName());

  private ZtvLockscreenHelper() {
  }

  /**
   * Format the media title to match kamehouse desktop style: remove file extensions, replace _,
   * - and . with spaces and capitalize the first letter of each word.
   */
  public static String formatMediaTitle(String filename) {
    if (filename == null || filename.isEmpty()) {
      return filename;
    }
    String result = filename;
    result = result.replace('-', ' ');
    result = result.replace('_', ' ');
    result = result.replaceAll(
        "\\.(mp3|MP3|wav|WAV|m4a|M4A|flac|FLAC|ogg|OGG|mp4|MP4|avi|AVI|mkv|MKV)$", " ");
    result = result.replace('.', ' ');
    result = result.trim().replaceAll(" +", " ");
    StringBuilder builder = new StringBuilder();
    boolean capitalizeNext = true;
    for (int i = 0; i < result.length(); i++) {
      char c = result.charAt(i);
      if (Character.isWhitespace(c)) {
        capitalizeNext = true;
        builder.append(c);
      } else if (capitalizeNext) {
        builder.append(Character.toUpperCase(c));
        capitalizeNext = false;
      } else {
        builder.append(Character.toLowerCase(c));
      }
    }
    return builder.toString();
  }

  /**
   * Format seconds as m:ss.
   */
  public static String formatTime(int seconds) {
    int safeSeconds = seconds;
    if (safeSeconds < 0) {
      safeSeconds = 0;
    }
    int minutes = safeSeconds / 60;
    int remainingSeconds = safeSeconds % 60;
    return String.format("%d:%02d", minutes, remainingSeconds);
  }

  /**
   * Remove a trailing slash from the backend url.
   */
  public static String trimTrailingSlash(String value) {
    if (value == null) {
      return null;
    }
    if (value.endsWith("/")) {
      return value.substring(0, value.length() - 1);
    }
    return value;
  }

  /**
   * Map a notification action to the ztv command to execute.
   */
  public static String getCommandForAction(String action, boolean isPlaying) {
    if (ACTION_PREVIOUS.equals(action)) {
      return "pl_previous";
    }
    if (ACTION_NEXT.equals(action)) {
      return "pl_next";
    }
    if (ACTION_PLAY.equals(action)) {
      return isPlaying ? "pl_pause" : "pl_play";
    }
    return null;
  }

  /**
   * Build the status api url for a ztv host.
   */
  public static String buildStatusUrl(String baseUrl, String host) {
    return baseUrl + "/kame-house-vlcrc/api/v1/vlc-rc/players/" + host + "/status";
  }

  /**
   * Build the commands api url for a ztv host.
   */
  public static String buildCommandsUrl(String baseUrl, String host) {
    return baseUrl + "/kame-house-vlcrc/api/v1/vlc-rc/players/" + host + "/commands";
  }

  /**
   * Build the ztv process api url to close the ztv player.
   */
  public static String buildZtvProcessUrl(String baseUrl) {
    return baseUrl + "/kame-house-vlcrc/api/v1/vlc-rc/vlc-process";
  }

  /**
   * Build the json body for a ztv command, or null if the body can't be built.
   */
  public static String buildCommandBody(String command, String value) {
    try {
      JSONObject body = new JSONObject();
      body.put("name", command);
      if (value != null) {
        body.put("val", value);
      }
      return body.toString();
    } catch (Exception e) {
      LOGGER.log(Level.FINE, "Error building ztv command body", e);
      return null;
    }
  }

  /**
   * Parse the vlcrc status json response. On error it returns the stopped state.
   */
  public static ZtvStatus parseStatus(String response) {
    try {
      JSONObject json = new JSONObject(response);
      String state = json.optString("state", STATE_STOPPED);
      int time = json.optInt("time", 0);
      int length = json.optInt("length", 0);
      String parsedTitle = null;
      JSONObject information = json.optJSONObject("information");
      if (information != null) {
        JSONObject meta = information.optJSONObject("meta");
        if (meta != null) {
          parsedTitle = meta.optString("filename", null);
          if (parsedTitle == null || parsedTitle.isEmpty() || "null".equals(parsedTitle)) {
            parsedTitle = meta.optString("title", null);
          }
        }
      }
      return new ZtvStatus(state, parsedTitle, time, length);
    } catch (Exception e) {
      LOGGER.log(Level.FINE, "Error parsing ztv status", e);
      return new ZtvStatus(STATE_STOPPED, null, 0, 0);
    }
  }

  /**
   * Immutable snapshot of the ztv player status.
   */
  @SuppressWarnings("java:S6206")
  public static final class ZtvStatus {

    private final String playbackState;
    private final String title;
    private final int currentTime;
    private final int totalTime;

    /**
     * Create a ztv status snapshot.
     */
    public ZtvStatus(String playbackState, String title, int currentTime, int totalTime) {
      this.playbackState = playbackState;
      this.title = title;
      this.currentTime = currentTime;
      this.totalTime = totalTime;
    }

    public String getPlaybackState() {
      return playbackState;
    }

    public String getTitle() {
      return title;
    }

    public int getCurrentTime() {
      return currentTime;
    }

    public int getTotalTime() {
      return totalTime;
    }
  }
}

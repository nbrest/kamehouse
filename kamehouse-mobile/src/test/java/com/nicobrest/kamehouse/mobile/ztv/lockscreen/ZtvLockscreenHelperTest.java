package com.nicobrest.kamehouse.mobile.ztv.lockscreen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the pure logic of the ZTV lock screen service.
 */
class ZtvLockscreenHelperTest {

  @Test
  void formatMediaTitleNull() {
    assertNull(ZtvLockscreenHelper.formatMediaTitle(null));
  }

  @Test
  void formatMediaTitleEmpty() {
    assertEquals("", ZtvLockscreenHelper.formatMediaTitle(""));
  }

  @Test
  void formatMediaTitleRemovesExtensionAndTitleCases() {
    assertEquals("My Cool Song", ZtvLockscreenHelper.formatMediaTitle("my_cool_song.mp3"));
  }

  @Test
  void formatMediaTitleReplacesDashes() {
    assertEquals("My Cool Song", ZtvLockscreenHelper.formatMediaTitle("my-cool-song.MP3"));
  }

  @Test
  void formatMediaTitleOnlyStripsExtensionNotDots() {
    assertEquals("My.cool.song", ZtvLockscreenHelper.formatMediaTitle("my.cool.song.wav"));
  }

  @Test
  void formatMediaTitleWithoutExtension() {
    assertEquals("Another Title", ZtvLockscreenHelper.formatMediaTitle("another title"));
  }

  @Test
  void formatMediaTitleCollapsesMultipleSpaces() {
    assertEquals("A B", ZtvLockscreenHelper.formatMediaTitle("a  -  b.mkv"));
  }

  @Test
  void formatTimeZero() {
    assertEquals("0:00", ZtvLockscreenHelper.formatTime(0));
  }

  @Test
  void formatTimeSecondsPadded() {
    assertEquals("0:05", ZtvLockscreenHelper.formatTime(5));
  }

  @Test
  void formatTimeOverAMinute() {
    assertEquals("1:01", ZtvLockscreenHelper.formatTime(61));
  }

  @Test
  void formatTimeLargeValue() {
    assertEquals("61:00", ZtvLockscreenHelper.formatTime(3660));
  }

  @Test
  void formatTimeNegative() {
    assertEquals("0:00", ZtvLockscreenHelper.formatTime(-10));
  }

  @Test
  void trimTrailingSlashNull() {
    assertNull(ZtvLockscreenHelper.trimTrailingSlash(null));
  }

  @Test
  void trimTrailingSlashRemovesSlash() {
    assertEquals("http://localhost:8080", ZtvLockscreenHelper.trimTrailingSlash("http://localhost:8080/"));
  }

  @Test
  void trimTrailingSlashKeepsValueWithoutSlash() {
    assertEquals("http://localhost:8080", ZtvLockscreenHelper.trimTrailingSlash("http://localhost:8080"));
  }

  @Test
  void getCommandForActionPrevious() {
    assertEquals("pl_previous",
        ZtvLockscreenHelper.getCommandForAction(ZtvLockscreenHelper.ACTION_PREVIOUS, false));
  }

  @Test
  void getCommandForActionNext() {
    assertEquals("pl_next",
        ZtvLockscreenHelper.getCommandForAction(ZtvLockscreenHelper.ACTION_NEXT, false));
  }

  @Test
  void getCommandForActionPlayWhenPlaying() {
    assertEquals("pl_pause",
        ZtvLockscreenHelper.getCommandForAction(ZtvLockscreenHelper.ACTION_PLAY, true));
  }

  @Test
  void getCommandForActionPlayWhenNotPlaying() {
    assertEquals("pl_play",
        ZtvLockscreenHelper.getCommandForAction(ZtvLockscreenHelper.ACTION_PLAY, false));
  }

  @Test
  void getCommandForActionUnknown() {
    assertNull(ZtvLockscreenHelper.getCommandForAction(ZtvLockscreenHelper.ACTION_CLOSE, false));
    assertNull(ZtvLockscreenHelper.getCommandForAction("unknown", false));
  }

  @Test
  void buildStatusUrl() {
    assertEquals("http://localhost:8080/kame-house-vlcrc/api/v1/vlc-rc/players/localhost/status",
        ZtvLockscreenHelper.buildStatusUrl("http://localhost:8080", "localhost"));
  }

  @Test
  void buildCommandsUrl() {
    assertEquals("http://localhost:8080/kame-house-vlcrc/api/v1/vlc-rc/players/localhost/commands",
        ZtvLockscreenHelper.buildCommandsUrl("http://localhost:8080", "localhost"));
  }

  @Test
  void buildZtvProcessUrl() {
    assertEquals("http://localhost:8080/kame-house-vlcrc/api/v1/vlc-rc/vlc-process",
        ZtvLockscreenHelper.buildZtvProcessUrl("http://localhost:8080"));
  }

  @Test
  void buildCommandBodyWithoutValue() throws Exception {
    String body = ZtvLockscreenHelper.buildCommandBody("pl_play", null);
    JSONObject json = new JSONObject(body);
    assertEquals("pl_play", json.getString("name"));
    assertTrue(json.isNull("val"));
  }

  @Test
  void buildCommandBodyWithValue() throws Exception {
    String body = ZtvLockscreenHelper.buildCommandBody("seek", "30");
    JSONObject json = new JSONObject(body);
    assertEquals("seek", json.getString("name"));
    assertEquals("30", json.getString("val"));
  }

  @Test
  void parseStatusFullResponse() {
    String response = "{"
        + "\"state\":\"playing\","
        + "\"time\":12,"
        + "\"length\":300,"
        + "\"information\":{\"meta\":{\"filename\":\"my_song.mp3\"}}"
        + "}";
    ZtvLockscreenHelper.ZtvStatus status = ZtvLockscreenHelper.parseStatus(response);
    assertEquals("playing", status.playbackState());
    assertEquals("my_song.mp3", status.title());
    assertEquals(12, status.currentTime());
    assertEquals(300, status.totalTime());
  }

  @Test
  void parseStatusFallsBackToTitleWhenFilenameMissing() {
    String response = "{"
        + "\"state\":\"paused\","
        + "\"information\":{\"meta\":{\"title\":\"Only Title\"}}"
        + "}";
    ZtvLockscreenHelper.ZtvStatus status = ZtvLockscreenHelper.parseStatus(response);
    assertEquals("paused", status.playbackState());
    assertEquals("Only Title", status.title());
  }

  @Test
  void parseStatusFallsBackToTitleWhenFilenameEmpty() {
    String response = "{\"information\":{\"meta\":{\"filename\":\"\",\"title\":\"Fallback\"}}}";
    ZtvLockscreenHelper.ZtvStatus status = ZtvLockscreenHelper.parseStatus(response);
    assertEquals("Fallback", status.title());
  }

  @Test
  void parseStatusFallsBackToTitleWhenFilenameIsNullString() {
    String response = "{\"information\":{\"meta\":{\"filename\":\"null\",\"title\":\"Fallback\"}}}";
    ZtvLockscreenHelper.ZtvStatus status = ZtvLockscreenHelper.parseStatus(response);
    assertEquals("Fallback", status.title());
  }

  @Test
  void parseStatusWithoutInformation() {
    ZtvLockscreenHelper.ZtvStatus status = ZtvLockscreenHelper.parseStatus("{\"state\":\"stopped\"}");
    assertEquals("stopped", status.playbackState());
    assertNull(status.title());
    assertEquals(0, status.currentTime());
    assertEquals(0, status.totalTime());
  }

  @Test
  void parseStatusWithoutMeta() {
    ZtvLockscreenHelper.ZtvStatus status =
        ZtvLockscreenHelper.parseStatus("{\"information\":{}}");
    assertNull(status.title());
  }

  @Test
  void parseStatusInvalidJsonReturnsStopped() {
    ZtvLockscreenHelper.ZtvStatus status = ZtvLockscreenHelper.parseStatus("not a json");
    assertEquals("stopped", status.playbackState());
    assertNull(status.title());
    assertEquals(0, status.currentTime());
    assertEquals(0, status.totalTime());
  }

  @Test
  void constantsAreDefined() {
    assertEquals("com.nicobrest.kamehouse.mobile.ztv.lockscreen.PREVIOUS", ZtvLockscreenHelper.ACTION_PREVIOUS);
    assertEquals("com.nicobrest.kamehouse.mobile.ztv.lockscreen.PLAY", ZtvLockscreenHelper.ACTION_PLAY);
    assertEquals("com.nicobrest.kamehouse.mobile.ztv.lockscreen.NEXT", ZtvLockscreenHelper.ACTION_NEXT);
    assertEquals("com.nicobrest.kamehouse.mobile.ztv.lockscreen.CLOSE", ZtvLockscreenHelper.ACTION_CLOSE);
    assertEquals("No media playing", ZtvLockscreenHelper.NO_MEDIA_TITLE);
  }
}

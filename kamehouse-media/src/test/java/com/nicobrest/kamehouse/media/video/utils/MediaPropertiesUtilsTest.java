package com.nicobrest.kamehouse.media.video.utils;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * PropertiesUtils tests.
 *
 * @author nbrest
 *
 * NOTE: I wouldn't test this class with it's current version (2019/05/19). 
 *       But adding the tests for practice.
 */
public class MediaPropertiesUtilsTest {

  @Test
  public void getMediaVideoPropertyTest() {
    String expectedPropertyValue = "/git/texts/video_playlists/linux/media-drive";
    
    String returnedPropertyValue = MediaPropertiesUtils.getProperty("playlists.path" +
        ".linux");
    
    assertEquals(expectedPropertyValue, returnedPropertyValue);
  }
}

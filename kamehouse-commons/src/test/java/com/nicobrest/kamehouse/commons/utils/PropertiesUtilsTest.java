package com.nicobrest.kamehouse.commons.utils;

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
public class PropertiesUtilsTest {

  @Test
  public void getMediaVideoPropertyTest() {
    String expectedPropertyValue = "/git/texts/video_playlists/linux/media-drive";
    
    String returnedPropertyValue = PropertiesUtils.getMediaVideoProperty("playlists.path.linux");
    
    assertEquals(expectedPropertyValue, returnedPropertyValue);
  }
}

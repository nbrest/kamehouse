package com.nicobrest.kamehouse.commons.utils;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nicobrest.kamehouse.commons.exception.KameHouseException;
import org.junit.jupiter.api.Test;

/**
 * NetworkUtils tests.
 *
 * @author nbrest
 */
public class NetworkUtilsTest {

  /**
   * Tests wakeOnLan success.
   */
  @Test
  public void wakeOnLanSuccessTest() {
    String mac = "aa:bb:cc:dd:ee:ff";
    String broadcast = "192.168.1.255";
    NetworkUtils.wakeOnLan(mac, broadcast);
    // no exception thrown -> success
  }

  /**
   * Tests wakeOnLan UnknownHostException.
   */
  @Test
  public void wakeOnLanUnknownHostExceptionTest() {
    String mac = "aa:bb:cc:dd:ee:ff";
    String broadcast = "259.259.259.259";
    assertThrows(
        KameHouseException.class,
        () -> {
          NetworkUtils.wakeOnLan(mac, broadcast);
        });
  }

  /**
   * Tests wakeOnLan invalid mac.
   */
  @Test
  public void wakeOnLanInvalidMacTest() {
    String mac = "aa:bb:cc:dd:ee:ffgggg";
    String broadcast = "192.168.1.255";
    assertThrows(
        KameHouseException.class,
        () -> {
          NetworkUtils.wakeOnLan(mac, broadcast);
        });
  }

  /**
   * Tests wakeOnLan invalid mac.
   */
  @Test
  public void wakeOnLanInvalidMac2Test() {
    String mac = "aa:bb:cc:dd:ee:ff:gg";
    String broadcast = "192.168.1.255";
    assertThrows(
        KameHouseException.class,
        () -> {
          NetworkUtils.wakeOnLan(mac, broadcast);
        });
  }
}
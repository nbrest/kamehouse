package com.nicobrest.kamehouse.commons.utils;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nicobrest.kamehouse.commons.exception.KameHouseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * NetworkUtils tests.
 *
 * @author nbrest
 */
class NetworkUtilsTest {

  /**
   * Tests wakeOnLan success.
   */
  @Test
  void wakeOnLanSuccessTest() {
    String mac = "aa:bb:cc:dd:ee:ff";
    String broadcast = "192.168.1.255";
    Assertions.assertDoesNotThrow(() -> {
      NetworkUtils.wakeOnLan(mac, broadcast);
    });
  }

  /**
   * Tests wakeOnLan exception cases.
   */
  @ParameterizedTest
  @CsvSource({
      "aa:bb:cc:dd:ee:ff, 259.259.259.259",
      "aa:bb:cc:dd:ee:ffgggg, 192.168.1.255",
      "aa:bb:cc:dd:ee:ff:gg, 192.168.1.255"
  })
  void wakeOnLanExceptionTest(String mac, String broadcast) {
    assertThrows(
        KameHouseException.class,
        () -> {
          NetworkUtils.wakeOnLan(mac, broadcast);
        });
  }
}

package com.nicobrest.kamehouse.admin.service;

import com.nicobrest.kamehouse.main.exception.KameHouseBadRequestException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Unit tests for the PowerManagementService class.
 * 
 * @author nbrest
 *
 */
public class PowerManagementServiceTest {

  private PowerManagementService powerManagementService;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void before() {
    powerManagementService = new PowerManagementService();
  }

  /**
   * WOL server successful test.
   */
  @Test
  public void wakeOnLanServerTest() throws KameHouseBadRequestException {
    powerManagementService.wakeOnLan("media.server");
    // no exception thrown expected
  }

  /**
   * WOL invalid server test.
   */
  @Test
  public void wakeOnLanInvalidServerTest() {
    thrown.expect(KameHouseBadRequestException.class);
    thrown.expectMessage("INVALID_SERVER");

    powerManagementService.wakeOnLan("INVALID_SERVER");
  }

  /**
   * WOL mac and broadcast successful test.
   */
  @Test
  public void wakeOnLanMacAndBroadcastTest() throws KameHouseBadRequestException {
    powerManagementService.wakeOnLan("AA:BB:CC:DD:EE:FF", "10.10.9.9");
    // no exception thrown expected
  }
}

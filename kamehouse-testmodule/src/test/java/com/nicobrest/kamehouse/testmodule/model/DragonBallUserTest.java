package com.nicobrest.kamehouse.testmodule.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.testmodule.model.dto.DragonBallUserDto;
import com.nicobrest.kamehouse.testmodule.testutils.DragonBallUserTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the DragonBallUser class.
 *
 * @author nbrest
 */
class DragonBallUserTest {

  private TestUtils<DragonBallUser, DragonBallUserDto> testUtils;

  /** Clears data from the repository before each test. */
  @BeforeEach
  public void setUp() {
    testUtils = new DragonBallUserTestUtils();
    testUtils.initTestData();
  }

  /** Tests attack and recover. */
  @Test
  void attackAndRecoverTest() {
    DragonBallUser goku = testUtils.getTestDataList().get(0);
    DragonBallUser gohan = testUtils.getTestDataList().get(1);
    assertEquals(1000, goku.getStamina());
    assertEquals(1000, gohan.getStamina());

    goku.attack(gohan);
    assertEquals(970, gohan.getStamina());
    gohan.recoverStamina();
    assertEquals(990, gohan.getStamina());

    for (int i = 0; i < 100; i++) {
      goku.attack(gohan);
    }
    assertEquals(0, gohan.getStamina());
  }
}

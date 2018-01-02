package com.nicobrest.kamehouse.model;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
 
import com.nicobrest.kamehouse.model.DragonBallUser;

import org.junit.Test;

/**
 * Unit tests for the DragonBallUser class.
 * 
 * @author nbrest
 */
public class DragonBallUserTest {

  /**
   * Test the hashCode and equals methods in DragonBallUser.
   */
  @Test
  public void hashCodeAndEqualsTest() {

    DragonBallUser goku = new DragonBallUser();
    goku.setId(10L);
    goku.setAge(49);
    goku.setEmail("gokuTestMock@dbz.com");
    goku.setUsername("gokuTestMock");
    goku.setPowerLevel(30);
    goku.setStamina(1000);

    DragonBallUser goku1 = new DragonBallUser();
    goku1.setId(10L);
    goku1.setAge(49);
    goku1.setEmail("gokuTestMock@dbz.com");
    goku1.setUsername("gokuTestMock");
    goku1.setPowerLevel(30);
    goku1.setStamina(1000);

    DragonBallUser gohan = new DragonBallUser();
    gohan.setId(12L);
    gohan.setAge(29);
    gohan.setEmail("gohanTestMock@dbz.com");
    gohan.setUsername("gohanTestMock");
    gohan.setPowerLevel(20);
    gohan.setStamina(1000);

    assertEquals(goku, goku1);
    assertEquals(goku.hashCode(), goku.hashCode());
    assertEquals(goku, goku1);
    assertEquals(goku.hashCode(), goku1.hashCode());
    assertTrue(goku.equals(goku1));
    assertThat(goku, not(equalTo(gohan)));
  }

  /**
   * Test the hashCode and equals methods in DragonBallUser.
   */
  @Test
  public void attackAndRecoverTest() {

    DragonBallUser goku = new DragonBallUser();
    goku.setId(10L);
    goku.setAge(49);
    goku.setEmail("gokuTestMock@dbz.com");
    goku.setUsername("gokuTestMock");
    goku.setPowerLevel(30);
    goku.setStamina(1000);

    DragonBallUser gohan = new DragonBallUser();
    gohan.setId(12L);
    gohan.setAge(29);
    gohan.setEmail("gohanTestMock@dbz.com");
    gohan.setUsername("gohanTestMock");
    gohan.setPowerLevel(20);
    gohan.setStamina(1000);

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

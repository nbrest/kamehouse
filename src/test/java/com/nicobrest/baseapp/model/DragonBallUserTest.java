package com.nicobrest.baseapp.model;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.nicobrest.baseapp.exception.BaseAppInvalidDataException;
import com.nicobrest.baseapp.model.DragonBallUser;

import org.junit.Rule;

import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit tests for the DragonBallUser class.
 * 
 * @author nbrest
 */
public class DragonBallUserTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(DragonBallUserTest.class);
  
  @Rule
  public ExpectedException thrown = ExpectedException.none();
  
  /**
   * Test the failure flow of validateUsernameFormat.
   * 
   * @author nbrest
   */
  @Test
  public void validateUsernameFormatExceptionTest() {
    LOGGER.info("***** Executing validateUsernameFormatExceptionTest");
    
    thrown.expect(BaseAppInvalidDataException.class);
    thrown.expectMessage("Invalid username format:");
    
    DragonBallUser user1 = new DragonBallUser(1L,".goku.9.enzo", "goku@dbz.com", 20, 20, 20);
    user1.validateAllFields();     
  }
  
  /**
   * Test the failure flow of validateEmailFormat.
   * 
   * @author nbrest
   */
  @Test
  public void validateEmailFormatExceptionTest() { 
    LOGGER.info("***** Executing validateEmailFormatExceptionTest");
    
    thrown.expect(BaseAppInvalidDataException.class);
    thrown.expectMessage("Invalid email address: ");
    
    DragonBallUser user1 = new DragonBallUser(1L,"goku", "goku.9.enzo@@dbz.com", 20, 20, 20);
    user1.validateAllFields(); 
  }
  
  /**
   * Test the failure flow of validatePositiveValue.
   * 
   * @author nbrest
   */
  @Test
  public void validatePositiveValueExceptionTest() { 
    LOGGER.info("***** Executing validatePositiveValueExceptionTest");
    
    thrown.expect(BaseAppInvalidDataException.class);
    thrown.expectMessage("The attribute should be a positive value. Current value:");
    
    DragonBallUser user1 = new DragonBallUser(1L,"goku", "goku@dbz.com", -10, 20, 20);
    user1.validateAllFields(); 
  }
  
  /**
   * Test the failure flow of validateStringLength.
   * 
   * @author nbrest
   */
  @Test
  public void validateStringLengthExceptionTest() { 
    LOGGER.info("***** Executing validateStringLengthExceptionTest");
    
    thrown.expect(BaseAppInvalidDataException.class);
    thrown.expectMessage("The string attribute excedes the maximum length of ");
    
    StringBuilder sb = new StringBuilder();
    for (int i = 0 ; i < 70 ; i++) {
      sb.append("goku");
    }
    String username = sb.toString();
    
    DragonBallUser user1 = new DragonBallUser(1L,username, "goku@dbz.com", -10, 20, 20);
    user1.validateAllFields(); 
  }
  
  /**
   * Test the hashCode and equals methods in DragonBallUser.
   * 
   * @author nbrest
   */
  @Test
  public void hashCodeAndEqualsTest() {
    LOGGER.info("***** Executing hashCodeAndEqualsTest");

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

    assertEquals(goku, goku);
    assertEquals(goku.hashCode(), goku.hashCode());
    assertEquals(goku, goku1);
    assertEquals(goku.hashCode(), goku1.hashCode());
    assertTrue(goku.equals(goku1));
    assertThat(goku, not(equalTo(gohan)));
  }

  /**
   * Test the hashCode and equals methods in DragonBallUser.
   * 
   * @author nbrest
   */
  @Test
  public void attackAndRecoverTest() {
    LOGGER.info("***** Executing attackAndRecoverTest");

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

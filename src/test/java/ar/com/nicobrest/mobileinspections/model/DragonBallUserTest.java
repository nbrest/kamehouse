package ar.com.nicobrest.mobileinspections.model;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals; 
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import ar.com.nicobrest.mobileinspections.model.DragonBallUser;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *        Unit tests for the DragonBallUser class.
 *         
 * @author nbrest
 */
public class DragonBallUserTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(DragonBallUserTest.class);
  
  /**
   *      Test the hashCode and equals methods in DragonBallUser.
   *      
   * @author nbrest
   */
  @Test
  public void hashCodeAndEqualsTest() {
    LOGGER.info("****************** Executing hashCodeAndEqualsTest ******************");
    
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
   *      Test the hashCode and equals methods in DragonBallUser.
   *      
   * @author nbrest
   */
  @Test
  public void attackAndRecoverTest() {
    LOGGER.info("****************** Executing attackAndRecoverTest ******************");
    
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
    
    for (int i = 0; i < 100 ; i++) {
      goku.attack(gohan);
    }
    assertEquals(0, gohan.getStamina()); 
  }  
}

package ar.com.nicobrest.mobileinspections.model;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals; 
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import ar.com.nicobrest.mobileinspections.model.HelloWorldUser;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since v0.03 
 * @author nicolas.brest
 *
 *         Unit tests for the HelloWorldUser class
 */
public class HelloWorldUserTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldUserTest.class);
  
  /**
   * @since v0.03
   * @author nbrest
   * 
   *      Test the hashCode and equals methods in HelloWorldUser
   */
  @Test
  public void hashCodeAndEqualsTest() {
    LOGGER.info("****************** Executing hashCodeAndEqualsTest ******************");
    
    HelloWorldUser goku = new HelloWorldUser();
    goku.setAge(49);
    goku.setEmail("gokuTestMock@dbz.com");
    goku.setUsername("gokuTestMock");

    HelloWorldUser goku1 = new HelloWorldUser();
    goku1.setAge(49);
    goku1.setEmail("gokuTestMock@dbz.com");
    goku1.setUsername("gokuTestMock");
    
    HelloWorldUser gohan = new HelloWorldUser();
    gohan.setAge(29);
    gohan.setEmail("gohanTestMock@dbz.com");
    gohan.setUsername("gohanTestMock");
        
    assertEquals(goku, goku);
    assertEquals(goku.hashCode(), goku.hashCode());
    assertEquals(goku, goku1);
    assertEquals(goku.hashCode(), goku1.hashCode());
    assertTrue(goku.equals(goku1));
    assertThat(goku, not(equalTo(gohan)));
  }
}

package com.nicobrest.kamehouse.model;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for the ApplicationUser model class.
 * 
 * @author nbrest
 *
 */
public class ApplicationUserTest {

  /**
   * Tests equals and hashCode.
   */
  @Test
  public void hashCodeAndEqualsTest() {
    ApplicationUser goku = new ApplicationUser();
    goku.setId(10L);
    goku.setEmail("goku@dbz.com");
    goku.setUsername("goku");
    goku.setPassword("gokupass");

    ApplicationUser goku1 = new ApplicationUser();
    goku1.setId(10L);
    goku1.setEmail("goku@dbz.com");
    goku1.setUsername("goku");
    goku1.setPassword("gokupass");
    
    ApplicationUser gohan = new ApplicationUser();
    gohan.setId(10L);
    gohan.setEmail("gohan@dbz.com");
    gohan.setUsername("gohan");
    gohan.setPassword("gohanpass");

    assertEquals(goku, goku1);
    assertEquals(goku.hashCode(), goku.hashCode());
    assertEquals(goku, goku1);
    assertEquals(goku.hashCode(), goku1.hashCode());
    assertTrue(goku.equals(goku1));
    assertThat(goku, not(equalTo(gohan)));
  }
}

package com.nicobrest.kamehouse.admin.model;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

import com.nicobrest.kamehouse.admin.model.ApplicationRole;

import org.junit.Test;

/**
 * Tests for the ApplicationRole model class.
 * 
 * @author nbrest
 *
 */
public class ApplicationRoleTest {

  /**
   * Tests hashCode and Equals.
   */
  @Test
  public void test() {
    ApplicationRole goku = new ApplicationRole();
    goku.setName("ROLE_GOKU");
    ApplicationRole goku1 = new ApplicationRole();
    goku1.setName("ROLE_GOKU");
    ApplicationRole gohan = new ApplicationRole();
    gohan.setName("ROLE_GOHAN");
    
    assertEquals(goku, goku1);
    assertEquals(goku.hashCode(), goku.hashCode());
    assertEquals(goku, goku1);
    assertEquals(goku.hashCode(), goku1.hashCode());
    assertTrue(goku.equals(goku1));
    assertThat(goku, not(equalTo(gohan)));
  }
}

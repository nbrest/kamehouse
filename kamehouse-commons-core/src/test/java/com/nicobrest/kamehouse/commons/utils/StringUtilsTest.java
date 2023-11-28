package com.nicobrest.kamehouse.commons.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import org.junit.jupiter.api.Test;

/**
 * StringUtils tests.
 *
 * @author nbrest
 */
class StringUtilsTest {

  @Test
  void sanitizeTest() {
    String input = "goku\ngohan\ttrunks\rsanad<>^&`a";
    String output = StringUtils.sanitize(input);
    assertEquals("gokugohantrunkssanada", output);
  }

  @Test
  void sanitizeEntityTest() {
    KameHouseUser kameHouseUser = new KameHouseUser();
    kameHouseUser.setUsername("goku\ngohan\ttrunks\rsanad<>^&`a");
    kameHouseUser.setPassword("goten");
    kameHouseUser.setId(1L);
    kameHouseUser.setEmail("madamada@dane.com");
    String output = StringUtils.sanitize(kameHouseUser);
    String expected = "{id:1,username:goku\\ngohan\\ttrunks\\rsanada,email:madamada@dane.com,"
        + "firstName:null,lastName:null,lastLogin:null,accountNonExpired:true,"
        + "accountNonLocked:true,credentialsNonExpired:true,enabled:true,password:****,"
        + "authorities:****}";
    assertEquals(expected, output);
  }
}

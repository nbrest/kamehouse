package com.nicobrest.kamehouse.commons.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import com.nicobrest.kamehouse.commons.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.commons.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.commons.exception.KameHouseException;
import com.nicobrest.kamehouse.commons.exception.KameHouseForbiddenException;
import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidCommandException;
import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.commons.model.dto.KameHouseRoleDto;
import com.nicobrest.kamehouse.commons.model.dto.KameHouseUserDto;
import org.junit.Test;

import java.util.Date;
import java.util.HashSet;

/**
 * Tests the model objects in kamehouse commons core module.
 */
public class CommonsCoreModelTest {

  private static String message = "goku";
  private static Exception cause = new NullPointerException();

  /**
   * Test kamehouse core models.
   */
  @Test
  public void baseModelClassesTest() {
    validateKameHouseGenericResponse();
    validateKameHouseRoleDto();
    validateKameHouseUserDto();
    validateKameHouseRole();
    validateKameHouseUser();
  }

  /**
   * Test kamehouse exceptions.
   */
  @Test
  public void exceptionClassesTest() {
    validateKameHouseBadRequestException();
    validateKameHouseConflictException();
    validateKameHouseException();
    validateKameHouseForbiddenException();
    validateKameHouseInvalidCommandException();
    validateKameHouseInvalidDataException();
    validateKameHouseNotFoundException();
    validateKameHouseServerErrorException();
  }

  /**
   * Test KameHouseGenericResponse.
   */
  private void validateKameHouseGenericResponse() {
    KameHouseGenericResponse kameHouseGenericResponse1 = new KameHouseGenericResponse();
    kameHouseGenericResponse1.setMessage("goku");
    KameHouseGenericResponse kameHouseGenericResponse2 = new KameHouseGenericResponse();
    kameHouseGenericResponse2.setMessage("gohan");

    validateModelObjects(kameHouseGenericResponse1, kameHouseGenericResponse2);
    assertNotNull(kameHouseGenericResponse1.getMessage());
  }

  /**
   * Test KameHouseRoleDto.
   */
  private void validateKameHouseRoleDto() {
    KameHouseRoleDto kameHouseRoleDto1 = new KameHouseRoleDto();
    kameHouseRoleDto1.setId(1L);
    kameHouseRoleDto1.setKameHouseUser(new KameHouseUserDto());
    kameHouseRoleDto1.setName("goku");
    KameHouseRoleDto kameHouseRoleDto2 = new KameHouseRoleDto();
    kameHouseRoleDto2.setId(2L);
    kameHouseRoleDto2.setKameHouseUser(new KameHouseUserDto());
    kameHouseRoleDto2.setName("gohan");

    validateModelObjects(kameHouseRoleDto1, kameHouseRoleDto2);
    assertNotNull(kameHouseRoleDto1.getName());
    assertNotNull(kameHouseRoleDto1.getId());
    assertNotNull(kameHouseRoleDto1.getKameHouseUser());
  }

  /**
   * Test KameHouseUserDto.
   */
  private void validateKameHouseUserDto() {
    KameHouseUserDto kameHouseUserDto1 = new KameHouseUserDto();
    kameHouseUserDto1.setPassword("goku");
    kameHouseUserDto1.setAccountNonExpired(true);
    kameHouseUserDto1.setAccountNonLocked(true);
    kameHouseUserDto1.setAuthorities(new HashSet<>());
    kameHouseUserDto1.setCredentialsNonExpired(true);
    kameHouseUserDto1.setEmail("goku@dbz.com");
    kameHouseUserDto1.setFirstName("goku");
    kameHouseUserDto1.setLastName("son");
    kameHouseUserDto1.setId(1L);
    kameHouseUserDto1.setEnabled(true);
    kameHouseUserDto1.setLastLogin(new Date());
    KameHouseUserDto kameHouseUserDto2 = new KameHouseUserDto();

    validateModelObjects(kameHouseUserDto1, kameHouseUserDto2);
    assertNotNull(kameHouseUserDto1.getPassword());
    assertNotNull(kameHouseUserDto1.isAccountNonExpired());
    assertNotNull(kameHouseUserDto1.isAccountNonLocked());
    assertNotNull(kameHouseUserDto1.getAuthorities());
    assertNotNull(kameHouseUserDto1.isCredentialsNonExpired());
    assertNotNull(kameHouseUserDto1.getEmail());
    assertNotNull(kameHouseUserDto1.getFirstName());
    assertNotNull(kameHouseUserDto1.getLastName());
    assertNotNull(kameHouseUserDto1.isEnabled());
    assertNotNull(kameHouseUserDto1.getLastLogin());
  }

  /**
   * Test KameHouseRole.
   */
  private void validateKameHouseRole() {
    KameHouseRole kameHouseRole1 = new KameHouseRole();
    kameHouseRole1.setName("ROLE_KAMISAMA");
    kameHouseRole1.setKameHouseUser(new KameHouseUser());
    kameHouseRole1.setId(1L);
    KameHouseRole kameHouseRole2 = new KameHouseRole();

    validateModelObjects(kameHouseRole1, kameHouseRole2);
    assertNotNull(kameHouseRole1.getName());
    assertNotNull(kameHouseRole1.getId());
    assertNotNull(kameHouseRole1.getKameHouseUser());
    assertNotNull(kameHouseRole1.getAuthority());
  }

  /**
   * Test KameHouseUser.
   */
  private void validateKameHouseUser() {
    KameHouseUser kameHouseUser1 = new KameHouseUser();
    kameHouseUser1.setPassword("goku");
    kameHouseUser1.setAccountNonExpired(true);
    kameHouseUser1.setAccountNonLocked(true);
    kameHouseUser1.setAuthorities(new HashSet<>());
    kameHouseUser1.setCredentialsNonExpired(true);
    kameHouseUser1.setEmail("goku@dbz.com");
    kameHouseUser1.setFirstName("goku");
    kameHouseUser1.setLastName("son");
    kameHouseUser1.setId(1L);
    kameHouseUser1.setEnabled(true);
    kameHouseUser1.setLastLogin(new Date());
    kameHouseUser1.addAuthority(new KameHouseRole());
    kameHouseUser1.removeAuthority(new KameHouseRole());
    KameHouseUser kameHouseUser2 = new KameHouseUser();
    kameHouseUser2.setLastLogin(null);
    kameHouseUser2.setAuthorities(null);

    validateModelObjects(kameHouseUser1, kameHouseUser2);
    assertNotNull(kameHouseUser1.isAccountNonExpired());
    assertNotNull(kameHouseUser1.isAccountNonLocked());
    assertNotNull(kameHouseUser1.isCredentialsNonExpired());
    assertNotNull(kameHouseUser1.isEnabled());
    assertNotNull(kameHouseUser1.getLastLogin());
  }

  /**
   * Compare model objects.
   */
  private void validateModelObjects(Object object1, Object object2) {
    assertNotEquals(object1, object2);
    assertNotEquals(object1.hashCode(), object2.hashCode());
    assertNotNull(object1.toString());
  }

  /**
   * Test KameHouseBadRequestException.
   */
  private void validateKameHouseBadRequestException () {
    KameHouseBadRequestException kameHouseBadRequestException =
        new KameHouseBadRequestException(message);
    validateException(kameHouseBadRequestException, message, null);
    KameHouseBadRequestException kameHouseBadRequestException2 =
        new KameHouseBadRequestException(message, cause);
    validateException(kameHouseBadRequestException2, message, cause);
  }

  /**
   * Test KameHouseConflictException.
   */
  private void validateKameHouseConflictException () {
    KameHouseConflictException kameHouseConflictException = new KameHouseConflictException(message);
    validateException(kameHouseConflictException, message, null);
    KameHouseConflictException kameHouseConflictException2 =
        new KameHouseConflictException(message, cause);
    validateException(kameHouseConflictException2, message, cause);
  }

  /**
   * Test KameHouseException.
   */
  private void validateKameHouseException () {
    KameHouseException kameHouseException = new KameHouseException(message);
    validateException(kameHouseException, message, null);
    KameHouseException kameHouseException2 = new KameHouseException(message, cause);
    validateException(kameHouseException2, message, cause);
    KameHouseException kameHouseException3 = new KameHouseException(cause);
    validateException(kameHouseException3, "java.lang.NullPointerException", cause);
  }

  /**
   * Test KameHouseForbiddenException.
   */
  private void validateKameHouseForbiddenException () {
    KameHouseForbiddenException kameHouseForbiddenException =
        new KameHouseForbiddenException(message);
    validateException(kameHouseForbiddenException, message, null);
  }

  /**
   * Test KameHouseInvalidCommandException.
   */
  private void validateKameHouseInvalidCommandException () {
    KameHouseInvalidCommandException kameHouseInvalidCommandException =
        new KameHouseInvalidCommandException(message);
    validateException(kameHouseInvalidCommandException, message, null);
    KameHouseInvalidCommandException kameHouseInvalidCommandException2 =
        new KameHouseInvalidCommandException(message, cause);
    validateException(kameHouseInvalidCommandException2, message, cause);
  }

  /**
   * Test KameHouseInvalidDataException.
   */
  private void validateKameHouseInvalidDataException () {
    KameHouseInvalidDataException kameHouseInvalidDataException =
        new KameHouseInvalidDataException(message);
    validateException(kameHouseInvalidDataException, message, null);
  }

  /**
   * Test KameHouseNotFoundException.
   */
  private void validateKameHouseNotFoundException () {
    KameHouseNotFoundException kameHouseNotFoundException =
        new KameHouseNotFoundException(message);
    validateException(kameHouseNotFoundException, message, null);
    KameHouseNotFoundException kameHouseNotFoundException2 =
        new KameHouseNotFoundException(message, cause);
    validateException(kameHouseNotFoundException2, message, cause);
  }

  /**
   * Test KameHouseServerErrorException.
   */
  private void validateKameHouseServerErrorException () {
    KameHouseServerErrorException kameHouseServerErrorException =
        new KameHouseServerErrorException(message);
    validateException(kameHouseServerErrorException, message, null);
    KameHouseServerErrorException kameHouseServerErrorException2 =
        new KameHouseServerErrorException(message, cause);
    validateException(kameHouseServerErrorException2, message, cause);
  }

  /**
   * Validate exception contents.
   */
  private void validateException(Exception exception, String message, Exception cause) {
    assertEquals(message, exception.getMessage());
    assertEquals(cause, exception.getCause());
  }
}

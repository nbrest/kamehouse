package com.nicobrest.kamehouse.commons.testutils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nicobrest.kamehouse.commons.model.KameHouseRole;
import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import com.nicobrest.kamehouse.commons.model.dto.KameHouseRoleDto;
import com.nicobrest.kamehouse.commons.model.dto.KameHouseUserDto;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Test data and common test methods to test KameHouseUsers in all layers of the application.
 *
 * @author nbrest
 */
public class KameHouseUserTestUtils extends AbstractTestUtils<KameHouseUser, KameHouseUserDto> {

  public static final String API_V1_ADMIN_KAMEHOUSE_USERS = "/api/v1/admin/kamehouse/users";
  public static final Long INVALID_ID = 987987L;
  public static final String INVALID_USERNAME = "yukimura";
  public static final String TEST_EMAIL = "goku@dbz.com";
  public static final String GOKU = "goku";
  public static final String SON = "son";

  @Override
  public void initTestData() {
    initSingleTestData();
    initTestDataList();
    initTestDataDto();
  }

  @Override
  public void assertEqualsAllAttributes(
      KameHouseUser expectedEntity, KameHouseUser returnedEntity) {
    assertEquals(expectedEntity, returnedEntity);
    assertEquals(expectedEntity.getId(), returnedEntity.getId());
    assertEquals(expectedEntity.getUsername(), returnedEntity.getUsername());
    assertEquals(expectedEntity.getPassword(), returnedEntity.getPassword());
    assertEquals(expectedEntity.getEmail(), returnedEntity.getEmail());
    assertEquals(expectedEntity.getFirstName(), returnedEntity.getFirstName());
    assertEquals(expectedEntity.getLastName(), returnedEntity.getLastName());
    assertEquals(expectedEntity.getLastLogin(), returnedEntity.getLastLogin());
    assertThat(returnedEntity.getAuthorities(), is(expectedEntity.getAuthorities()));
  }

  /** Returns a KameHouse user with an invalid username. */
  public KameHouseUser getBadUsernameKameHouseUser() {
    KameHouseUser badUsernameKameHouseUser = new KameHouseUser();
    badUsernameKameHouseUser.setId(1000L);
    badUsernameKameHouseUser.setEmail(TEST_EMAIL);
    badUsernameKameHouseUser.setUsername(null);
    badUsernameKameHouseUser.setPassword("gokupass");
    return badUsernameKameHouseUser;
  }

  /** Returns a KameHouse user with an invalid password. */
  public KameHouseUser getBadPasswordKameHouseUser() {
    KameHouseUser badPasswordKameHouseUser = new KameHouseUser();
    badPasswordKameHouseUser.setId(1000L);
    badPasswordKameHouseUser.setEmail(TEST_EMAIL);
    badPasswordKameHouseUser.setUsername("gokuuser");
    badPasswordKameHouseUser.setPassword(null);
    return badPasswordKameHouseUser;
  }

  private void initSingleTestData() {
    singleTestData = new KameHouseUser();
    singleTestData.setId(null);
    singleTestData.setEmail(TEST_EMAIL);
    singleTestData.setUsername(GOKU);
    singleTestData.setPassword(GOKU);
    singleTestData.setFirstName(GOKU);
    singleTestData.setLastName(SON);
    KameHouseRole kameHouseRole = new KameHouseRole();
    kameHouseRole.setId(null);
    kameHouseRole.setName("ROLE_KAMISAMA");
    kameHouseRole.setKameHouseUser(singleTestData);
    Set<KameHouseRole> authorities = new HashSet<>();
    authorities.add(kameHouseRole);
    singleTestData.setAuthorities(authorities);
  }

  private void initTestDataDto() {
    testDataDto = new KameHouseUserDto();
    testDataDto.setId(null);
    testDataDto.setEmail(TEST_EMAIL);
    testDataDto.setUsername(GOKU);
    testDataDto.setPassword(GOKU);
    testDataDto.setFirstName(GOKU);
    testDataDto.setLastName(SON);
    testDataDto.setAccountNonExpired(true);
    testDataDto.setAccountNonLocked(true);
    testDataDto.setCredentialsNonExpired(true);
    testDataDto.setEnabled(true);
    testDataDto.setLastLogin(new Date());
    KameHouseRoleDto kameHouseRoleDto = new KameHouseRoleDto();
    kameHouseRoleDto.setId(null);
    kameHouseRoleDto.setName("ROLE_KAMISAMA");
    kameHouseRoleDto.setKameHouseUser(testDataDto);
    Set<KameHouseRoleDto> authoritiesDto = new HashSet<>();
    authoritiesDto.add(kameHouseRoleDto);
    testDataDto.setAuthorities(authoritiesDto);
  }

  private void initTestDataList() {
    KameHouseUser kameHouseUser2 = new KameHouseUser();
    kameHouseUser2.setId(null);
    kameHouseUser2.setEmail("gohan@dbz.com");
    kameHouseUser2.setUsername("gohan");
    kameHouseUser2.setPassword("gohan");
    Set<KameHouseRole> roles2 = new HashSet<>();
    KameHouseRole userRole2 = new KameHouseRole();
    userRole2.setName("ROLE_SAIYAJIN");
    userRole2.setKameHouseUser(kameHouseUser2);
    roles2.add(userRole2);
    kameHouseUser2.setAuthorities(roles2);

    KameHouseUser kameHouseUser3 = new KameHouseUser();
    kameHouseUser3.setId(null);
    kameHouseUser3.setEmail("goten@dbz.com");
    kameHouseUser3.setUsername("goten");
    kameHouseUser3.setPassword("goten");
    Set<KameHouseRole> roles3 = new HashSet<>();
    KameHouseRole userRole3 = new KameHouseRole();
    userRole3.setName("ROLE_SAIYAJIN");
    userRole3.setKameHouseUser(kameHouseUser3);
    roles3.add(userRole3);
    kameHouseUser3.setAuthorities(roles3);

    testDataList = new LinkedList<>();
    testDataList.add(singleTestData);
    testDataList.add(kameHouseUser2);
    testDataList.add(kameHouseUser3);
  }
}

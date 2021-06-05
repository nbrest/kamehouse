package com.nicobrest.kamehouse.admin.testutils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import com.nicobrest.kamehouse.admin.model.ApplicationRole;
import com.nicobrest.kamehouse.admin.model.ApplicationUser;
import com.nicobrest.kamehouse.admin.model.dto.ApplicationRoleDto;
import com.nicobrest.kamehouse.admin.model.dto.ApplicationUserDto;
import com.nicobrest.kamehouse.main.testutils.AbstractTestUtils;
import com.nicobrest.kamehouse.main.testutils.TestUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Test data and common test methods to test ApplicationUsers in all layers of
 * the application.
 * 
 * @author nbrest
 *
 */
public class ApplicationUserTestUtils extends
    AbstractTestUtils<ApplicationUser, ApplicationUserDto> implements
    TestUtils<ApplicationUser, ApplicationUserDto> {

  public static final String API_V1_ADMIN_APPLICATION_USERS = "/api/v1/admin/application/users/";
  public static final Long INVALID_ID = 987987L;
  public static final String INVALID_USERNAME = "yukimura";

  @Override
  public void initTestData() {
    initSingleTestData();
    initTestDataList();
    initTestDataDto();
  }

  @Override
  public void assertEqualsAllAttributes(ApplicationUser expectedEntity,
      ApplicationUser returnedEntity) {
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

  /**
   * Returns an application user with an invalid username.
   */
  public ApplicationUser getBadUsernameApplicationUser() {
    ApplicationUser badUsernameApplicationUser = new ApplicationUser();
    badUsernameApplicationUser.setId(1000L);
    badUsernameApplicationUser.setEmail("goku@dbz.com");
    badUsernameApplicationUser.setUsername(null);
    badUsernameApplicationUser.setPassword("gokupass");
    return badUsernameApplicationUser;
  }
  
  /**
   * Returns an application user with an invalid password.
   */
  public ApplicationUser getBadPasswordApplicationUser() {
    ApplicationUser badPasswordApplicationUser = new ApplicationUser();
    badPasswordApplicationUser.setId(1000L);
    badPasswordApplicationUser.setEmail("goku@dbz.com");
    badPasswordApplicationUser.setUsername("gokuuser");
    badPasswordApplicationUser.setPassword(null);
    return badPasswordApplicationUser;
  }
  
  private void initSingleTestData() {
    singleTestData = new ApplicationUser();
    singleTestData.setId(null);
    singleTestData.setEmail("goku@dbz.com");
    singleTestData.setUsername("goku");
    singleTestData.setPassword("goku");
    singleTestData.setFirstName("Goku");
    singleTestData.setLastName("Son");
    Set<ApplicationRole> authorities = new HashSet<>();
    ApplicationRole applicationRole = new ApplicationRole();
    applicationRole.setId(null);
    applicationRole.setName("ADMIN_ROLE");
    applicationRole.setApplicationUser(singleTestData);
    authorities.add(applicationRole);
    singleTestData.setAuthorities(authorities);
  }

  private void initTestDataDto() {
    testDataDto = new ApplicationUserDto();
    testDataDto.setId(null);
    testDataDto.setEmail("goku@dbz.com");
    testDataDto.setUsername("goku");
    testDataDto.setPassword("goku");
    testDataDto.setFirstName("Goku");
    testDataDto.setLastName("Son");
    testDataDto.setAccountNonExpired(true);
    testDataDto.setAccountNonLocked(true);
    testDataDto.setCredentialsNonExpired(true);
    testDataDto.setEnabled(true);
    testDataDto.setLastLogin(new Date());
    Set<ApplicationRoleDto> authoritiesDto = new HashSet<>();
    ApplicationRoleDto applicationRoleDto = new ApplicationRoleDto();
    applicationRoleDto.setId(null);
    applicationRoleDto.setName("ADMIN_ROLE");
    applicationRoleDto.setApplicationUser(testDataDto);
    authoritiesDto.add(applicationRoleDto);
    testDataDto.setAuthorities(authoritiesDto);
  }

  private void initTestDataList() {
    ApplicationUser applicationUser2 = new ApplicationUser();
    applicationUser2.setId(null);
    applicationUser2.setEmail("gohan@dbz.com");
    applicationUser2.setUsername("gohan");
    applicationUser2.setPassword("gohan");
    Set<ApplicationRole> roles2 = new HashSet<ApplicationRole>();
    ApplicationRole userRole2 = new ApplicationRole();
    userRole2.setName("ROLE_USER");
    userRole2.setApplicationUser(applicationUser2);
    roles2.add(userRole2);
    applicationUser2.setAuthorities(roles2);

    ApplicationUser applicationUser3 = new ApplicationUser();
    applicationUser3.setId(null);
    applicationUser3.setEmail("goten@dbz.com");
    applicationUser3.setUsername("goten");
    applicationUser3.setPassword("goten");
    Set<ApplicationRole> roles3 = new HashSet<ApplicationRole>();
    ApplicationRole userRole3 = new ApplicationRole();
    userRole3.setName("ROLE_USER");
    userRole3.setApplicationUser(applicationUser3);
    roles3.add(userRole3);
    applicationUser3.setAuthorities(roles3);

    testDataList = new LinkedList<>();
    testDataList.add(singleTestData);
    testDataList.add(applicationUser2);
    testDataList.add(applicationUser3);
  }
}

package com.nicobrest.kamehouse.admin.testutils;

import com.nicobrest.kamehouse.admin.model.ApplicationRole;
import com.nicobrest.kamehouse.admin.model.ApplicationUser;
import com.nicobrest.kamehouse.admin.service.dto.ApplicationRoleDto;
import com.nicobrest.kamehouse.admin.service.dto.ApplicationUserDto;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ApplicationUserTestUtils {

  public static final String API_V1_ADMIN_APPLICATION_USERS = "/api/v1/admin/application/users/";
  public static final Long INVALID_ID = 987987L;
  public static final String INVALID_USERNAME = "yukimura";
  private static ApplicationUser applicationUser;
  private static List<ApplicationUser> applicationUsersList;
  private static ApplicationUserDto applicationUserDto;
  
  static {
    initApplicationUserTestData();
  }
  
  public static ApplicationUser getApplicationUser() {
    return applicationUser;
  }

  public static List<ApplicationUser> getApplicationUsersList() {
    return applicationUsersList;
  }

  public static ApplicationUserDto getApplicationUserDto() {
    return applicationUserDto;
  }

  public static void initApplicationUserTestData() {
    initApplicationUser();
    initApplicationUsersList();
    initApplicationUserDto();
  }
  
  public static void initApplicationUser() {
    applicationUser = new ApplicationUser();
    applicationUser.setId(1001L);
    applicationUser.setEmail("goku@dbz.com");
    applicationUser.setUsername("goku");
    applicationUser.setPassword("goku");
    applicationUser.setFirstName("Goku");
    applicationUser.setLastName("Son");
    Set<ApplicationRole> authorities = new HashSet<>();
    ApplicationRole applicationRole = new ApplicationRole();
    applicationRole.setId(10L);
    applicationRole.setName("ADMIN_ROLE");
    applicationRole.setApplicationUser(applicationUser);
    authorities.add(applicationRole);
    applicationUser.setAuthorities(authorities);
  }
  
  public static void initApplicationUserDto() {
    applicationUserDto = new ApplicationUserDto();
    applicationUserDto.setId(1001L);
    applicationUserDto.setEmail("goku@dbz.com");
    applicationUserDto.setUsername("goku");
    applicationUserDto.setPassword("goku");
    applicationUserDto.setFirstName("Goku");
    applicationUserDto.setLastName("Son");
    applicationUserDto.setAccountNonExpired(true);
    applicationUserDto.setAccountNonLocked(true);
    applicationUserDto.setCredentialsNonExpired(true);
    applicationUserDto.setEnabled(true);
    applicationUserDto.setLastLogin(new Date());
    Set<ApplicationRoleDto> authoritiesDto = new HashSet<>();
    ApplicationRoleDto applicationRoleDto = new ApplicationRoleDto();
    applicationRoleDto.setId(10L);
    applicationRoleDto.setName("ADMIN_ROLE");
    applicationRoleDto.setApplicationUser(applicationUserDto);
    authoritiesDto.add(applicationRoleDto);
    applicationUserDto.setAuthorities(authoritiesDto);
  }
  
  public static void initApplicationUsersList() {
    ApplicationUser applicationUser2 = new ApplicationUser();
    applicationUser2.setId(1002L);
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
    applicationUser3.setId(1003L);
    applicationUser3.setEmail("goten@dbz.com");
    applicationUser3.setUsername("goten");
    applicationUser3.setPassword("goten");
    Set<ApplicationRole> roles3 = new HashSet<ApplicationRole>();
    ApplicationRole userRole3 = new ApplicationRole();
    userRole3.setName("ROLE_USER");
    userRole3.setApplicationUser(applicationUser3);
    roles3.add(userRole3);
    applicationUser3.setAuthorities(roles3);
    
    applicationUsersList = new LinkedList<ApplicationUser>();
    applicationUsersList.add(applicationUser);
    applicationUsersList.add(applicationUser2);
    applicationUsersList.add(applicationUser3);
  }
}

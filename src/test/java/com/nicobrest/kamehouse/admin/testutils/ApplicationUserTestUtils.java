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

  private static ApplicationUser applicationUser;
  private static List<ApplicationUser> applicationUsersList;
  private static ApplicationUserDto applicationUserDto;
  
  public static ApplicationUser getApplicationUser() {
    return applicationUser;
  }

  public static List<ApplicationUser> getApplicationUsersList() {
    return applicationUsersList;
  }

  public static ApplicationUserDto getApplicationUserDto() {
    return applicationUserDto;
  }

  static {
    initApplicationUserTestData();
  }
  
  public static void initApplicationUserTestData() {
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
    authorities.add(applicationRole);
    applicationUser.setAuthorities(authorities);
    
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
    authoritiesDto.add(applicationRoleDto);
    applicationUserDto.setAuthorities(authoritiesDto);

    ApplicationUser applicationUserMock2 = new ApplicationUser();
    applicationUserMock2.setId(1002L);
    applicationUserMock2.setEmail("gohan@dbz.com");
    applicationUserMock2.setUsername("gohan");
    applicationUserMock2.setPassword("gohan");
    Set<ApplicationRole> rolesMock2 = new HashSet<ApplicationRole>();
    ApplicationRole userRoleMock2 = new ApplicationRole();
    userRoleMock2.setName("ROLE_USER");
    rolesMock2.add(userRoleMock2);
    applicationUserMock2.setAuthorities(rolesMock2);
    
    ApplicationUser applicationUserMock3 = new ApplicationUser();
    applicationUserMock3.setId(1003L);
    applicationUserMock3.setEmail("goten@dbz.com");
    applicationUserMock3.setUsername("goten");
    applicationUserMock3.setPassword("goten");
    Set<ApplicationRole> rolesMock3 = new HashSet<ApplicationRole>();
    ApplicationRole userRoleMock3 = new ApplicationRole();
    userRoleMock3.setName("ROLE_USER");
    rolesMock3.add(userRoleMock3);
    applicationUserMock3.setAuthorities(rolesMock3);
    
    applicationUsersList = new LinkedList<ApplicationUser>();
    applicationUsersList.add(applicationUser);
    applicationUsersList.add(applicationUserMock2);
    applicationUsersList.add(applicationUserMock3);
  }
}

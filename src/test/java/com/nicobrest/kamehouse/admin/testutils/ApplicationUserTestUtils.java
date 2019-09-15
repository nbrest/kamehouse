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

  private static ApplicationUser applicationUserMock;
  private static List<ApplicationUser> applicationUsersMockList;
  private static ApplicationUserDto applicationUserDtoMock;
  
  public static ApplicationUser getApplicationUserMock() {
    return applicationUserMock;
  }

  public static List<ApplicationUser> getApplicationUsersMockList() {
    return applicationUsersMockList;
  }

  public static ApplicationUserDto getApplicationUserDtoMock() {
    return applicationUserDtoMock;
  }

  static {
    initApplicationUserMocks();
  }
  
  public static void initApplicationUserMocks() {
    
    applicationUserMock = new ApplicationUser();
    applicationUserMock.setId(1001L);
    applicationUserMock.setEmail("goku@dbz.com");
    applicationUserMock.setUsername("goku");
    applicationUserMock.setPassword("goku");
    applicationUserMock.setFirstName("Goku");
    applicationUserMock.setLastName("Son");
    Set<ApplicationRole> authorities = new HashSet<>();
    ApplicationRole applicationRole = new ApplicationRole();
    applicationRole.setId(10L);
    applicationRole.setName("ADMIN_ROLE");
    authorities.add(applicationRole);
    applicationUserMock.setAuthorities(authorities);
    
    applicationUserDtoMock = new ApplicationUserDto();
    applicationUserDtoMock.setId(1001L);
    applicationUserDtoMock.setEmail("goku@dbz.com");
    applicationUserDtoMock.setUsername("goku");
    applicationUserDtoMock.setPassword("goku");
    applicationUserDtoMock.setFirstName("Goku");
    applicationUserDtoMock.setLastName("Son");
    applicationUserDtoMock.setAccountNonExpired(true);
    applicationUserDtoMock.setAccountNonLocked(true);
    applicationUserDtoMock.setCredentialsNonExpired(true);
    applicationUserDtoMock.setEnabled(true);
    applicationUserDtoMock.setLastLogin(new Date());
    Set<ApplicationRoleDto> authoritiesDto = new HashSet<>();
    ApplicationRoleDto applicationRoleDto = new ApplicationRoleDto();
    applicationRoleDto.setId(10L);
    applicationRoleDto.setName("ADMIN_ROLE");
    authoritiesDto.add(applicationRoleDto);
    applicationUserDtoMock.setAuthorities(authoritiesDto);

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
    
    applicationUsersMockList = new LinkedList<ApplicationUser>();
    applicationUsersMockList.add(applicationUserMock);
    applicationUsersMockList.add(applicationUserMock2);
    applicationUsersMockList.add(applicationUserMock3);
  }
}

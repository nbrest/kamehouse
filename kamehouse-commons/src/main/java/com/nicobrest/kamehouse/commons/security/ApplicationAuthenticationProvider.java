package com.nicobrest.kamehouse.commons.security;

import com.nicobrest.kamehouse.commons.model.ApplicationUser;
import com.nicobrest.kamehouse.commons.service.ApplicationUserAuthenticationService;
import com.nicobrest.kamehouse.commons.utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Custom AuthenticationProvider for the application that uses the
 * applicationUserAuthenticationService to get the application users from the repository.
 * 
 * @author nbrest
 *
 */
@Component
public class ApplicationAuthenticationProvider implements AuthenticationProvider {

  @Autowired
  private ApplicationUserAuthenticationService applicationUserAuthenticationService;

  public void setApplicationUserAuthenticationService(ApplicationUserAuthenticationService
                                                          applicationUserAuthenticationService) {
    this.applicationUserAuthenticationService = applicationUserAuthenticationService;
  }

  public ApplicationUserAuthenticationService getApplicationUserAuthenticationService() {
    return applicationUserAuthenticationService;
  }

  @Override
  public Authentication authenticate(Authentication authentication) {
    String username = authentication.getName();
    String password = (String) authentication.getCredentials();

    ApplicationUser user = applicationUserAuthenticationService.loadUserByUsername(username);

    if (user == null || !user.getUsername().equalsIgnoreCase(username)) {
      throw new BadCredentialsException("Username not found.");
    }
    if (password == null || !PasswordUtils.isValidPassword(password, user.getPassword())) {
      throw new BadCredentialsException("Wrong password.");
    }
    Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
    return new UsernamePasswordAuthenticationToken(user, password, authorities);
  }

  @Override
  public boolean supports(Class<?> arg0) {
    return true;
  }
}

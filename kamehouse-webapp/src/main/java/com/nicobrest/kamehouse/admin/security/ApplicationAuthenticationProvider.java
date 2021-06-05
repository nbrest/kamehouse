package com.nicobrest.kamehouse.admin.security;

import com.nicobrest.kamehouse.admin.model.ApplicationUser;
import com.nicobrest.kamehouse.admin.service.ApplicationUserService;

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
 * applicationUserService to get the application users from the repository.
 * 
 * @author nbrest
 *
 */
@Component
public class ApplicationAuthenticationProvider implements AuthenticationProvider {

  @Autowired
  private ApplicationUserService applicationUserService;

  public void setApplicationUserService(ApplicationUserService applicationUserService) {
    this.applicationUserService = applicationUserService;
  }

  public ApplicationUserService getApplicationUserService() {
    return applicationUserService;
  }

  @Override
  public Authentication authenticate(Authentication authentication) {
    String username = authentication.getName();
    String password = (String) authentication.getCredentials();

    ApplicationUser user = applicationUserService.loadUserByUsername(username);

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

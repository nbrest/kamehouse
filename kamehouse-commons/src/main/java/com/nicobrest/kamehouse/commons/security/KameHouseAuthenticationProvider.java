package com.nicobrest.kamehouse.commons.security;

import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import com.nicobrest.kamehouse.commons.service.KameHouseUserAuthenticationService;
import com.nicobrest.kamehouse.commons.utils.PasswordUtils;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * Custom AuthenticationProvider for the application that uses kameHouseUserAuthenticationService to
 * get the kamehouse users from the repository.
 *
 * @author nbrest
 */
@Component
public class KameHouseAuthenticationProvider implements AuthenticationProvider {

  @Autowired
  private KameHouseUserAuthenticationService kameHouseUserAuthenticationService;

  @Override
  public Authentication authenticate(Authentication authentication) {
    String username = authentication.getName();
    String password = (String) authentication.getCredentials();

    KameHouseUser user = kameHouseUserAuthenticationService.loadUserByUsername(username);

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

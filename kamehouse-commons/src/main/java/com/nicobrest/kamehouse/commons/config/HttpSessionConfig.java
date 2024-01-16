package com.nicobrest.kamehouse.commons.config;

import com.nicobrest.kamehouse.commons.model.KameHouseRole;
import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import java.util.HashSet;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * Class to enable jdbc http session to store sessions in the database. This allows me to share the
 * sessions between the different modules/services of kamehouse.
 *
 * <p>I need to manually create the spring session schema in kamehouse mariadb from:
 * <a
 * href="https://github.com/spring-projects/spring-session/blob/main/spring-session-jdbc/src/main/resources/org/springframework/session/jdbc/schema-mysql.sql">spring
 * session</a>
 * or the one I have defined in spring-session.sql
 *
 * <p>maxInactiveIntervalInSeconds = 2629746 : 1 month in seconds
 */
@Configuration
@EnableJdbcHttpSession(maxInactiveIntervalInSeconds = 2629746)
public class HttpSessionConfig extends AbstractHttpSessionApplicationInitializer {

  /**
   * Replaces the default name for the session cookie from SESSION to KAMEHOUSE-SESSION-ID.
   */
  @Bean
  public CookieSerializer cookieSerializer() {
    DefaultCookieSerializer serializer = new DefaultCookieSerializer();
    serializer.setCookieName("KAMEHOUSE-SESSION-ID");
    serializer.setCookiePath("/");
    serializer.setDomainNamePattern("^.+?\\.(\\w+\\.[a-z]+)$");
    return serializer;
  }

  /**
   * Default user to return when the user is not logged in.
   */
  @Bean
  public KameHouseUser anonymousUser() {
    KameHouseUser anonymousUser = new KameHouseUser();
    anonymousUser.setUsername("anonymousUser");
    KameHouseRole kameHouseRole = new KameHouseRole();
    kameHouseRole.setName("ROLE_ANONYMOUS");
    Set<KameHouseRole> kameHouseRoles = new HashSet<>();
    kameHouseRoles.add(kameHouseRole);
    anonymousUser.setAuthorities(kameHouseRoles);
    return anonymousUser;
  }
}

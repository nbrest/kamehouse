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
 * or the one I have defined in /scripts/sql/mariadb/spring-session.sql
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

  /*
   * By default spring session sets a Cookie SESSION with the JSESSIONID and uses that to identify
   * the session. Even though in the database I don't see that as the value stored as session id.
   *
   * <p>Defining a httpSessionIdResolver I can change the default behaviour to use any header as the
   * session identifier. The problem with using a custom header is that when I do a GET to any page
   * in the frontend from the browser or refresh a page, it doesn't send that header, so it doesn't
   * detect the existing session. I think the only way to do that is to keep using a cookie, that is
   * always being sent by the browser when it's set.
   */
  /*
  @Bean
  public HttpSessionIdResolver httpSessionIdResolver() {
    return HeaderHttpSessionIdResolver.xAuthToken();
  }
  */
}

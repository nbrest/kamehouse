package com.nicobrest.kamehouse.commons.config;

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
 * I need to manually create the spring session schema in kamehouse mysql db from:
 * https://github.com/spring-projects/spring-session/blob/main/spring-session-jdbc/src/main/resources/org/springframework/session/jdbc/schema-mysql.sql
 * or the one I have defined in /scripts/sql/mysql/spring-session.sql
 *
 * maxInactiveIntervalInSeconds = 2629746 : 1 month in seconds
 * cleanupCron = "0 0 2 1 1 *" : run once a year in january 1st 2AM (to disable session expiry)
 */
@Configuration
@EnableJdbcHttpSession( maxInactiveIntervalInSeconds = 2629746, cleanupCron = "0 0 2 1 1 *" )
public class HttpSessionConfig extends AbstractHttpSessionApplicationInitializer {

  /**
   * Replaces the default name for the session cookie from SESSION to KAMEHOUSE-SESSION-ID.
   */
  @Bean
  public CookieSerializer cookieSerializer() {
    DefaultCookieSerializer serializer = new DefaultCookieSerializer();
    serializer.setCookieName("KAMEHOUSE-SESSION-ID");
    serializer.setCookiePath("/kame-house");
    serializer.setDomainNamePattern("^.+?\\.(\\w+\\.[a-z]+)$");
    return serializer;
  }

  /**
     By default spring session sets a Cookie SESSION with the JSESSIONID and uses that to identify
     the session. Even though in the database I don't see that as the value stored as session id.

     Defining a httpSessionIdResolver I can change the default behaviour to use any header as the
     session identifier. The problem with using a custom header is that when I do a GET to any page
     in the frontend from the browser or refresh a page, it doesn't send that header, so it doesn't
     detect the existing session. I think the only way to do that is to keep using a cookie, that is
     always being sent by the browser when it's set.
   */
  /*
  @Bean
  public HttpSessionIdResolver httpSessionIdResolver() {
    return HeaderHttpSessionIdResolver.xAuthToken();
  }
  */
}
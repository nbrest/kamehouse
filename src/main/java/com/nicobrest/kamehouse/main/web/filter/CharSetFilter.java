package com.nicobrest.kamehouse.main.web.filter;

import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Filter to handle requests and responses with UTF-8 charset.
 */
public class CharSetFilter implements Filter {
  private static final String CONTENT_TYPE = ContentType.TEXT_HTML.toString() + "; charset="
      + StandardCharsets.UTF_8.name();

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // No initialization is required to execute this filter.
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain next)
      throws IOException, ServletException {
    request.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setContentType(CONTENT_TYPE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    next.doFilter(request, response);
  }

  @Override
  public void destroy() {
    // No resources need to be cleared after the execution of the filter.
  }
}

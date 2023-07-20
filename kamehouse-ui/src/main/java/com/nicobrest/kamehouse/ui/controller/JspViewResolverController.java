package com.nicobrest.kamehouse.ui.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller to resolve jsp views.
 *
 * @author nbrest
 */
@Controller
public class JspViewResolverController extends AbstractController {

  /** View resolver for the jsp app pages. */
  @GetMapping(path = "/jsp/**")
  public String jsp(HttpServletRequest request, HttpServletResponse response) {
    logger.debug("/jsp/** (GET) with path: {}", request.getServletPath());
    if (request.getServletPath().endsWith("/")) {
      return request.getServletPath() + "index";
    } else {
      return request.getServletPath();
    }
  }
}

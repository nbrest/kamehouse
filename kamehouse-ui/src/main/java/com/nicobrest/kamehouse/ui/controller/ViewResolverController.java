package com.nicobrest.kamehouse.ui.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller to resolve views in the application.
 *
 * @author nbrest
 */
@Controller
public class ViewResolverController extends AbstractController {

  /** View resolver for the jsp app pages. */
  @GetMapping(path = "/jsp/**")
  public String testModuleJsp(HttpServletRequest request, HttpServletResponse response) {
    logger.debug("/jsp/** (GET) with path: {}", request.getServletPath());
    if (request.getServletPath().endsWith("/")) {
      return request.getServletPath() + "index";
    } else {
      return request.getServletPath();
    }
  }
}

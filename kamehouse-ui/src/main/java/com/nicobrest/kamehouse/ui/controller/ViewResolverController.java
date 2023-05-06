package com.nicobrest.kamehouse.ui.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller to resolve views in the application.
 *
 * @author nbrest
 */
@Controller
public class ViewResolverController extends AbstractController {

  /** View resolver for the logout page. */
  @GetMapping(path = "/logout")
  public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      new SecurityContextLogoutHandler().logout(request, response, authentication);
    }
    return "redirect:/login.html?logout";
  }

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

package com.nicobrest.kamehouse.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller to resolve views for all the jsps in the application.
 * 
 * @author nbrest
 *
 */
@Controller
public class ViewResolverController {

  private static final Logger logger = LoggerFactory.getLogger(ViewResolverController.class);

  /**
   * View resolver for the home page.
   */
  @RequestMapping(value = { "/", "/welcome**" }, method = RequestMethod.GET)
  public ModelAndView homePage() {
    logger.trace("In controller /, /welcome** (GET)");
    ModelAndView model = new ModelAndView();
    model.setViewName("/index");
    return model;
  }

  /**
   * View resolver for the about page.
   */
  @RequestMapping(value = "/about", method = RequestMethod.GET)
  public String aboutPage() {
    logger.trace("In controller /about (GET)");
    return "/about";
  }

  /**
   * View resolver for the admin page.
   */
  @RequestMapping(value = "/admin/**", method = RequestMethod.GET)
  public String adminPage(HttpServletRequest request, HttpServletResponse response) {
    logger.trace("In controller /admin/** (GET) with path: " + request.getServletPath());
    if (request.getServletPath().equals("/admin/")) {
      return "/admin/index";
    } else {
      return request.getServletPath();
    }
  }

  /**
   * View resolver for the angular page.
   */
  @RequestMapping(value = "/app/**", method = RequestMethod.GET)
  public String appPage(HttpServletRequest request, HttpServletResponse response) {
    logger.trace("In controller /app/** (GET) with path: " + request.getServletPath());
    if (request.getServletPath().equals("/app/")) {
      return "/app/index";
    } else {
      return request.getServletPath();
    }
  }

  /**
   * View resolver for the contact us page.
   */
  @RequestMapping(value = "/contact-us", method = RequestMethod.GET)
  public String contactUsPage() {
    logger.trace("In controller /contact-us (GET)");
    return "/contact-us";
  }

  /**
   * View resolver for the jsp app page.
   */
  @RequestMapping(value = "/jsp/**", method = RequestMethod.GET)
  public String jspPage(HttpServletRequest request, HttpServletResponse response) {
    logger.trace("In controller /jsp/** (GET) with path: " + request.getServletPath());
    if (request.getServletPath().equals("/jsp/")) {
      return "/jsp/index";
    } else {
      return request.getServletPath();
    }
  }

  /**
   * View resolver for the login page.
   */
  @RequestMapping(value = "/login", method = RequestMethod.GET)
  public String loginPage() {
    logger.trace("In controller /login (GET)");
    return "/login";
  }

  /**
   * View resolver for the logout page.
   */
  @RequestMapping(value = "/logout", method = RequestMethod.GET)
  public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
    logger.trace("In controller /logout (GET)");
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      new SecurityContextLogoutHandler().logout(request, response, authentication);
    }
    return "redirect:/login?logout";
    // return "forward:/login?logout"; //forwards the request without
    // redirecting
  }
}

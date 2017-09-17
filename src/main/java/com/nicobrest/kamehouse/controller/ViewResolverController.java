package com.nicobrest.kamehouse.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
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
    logger.debug("In controller /, /welcome** (GET)");
    ModelAndView model = new ModelAndView();
    model.setViewName("/index");
    return model;
  }

  /**
   * View resolver for the about page.
   */
  @RequestMapping(value = "/about", method = RequestMethod.GET)
  public String aboutPage() {
    logger.debug("In controller /about (GET)");
    return "/about";
  }

  /**
   * View resolver for the admin page.
   */
  @RequestMapping(value = "/admin/", method = RequestMethod.GET)
  public String adminPage() {
    logger.debug("In controller /admin/ (GET)");
    return "/admin/index";
  }

  /**
   * View resolver for the admin pages.
   */
  @RequestMapping(value = "/admin/{adminFirstLevelPage}", method = RequestMethod.GET)
  public String adminFirstLevelSubpages(@PathVariable String adminFirstLevelPage) {
    logger.debug("In controller /admin/" + adminFirstLevelPage + " (GET)");
    return "/admin/" + adminFirstLevelPage;
  }

  /**
   * View resolver for the angular page.
   */
  @RequestMapping(value = "/app/", method = RequestMethod.GET)
  public String appPage() {
    logger.debug("In controller /app/ (GET)");
    return "/app/index";
  }

  /**
   * View resolver for the contact us page.
   */
  @RequestMapping(value = "/contact-us", method = RequestMethod.GET)
  public String contactUsPage() {
    logger.debug("In controller /contact-us (GET)");
    return "/contact-us";
  }

  /**
   * View resolver for the jsp app page.
   */
  @RequestMapping(value = "/jsp/", method = RequestMethod.GET)
  public String jspPage() {
    logger.debug("In controller /jsp/ (GET)");
    return "/jsp/index";
  }

  /**
   * View resolver for the jsp app pages.
   */
  // TODO: See if I can use just one method with regular expresions containing
  // all levels
  @RequestMapping(value = "/jsp/{jspFirstLevelPage}", method = RequestMethod.GET)
  public String jspFirstLevelSubpages(@PathVariable String jspFirstLevelPage) {
    logger.debug("In controller /jsp/" + jspFirstLevelPage + " (GET)");
    return "/jsp/" + jspFirstLevelPage;
  }

  /**
   * View resolver for the jsp app pages.
   */
  @RequestMapping(value = "/jsp/{jspFirstLevelPage}/{jspSecondLevelPage}", method = {
      RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST, RequestMethod.DELETE })
  public String jspSecondLevelSubpages(@PathVariable String jspFirstLevelPage,
      @PathVariable String jspSecondLevelPage) {
    logger.debug("In controller /jsp/" + jspFirstLevelPage + "/" + jspSecondLevelPage);
    return "/jsp/" + jspFirstLevelPage + "/" + jspSecondLevelPage;
  }

  /**
   * View resolver for the jsp app pages.
   */
  @RequestMapping(value = "/jsp/{jspFirstLevelPage}/{jspSecondLevelPage}/{jspThirdLevelPage}",
      method = { RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST, RequestMethod.DELETE })
  public String jspThirdLevelSubpages(@PathVariable String jspFirstLevelPage,
      @PathVariable String jspSecondLevelPage, @PathVariable String jspThirdLevelPage) {
    logger.debug("In controller /jsp/" + jspFirstLevelPage + "/" + jspSecondLevelPage + "/"
        + jspThirdLevelPage);
    return "/jsp/" + jspFirstLevelPage + "/" + jspSecondLevelPage + "/" + jspThirdLevelPage;
  }

  /**
   * View resolver for the login page.
   */
  @RequestMapping(value = "/login", method = RequestMethod.GET)
  public String loginPage() {
    logger.debug("In controller /login (GET)");
    return "login";
  }

  /**
   * View resolver for the logout page.
   */
  @RequestMapping(value = "/logout", method = RequestMethod.GET)
  public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
    logger.debug("In controller /logout (GET)");
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      new SecurityContextLogoutHandler().logout(request, response, authentication);
    }
    return "redirect:/login?logout";
    // return "forward:/login?logout"; //forwards the request without
    // redirecting
  }
}

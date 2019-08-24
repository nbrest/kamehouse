package com.nicobrest.kamehouse.main.controller;

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
 * Controller to resolve views for all the static htmls and jsps in the
 * application.
 * 
 * @author nbrest
 *
 */
@Controller
public class ViewResolverController {

  private static final Logger logger = LoggerFactory.getLogger(ViewResolverController.class);

  /**
   * View resolver for static html files.
   */
  @RequestMapping(value = { "/", "/about", "/admin", "/admin/**", "/contact-us", "/test-module",
      "/test-module/", "/test-module/angular-1", "/test-module/angular-1/**",
      "/test-module/websocket", "/test-module/websocket/**", "/vlc-player" },
      method = RequestMethod.GET)
  public ModelAndView includeStaticHtml(HttpServletRequest request, HttpServletResponse response) {

    String originalRequestUrl = (String) request.getServletPath();
    String staticHtmlToLoad = "/static" + originalRequestUrl;

    // Certain urls are meant to be the root folder in a tree structure, for
    // those, I need to make sure I call them with the trailing /. If I don't, I
    // need to add that trailing / here so it maps to an index page.
    if (staticHtmlToLoad.endsWith("admin") || staticHtmlToLoad.endsWith("test-module")
        || staticHtmlToLoad.endsWith("test-module/angular-1") || staticHtmlToLoad.endsWith(
            "test-module/websocket")) {
      staticHtmlToLoad = staticHtmlToLoad.concat("/");
    }
    // If the file ends with / I'm in the root of a folder, append index to the
    // filename
    if (staticHtmlToLoad.endsWith("/")) {
      staticHtmlToLoad = staticHtmlToLoad + "index";
    }
    // Always append .html extension
    staticHtmlToLoad = staticHtmlToLoad + ".html";

    logger.trace("In controller import-static (GET) with request '" + request.getServletPath()
        + "' Loading static html: '" + staticHtmlToLoad + "'");

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("/include-static-html");
    modelAndView.addObject("staticHtmlToLoad", staticHtmlToLoad); 
    return modelAndView;
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

  /**
   * View resolver for the test module jsp app page.
   */
  @RequestMapping(value = "/test-module/jsp/**", method = RequestMethod.GET)
  public String testModuleJsp(HttpServletRequest request, HttpServletResponse response) {
    logger.trace("In controller /test-module/jsp/** (GET) with path: " + request.getServletPath());
    if (request.getServletPath().equals("/test-module/jsp/")) {
      return "/test-module/jsp/index";
    } else {
      return request.getServletPath();
    }
  }
}

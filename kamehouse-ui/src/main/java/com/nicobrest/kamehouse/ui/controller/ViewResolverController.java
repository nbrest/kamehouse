package com.nicobrest.kamehouse.ui.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractController;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Controller to resolve views for all the static htmls and jsps in the
 * application.
 * 
 * @author nbrest
 *
 */
@Controller
public class ViewResolverController extends AbstractController {

  private static final String ERROR_404_PAGE = "/error/404.html";

  /**
   * View resolver for static html files. Loads the content of the html and returns it to the view
   * so that include-static-html.jsp can render it.
   */
  @GetMapping(path = { "/", "/*", "/index.html",
      "/about",
      "/admin", "/admin/**",
      "/contact-us",
      "/login",
      "/tennisworld", "/tennisworld/**",
      "/test-module", "/test-module/", "/test-module/index.html",
      "/test-module/angular-1", "/test-module/angular-1/**",
      "/test-module/scheduler", "/test-module/scheduler/**",
      "/test-module/websocket", "/test-module/websocket/**",
      "/vlc-player" })
  public ModelAndView includeStaticHtml(HttpServletRequest request, HttpServletResponse response) {
    String originalRequestUrl = request.getServletPath();
    if (!originalRequestUrl.startsWith("/")) {
      originalRequestUrl = "/" + originalRequestUrl;
    }
    String staticHtmlToLoad = originalRequestUrl;

    // Certain urls are meant to be the root folder in a tree structure, for
    // those, I need to make sure I call them with the trailing /. If I don't, I
    // need to add that trailing / here so it maps to an index page.
    if (staticHtmlToLoad.endsWith("admin")
        || staticHtmlToLoad.endsWith("tennisworld")
        || staticHtmlToLoad.endsWith("test-module")
        || staticHtmlToLoad.endsWith("test-module/angular-1")
        || staticHtmlToLoad.endsWith("test-module/scheduler")
        || staticHtmlToLoad.endsWith("test-module/websocket")) {
      staticHtmlToLoad = staticHtmlToLoad.concat("/");
    }
    // If the file ends with / I'm in the root of a folder, append index to the
    // filename
    if (staticHtmlToLoad.endsWith("/")) {
      staticHtmlToLoad = staticHtmlToLoad + "index";
    }
    // Always append .html extension
    if (!staticHtmlToLoad.endsWith(".html")) {
      staticHtmlToLoad = staticHtmlToLoad + ".html";
    }

    logger.debug("/include-static (GET) with request '{}' Loading static html: '{}'",
        request.getServletPath(), staticHtmlToLoad);

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("/include-static-html");
    modelAndView.addObject("staticHtmlToLoad", staticHtmlToLoad);
    String staticHtmlContent = loadStaticHtml(request, staticHtmlToLoad);
    if (staticHtmlContent != null) {
      modelAndView.addObject("hasError", "false");
      modelAndView.addObject("staticHtmlContent",staticHtmlContent);
    } else {
      modelAndView.addObject("hasError", "true");
      modelAndView.addObject("staticHtmlContent", loadStaticHtml(request, ERROR_404_PAGE));
    }
    return modelAndView;
  }

  /**
   * View resolver for the logout page.
   */
  @GetMapping(path = "/logout")
  public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      new SecurityContextLogoutHandler().logout(request, response, authentication);
    }
    return "redirect:/login?logout";
    // return "forward:/login?logout"; //forwards the request without redirecting
  }

  /**
   * View resolver for the test module jsp app page.
   */
  @GetMapping(path = "/test-module/jsp/**")
  public String testModuleJsp(HttpServletRequest request, HttpServletResponse response) {
    logger.debug("/test-module/jsp/** (GET) with path: {}", request.getServletPath());
    if (request.getServletPath().equals("/test-module/jsp/")) {
      return "/test-module/jsp/index";
    } else {
      return request.getServletPath();
    }
  }

  /**
   * Load the static html file content from the filesystem.
   */
  private String loadStaticHtml(HttpServletRequest request, String staticHtmlToLoad) {
    try {
      HttpSession session = request.getSession();
      if (session != null && session.getServletContext() != null) {
        ServletContext servletContext = session.getServletContext();
        String staticHtmlToLoadAbsolutePath = servletContext.getRealPath(staticHtmlToLoad);
        byte[] staticHtmlBytes = Files.readAllBytes(Paths.get(staticHtmlToLoadAbsolutePath));
        return new String(staticHtmlBytes, StandardCharsets.UTF_8);
      }
    } catch (IOException e) {
      logger.error("Error loading {} content", staticHtmlToLoad);
    }
    return null;
  }
}

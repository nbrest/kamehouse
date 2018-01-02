package com.nicobrest.kamehouse.servlet;

import com.nicobrest.kamehouse.service.DragonBallUserService;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet to process requests to delete a DragonBallUser from the repository.
 * Ideally I would use a spring controller instead of a raw Servlet but I'm
 * using Servlets for the JSPs just for practice.
 * 
 * @author nbrest
 *
 */
@WebServlet("/jsp/dragonball/users/users-delete-action")
public class DragonBallUserDeleteActionServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  private static DragonBallUserService dragonBallUserService;

  public static void setDragonBallUserService(DragonBallUserService dragonBallUserService) {
    DragonBallUserDeleteActionServlet.dragonBallUserService = dragonBallUserService;
  }

  public static DragonBallUserService getDragonBallUserService() {
    return DragonBallUserDeleteActionServlet.dragonBallUserService;
  }

  /**
   * Configures private static dragonBallUserService. @Autowired doesn't work
   * because the servlet is not managed by spring and the initialization of
   * static fields probably happens before the spring context loads. The only
   * way for @Autowired to work was to have the property non-static and use
   * SpringBeanAutowiringSupport in the init method, but findbugs reports having
   * non-static fields in a Servlet as a bug.
   */
  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(this
        .getServletContext());
    DragonBallUserService dragonBallUserService = (DragonBallUserService) context.getBean(
        "dragonBallUserService");
    setDragonBallUserService(dragonBallUserService);
  }

  /**
   * Deletes the DragonBallUser from the repository with the id taken from the
   * request parameters.
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    Long userId = Long.parseLong(request.getParameter("id"));
    getDragonBallUserService().deleteDragonBallUser(userId);
    response.sendRedirect("users-list");
  }
}

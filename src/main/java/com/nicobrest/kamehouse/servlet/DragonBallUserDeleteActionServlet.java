package com.nicobrest.kamehouse.servlet;

import com.nicobrest.kamehouse.service.DragonBallUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet to process requests to delete a DragonBallUser from the repository.
 * 
 * @author nbrest
 *
 */
@WebServlet("/jsp/dragonball/users/users-delete-action")
public class DragonBallUserDeleteActionServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  @Autowired
  private static DragonBallUserService dragonBallUserService;

  public static void setDragonBallUserService(DragonBallUserService dragonBallUserService) {

    DragonBallUserDeleteActionServlet.dragonBallUserService = dragonBallUserService;
  }

  public static DragonBallUserService getDragonBallUserService() {

    return DragonBallUserDeleteActionServlet.dragonBallUserService;
  }

  /**
   * Configures spring context.
   */
  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config
        .getServletContext());
  }

  /**
   * Deletes the DragonBallUser from the repository with the id taken from the
   * request parameters.
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    Long userId = Long.parseLong(request.getParameter("id"));
    dragonBallUserService.deleteDragonBallUser(userId);
    response.sendRedirect("users-list");
  }

}

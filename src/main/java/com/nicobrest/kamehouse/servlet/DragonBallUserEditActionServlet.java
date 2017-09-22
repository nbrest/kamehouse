package com.nicobrest.kamehouse.servlet;

import com.nicobrest.kamehouse.model.DragonBallUser;
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
 * Servlet to process requests to edit a DragonBallUser from the repository.
 * 
 * @author nbrest
 *
 */
@WebServlet("/jsp/dragonball/users/users-edit-action")
public class DragonBallUserEditActionServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  @Autowired
  private static DragonBallUserService dragonBallUserService;

  public static void setDragonBallUserService(DragonBallUserService dragonBallUserService) {

    DragonBallUserEditActionServlet.dragonBallUserService = dragonBallUserService;
  }

  public static DragonBallUserService getDragonBallUserService() {

    return DragonBallUserEditActionServlet.dragonBallUserService;
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
   * Edits a DragonBallUser from the repository with the data taken from the
   * request parameters.
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    DragonBallUser dragonBallUser = new DragonBallUser();
    dragonBallUser.setId(Long.parseLong(request.getParameter("id")));
    dragonBallUser.setUsername(request.getParameter("username"));
    dragonBallUser.setEmail(request.getParameter("email"));
    dragonBallUser.setAge(Integer.parseInt(request.getParameter("age")));
    dragonBallUser.setStamina(Integer.parseInt(request.getParameter("stamina")));
    dragonBallUser.setPowerLevel(Integer.parseInt(request.getParameter("powerLevel")));

    dragonBallUserService.updateDragonBallUser(dragonBallUser);
    response.sendRedirect("users-list");
  }

}

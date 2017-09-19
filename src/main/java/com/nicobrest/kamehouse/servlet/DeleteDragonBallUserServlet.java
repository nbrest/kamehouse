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

@WebServlet("/jsp/dragonball/users/users-delete-action")
public class DeleteDragonBallUserServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  @Autowired
  private DragonBallUserService dragonBallUserService;

  /**
   * Getters and Setters.
   *
   * @author nbrest
   */
  public void setDragonBallUserService(DragonBallUserService dragonBallUserService) {

    this.dragonBallUserService = dragonBallUserService;
  }

  /**
   * Getters and Setters.
   *
   * @author nbrest
   */
  public DragonBallUserService getDragonBallUserService() {

    return this.dragonBallUserService;
  }

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config
        .getServletContext());
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    Long userId = Long.parseLong(request.getParameter("id"));
    dragonBallUserService.deleteDragonBallUser(userId);
    response.sendRedirect("users-list");
  }

}

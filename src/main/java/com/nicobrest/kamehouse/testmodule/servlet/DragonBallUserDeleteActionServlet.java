package com.nicobrest.kamehouse.testmodule.servlet;

import com.nicobrest.kamehouse.testmodule.service.dto.DragonBallUserDto;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
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
@WebServlet("/test-module/jsp/dragonball/users/users-delete-action")
public class DragonBallUserDeleteActionServlet extends AbstractDragonBallUserServlet {

  protected static final long serialVersionUID = 1L;
  
  /**
   * Deletes the DragonBallUser from the repository with the id taken from the
   * request parameters.
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    try {
      Long userId = Long.parseLong(request.getParameter("id"));
      getDragonBallUserService().delete(userId);
      response.sendRedirect("users-list");
    } catch (NumberFormatException e) {
      logger.error("Error occurred processing request.", e);
    }
  }

  @Override
  void consumeDragonBallUserDto(DragonBallUserDto dragonBallUserDto) {
    // Method not needed in this class.
  }
}

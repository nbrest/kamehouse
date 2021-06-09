package com.nicobrest.kamehouse.testmodule.servlet;

import com.nicobrest.kamehouse.testmodule.model.dto.DragonBallUserDto;

import javax.servlet.annotation.WebServlet;

/**
 * Servlet to process requests to add a DragonBallUser from the repository.
 * Ideally I would use a spring controller instead of a raw Servlet but I'm
 * using Servlets for the JSPs just for practice.
 * 
 * @author nbrest
 *
 */
@WebServlet("/api/v1/servlet/test-module/dragonball/users/users-add-action")
public class DragonBallUserAddActionServlet extends AbstractDragonBallUserServlet {

  private static final long serialVersionUID = 1L;

  @Override
  void processDto(DragonBallUserDto dragonBallUserDto) {
    getDragonBallUserService().create(dragonBallUserDto);
  }
}

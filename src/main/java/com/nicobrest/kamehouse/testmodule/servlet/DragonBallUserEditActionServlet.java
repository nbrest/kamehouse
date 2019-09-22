package com.nicobrest.kamehouse.testmodule.servlet;

import com.nicobrest.kamehouse.testmodule.service.dto.DragonBallUserDto;

import javax.servlet.annotation.WebServlet;

/**
 * Servlet to process requests to edit a DragonBallUser from the repository.
 * Ideally I would use a spring controller instead of a raw Servlet but I'm
 * using Servlets for the JSPs just for practice.
 * 
 * @author nbrest
 *
 */
@WebServlet("/test-module/jsp/dragonball/users/users-edit-action")
public class DragonBallUserEditActionServlet extends AbstractDragonBallUserServlet {

  private static final long serialVersionUID = 1L;

  @Override
  void consumeDragonBallUserDto(DragonBallUserDto dragonBallUserDto) {
    getDragonBallUserService().update(dragonBallUserDto);
  }
}

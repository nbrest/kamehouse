package com.nicobrest.kamehouse.testmodule.servlet;

import com.nicobrest.kamehouse.testmodule.service.DragonBallUserService;
import com.nicobrest.kamehouse.testmodule.service.dto.DragonBallUserDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * Servlet to process requests to add a DragonBallUser from the repository.
 * Ideally I would use a spring controller instead of a raw Servlet but I'm
 * using Servlets for the JSPs just for practice.
 * 
 * @author nbrest
 *
 */
@WebServlet("/test-module/jsp/dragonball/users/users-add-action")
public class DragonBallUserAddActionServlet extends HttpServlet {

  private static final Logger logger = LoggerFactory
      .getLogger(DragonBallUserAddActionServlet.class);
  private static final long serialVersionUID = 1L;

  private static DragonBallUserService dragonBallUserService;

  public static void setDragonBallUserService(DragonBallUserService dragonBallUserService) {
    DragonBallUserAddActionServlet.dragonBallUserService = dragonBallUserService;
  }

  public static DragonBallUserService getDragonBallUserService() {
    return DragonBallUserAddActionServlet.dragonBallUserService;
  }

  /**
   * Configures private static dragonBallUserService. @Autowired doesn't work
   * because the servlet is not managed by spring and the initialization of static
   * fields probably happens before the spring context loads. The only way
   * for @Autowired to work was to have the property non-static and use
   * SpringBeanAutowiringSupport in the init method, but findbugs reports having
   * non-static fields in a Servlet as a bug.
   */
  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    ApplicationContext context = WebApplicationContextUtils
        .getRequiredWebApplicationContext(this.getServletContext());
    DragonBallUserService dragonBallUserService = (DragonBallUserService) context
        .getBean("dragonBallUserService");
    setDragonBallUserService(dragonBallUserService);
  }

  /**
   * Adds a DragonBallUser to the repository with the data taken from the request
   * parameters.
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    try {
      DragonBallUserDto dragonBallUserDto = new DragonBallUserDto();
      dragonBallUserDto.setUsername(request.getParameter("username"));
      dragonBallUserDto.setEmail(request.getParameter("email"));
      dragonBallUserDto.setAge(Integer.parseInt(request.getParameter("age")));
      dragonBallUserDto.setStamina(Integer.parseInt(request.getParameter("stamina")));
      dragonBallUserDto.setPowerLevel(Integer.parseInt(request.getParameter("powerLevel")));

      getDragonBallUserService().createDragonBallUser(dragonBallUserDto);
      response.sendRedirect("users-list");
    } catch (NumberFormatException e) {
      logger.error("Error occurred processing request.", e);
    }  
  }
}

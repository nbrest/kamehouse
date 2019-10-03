package com.nicobrest.kamehouse.testmodule.servlet;

import com.nicobrest.kamehouse.testmodule.model.dto.DragonBallUserDto;
import com.nicobrest.kamehouse.testmodule.service.DragonBallUserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Abstract class to group common DragonBallUser servlets functionality.
 * 
 * @author nbrest
 *
 */
public abstract class AbstractDragonBallUserServlet extends HttpServlet {

  protected final Logger logger = LoggerFactory.getLogger(getClass());
  protected static final long serialVersionUID = 1L;

  private static DragonBallUserService dragonBallUserService;

  protected static void setDragonBallUserService(DragonBallUserService dragonBallUserServiceBean) {
    dragonBallUserService = dragonBallUserServiceBean;
  }

  protected static DragonBallUserService getDragonBallUserService() {
    return dragonBallUserService;
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
    ApplicationContext context =
        WebApplicationContextUtils.getRequiredWebApplicationContext(this.getServletContext());
    DragonBallUserService dragonBallUserServiceBean =
        (DragonBallUserService) context.getBean("dragonBallUserService");
    setDragonBallUserService(dragonBallUserServiceBean);
  }

  /**
   * Default implementation of doPost that processes the DragonBallUserDto from
   * the request. The method that actually consumes the DTO
   * (consumeDragonBallUserDto) is implemented in the concrete classes.
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    try {
      DragonBallUserDto dragonBallUserDto = getDtoFromRequest(request);
      processDto(dragonBallUserDto);
      response.sendRedirect("users-list");
    } catch (NumberFormatException | IOException e) {
      logger.error("Error occurred processing request.", e);
    }
  }

  /**
   * Gets the DTO object from the request parameters.
   */
  private DragonBallUserDto getDtoFromRequest(HttpServletRequest request) {
    DragonBallUserDto dragonBallUserDto = new DragonBallUserDto();
    if (request.getParameter("id") != null) {
      dragonBallUserDto.setId(Long.parseLong(request.getParameter("id")));
    }
    dragonBallUserDto.setUsername(request.getParameter("username"));
    dragonBallUserDto.setEmail(request.getParameter("email"));
    dragonBallUserDto.setAge(Integer.parseInt(request.getParameter("age")));
    dragonBallUserDto.setStamina(Integer.parseInt(request.getParameter("stamina")));
    dragonBallUserDto.setPowerLevel(Integer.parseInt(request.getParameter("powerLevel")));
    return dragonBallUserDto;
  }

  /**
   * Processes the DTO from the request.
   */
  abstract void processDto(DragonBallUserDto dragonBallUserDto);
}

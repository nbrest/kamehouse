package com.nicobrest.kamehouse.testmodule.servlet;

import com.nicobrest.kamehouse.commons.exception.KameHouseException;
import com.nicobrest.kamehouse.commons.servlet.AbstractKameHouseServlet;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;
import com.nicobrest.kamehouse.testmodule.model.dto.DragonBallUserDto;
import com.nicobrest.kamehouse.testmodule.service.DragonBallUserService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * DragonBallUser servlets functionality.
 *
 * @author nbrest
 */
@WebServlet("/api/v1/servlet/test-module/dragonball/users")
public class DragonBallUserServlet extends AbstractKameHouseServlet {

  private static DragonBallUserService dragonBallUserService;
  private static final String ROLE_SAIYAJIN = "ROLE_SAIYAJIN";

  protected static void setDragonBallUserService(DragonBallUserService dragonBallUserServiceBean) {
    dragonBallUserService = dragonBallUserServiceBean;
  }

  protected static DragonBallUserService getDragonBallUserService() {
    return dragonBallUserService;
  }

  /**
   * Configures private static dragonBallUserService. @Autowired doesn't work because the servlet is
   * not managed by spring and the initialization of static fields probably happens before the
   * spring context loads. The only way for @Autowired to work was to have the property non-static
   * and use SpringBeanAutowiringSupport in the init method, but findbugs reports having non-static
   * fields in a Servlet as a bug.
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
   * Get all the dragonball users. Or get a single dragonball user if the username parameter is
   * set.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) {
    try {
      String username = StringUtils.sanitizeInput(request.getParameter("username"));
      if (!StringUtils.isEmpty(username)) {
        DragonBallUser dragonBallUser = getDragonBallUserService().getByUsername(username);
        setResponseBody(response, JsonUtils.toJsonString(dragonBallUser));
      } else {
        List<DragonBallUser> dragonBallUsers = getDragonBallUserService().readAll();
        setResponseBody(response, JsonUtils.toJsonString(dragonBallUsers));
      }
    } catch (KameHouseException e) {
      handleKameHouseException(response, e);
    }
  }

  /**
   * Create a new dragonball user.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) {
    try {
      authorize(request, List.of(ROLE_SAIYAJIN));
      DragonBallUserDto dragonBallUserDto = getDtoFromRequest(request);
      Long createdId = getDragonBallUserService().create(dragonBallUserDto);
      setResponseBody(response, JsonUtils.toJsonString(createdId));
    } catch (KameHouseException e) {
      handleKameHouseException(response, e);
    }
  }

  /**
   * Update a dragonball user.
   */
  @Override
  public void doPut(HttpServletRequest request, HttpServletResponse response) {
    try {
      authorize(request, List.of(ROLE_SAIYAJIN));
      DragonBallUserDto dragonBallUserDto = getDtoFromRequest(request);
      getDragonBallUserService().update(dragonBallUserDto);
    } catch (KameHouseException e) {
      handleKameHouseException(response, e);
    }
  }

  /**
   * Delete a dragonball user.
   */
  @Override
  public void doDelete(HttpServletRequest request, HttpServletResponse response) {
    try {
      authorize(request, List.of(ROLE_SAIYAJIN));
      Long userId = getLongUrlDecodedParam(request, "id");
      DragonBallUser deletedUser = getDragonBallUserService().delete(userId);
      setResponseBody(response, JsonUtils.toJsonString(deletedUser));
    } catch (KameHouseException e) {
      handleKameHouseException(response, e);
    }
  }

  /**
   * Gets the DTO object from the request parameters.
   */
  private DragonBallUserDto getDtoFromRequest(HttpServletRequest request) {
    DragonBallUserDto dragonBallUserDto = new DragonBallUserDto();
    if (request.getParameter("id") != null) {
      dragonBallUserDto.setId(getLongUrlDecodedParam(request, "id"));
    }
    dragonBallUserDto.setUsername(getUrlDecodedParam(request, "username"));
    dragonBallUserDto.setEmail(getUrlDecodedParam(request, "email"));
    dragonBallUserDto.setAge(Integer.parseInt(getUrlDecodedParam(request, "age")));
    dragonBallUserDto.setStamina(Integer.parseInt(getUrlDecodedParam(request, "stamina")));
    dragonBallUserDto.setPowerLevel(Integer.parseInt(getUrlDecodedParam(request, "powerLevel")));
    return dragonBallUserDto;
  }
}

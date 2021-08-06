package com.nicobrest.kamehouse.testmodule.servlet;

import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;
import com.nicobrest.kamehouse.testmodule.model.dto.DragonBallUserDto;
import com.nicobrest.kamehouse.testmodule.service.DragonBallUserService;

import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Abstract class to group common DragonBallUser servlets functionality.
 * 
 * @author nbrest
 *
 */
@WebServlet("/api/v1/servlet/test-module/dragonball/users")
public class DragonBallUserServlet extends HttpServlet {

  private static final Logger logger = LoggerFactory.getLogger(DragonBallUserServlet.class);
  private static final long serialVersionUID = 1L;

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
   * Get all the dragonball users. Or get a single dragonball user if the username parameter is set.
   */
  @Override
  public void doGet(HttpServletRequest request,
                    HttpServletResponse response) throws ServletException {
    try {
      String username = request.getParameter("username");
      if (!StringUtils.isEmpty(username)) {
        DragonBallUser dragonBallUser = getDragonBallUserService().getByUsername(username);
        setResponseBody(response, JsonUtils.toJsonString(dragonBallUser));
      } else {
        List<DragonBallUser> dragonBallUsers = getDragonBallUserService().readAll();
        setResponseBody(response, JsonUtils.toJsonString(dragonBallUsers));
      }
    } catch (IOException e) {
      throw new ServletException(e);
    }
  }

  /**
   * Create a new dragonball user.
   */
  @Override
  public void doPost(HttpServletRequest request,
                     HttpServletResponse response) throws ServletException {
    try {
      DragonBallUserDto dragonBallUserDto = getDtoFromRequest(request);
      Long createdId = getDragonBallUserService().create(dragonBallUserDto);
      setResponseBody(response, JsonUtils.toJsonString(createdId));
    } catch (NumberFormatException | IOException e) {
      logger.error("Error occurred processing request.", e);
      throw new ServletException(e);
    }
  }

  /**
   * Update a dragonball user.
   */
  @Override
  public void doPut(HttpServletRequest request,
                    HttpServletResponse response) throws ServletException {
    DragonBallUserDto dragonBallUserDto = getDtoFromRequest(request);
    getDragonBallUserService().update(dragonBallUserDto);
  }

  /**
   * Delete a dragonball user.
   */
  @Override
  public void doDelete(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException {
    try {
      Long userId = Long.parseLong(getUrlDecodedParam(request,"id"));
      DragonBallUser deletedUser = getDragonBallUserService().delete(userId);
      setResponseBody(response, JsonUtils.toJsonString(deletedUser));
    } catch (NumberFormatException | IOException e) {
      logger.error("Error parsing id paramter", e);
      throw new ServletException(e);
    }
  }

  /**
   * Gets the DTO object from the request parameters.
   */
  private DragonBallUserDto getDtoFromRequest(HttpServletRequest request) throws ServletException {
    try {
      DragonBallUserDto dragonBallUserDto = new DragonBallUserDto();
      if (request.getParameter("id") != null) {
        dragonBallUserDto.setId(Long.parseLong(getUrlDecodedParam(request,"id")));
      }
      dragonBallUserDto.setUsername(getUrlDecodedParam(request,"username"));
      dragonBallUserDto.setEmail(getUrlDecodedParam(request,"email"));
      dragonBallUserDto.setAge(Integer.parseInt(getUrlDecodedParam(request,"age")));
      dragonBallUserDto.setStamina(Integer.parseInt(getUrlDecodedParam(request,"stamina")));
      dragonBallUserDto.setPowerLevel(Integer.parseInt(getUrlDecodedParam(request,"powerLevel")));
      return dragonBallUserDto;
    } catch (NumberFormatException | UnsupportedEncodingException e) {
      logger.error("Error parsing DragonBallUserDto", e);
      throw new ServletException(e);
    }
  }

  /**
   * Write the response body.
   */
  private void setResponseBody(HttpServletResponse response, String responseBody)
      throws IOException {
    response.getWriter().write(responseBody);
    response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
  }

  /**
   * Decode URL Encoded parameters.
   */
  private String getUrlDecodedParam(HttpServletRequest request, String paramName)
      throws UnsupportedEncodingException {
    return URLDecoder.decode(request.getParameter(paramName), StandardCharsets.UTF_8.name());
  }
}

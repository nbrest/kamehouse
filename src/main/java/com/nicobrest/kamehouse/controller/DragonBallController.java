package com.nicobrest.kamehouse.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicobrest.kamehouse.exception.KameHouseForbiddenException;
import com.nicobrest.kamehouse.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.model.DragonBallUser;
import com.nicobrest.kamehouse.service.DragonBallUserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Controller class for the test endpoint /dragonball.
 *
 * @author nbrest
 * @version 1
 */
@Controller
@RequestMapping(value = "/api/v1/dragonball")
public class DragonBallController {

  private static final Logger LOGGER = LoggerFactory.getLogger(DragonBallController.class);

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

  /**
   * /dragonball/model-and-view Returns the ModelAndView object for the test
   * endpoint.
   *
   * @author nbrest
   */
  @RequestMapping(value = "/model-and-view", method = RequestMethod.GET)
  public ModelAndView getModelAndView(@RequestParam(value = "name", required = false,
      defaultValue = "Goku") String name) {

    LOGGER.info("In controller /dragonball/model-and-view (GET)");

    String message = "message: dragonball ModelAndView!";

    ModelAndView mv = new ModelAndView("jsp/dragonball/model-and-view.jsp");
    mv.addObject("message", message);
    mv.addObject("name", name);

    LOGGER.info("In controller /dragonball/model-and-view Model keys: " + mv.getModel().keySet()
        .toString());
    LOGGER.info("In controller /dragonball/model-and-view Model values: " + mv.getModel().values()
        .toString());
    LOGGER.info("In controller /dragonball/model-and-view Model values: " + mv.getViewName());

    return mv;
  }

  /**
   * /dragonball/users Returns all DragonBallUsers.
   *
   * @author nbrest
   */
  @RequestMapping(value = "/users", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<List<DragonBallUser>> getUsers(@RequestParam(value = "action",
      required = false, defaultValue = "goku") String action) throws Exception {

    LOGGER.info("In controller /dragonball/users (GET)");

    switch (action) {
      case "KameHouseNotFoundException":
        throw new KameHouseNotFoundException(
            "*** KameHouseNotFoundException in getUsers ***");
        // break;
      case "RuntimeException":
        throw new RuntimeException("*** RuntimeException in getUsers ***");
        // break;
      case "Exception":
        throw new Exception("*** Exception in getUsers ***");
        // break;
      default:
        break;
    }

    List<DragonBallUser> dbUsers = dragonBallUserService.getAllDragonBallUsers();

    return new ResponseEntity<List<DragonBallUser>>(dbUsers, HttpStatus.OK);
  }

  /**
   * /dragonball/users Creates a new DragonBallUser in the repository.
   *
   * @author nbrest
   */
  @RequestMapping(value = "/users", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<Long> postUsers(@RequestBody DragonBallUser dragonBallUser) {

    LOGGER.info("In controller /dragonball/users (POST)");

    Long dbUserId = dragonBallUserService.createDragonBallUser(dragonBallUser);

    return new ResponseEntity<Long>(dbUserId, HttpStatus.CREATED);
  }

  /**
   * /dragonball/users/{id} Returns a specific DragonBallUser from the
   * repository based on the id.
   *
   * @author nbrest
   */
  @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<DragonBallUser> getUsersId(@PathVariable Long id) {
    LOGGER.info("In controller /dragonball/users/{id} (GET)");

    DragonBallUser dbUser = dragonBallUserService.getDragonBallUser(id);

    return new ResponseEntity<DragonBallUser>(dbUser, HttpStatus.OK);
  }

  /**
   * /dragonball/users/username/{username} Returns a specific DragonBallUser from the
   * repository based on the username.
   *
   * @author nbrest
   */
  @RequestMapping(value = "/users/username/{username:.+}", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<DragonBallUser> getUsersUsername(@PathVariable String username) {
    // The :.+ on the endpoint mapping is to allow dots in the username,
    // otherwise it strips the
    // part following the first dot
    LOGGER.info("In controller /dragonball/users/username/{username:.+} (GET)");

    DragonBallUser dbUser = dragonBallUserService.getDragonBallUser(username);

    return new ResponseEntity<DragonBallUser>(dbUser, HttpStatus.OK);
  }

  /**
   * /dragonball/users/emails/{email:.+} Returns a specific DragonBallUser from
   * the repository based on the email (URLEncoded with UTF-8).
   *
   * @author nbrest
   */
  @RequestMapping(value = "/users/emails/{email:.+}", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<String> getUsersByEmail(@PathVariable String email) {

    LOGGER.info("In controller /dragonball/users/emails/{email:.+} (GET)");

    /*
     * url encoded parameters are automatically decoded, there´s no need to do
     * it here. String emailDecoded; try { emailDecoded =
     * URLDecoder.decode(email, "UTF-8"); } catch (UnsupportedEncodingException
     * e) { e.printStackTrace(); throw new KameHouseBadRequestException(
     * "Error parsing email url parameter", e); }
     */
    DragonBallUser dbUser = dragonBallUserService.getDragonBallUserByEmail(email);
    String dbUserJson = convertToJsonString(dbUser);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json;charset=UTF-8");
    ResponseEntity<String> response = new ResponseEntity<String>(dbUserJson, headers,
        HttpStatus.OK);

    return response;
  }

  /**
   * /dragonball/users/{id} Updates a user in the repository.
   *
   * @author nbrest
   */
  @RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
  @ResponseBody
  public ResponseEntity<?> putUsersUsername(@PathVariable Long id,
      @RequestBody DragonBallUser dragonBallUser) {

    LOGGER.info("In controller /dragonball/users/{id} (PUT)");

    if (!id.equals(dragonBallUser.getId())) {
      throw new KameHouseForbiddenException("Id in path variable doesn´t match"
          + "id in request body.");
    }
    dragonBallUserService.updateDragonBallUser(dragonBallUser);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * /dragonball/users/{id} Deletes an existing user from the repository.
   *
   * @author nbrest
   */
  @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<DragonBallUser> deleteUsersUsername(@PathVariable Long id) {

    LOGGER.info("In controller /dragonball/users/{username} (DELETE)");

    DragonBallUser deletedDbUser = dragonBallUserService.deleteDragonBallUser(id);

    return new ResponseEntity<DragonBallUser>(deletedDbUser, HttpStatus.OK);
  }

  /**
   * Converts an Object to a Json String.
   *
   * @author nbrest
   */
  private String convertToJsonString(Object obj) {

    ObjectMapper mapper = new ObjectMapper();
    String jsonString;
    try {
      jsonString = mapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      throw new KameHouseServerErrorException("Error mapping Object to a Json string", e);
    }
    return jsonString;
  }
}

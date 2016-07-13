package ar.com.nicobrest.mobileinspections.controller;

import ar.com.nicobrest.mobileinspections.exception.DragonBallUserAlreadyExistsException;
import ar.com.nicobrest.mobileinspections.exception.DragonBallUserForbiddenException;
import ar.com.nicobrest.mobileinspections.exception.DragonBallUserNotFoundException;
import ar.com.nicobrest.mobileinspections.model.DragonBallUser;
import ar.com.nicobrest.mobileinspections.service.DragonBallUserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Controller class for the dragonball test endpoints /dragonball
 * /dragonball/modelAndView /dragonball/users /dragonball/users/{username}.
 * 
 * @author nbrest
 */
@Controller
@RequestMapping(value = "/dragonball")
public class DragonBallController {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(DragonBallController.class);

  @Autowired
  private DragonBallUserService dragonBallUserService;

  /**
   * Getters and Setters.
   * 
   * @author nbrest
   * @param dragonBallUserService
   *          DragonBallUserService
   */
  public void setDragonBallUserService(
      DragonBallUserService dragonBallUserService) {

    this.dragonBallUserService = dragonBallUserService;
  }

  /**
   * Getters and Setters.
   * 
   * @author nbrest
   * @return DragonBallUserService
   */
  public DragonBallUserService getDragonBallUserService() {

    return this.dragonBallUserService;
  }

  /**
   * /dragonball/modelAndView Returns the ModelAndView object for the test
   * endpoint.
   * 
   * @author nbrest
   * @param name
   *          : Nombre del usuario que visita el sitio
   * @return ModelAndView
   */
  @RequestMapping(value = "/modelAndView", method = RequestMethod.GET)
  public ModelAndView getModelAndView(
      @RequestParam(value = "name", required = false, defaultValue = "Goku") String name) {

    LOGGER.info("In controller /dragonball/modelAndView (GET)");

    String message = "message: dragonball ModelAndView!";

    ModelAndView mv = new ModelAndView("dragonball/modelAndView");
    mv.addObject("message", message);
    mv.addObject("name", name);

    LOGGER.info("In controller /dragonball/modelAndView Model keys: "
        + mv.getModel().keySet().toString());
    LOGGER.info("In controller /dragonball/modelAndView Model values: "
        + mv.getModel().values().toString());

    return mv;
  }

  /**
   * /dragonball/users Returns all DragonBallUsers in json format.
   * 
   * @author nbrest
   * @return DragonBallUser list
   * @throws Exception
   *           : General exception
   */
  @RequestMapping(value = "/users", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<List<DragonBallUser>> getUsers(
      @RequestParam(value = "action", required = false, defaultValue = "goku") String action)
      throws Exception {

    LOGGER.info("In controller /dragonball/users (GET)");

    switch (action) {
      case "DragonBallUserNotFoundException":
        throw new DragonBallUserNotFoundException(
          "*** DragonBallUserNotFoundException in getUsers ***");
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

    List<DragonBallUser> dbUsers = dragonBallUserService
        .getAllDragonBallUsers();

    return new ResponseEntity<List<DragonBallUser>>(dbUsers, HttpStatus.OK);
  }

  /**
   * /dragonball/users Creates a new DragonBallUser in the repository.
   * 
   * @author nbrest
   * @param dragonBallUser
   *          User to add to the repository
   * @return Long Returns the id of the newly created DragonBallUser
   * @throws DragonBallUserAlreadyExistsException
   *           User defined exception
   * @throws DragonBallUserNotFoundException
   *           User defined exception
   */
  @RequestMapping(value = "/users", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<Long> postUsers(
      @RequestBody DragonBallUser dragonBallUser)
      throws DragonBallUserAlreadyExistsException,
      DragonBallUserNotFoundException {

    LOGGER.info("In controller /dragonball/users (POST)");

    Long dbUserId = dragonBallUserService.createDragonBallUser(dragonBallUser);

    return new ResponseEntity<Long>(dbUserId, HttpStatus.CREATED);
  }

  /**
   * /dragonball/users/{username} Returns a specific DragonBallUser from the
   * repository.
   * 
   * @author nbrest
   * @param username
   *          User name to get from the repository
   * @return DragonBallUser
   * @throws DragonBallUserNotFoundException
   *           User defined exception
   */
  @RequestMapping(value = "/users/{username}", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<DragonBallUser> getUsersUsername(
      @PathVariable String username) throws DragonBallUserNotFoundException {

    LOGGER.info("In controller /dragonball/users/{username} (GET)");

    DragonBallUser dbUser = dragonBallUserService.getDragonBallUser(username);

    return new ResponseEntity<DragonBallUser>(dbUser, HttpStatus.OK);
  }

  /**
   * /dragonball/users/{id} Updates a user in the repository.
   * 
   * @author nbrest
   * @param id
   *          : id of user to update
   * @param dragonBallUser
   *          User to update
   * @return Long
   * @throws DragonBallUserForbiddenException
   *           User defined exception
   * @throws DragonBallUserNotFoundException
   *           User defined exception
   */
  @RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
  @ResponseBody
  public ResponseEntity<?> putUsersUsername(@PathVariable Long id,
      @RequestBody DragonBallUser dragonBallUser)
      throws DragonBallUserForbiddenException, DragonBallUserNotFoundException,
      DragonBallUserAlreadyExistsException {

    LOGGER.info("In controller /dragonball/users/{id} (PUT)");

    if (!id.equals(dragonBallUser.getId())) {
      throw new DragonBallUserForbiddenException(
          "Id in path variable doesn´t match" + "id in request body.");
    }
    dragonBallUserService.updateDragonBallUser(dragonBallUser);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * /dragonball/users/{id} Deletes an existing user from the repository.
   * 
   * @author nbrest
   * @return DragonBallUser Deleted user
   * @throws DragonBallUserNotFoundException
   *           User defined exception
   */
  @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<DragonBallUser> deleteUsersUsername(
      @PathVariable Long id) throws DragonBallUserNotFoundException {

    LOGGER.info("In controller /dragonball/users/{username} (DELETE)");

    DragonBallUser deletedDbUser = dragonBallUserService
        .deleteDragonBallUser(id);

    return new ResponseEntity<DragonBallUser>(deletedDbUser, HttpStatus.OK);
  }
}

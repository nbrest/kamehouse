package com.nicobrest.kamehouse.testmodule.controller;

import com.nicobrest.kamehouse.main.controller.AbstractController;
import com.nicobrest.kamehouse.main.exception.KameHouseException;
import com.nicobrest.kamehouse.main.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;
import com.nicobrest.kamehouse.testmodule.service.DragonBallUserService;
import com.nicobrest.kamehouse.testmodule.service.dto.DragonBallUserDto;
import com.nicobrest.kamehouse.utils.JsonUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Controller class for the test endpoint /dragonball.
 *
 * @author nbrest
 */
@Controller
@RequestMapping(value = "/api/v1/dragonball")
public class DragonBallController extends AbstractController {

  @Autowired
  private DragonBallUserService dragonBallUserService;

  /**
   * /dragonball/model-and-view Returns the ModelAndView object for the test
   * endpoint.
   */
  @GetMapping(path = "/model-and-view")
  public ModelAndView getModelAndView(@RequestParam(value = "name", required = false,
      defaultValue = "Goku") String name) {
    logger.trace("In controller /dragonball/model-and-view (GET)");
    String message = "message: dragonball ModelAndView!";
    ModelAndView mv = new ModelAndView("jsp/test-module/jsp/dragonball/model-and-view");
    mv.addObject("message", message);
    mv.addObject("name", name);
    return mv;
  }

  /**
   * /dragonball/users Returns all DragonBallUsers.
   */
  @GetMapping(path = "/users")
  @ResponseBody
  public ResponseEntity<List<DragonBallUser>> getAll(@RequestParam(value = "action",
      required = false, defaultValue = "goku") String action) {
    logger.trace("In controller /dragonball/users (GET)");
    // switch test to test parameters and exceptions
    switch (action) {
      case "KameHouseNotFoundException":
        throw new KameHouseNotFoundException("*** KameHouseNotFoundException in getUsers ***");
      case "KameHouseException":
        throw new KameHouseException("*** KameHouseException in getUsers ***");
      default:
        break;
    }
    List<DragonBallUser> dbUsers = dragonBallUserService.getAll();
    return generateGetResponseEntity(dbUsers);
  }

  /**
   * /dragonball/users Creates a new DragonBallUser in the repository.
   */
  @PostMapping(path = "/users")
  @ResponseBody
  public ResponseEntity<Long> create(@RequestBody DragonBallUserDto dto) {
    logger.trace("In controller /dragonball/users (POST)");
    Long dbUserId = dragonBallUserService.create(dto);
    return generatePostResponseEntity(dbUserId);
  }

  /**
   * /dragonball/users/{id} Returns a specific DragonBallUser from the
   * repository based on the id.
   */
  @GetMapping(path = "/users/{id}")
  @ResponseBody
  public ResponseEntity<DragonBallUser> read(@PathVariable Long id) {
    logger.trace("In controller /dragonball/users/{id} (GET)");
    DragonBallUser dbUser = dragonBallUserService.read(id);
    return generateGetResponseEntity(dbUser);
  }

  /**
   * /dragonball/users/username/{username} Returns a specific DragonBallUser
   * from the repository based on the username.
   */
  @GetMapping(path = "/users/username/{username:.+}")
  @ResponseBody
  public ResponseEntity<DragonBallUser> getByUsername(@PathVariable String username) {
    // The :.+ on the endpoint mapping is to allow dots in the username,
    // otherwise it strips the
    // part following the first dot
    logger.trace("In controller /dragonball/users/username/{username:.+} (GET)");
    DragonBallUser dbUser = dragonBallUserService.getByUsername(username);
    return generateGetResponseEntity(dbUser);
  }

  /**
   * /dragonball/users/emails/{email:.+} Returns a specific DragonBallUser from
   * the repository based on the email (URLEncoded with UTF-8).
   */
  @GetMapping(path = "/users/emails/{email:.+}")
  @ResponseBody
  public ResponseEntity<String> getByEmail(@PathVariable String email) {
    logger.trace("In controller /dragonball/users/emails/{email:.+} (GET)");
    DragonBallUser dbUser = dragonBallUserService.getByEmail(email);
    String dbUserJson = JsonUtils.toJsonString(dbUser);
    // Leaving this one as is as a test instead of using
    // generateGetResponseEntity
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json;charset=UTF-8");
    return new ResponseEntity<>(dbUserJson, headers, HttpStatus.OK);
  }

  /**
   * /dragonball/users/{id} Updates a user in the repository.
   */
  @PutMapping(path = "/users/{id}")
  @ResponseBody
  public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody DragonBallUserDto dto) {
    logger.trace("In controller /dragonball/users/{id} (PUT)");
    validatePathAndRequestBodyIds(id, dto.getId());
    dragonBallUserService.update(dto);
    return generatePutResponseEntity();
  }

  /**
   * /dragonball/users/{id} Deletes an existing user from the repository.
   */
  @DeleteMapping(path = "/users/{id}")
  @ResponseBody
  public ResponseEntity<DragonBallUser> delete(@PathVariable Long id) {
    logger.trace("In controller /dragonball/users/{id} (DELETE)");
    DragonBallUser deletedDbUser = dragonBallUserService.delete(id);
    return generateDeleteResponseEntity(deletedDbUser);
  }
}

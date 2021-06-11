package com.nicobrest.kamehouse.testmodule.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractCrudController;
import com.nicobrest.kamehouse.commons.exception.KameHouseException;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;
import com.nicobrest.kamehouse.testmodule.model.dto.DragonBallUserDto;
import com.nicobrest.kamehouse.testmodule.service.DragonBallUserService;

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
public class DragonBallController extends AbstractCrudController {

  private static final String DB_USERS = "/dragonball/users";
  private static final String DB_USERS_ID = "/dragonball/users/";
  
  @Autowired
  private DragonBallUserService dragonBallUserService;

  /**
   * Creates a new DragonBallUser in the repository.
   */
  @PostMapping(path = "/users")
  @ResponseBody
  public ResponseEntity<Long> create(@RequestBody DragonBallUserDto dto) {
    return create(DB_USERS, dragonBallUserService, dto);
  }

  /**
   * Returns a specific DragonBallUser from the repository based on the id.
   */
  @GetMapping(path = "/users/{id}")
  @ResponseBody
  public ResponseEntity<DragonBallUser> read(@PathVariable Long id) {
    return read(DB_USERS_ID + id, dragonBallUserService, id);
  }

  /**
   * Returns all DragonBallUsers.
   */
  @GetMapping(path = "/users")
  @ResponseBody
  public ResponseEntity<List<DragonBallUser>> readAll(@RequestParam(value = "action",
      required = false, defaultValue = "goku") String action) {
    // switch to test parameters and exceptions
    switch (action) {
      case "KameHouseNotFoundException":
        throw new KameHouseNotFoundException("*** KameHouseNotFoundException in getUsers ***");
      case "KameHouseException":
        throw new KameHouseException("*** KameHouseException in getUsers ***");
      default:
        break;
    }
    return readAll(DB_USERS, dragonBallUserService);
  }

  /**
   * Updates a user in the repository.
   */
  @PutMapping(path = "/users/{id}")
  @ResponseBody
  public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody DragonBallUserDto dto) {
    return update(DB_USERS_ID + id, dragonBallUserService, id, dto);
  }

  /**
   * Deletes an existing user from the repository.
   */
  @DeleteMapping(path = "/users/{id}")
  @ResponseBody
  public ResponseEntity<DragonBallUser> delete(@PathVariable Long id) {
    return delete(DB_USERS_ID + id, dragonBallUserService, id);
  }

  /**
   * Returns a specific DragonBallUser from the repository based on the
   * username.
   */
  @GetMapping(path = "/users/username/{username:.+}")
  @ResponseBody
  public ResponseEntity<DragonBallUser> getByUsername(@PathVariable String username) {
    // The :.+ on the endpoint mapping is to allow dots in the username,
    // otherwise it strips the
    // part following the first dot
    logger.trace("/dragonball/users/username/[username] (GET)");
    DragonBallUser dbUser = dragonBallUserService.getByUsername(username);
    return generateGetResponseEntity(dbUser);
  }

  /**
   * Returns a specific DragonBallUser from the repository based on the email
   * (URLEncoded with UTF-8).
   */
  @GetMapping(path = "/users/emails")
  @ResponseBody
  public ResponseEntity<String> getByEmail(@RequestParam(value = "email",
      required = true) String email) {
    logger.trace("/dragonball/users/emails?email=[email] (GET)");
    DragonBallUser dbUser = dragonBallUserService.getByEmail(email);
    String dbUserJson = JsonUtils.toJsonString(dbUser);
    // Leaving this one as is as a test instead of using
    // generateGetResponseEntity
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json;charset=UTF-8");
    return new ResponseEntity<>(dbUserJson, headers, HttpStatus.OK);
  }
}

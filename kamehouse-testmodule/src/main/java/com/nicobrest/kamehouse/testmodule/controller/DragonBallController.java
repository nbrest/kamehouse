package com.nicobrest.kamehouse.testmodule.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractCrudController;
import com.nicobrest.kamehouse.commons.exception.KameHouseException;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.testmodule.model.DragonBallUser;
import com.nicobrest.kamehouse.testmodule.model.dto.DragonBallUserDto;
import com.nicobrest.kamehouse.testmodule.service.DragonBallUserService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * Controller class for the test endpoint /dragonball.
 *
 * @author nbrest
 */
@Controller
@RequestMapping(value = "/api/v1/test-module/dragonball")
public class DragonBallController extends
    AbstractCrudController<DragonBallUser, DragonBallUserDto> {

  @Autowired
  private DragonBallUserService dragonBallUserService;

  @Override
  @SuppressFBWarnings(value = "EI_EXPOSE_REP")
  public CrudService<DragonBallUser, DragonBallUserDto> getCrudService() {
    return dragonBallUserService;
  }

  /**
   * Creates a new DragonBallUser in the repository.
   */
  @PostMapping(path = "/users")
  @ResponseBody
  @Override
  public ResponseEntity<Long> create(@RequestBody DragonBallUserDto dto) {
    return super.create(dto);
  }

  /**
   * Returns a specific DragonBallUser from the repository based on the id.
   */
  @GetMapping(path = "/users/{id}")
  @ResponseBody
  @Override
  public ResponseEntity<DragonBallUser> read(@PathVariable Long id) {
    return super.read(id);
  }

  /**
   * Returns all DragonBallUsers.
   */
  @GetMapping(path = "/users")
  @ResponseBody
  public ResponseEntity<List<DragonBallUser>> readAll(
      @RequestParam(value = "action", required = false, defaultValue = "goku") String action) {
    // switch to test parameters and exceptions
    switch (action) {
      case "KameHouseNotFoundException":
        throw new KameHouseNotFoundException("*** KameHouseNotFoundException in getUsers ***");
      case "KameHouseException":
        throw new KameHouseException("*** KameHouseException in getUsers ***");
      default:
        break;
    }
    return super.readAll();
  }

  /**
   * Updates a user in the repository.
   */
  @PutMapping(path = "/users/{id}")
  @ResponseBody
  @Override
  public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody DragonBallUserDto dto) {
    return super.update(id, dto);
  }

  /**
   * Deletes an existing user from the repository.
   */
  @DeleteMapping(path = "/users/{id}")
  @ResponseBody
  @Override
  public ResponseEntity<DragonBallUser> delete(@PathVariable Long id) {
    return super.delete(id);
  }

  /**
   * Returns a specific DragonBallUser from the repository based on the username.
   */
  @GetMapping(path = "/users/username/{username:.+}")
  @ResponseBody
  public ResponseEntity<DragonBallUser> getByUsername(@PathVariable String username) {
    // The :.+ on the endpoint mapping is to allow dots in the username,
    // otherwise it strips the
    // part following the first dot
    DragonBallUser dbUser = dragonBallUserService.getByUsername(username);
    return generateGetResponseEntity(dbUser);
  }

  /**
   * Returns a specific DragonBallUser from the repository based on the email (URLEncoded with
   * UTF-8).
   */
  @GetMapping(path = "/users/emails")
  @ResponseBody
  public ResponseEntity<DragonBallUser> getByEmail(
      @RequestParam(value = "email", required = true) String email) {
    DragonBallUser dbUser = dragonBallUserService.getByEmail(email);
    return generateGetResponseEntity(dbUser);
  }
}

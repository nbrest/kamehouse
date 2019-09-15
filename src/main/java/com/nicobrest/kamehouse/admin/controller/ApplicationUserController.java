package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.model.ApplicationUser;
import com.nicobrest.kamehouse.admin.service.ApplicationUserService;
import com.nicobrest.kamehouse.admin.service.dto.ApplicationUserDto;
import com.nicobrest.kamehouse.utils.ControllerUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Controller class for the application users.
 *
 * @author nbrest
 */
@Controller
@RequestMapping(value = "/api/v1/admin/application")
public class ApplicationUserController {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private ApplicationUserService applicationUserService;

  public void setApplicationUserService(ApplicationUserService applicationUserService) {
    this.applicationUserService = applicationUserService;
  }

  public ApplicationUserService getApplicationUserService() {
    return this.applicationUserService;
  }

  /**
   * Returns all application users.
   */
  @GetMapping(path = "/users")
  @ResponseBody
  public ResponseEntity<List<ApplicationUser>> getUsers() {
    logger.trace("In controller /application/users/ (GET)");
    List<ApplicationUser> applicationUsers = applicationUserService.getAllUsers();
    // Don't return the passwords through the API.
    for (ApplicationUser appUser : applicationUsers) {
      appUser.setPassword(null);
    }
    return new ResponseEntity<>(applicationUsers, HttpStatus.OK);
  }

  /**
   * Creates a new ApplicationUser in the repository.
   */
  @PostMapping(path = "/users")
  @ResponseBody
  public ResponseEntity<Long> postUsers(@RequestBody ApplicationUserDto applicationUserDto) {
    logger.trace("In controller /application/users (POST)");
    Long applicationUserId = applicationUserService.createUser(applicationUserDto);
    return new ResponseEntity<>(applicationUserId, HttpStatus.CREATED);
  }

  /**
   * Returns a specific ApplicationUser from the repository based on the username.
   */
  @GetMapping(path = "/users/{username:.+}")
  @ResponseBody
  public ResponseEntity<ApplicationUser> getUsersUsername(@PathVariable String username) {
    logger.trace("In controller /application/users/{username:.+} (GET)");
    ApplicationUser applicationUser = applicationUserService.loadUserByUsername(username);
    // Don't return the password through the API.
    applicationUser.setPassword(null);
    return new ResponseEntity<>(applicationUser, HttpStatus.OK);
  }

  /**
   * Updates a user in the repository.
   */
  @PutMapping(path = "/users/{id}")
  @ResponseBody
  public ResponseEntity<Void> putUsersId(@PathVariable Long id,
      @RequestBody ApplicationUserDto applicationUserDto) {
    logger.trace("In controller /application/users/{id} (PUT)");
    ControllerUtils.validatePathAndRequestBodyIds(id, applicationUserDto.getId());
    applicationUserService.updateUser(applicationUserDto);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Deletes an existing user from the repository.
   */
  @DeleteMapping(path = "/users/{id}")
  @ResponseBody
  public ResponseEntity<ApplicationUser> deleteUsersId(@PathVariable Long id) {
    logger.trace("In controller /application/users/{id} (DELETE)");
    ApplicationUser deletedAppUser = applicationUserService.deleteUser(id);
    // Don't return the passwords through the API.
    deletedAppUser.setPassword(null);
    return new ResponseEntity<>(deletedAppUser, HttpStatus.OK);
  }
}

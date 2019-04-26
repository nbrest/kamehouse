package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.model.ApplicationUser;
import com.nicobrest.kamehouse.admin.service.ApplicationUserService;
import com.nicobrest.kamehouse.main.exception.KameHouseForbiddenException;

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

  private static final Logger logger = LoggerFactory.getLogger(ApplicationUserController.class);

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
  @RequestMapping(value = "/users", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<List<ApplicationUser>> getUsers() {

    logger.trace("In controller /application/users/ (GET)");

    List<ApplicationUser> applicationUsers = applicationUserService.getAllUsers();
    //Don't return the passwords through the API.
    for (ApplicationUser appUser : applicationUsers) {
      appUser.setPassword(null);
    }
    return new ResponseEntity<List<ApplicationUser>>(applicationUsers, HttpStatus.OK);
  }

  /**
   * Creates a new ApplicationUser in the repository.
   */
  @RequestMapping(value = "/users", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<Long> postUsers(@RequestBody ApplicationUser applicationUser) {

    logger.trace("In controller /application/users (POST)");

    Long applicationUserId = applicationUserService.createUser(applicationUser);

    return new ResponseEntity<Long>(applicationUserId, HttpStatus.CREATED);
  }

  /**
   * Returns a specific ApplicationUser from the repository based on the
   * username.
   */
  @RequestMapping(value = "/users/{username:.+}", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<ApplicationUser> getUsersUsername(@PathVariable String username) {

    logger.trace("In controller /application/users/{username:.+} (GET)");

    ApplicationUser applicationUser = applicationUserService.loadUserByUsername(username);
    //Don't return the password through the API.
    applicationUser.setPassword(null);
    
    return new ResponseEntity<ApplicationUser>(applicationUser, HttpStatus.OK);
  }

  /**
   * Updates a user in the repository.
   */
  @RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
  @ResponseBody
  public ResponseEntity<?> putUsersUsername(@PathVariable Long id,
      @RequestBody ApplicationUser applicationUser) {

    logger.trace("In controller /application/users/{id} (PUT)");

    if (!id.equals(applicationUser.getId())) {
      //TODO: This should be a bad request exception
      throw new KameHouseForbiddenException("Id in path variable doesn´t match"
          + "id in request body.");
    }
    applicationUserService.updateUser(applicationUser);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Deletes an existing user from the repository.
   */
  @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<ApplicationUser> deleteUsersUsername(@PathVariable Long id) {

    logger.trace("In controller /application/users/{id} (DELETE)");

    ApplicationUser deletedAppUser = applicationUserService.deleteUser(id);
    //Don't return the passwords through the API.
    deletedAppUser.setPassword(null);
    
    return new ResponseEntity<ApplicationUser>(deletedAppUser, HttpStatus.OK);
  }
}

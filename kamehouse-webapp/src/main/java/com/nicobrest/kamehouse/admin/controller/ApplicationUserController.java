package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.model.ApplicationUser;
import com.nicobrest.kamehouse.admin.model.dto.ApplicationUserDto;
import com.nicobrest.kamehouse.admin.service.ApplicationUserService;
import com.nicobrest.kamehouse.commons.controller.AbstractCrudController;

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
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Controller class for the application users.
 *
 * @author nbrest
 */
@Controller
@RequestMapping(value = "/api/v1/admin/application")
public class ApplicationUserController extends AbstractCrudController {

  private static final String APP_USERS = "/application/users";
  private static final String APP_USERS_ID = "/application/users/";
  
  @Autowired
  private ApplicationUserService applicationUserService;

  /**
   * Creates a new ApplicationUser in the repository.
   */
  @PostMapping(path = "/users")
  @ResponseBody
  public ResponseEntity<Long> create(@RequestBody ApplicationUserDto dto) {
    return create(APP_USERS, applicationUserService, dto);
  }

  /**
   * Reads an application user by it's id.
   */
  @GetMapping(path = "/users/{id}")
  @ResponseBody
  public ResponseEntity<ApplicationUser> read(@PathVariable Long id) {
    ResponseEntity<ApplicationUser> responseEntity =
        read(APP_USERS_ID + id, applicationUserService, id);
    // Don't return the password through the API.
    removePassword(responseEntity.getBody());
    return responseEntity;
  }

  /**
   * Reads all application users.
   */
  @GetMapping(path = "/users")
  @ResponseBody
  public ResponseEntity<List<ApplicationUser>> readAll() {
    ResponseEntity<List<ApplicationUser>> responseEntity =
        readAll(APP_USERS, applicationUserService);
    // Don't return the passwords through the API.
    removePassword(responseEntity.getBody());
    return responseEntity;
  }

  /**
   * Updates a user in the repository.
   */
  @PutMapping(path = "/users/{id}")
  @ResponseBody
  public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody ApplicationUserDto dto) {
    return update(APP_USERS_ID + id, applicationUserService, id, dto);
  }

  /**
   * Deletes an existing user from the repository.
   */
  @DeleteMapping(path = "/users/{id}")
  @ResponseBody
  public ResponseEntity<ApplicationUser> delete(@PathVariable Long id) {
    ResponseEntity<ApplicationUser> responseEntity =
        delete(APP_USERS_ID + id, applicationUserService, id);
    // Don't return the password through the API.
    removePassword(responseEntity.getBody());
    return responseEntity;
  }

  /**
   * Gets a specific ApplicationUser from the repository based on the username.
   */
  @GetMapping(path = "/users/username/{username:.+}")
  @ResponseBody
  public ResponseEntity<ApplicationUser> loadUserByUsername(@PathVariable String username) {
    logger.trace("/application/users/username/[] (GET)");
    ApplicationUser applicationUser = applicationUserService.loadUserByUsername(username);
    // Don't return the password through the API.
    removePassword(applicationUser);
    return generateGetResponseEntity(applicationUser);
  }

  /**
   * Removes the password from the application user.
   */
  private void removePassword(ApplicationUser applicationUser) { 
    if (applicationUser != null) {
      applicationUser.setPassword(null);
    }
  }
  
  /**
   * Removes the password from the application users.
   */
  private void removePassword(List<ApplicationUser> applicationUsers) { 
    if (applicationUsers != null) {
      for (ApplicationUser applicationUser : applicationUsers) {
        applicationUser.setPassword(null);
      }
    }
  }
}

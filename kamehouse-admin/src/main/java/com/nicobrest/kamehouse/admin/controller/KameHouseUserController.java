package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.service.KameHouseUserService;
import com.nicobrest.kamehouse.commons.controller.AbstractCrudController;
import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import com.nicobrest.kamehouse.commons.model.dto.KameHouseUserDto;

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
 * Controller class for the KameHouse users.
 *
 * @author nbrest
 */
@Controller
@RequestMapping(value = "/api/v1/admin/kamehouse")
public class KameHouseUserController extends AbstractCrudController {
  
  @Autowired
  private KameHouseUserService kameHouseUserService;

  /**
   * Creates a new KameHouseUser in the repository.
   */
  @PostMapping(path = "/users")
  @ResponseBody
  public ResponseEntity<Long> create(@RequestBody KameHouseUserDto dto) {
    return create(kameHouseUserService, dto);
  }

  /**
   * Reads an kamehouse by it's id.
   */
  @GetMapping(path = "/users/{id}")
  @ResponseBody
  public ResponseEntity<KameHouseUser> read(@PathVariable Long id) {
    return generatePasswordLessResponseEntity(read(kameHouseUserService, id));
  }

  /**
   * Reads all kamehouse users.
   */
  @GetMapping(path = "/users")
  @ResponseBody
  public ResponseEntity<List<KameHouseUser>> readAll() {
    return generatePasswordLessResponseEntity(readAll(kameHouseUserService));
  }

  /**
   * Updates a user in the repository.
   */
  @PutMapping(path = "/users/{id}")
  @ResponseBody
  public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody KameHouseUserDto dto) {
    return update(kameHouseUserService, id, dto);
  }

  /**
   * Deletes an existing user from the repository.
   */
  @DeleteMapping(path = "/users/{id}")
  @ResponseBody
  public ResponseEntity<KameHouseUser> delete(@PathVariable Long id) {
    return generatePasswordLessResponseEntity(delete(kameHouseUserService, id));
  }

  /**
   * Gets a specific KameHouseUser from the repository based on the username.
   */
  @GetMapping(path = "/users/username/{username:.+}")
  @ResponseBody
  public ResponseEntity<KameHouseUser> loadUserByUsername(@PathVariable String username) {
    KameHouseUser kameHouseUser = kameHouseUserService.loadUserByUsername(username);
    return generatePasswordLessResponseEntity(generateGetResponseEntity(kameHouseUser));
  }
}

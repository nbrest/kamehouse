package com.nicobrest.kamehouse.tennisworld.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractCrudController;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldUser;
import com.nicobrest.kamehouse.tennisworld.model.dto.TennisWorldUserDto;
import com.nicobrest.kamehouse.tennisworld.service.TennisWorldUserService;
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
 * Controller class for the tennis world users.
 * By default, if I don't mask the password, because its a byte[] it gets returned base64 encoded.
 *
 * @author nbrest
 */
@Controller
@RequestMapping(value = "/api/v1/tennis-world")
public class TennisWorldUserController extends AbstractCrudController {
  
  @Autowired
  private TennisWorldUserService tennisWorldUserService;

  /**
   * Creates a new entity in the repository.
   */
  @PostMapping(path = "/users")
  @ResponseBody
  public ResponseEntity<Long> create(@RequestBody TennisWorldUserDto dto) {
    return create(tennisWorldUserService, dto);
  }

  /**
   * Returns a specific entity from the repository based on the id.
   */
  @GetMapping(path = "/users/{id}")
  @ResponseBody
  public ResponseEntity<TennisWorldUser> read(@PathVariable Long id) {
    return read(tennisWorldUserService, id);
  }

  /**
   * Returns all entities.
   */
  @GetMapping(path = "/users")
  @ResponseBody
  public ResponseEntity<List<TennisWorldUser>> readAll() {
    return readAll(tennisWorldUserService);
  }

  /**
   * Updates an entity in the repository.
   */
  @PutMapping(path = "/users/{id}")
  @ResponseBody
  public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody TennisWorldUserDto dto) {
    return update(tennisWorldUserService, id, dto);
  }

  /**
   * Deletes an entity from the repository.
   */
  @DeleteMapping(path = "/users/{id}")
  @ResponseBody
  public ResponseEntity<TennisWorldUser> delete(@PathVariable Long id) {
    return delete(tennisWorldUserService, id);
  }
}

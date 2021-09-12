package com.nicobrest.kamehouse.tennisworld.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractCrudController;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.tennisworld.model.TennisWorldUser;
import com.nicobrest.kamehouse.tennisworld.model.dto.TennisWorldUserDto;
import com.nicobrest.kamehouse.tennisworld.service.TennisWorldUserService;
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
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller class for the tennis world users. By default, if I don't mask the password, because
 * its a byte[] it gets returned base64 encoded.
 *
 * @author nbrest
 */
@Controller
@RequestMapping(value = "/api/v1/tennis-world")
public class TennisWorldUserController extends
    AbstractCrudController<TennisWorldUser, TennisWorldUserDto> {

  @Autowired
  private TennisWorldUserService tennisWorldUserService;

  @Override
  @SuppressFBWarnings(value = "EI_EXPOSE_REP")
  public CrudService<TennisWorldUser, TennisWorldUserDto> getCrudService() {
    return tennisWorldUserService;
  }

  /**
   * Creates a new entity in the repository.
   */
  @PostMapping(path = "/users")
  @ResponseBody
  @Override
  public ResponseEntity<Long> create(@RequestBody TennisWorldUserDto dto) {
    return super.create(dto);
  }

  /**
   * Returns a specific entity from the repository based on the id.
   */
  @GetMapping(path = "/users/{id}")
  @ResponseBody
  @Override
  public ResponseEntity<TennisWorldUser> read(@PathVariable Long id) {
    return generatePasswordLessResponseEntity(super.read(id));
  }

  /**
   * Returns all entities.
   */
  @GetMapping(path = "/users")
  @ResponseBody
  @Override
  public ResponseEntity<List<TennisWorldUser>> readAll() {
    return generatePasswordLessResponseEntity(super.readAll());
  }

  /**
   * Updates an entity in the repository.
   */
  @PutMapping(path = "/users/{id}")
  @ResponseBody
  @Override
  public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody TennisWorldUserDto dto) {
    return super.update(id, dto);
  }

  /**
   * Deletes an entity from the repository.
   */
  @DeleteMapping(path = "/users/{id}")
  @ResponseBody
  @Override
  public ResponseEntity<TennisWorldUser> delete(@PathVariable Long id) {
    return generatePasswordLessResponseEntity(super.delete(id));
  }
}

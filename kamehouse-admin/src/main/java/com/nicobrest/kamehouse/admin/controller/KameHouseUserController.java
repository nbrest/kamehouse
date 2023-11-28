package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.service.KameHouseUserService;
import com.nicobrest.kamehouse.commons.controller.AbstractCrudController;
import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import com.nicobrest.kamehouse.commons.model.dto.KameHouseUserDto;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for the KameHouse users.
 *
 * @author nbrest
 */
@RestController
@RequestMapping(value = "/api/v1/admin/kamehouse")
public class KameHouseUserController extends
    AbstractCrudController<KameHouseUser, KameHouseUserDto> {

  private KameHouseUserService kameHouseUserService;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public KameHouseUserController(KameHouseUserService kameHouseUserService) {
    this.kameHouseUserService = kameHouseUserService;
  }

  @Override
  @SuppressFBWarnings(value = "EI_EXPOSE_REP")
  public CrudService<KameHouseUser, KameHouseUserDto> getCrudService() {
    return kameHouseUserService;
  }

  /**
   * Creates a new KameHouseUser in the repository.
   */
  @PostMapping(path = "/users")
  @Override
  public ResponseEntity<Long> create(@RequestBody KameHouseUserDto dto) {
    return super.create(dto);
  }

  /**
   * Reads an kamehouse by it's id.
   */
  @GetMapping(path = "/users/{id}")
  @Override
  public ResponseEntity<KameHouseUser> read(@PathVariable Long id) {
    return generatePasswordLessResponseEntity(super.read(id));
  }

  /**
   * Reads all kamehouse users.
   */
  @GetMapping(path = "/users")
  @Override
  public ResponseEntity<List<KameHouseUser>> readAll() {
    return generatePasswordLessResponseEntity(super.readAll());
  }

  /**
   * Updates a user in the repository.
   */
  @PutMapping(path = "/users/{id}")
  @Override
  public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody KameHouseUserDto dto) {
    return super.update(id, dto);
  }

  /**
   * Deletes an existing user from the repository.
   */
  @DeleteMapping(path = "/users/{id}")
  @Override
  public ResponseEntity<KameHouseUser> delete(@PathVariable Long id) {
    return generatePasswordLessResponseEntity(super.delete(id));
  }

  /**
   * Gets a specific KameHouseUser from the repository based on the username.
   */
  @GetMapping(path = "/users/username/{username:.+}")
  public ResponseEntity<KameHouseUser> loadUserByUsername(@PathVariable String username) {
    KameHouseUser kameHouseUser = kameHouseUserService.loadUserByUsername(
        StringUtils.sanitize(username));
    return generatePasswordLessResponseEntity(generateGetResponseEntity(kameHouseUser));
  }
}

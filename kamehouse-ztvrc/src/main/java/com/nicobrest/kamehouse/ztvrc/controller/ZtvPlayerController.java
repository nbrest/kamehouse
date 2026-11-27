package com.nicobrest.kamehouse.ztvrc.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractCrudController;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import com.nicobrest.kamehouse.ztvrc.model.ZtvPlayer;
import com.nicobrest.kamehouse.ztvrc.model.dto.ZtvPlayerDto;
import com.nicobrest.kamehouse.ztvrc.service.ZtvPlayerService;
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
 * Controller to manage the ztv Players registered in the application.
 *
 * @author nbrest
 */
@RestController
@RequestMapping(value = "/api/v1/ztv-rc")
public class ZtvPlayerController extends AbstractCrudController<ZtvPlayer, ZtvPlayerDto> {

  private final ZtvPlayerService ztvPlayerService;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public ZtvPlayerController(ZtvPlayerService ztvPlayerService) {
    this.ztvPlayerService = ztvPlayerService;
  }

  @Override
  @SuppressFBWarnings(value = "EI_EXPOSE_REP")
  public CrudService<ZtvPlayer, ZtvPlayerDto> getCrudService() {
    return ztvPlayerService;
  }

  /**
   * Creates a ztv Player.
   */
  @PostMapping(path = "/players")
  @Override
  public ResponseEntity<Long> create(@RequestBody ZtvPlayerDto dto) {
    return super.create(dto);
  }

  /**
   * Reads a ztv Player by it's id.
   */
  @GetMapping(path = "/players/{id}")
  @Override
  public ResponseEntity<ZtvPlayer> read(@PathVariable Long id) {
    return super.read(id);
  }

  /**
   * Reads all ztv Players registered in the application.
   */
  @GetMapping(path = "/players")
  @Override
  public ResponseEntity<List<ZtvPlayer>> readAll() {
    return super.readAll();
  }

  /**
   * Updates the ztv Player passed as a URL parameter.
   */
  @PutMapping(path = "/players/{id}")
  @Override
  public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody ZtvPlayerDto dto) {
    return super.update(id, dto);
  }

  /**
   * Deletes the ztv Player passed as a URL parameter.
   */
  @DeleteMapping(path = "/players/{id}")
  @Override
  public ResponseEntity<ZtvPlayer> delete(@PathVariable Long id) {
    return super.delete(id);
  }

  /**
   * Gets the ztv Player passed as a URL parameter.
   */
  @GetMapping(path = "/players/hostname/{hostname}")
  public ResponseEntity<ZtvPlayer> getByHostname(@PathVariable String hostname) {
    String hostnameSanitized = StringUtils.sanitize(hostname);
    ZtvPlayer ztvPlayer = ztvPlayerService.getByHostname(hostnameSanitized);
    return generateGetResponseEntity(ztvPlayer);
  }
}

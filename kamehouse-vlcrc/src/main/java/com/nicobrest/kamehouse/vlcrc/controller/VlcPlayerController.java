package com.nicobrest.kamehouse.vlcrc.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractCrudController;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import com.nicobrest.kamehouse.vlcrc.model.VlcPlayer;
import com.nicobrest.kamehouse.vlcrc.model.dto.VlcPlayerDto;
import com.nicobrest.kamehouse.vlcrc.service.VlcPlayerService;
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
 * Controller to manage the VLC Players registered in the application.
 *
 * @author nbrest
 */
@Controller
@RequestMapping(value = "/api/v1/vlc-rc")
public class VlcPlayerController extends AbstractCrudController<VlcPlayer, VlcPlayerDto> {

  private VlcPlayerService vlcPlayerService;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public VlcPlayerController(VlcPlayerService vlcPlayerService) {
    this.vlcPlayerService = vlcPlayerService;
  }

  @Override
  @SuppressFBWarnings(value = "EI_EXPOSE_REP")
  public CrudService<VlcPlayer, VlcPlayerDto> getCrudService() {
    return vlcPlayerService;
  }

  /**
   * Creates a VLC Player.
   */
  @PostMapping(path = "/players")
  @ResponseBody
  @Override
  public ResponseEntity<Long> create(@RequestBody VlcPlayerDto dto) {
    return super.create(dto);
  }

  /**
   * Reads a VLC Player by it's id.
   */
  @GetMapping(path = "/players/{id}")
  @ResponseBody
  @Override
  public ResponseEntity<VlcPlayer> read(@PathVariable Long id) {
    return super.read(id);
  }

  /**
   * Reads all VLC Players registered in the application.
   */
  @GetMapping(path = "/players")
  @ResponseBody
  @Override
  public ResponseEntity<List<VlcPlayer>> readAll() {
    return super.readAll();
  }

  /**
   * Updates the VLC Player passed as a URL parameter.
   */
  @PutMapping(path = "/players/{id}")
  @Override
  public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody VlcPlayerDto dto) {
    return super.update(id, dto);
  }

  /**
   * Deletes the VLC Player passed as a URL parameter.
   */
  @DeleteMapping(path = "/players/{id}")
  @ResponseBody
  @Override
  public ResponseEntity<VlcPlayer> delete(@PathVariable Long id) {
    return super.delete(id);
  }

  /**
   * Gets the VLC Player passed as a URL parameter.
   */
  @GetMapping(path = "/players/hostname/{hostname}")
  @ResponseBody
  public ResponseEntity<VlcPlayer> getByHostname(@PathVariable String hostname) {
    String hostnameSanitized = StringUtils.sanitizeInput(hostname);
    VlcPlayer vlcPlayer = vlcPlayerService.getByHostname(hostnameSanitized);
    return generateGetResponseEntity(vlcPlayer);
  }
}

package com.nicobrest.kamehouse.commons.controller;

import com.nicobrest.kamehouse.commons.model.KameHouseCache;
import com.nicobrest.kamehouse.commons.service.EhCacheService;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller to check the status and clear all the ehcaches.
 *
 * @author nbrest
 */
@Controller
@RequestMapping(value = "/api/v1/commons/ehcache")
public class EhCacheController extends AbstractController {

  private EhCacheService ehCacheService;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public EhCacheController(EhCacheService ehCacheService) {
    this.ehCacheService = ehCacheService;
  }

  /**
   * Returns the status of all the ehcaches or the cache specified as a parameter.
   */
  @GetMapping
  @ResponseBody
  public ResponseEntity<List<KameHouseCache>> read(
      @RequestParam(value = "name", required = false) String cacheName) {
    String cacheNameSanitized = StringUtils.sanitizeInput(cacheName);
    List<KameHouseCache> cacheList;
    if (!StringUtils.isEmpty(cacheNameSanitized)) {
      cacheList = new ArrayList<>();
      KameHouseCache kameHouseCache = ehCacheService.get(cacheNameSanitized);
      if (kameHouseCache != null) {
        cacheList.add(kameHouseCache);
      }
    } else {
      cacheList = ehCacheService.getAll();
    }
    return generateGetResponseEntity(cacheList, false);
  }

  /**
   * Clears all the ehcaches or the cache specified as a parameter.
   */
  @DeleteMapping
  public ResponseEntity<Void> clear(
      @RequestParam(value = "name", required = false) String cacheName) {
    String cacheNameSanitized = StringUtils.sanitizeInput(cacheName);
    if (!StringUtils.isEmpty(cacheNameSanitized)) {
      ehCacheService.clear(cacheNameSanitized);
    } else {
      ehCacheService.clearAll();
    }
    return EMPTY_SUCCESS_RESPONSE;
  }
}

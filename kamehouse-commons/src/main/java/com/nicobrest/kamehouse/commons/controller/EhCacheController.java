package com.nicobrest.kamehouse.commons.controller;

import com.nicobrest.kamehouse.commons.model.ApplicationCache;
import com.nicobrest.kamehouse.commons.service.EhCacheService;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
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

  @Autowired private EhCacheService ehCacheService;

  /** Returns the status of all the ehcaches or the cache specified as a parameter. */
  @GetMapping
  @ResponseBody
  public ResponseEntity<List<ApplicationCache>> read(
      @RequestParam(value = "name", required = false) String cacheName) {
    List<ApplicationCache> cacheList;
    if (!StringUtils.isBlank(cacheName)) {
      cacheList = new ArrayList<>();
      ApplicationCache applicationCache = ehCacheService.get(cacheName);
      if (applicationCache != null) {
        cacheList.add(applicationCache);
      }
    } else {
      cacheList = ehCacheService.getAll();
    }
    return generateGetResponseEntity(cacheList, false);
  }

  /** Clears all the ehcaches or the cache specified as a parameter. */
  @DeleteMapping
  public ResponseEntity<Void> clear(
      @RequestParam(value = "name", required = false) String cacheName) {
    if (!StringUtils.isBlank(cacheName)) {
      ehCacheService.clear(cacheName);
    } else {
      ehCacheService.clearAll();
    }
    return EMPTY_SUCCESS_RESPONSE;
  }
}

package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.model.ApplicationCache;
import com.nicobrest.kamehouse.admin.service.EhCacheService;
import com.nicobrest.kamehouse.main.controller.AbstractController;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller to check the status and clear all the ehcaches.
 * 
 * @author nbrest
 *
 */
@Controller
@RequestMapping(value = "/api/v1/admin/ehcache")
public class EhCacheController extends AbstractController {

  @Autowired
  private EhCacheService ehCacheService;

  /**
   * Returns the status of all the ehcaches or the cache specified as a parameter.
   */
  @GetMapping
  @ResponseBody
  public ResponseEntity<List<ApplicationCache>>
      read(@RequestParam(value = "name", required = false) String cacheName) {
    logger.trace("/api/v1/admin/ehcache?name={} (GET)", cacheName);
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

  /**
   * Clears all the ehcaches or the cache specified as a parameter.
   */
  @DeleteMapping
  public ResponseEntity<Void>
      clear(@RequestParam(value = "name", required = false) String cacheName) {
    logger.trace("/api/v1/admin/ehcache?name={} (DELETE)", cacheName);
    if (!StringUtils.isBlank(cacheName)) {
      ehCacheService.clear(cacheName);
    } else {
      ehCacheService.clearAll();
    }
    return EMPTY_SUCCESS_RESPONSE;
  }
}

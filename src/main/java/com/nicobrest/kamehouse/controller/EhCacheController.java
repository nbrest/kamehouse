package com.nicobrest.kamehouse.controller;

import com.nicobrest.kamehouse.service.EhCacheService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controller to check the status and clear all the ehcaches.
 * 
 * @author nbrest
 *
 */
@Controller
@RequestMapping(value = "/api/v1/admin/ehcache")
public class EhCacheController {

  @Autowired
  private EhCacheService ehCacheService;

  /**
   * Returns the status of all the ehcaches or the cache specified as a
   * parameter.
   */
  @RequestMapping(value = "/status", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<List<Map<String, Object>>> getCache(@RequestParam(value = "name",
      required = false) String cacheName) {

    List<Map<String, Object>> cacheList;
    if (!StringUtils.isBlank(cacheName)) {
      cacheList = new ArrayList<Map<String, Object>>();
      Map<String,Object> cache = ehCacheService.getCache(cacheName);
      if (!cache.isEmpty()) {
        cacheList.add(cache);
      }
    } else {
      cacheList = ehCacheService.getAllCaches();
    }
    return new ResponseEntity<List<Map<String, Object>>>(cacheList, HttpStatus.OK);
  }

  /**
   * Clears all the ehcaches or the cache specified as a parameter.
   */
  @RequestMapping(value = "/clear", method = RequestMethod.DELETE)
  public ResponseEntity<Void> clearCache(@RequestParam(value = "name",
      required = false) String cacheName) {

    if (!StringUtils.isBlank(cacheName)) {
      ehCacheService.clearCache(cacheName);
    } else {
      ehCacheService.clearAllCaches();
    }
    return new ResponseEntity<Void>(HttpStatus.OK);
  }
}

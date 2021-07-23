package com.nicobrest.kamehouse.commons.controller;

import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller class to get the status of the current module. Being able to deploy each module
 * individually, it might be useful to be able to get the current version and build time of each
 * module.
 * 
 * @author nbrest
 *
 */
@Controller
@RequestMapping(value = "/api/v1/commons/module")
public class ModuleStatusController extends AbstractController {

  /**
   * Gets the status of the current module.
   */
  @GetMapping(path = "/status")
  @ResponseBody
  public ResponseEntity<Map<String, String>> getModuleStatus() {
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("buildVersion", PropertiesUtils.getProperty("kamehouse.build.version"));
    responseBody.put("buildDate", PropertiesUtils.getProperty("kamehouse.build.date"));
    responseBody.put("server", PropertiesUtils.getHostname());
    responseBody.put("module", PropertiesUtils.getModuleName());
    return generateGetResponseEntity(responseBody);
  }
}

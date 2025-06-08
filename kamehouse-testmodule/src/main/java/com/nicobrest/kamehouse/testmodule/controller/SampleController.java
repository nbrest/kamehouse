package com.nicobrest.kamehouse.testmodule.controller;

import com.nicobrest.kamehouse.commons.utils.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Sample ModelAndView controller that uses the test module views.
 */
@Controller
@RequestMapping(value = "/api/v1/testmodule/sample")
public class SampleController {

  /**
   * Returns the ModelAndView object for the test endpoint.
   */
  @GetMapping(path = "/dragonball/model-and-view")
  public ModelAndView getModelAndView(@RequestParam(value = "name", required = false,
      defaultValue = "Goku") String name) {
    String nameSanitized = StringUtils.sanitize(name);
    String message = "mada mada dane";
    ModelAndView mv = new ModelAndView("/jsp/test-module/dragonball/model-and-view");
    mv.addObject("message", message);
    mv.addObject("name", nameSanitized);
    return mv;
  }
}

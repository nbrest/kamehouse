package com.nicobrest.kamehouse.ui.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Sample ModelAndView controller that uses the test module views.
 */
@Controller
@RequestMapping(value = "/ui/sample")
public class SampleController {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * Returns the ModelAndView object for the test endpoint.
   */
  @GetMapping(path = "/dragonball/model-and-view")
  public ModelAndView getModelAndView(@RequestParam(value = "name", required = false,
      defaultValue = "Goku") String name) {
    logger.trace("/dragonball/model-and-view?name=[name] (GET)");
    String message = "dragonball ModelAndView message attribute";
    ModelAndView mv = new ModelAndView("/test-module/jsp/dragonball/model-and-view");
    mv.addObject("message", message);
    mv.addObject("name", name);
    return mv;
  }
}

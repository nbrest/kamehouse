package ar.com.nicobrest.mobileinspections.controller;
 
import ar.com.nicobrest.mobileinspections.exception.DragonBallNotFoundException;
import ar.com.nicobrest.mobileinspections.model.DragonBallUser;
import ar.com.nicobrest.mobileinspections.service.DragonBallUserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
 
import java.util.List; 
 
/**
 * @since v0.02 
 * @author nbrest
 * 
 *         Controller class for the dragonball test endpoints
 */
@Controller
@RequestMapping(value = "/dragonball")
public class DragonBallController {

  private static final Logger LOGGER = LoggerFactory.getLogger(DragonBallController.class);

  @Autowired
  private DragonBallUserService dragonBallUserService;

  /**
   * @since v0.03 
   * @author nbrest
   * @param dragonBallUserService
   *    
   *      Getters and Setters
   */
  public void setDragonBallUserService(DragonBallUserService dragonBallUserService) {
    
    this.dragonBallUserService = dragonBallUserService;
  }
  
  /**
   * @since v0.03 
   * @author nbrest
   * @return DragonBallUserService
   * 
   *      Getters and Setters
   */
  public DragonBallUserService getDragonBallUserService() {
    
    return this.dragonBallUserService;
  }
  
  /** 
   * @since v0.02 
   * @author nbrest
   * @param name : Nombre del usuario que visita el sitio
   * @return ModelAndView
   * 
   *         Returns the ModelAndView object for the test endpoint
   *         /dragonball/modelAndView
   */
  @RequestMapping(value = "/modelAndView", method = RequestMethod.GET)
  public ModelAndView getModelAndView(
      @RequestParam(value = "name", required = false, defaultValue = "Goku") String name) {

    LOGGER.info("In controller /dragonball/modelAndView");

    String message = "message: dragonball ModelAndView!";

    ModelAndView mv = new ModelAndView("dragonball/modelAndView");
    mv.addObject("message", message);
    mv.addObject("name", name);
    
    LOGGER.info("In controller /dragonball/modelAndView Model keys: " 
        + mv.getModel().keySet().toString());
    LOGGER.info("In controller /dragonball/modelAndView Model values: " 
        + mv.getModel().values().toString());
    
    return mv;
  }

  /**
   * @since v0.02 
   * @author nbrest
   * @return DragonBallUser list
   * 
   *         Returns the DragonBallUser object in json format for the test
   *         endpoint /dragonball/json
   *         
   * @throws Exception : General exception
   */
  @RequestMapping(value = "/json", method = RequestMethod.GET)
  @ResponseBody
  public List<DragonBallUser> getJson(
      @RequestParam(value = "action", required = false, defaultValue = "goku") 
      String action) throws Exception {

    LOGGER.info("In controller /dragonball/json");
 
    switch (action) {
      case "DragonBallNotFoundException":
        throw new DragonBallNotFoundException("*** DragonBallNotFoundException in getJson ***");
        // break;
      case "RuntimeException":
        throw new RuntimeException("*** RuntimeException in getJson ***");
        // break;
      case "Exception":
        throw new Exception("*** Exception in getJson ***");
        // break;
      default:
        break;
    }

    return dragonBallUserService.getAllDragonBallUsers();
  }

}

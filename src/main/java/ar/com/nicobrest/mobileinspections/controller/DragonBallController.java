package ar.com.nicobrest.mobileinspections.controller;
 
import ar.com.nicobrest.mobileinspections.exception.DragonBallUserAlreadyExistsException;
import ar.com.nicobrest.mobileinspections.exception.DragonBallUserNotFoundException;
import ar.com.nicobrest.mobileinspections.model.DragonBallUser;
import ar.com.nicobrest.mobileinspections.service.DragonBallUserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
 
import java.util.List; 
 
/**
 *         Controller class for the dragonball test endpoints
 *         
 * @since v0.02 
 * @author nbrest
 */
@Controller
@RequestMapping(value = "/dragonball")
public class DragonBallController {

  private static final Logger LOGGER = LoggerFactory.getLogger(DragonBallController.class);

  @Autowired
  private DragonBallUserService dragonBallUserService;

  /**
   *      Getters and Setters
   *      
   * @since v0.03 
   * @author nbrest
   * @param dragonBallUserService DragonBallUserService
   */
  public void setDragonBallUserService(DragonBallUserService dragonBallUserService) {
    
    this.dragonBallUserService = dragonBallUserService;
  }
  
  /**
   *      Getters and Setters
   *      
   * @since v0.03 
   * @author nbrest
   * @return DragonBallUserService
   */
  public DragonBallUserService getDragonBallUserService() {
    
    return this.dragonBallUserService;
  }
  
  /** 
   *         Returns the ModelAndView object for the test endpoint
   *         /dragonball/modelAndView
   *         
   * @since v0.02 
   * @author nbrest
   * @param name : Nombre del usuario que visita el sitio
   * @return ModelAndView
   */
  @RequestMapping(value = "/modelAndView", method = RequestMethod.GET)
  public ModelAndView getModelAndView(
      @RequestParam(value = "name", required = false, defaultValue = "Goku") String name) {

    LOGGER.info("In controller /dragonball/modelAndView (GET)");

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
   *         Returns all DragonBallUsers in json format for the test
   *         endpoint /dragonball/users
   *         
   * @since v0.02 
   * @author nbrest
   * @return DragonBallUser list
   * @throws Exception : General exception
   */
  @RequestMapping(value = "/users", method = RequestMethod.GET)
  @ResponseBody
  public List<DragonBallUser> getUsers(
      @RequestParam(value = "action", required = false, defaultValue = "goku") 
      String action) throws Exception {

    LOGGER.info("In controller /dragonball/users (GET)");
 
    switch (action) {
      case "DragonBallUserNotFoundException":
        throw new DragonBallUserNotFoundException(
            "*** DragonBallUserNotFoundException in getUsers ***");
        // break;
      case "RuntimeException":
        throw new RuntimeException("*** RuntimeException in getUsers ***");
        // break;
      case "Exception":
        throw new Exception("*** Exception in getUsers ***");
        // break;
      default:
        break;
    }

    return dragonBallUserService.getAllDragonBallUsers();
  }

  /**
   *      Creates a new DragonBallUser in the repository
   *      
   * @since v0.03
   * @author nbrest
   * @param dragonBallUser User to add to the repository
   * @return DragonBallUser
   * @throws DragonBallUserAlreadyExistsException User defined exception
   * @throws DragonBallUserNotFoundException User defined exception
   */
  @RequestMapping(value = "/users", method = RequestMethod.POST)
  @ResponseBody
  public DragonBallUser postUser(@RequestBody DragonBallUser dragonBallUser) 
      throws DragonBallUserAlreadyExistsException, DragonBallUserNotFoundException {
    
    LOGGER.info("In controller /dragonball/users (POST)");
    
    dragonBallUserService.createDragonBallUser(dragonBallUser);
    
    return dragonBallUserService.getDragonBallUser(dragonBallUser.getUsername());
  }
}

package ar.com.nicobrest.mobileinspections.controller;
 
import ar.com.nicobrest.mobileinspections.exception.HelloWorldNotFoundException;
import ar.com.nicobrest.mobileinspections.model.HelloWorldUser;
import ar.com.nicobrest.mobileinspections.service.HelloWorldUserService;

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
 *         Controller class for the helloWorld test endpoints
 */
@Controller
@RequestMapping(value = "/helloWorld")
public class HelloWorldController {

  private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldController.class);

  @Autowired
  private HelloWorldUserService helloWorldUserService;

  /**
   * @since v0.03 
   * @author nbrest
   * @param helloWorldUserService
   *    
   *      Getters and Setters
   */
  public void setHelloWorldUserService(HelloWorldUserService helloWorldUserService) {
    
    this.helloWorldUserService = helloWorldUserService;
  }
  
  /**
   * @since v0.03 
   * @author nbrest
   * @return HelloWorldUserService
   * 
   *      Getters and Setters
   */
  public HelloWorldUserService getHelloWorldUserService() {
    
    return this.helloWorldUserService;
  }
  
  /** 
   * @since v0.02 
   * @author nbrest
   * @param name : Nombre del usuario que visita el sitio
   * @return ModelAndView
   * 
   *         Returns the ModelAndView object for the test endpoint
   *         /helloWorld/modelAndView
   */
  @RequestMapping(value = "/modelAndView", method = RequestMethod.GET)
  public ModelAndView getModelAndView(
      @RequestParam(value = "name", required = false, defaultValue = "Goku") String name) {

    LOGGER.info("In controller /helloWorld/modelAndView");

    String message = "message: HelloWorld ModelAndView!";

    ModelAndView mv = new ModelAndView("helloWorld/modelAndView");
    mv.addObject("message", message);
    mv.addObject("name", name);
    
    LOGGER.info("In controller /helloWorld/modelAndView Model keys: " 
        + mv.getModel().keySet().toString());
    LOGGER.info("In controller /helloWorld/modelAndView Model values: " 
        + mv.getModel().values().toString());
    
    return mv;
  }

  /**
   * @since v0.02 
   * @author nbrest
   * @return HelloWorldUser
   * 
   *         Returns the HelloWorldUser object in json format for the test
   *         endpoint /helloWorld/json
   *         
   * @throws Exception : General exception
   */
  @RequestMapping(value = "/json", method = RequestMethod.GET)
  @ResponseBody
  public List<HelloWorldUser> getJson(
      @RequestParam(value = "action", required = false, defaultValue = "goku") 
      String action) throws Exception {

    LOGGER.info("In controller /helloWorld/json");
 
    switch (action) {
      case "HelloWorldNotFoundException":
        throw new HelloWorldNotFoundException("*** HelloWorldNotFoundException in getJson ***");
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

    return helloWorldUserService.getAllHelloWorldUsers();
  }

}

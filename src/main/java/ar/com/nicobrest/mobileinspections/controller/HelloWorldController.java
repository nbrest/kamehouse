package ar.com.nicobrest.mobileinspections.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import ar.com.nicobrest.mobileinspections.model.HelloWorldUser; 

/**
 *
 * @author nbrest
 * 
 * Controller class for the helloWorld test endpoints
 */
@Controller
@RequestMapping(value = "/helloWorld")
public class HelloWorldController {

	private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldController.class);
	
	/**
	 * 
	 * @param name
	 * @return ModelAndView
	 * 
	 * Returns the ModelAndView object for the test endpoint /helloWorld/modelAndView
	 */
	@RequestMapping(value = "/modelAndView", method = RequestMethod.GET)
	public ModelAndView getModelAndView(
			@RequestParam(value = "name", required = false, defaultValue = "Goku") String name) {
 
		LOGGER.info("In controller /helloWorld/modelAndView");
		
		String message = "message: HelloWorld ModelAndView!";

		ModelAndView mv = new ModelAndView("helloWorld_modelAndView");
		mv.addObject("message", message);
		mv.addObject("name", name);

		return mv;
	}

	/**
	 * 
	 * @return HelloWorldUser
	 * 
	 * Returns the HelloWorldUser object in json format for the test endpoint /helloWorld/json
	 */
	@RequestMapping(value = "/json", method = RequestMethod.GET)
	public @ResponseBody HelloWorldUser getJson() {

		LOGGER.info("In controller /helloWorld/json");
		
		HelloWorldUser helloWorldUser = new HelloWorldUser();
		helloWorldUser.setAge(21);
		helloWorldUser.setEmail("goku@dbz.com");
		helloWorldUser.setUsername("goku"); 

		return helloWorldUser;
	}

}

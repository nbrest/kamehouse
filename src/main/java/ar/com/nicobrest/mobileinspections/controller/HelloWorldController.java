package ar.com.nicobrest.mobileinspections.controller;
   
import java.util.Date;
 
import org.springframework.stereotype.Controller;  
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import ar.com.nicobrest.mobileinspections.model.HelloWorldUser;
 
 
@Controller
@RequestMapping(value = "/helloWorld")
public class HelloWorldController {

	@RequestMapping(value = "/modelAndView", method = RequestMethod.GET)
	public ModelAndView getModelAndView(
			@RequestParam(value = "name", required = false, defaultValue = "Goku") String name) {

		System.out.println(new Date() + ": In controller /helloWorld/modelAndView");

		String message = "message: HelloWorld ModelAndView!";		
 
		ModelAndView mv = new ModelAndView("helloWorld_modelAndView");
		mv.addObject("message", message);
		mv.addObject("name", name);
		
		return mv;
	}
 
	  @RequestMapping(value = "/json",  method = RequestMethod.GET)
	  public @ResponseBody HelloWorldUser getJson() {
	      
		  HelloWorldUser helloWorldUser = new HelloWorldUser();
		  helloWorldUser.setAge(21);
		  helloWorldUser.setEmail("goku@dbz.com");
		  helloWorldUser.setUsername("goku");
		  
		  System.out.println(new Date() + ": In controller /helloWorld/json");
		  
	      return helloWorldUser;
	  }

}

package web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping
@Controller
public class HomeController {
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String home(){
		return "home";
	}
	
	@RequestMapping(value="/hellows", method=RequestMethod.GET)
	public String hello(){
		return "hello";
	}
	
}

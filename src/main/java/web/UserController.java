package web;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import data.User;
import data.UserRepository;

import org.springframework.web.bind.annotation.ModelAttribute;

import com.google.gson.Gson;

@Controller
public class UserController {
	
	@Autowired
	private SessionRegistry sessionRegistry;
	
	@Autowired
	UserRepository userRepository;
	
	@RequestMapping(value="/register", method=RequestMethod.GET)
	public String showRegistrationForm( Model model){
		model.addAttribute("user", new User());
		return "registerForm";
	}
	
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String showLoginForm( Model model){
		return "loginForm";
	}
	
	@RequestMapping(value="/logout", method=RequestMethod.GET)
	public String logoutRequest(HttpSession session){
		if(session.getAttribute("user") != null){
			session.invalidate();
		}
		return "home";
	}
	
	@RequestMapping(value="/cam", method=RequestMethod.GET)
	public String camSession(HttpSession session){
		if(session.getAttribute("user") != null){
			session.invalidate();
		}
		return "cam";
	}
	
	@RequestMapping(value="/cam/{username}", method=RequestMethod.GET)
    public String getForDay(@PathVariable("username") String username, Model model, Principal principal) {
		model.addAttribute("id", username);
		model.addAttribute("yourid", principal.getName());
		return "cam";
    }
	
	@RequestMapping(value="/video/{id}", method=RequestMethod.GET)
    public String calling(@PathVariable("id") String id, Model model, Principal principal) {
		model.addAttribute("id", id);
		model.addAttribute("yourid", principal.getName());
		return "video";
    }
	
	@RequestMapping(value="/profile/{username}", method=RequestMethod.GET)
	public String showProfile(@PathVariable("username") String userNick, Model model){
		List<Object> principals = sessionRegistry.getAllPrincipals();
		List<String> usersNamesList = new ArrayList<String>();
		for (Object principal: principals) {
		    if (principal instanceof UserDetails) {
		        usersNamesList.add(((UserDetails) principal).getUsername());
		    }
		}
		User user = userRepository.findByUsername(userNick);
		model.addAttribute("user", user);
		model.addAttribute("status", usersNamesList.contains(userNick));
		return "profile";
	}
	
	@RequestMapping(value="/register", method=RequestMethod.POST)
	public String processRegistration(
			@ModelAttribute(value="user") @Valid User user, 
			BindingResult bindingResult, RedirectAttributes model) throws IllegalStateException, IOException{
		if(!user.getConfirmPassword().equals(user.getPassword()))
			bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "confirm password not match");
		
		if(userRepository.findByUsername(user.getUsername()) != null)
			bindingResult.rejectValue("username", "error.user", "this username already in use");
		
		if(userRepository.findByEmail(user.getEmail()) != null)
			bindingResult.rejectValue("email", "error.user", "this email already in use");

		if(bindingResult.hasErrors())
			return "registerForm";
		
		userRepository.save(user);
		model.addFlashAttribute(user);		
		return "registerSucceed";
	}
	
}
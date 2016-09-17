package web.controllers;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import data.entities.User;
import data.entities.UserProfile;
import data.entities.UserRole;

import org.springframework.web.bind.annotation.ModelAttribute;

import repositories.PeerConnectionRepository;
import repositories.UserProfileRepository;
import repositories.UserRepository;
import repositories.UserRoleRepository;

@Controller
public class UserController {
	
	@Autowired
	private PeerConnectionRepository paidConversationRepository;
	@Autowired
	private SessionRegistry sessionRegistry;
	@Autowired
	UserRepository userRepository;
	@Autowired
	UserRoleRepository userRoleRepository;
	@Autowired
	UserProfileRepository userProfileRepository;
	
	@RequestMapping(value="/example", method=RequestMethod.GET)
	public String example( Model model){
		return "example";
	}
	
	@RequestMapping(value="/register", method=RequestMethod.GET)
	public String showRegistrationForm( Model model){
		model.addAttribute("user", new User());
		return "register";
	}
	
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String showLoginForm( Model model){
		return "login";
	}
	
	@RequestMapping(value="/logout", method=RequestMethod.GET)
	public String logoutRequest(HttpSession session, Principal principal){
		sessionRegistry.removeSessionInformation(session.getId());
		if(session.getAttribute("user") != null){
			session.invalidate();
		}
		return "home";
	}
	
	@RequestMapping(value="/user/{username}", method=RequestMethod.GET)
	@ResponseBody
	public User getUser(@PathVariable("username") String username, Principal principal, Model model){
		if (principal.getName().equals(username)){
			User user = userRepository.findByUsername(username);
			return user;
		} else {
			return null;
		}
	}
	
	@RequestMapping(value="/exit", method=RequestMethod.GET)
	@ResponseBody
	public String exit(Principal principal, Model model, HttpSession session){
		if (principal == null)
			return "error";
		else {
			sessionRegistry.removeSessionInformation(session.getId());
			if(session.getAttribute("user") != null){
				session.invalidate();
			}
			return "costam";
		}
	}
	
	@RequestMapping(value="/username", method=RequestMethod.GET)
	@ResponseBody
	public String getUsername(Principal principal, Model model){
		if(principal != null)
			return principal.getName();
		else
			return "err";
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
			return "register";
		
		user.setCoins((long) 0);
		user.setEnabled(true);
		userRepository.save(user);
		UserProfile userProfile = new UserProfile(user);
		userProfileRepository.save(userProfile);
		UserRole userRole = new UserRole(user.getUsername(),"ROLE_USER");
		userRoleRepository.save(userRole);
		model.addFlashAttribute(user);		
		return "login";
	}
	
}
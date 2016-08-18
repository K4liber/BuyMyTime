package web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import data.User;
import data.UserLoginModel;
import data.UserRepository;

import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class UserController {
	
	@Autowired
	UserRepository userRepository;
	
	@RequestMapping(value="/register", method=RequestMethod.GET)
	public String showRegistrationForm( Model model){
		model.addAttribute("user", new User());
		return "registerForm";
	}
	
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String showLoginForm( Model model){
		model.addAttribute("userLoginModel", new UserLoginModel());
		return "loginForm";
	}
	
	@RequestMapping(value="/logout", method=RequestMethod.GET)
	public String logoutRequest(HttpSession session){
		if(session.getAttribute("user") != null){
			session.removeAttribute("user");
		}
		return "home";
	}
	
	@RequestMapping(value="/profile/{userNick}", method=RequestMethod.GET)
	public String showProfile(@PathVariable("userNick") String userNick, Model model){
		User user = userRepository.findByNick(userNick);
		model.addAttribute("user", user);
		return "profile";
	}
	
	@RequestMapping(value="/register", method=RequestMethod.POST)
	public String processRegistration(
			@ModelAttribute(value="user") @Valid User user, 
			BindingResult bindingResult, RedirectAttributes model) throws IllegalStateException, IOException{
		if(!user.getConfirmPassword().equals(user.getPassword()))
			bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "confirm password not match");
		
		if(userRepository.findByNick(user.getNick()) != null)
			bindingResult.rejectValue("nick", "error.user", "this nick already in use");
		
		if(userRepository.findByEmail(user.getEmail()) != null)
			bindingResult.rejectValue("email", "error.user", "this email already in use");

		if(bindingResult.hasErrors())
			return "registerForm";
		
		userRepository.save(user);
		model.addFlashAttribute(user);		
		return "registerSucceed";
	}
	
	@RequestMapping(value = { "/login" }, method = RequestMethod.POST)
	public String login(HttpServletRequest request,@ModelAttribute(value="userLoginModel") @Valid UserLoginModel userLoginModel,
			BindingResult bindingResult, RedirectAttributes model) throws Exception {
		HttpSession session = request.getSession();
		User user = userRepository.findByNick(userLoginModel.getNick());
		if(user == null )
			bindingResult.rejectValue("nick", "error.userLoginModel", "nick not found");
		
		if(user != null && !user.getPassword().equals(userLoginModel.getPassword()))
			bindingResult.rejectValue("password", "error.userLoginModel", "wrong password");
		
		if(bindingResult.hasErrors())
			return "loginForm";
		
		session.setAttribute("user", user);
	    return "home";
	}
	
}
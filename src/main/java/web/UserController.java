package web;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import data.entities.Contact;
import data.entities.User;
import data.messages.ChatMessage;
import data.views.UserProfile;

import org.springframework.web.bind.annotation.ModelAttribute;

import repositories.ChatMessageRepository;
import repositories.ContactRepository;
import repositories.UserRepository;

import com.google.gson.Gson;

@Controller
public class UserController {
	
	@Autowired
	private SessionRegistry sessionRegistry;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ContactRepository contactRepository;
	
	@Autowired
	ChatMessageRepository chatMessageRepository;
	
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
	
	@RequestMapping(value="/cam/{username}", method=RequestMethod.GET)
    public String getForDay(@PathVariable("username") String username, Model model, Principal principal) {
		model.addAttribute("id", username);
		model.addAttribute("yourid", principal.getName());
		return "cam";
    }
	
	@RequestMapping(value="/stream/{username}", method=RequestMethod.GET)
    public String startStream(@PathVariable("username") String username, Model model, Principal principal) {
		model.addAttribute("id", username);
		model.addAttribute("yourid", principal.getName());
		return "stream";
    }
	
	@RequestMapping(value="/video/{id}", method=RequestMethod.GET)
    public String calling(@PathVariable("id") String id, Model model, Principal principal) {
		model.addAttribute("id", id);
		model.addAttribute("yourid", principal.getName());
		return "video";
    }
	
	@RequestMapping(value="/profile/{username}", method=RequestMethod.GET)
	@ResponseBody
	public UserProfile showProfile(@PathVariable("username") String userNick, Model model){
		List<Object> principals = sessionRegistry.getAllPrincipals();
		List<String> usersNamesList = new ArrayList<String>();
		for (Object principal: principals) {
		    if (principal instanceof UserDetails) {
		        usersNamesList.add(((UserDetails) principal).getUsername());
		    }
		}
		User user = userRepository.findByUsername(userNick);
		UserProfile profile = new UserProfile(user);
		profile.setStatus(usersNamesList.contains(userNick));
		return profile;
	}
	
	@RequestMapping(value="/username", method=RequestMethod.GET)
	@ResponseBody
	public String getUsername(Principal principal, Model model){
		return principal.getName();
	}
	
	@RequestMapping(value="/contacts", method=RequestMethod.GET)
	@ResponseBody
	public List<Contact> getContacts(Principal principal, Model model){
		String username = principal.getName();
		List<Contact> list = contactRepository.findAllByUsername(username);
		return list;
	}
	
	@RequestMapping(value="/addContact/{contactUsername}", method=RequestMethod.GET)
	@ResponseBody
	public String addContact(Principal principal,
			@PathVariable("contactUsername") String contactUsername, Model model){
		String username = principal.getName();
		Contact exist = contactRepository.findByUsernameAndContactUsername(username, contactUsername);
		if(exist != null)
			return "exist";
	    Contact newContact = new Contact();
	    newContact.setUsername(username);
	    newContact.setContactUsername(contactUsername);
		contactRepository.save(newContact);
		Contact saved = contactRepository.findByUsernameAndContactUsername(username, contactUsername);
		if(saved != null)
			return "success";
		else
			return "error";
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
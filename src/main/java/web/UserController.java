package web;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import data.entities.Contact;
import data.entities.User;
import data.entities.UserRole;
import data.messages.ChatMessage;
import data.views.UserContact;
import data.views.UserMessage;
import data.views.UserProfile;

import org.springframework.web.bind.annotation.ModelAttribute;

import repositories.ChatMessageRepository;
import repositories.ContactRepository;
import repositories.UserProfileRepository;
import repositories.UserRepository;
import repositories.UserRoleRepository;

@Controller
public class UserController {
	
	@Autowired
	private SessionRegistry sessionRegistry;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	UserRoleRepository userRoleRepository;
	
	@Autowired
	UserProfileRepository userProfileRepository;
	
	@Autowired
	ContactRepository contactRepository;
	
	@Autowired
	ChatMessageRepository chatMessageRepository;
	
	@RequestMapping(value="/register", method=RequestMethod.GET)
	public String showRegistrationForm( Model model){
		model.addAttribute("user", new User());
		return "register";
	}
	
	@RequestMapping(value="/edit", method=RequestMethod.GET)
	public String showEditForm( Model model, Principal principal){
		UserProfile userProfile = userProfileRepository.findByUsername(principal.getName());
		model.addAttribute("username", principal.getName());
		model.addAttribute("userProfile", userProfile);
		return "edit";
	}
	
	@RequestMapping(value="/edit", method=RequestMethod.POST)
	public String handleFileUpload(@RequestParam("file") MultipartFile file,
        Principal principal, HttpServletRequest request) throws IllegalStateException, IOException {
		System.out.println(file.getOriginalFilename());
		String realPathtoUploads = request.getSession().getServletContext().getRealPath("/resources/img");
		System.out.println(realPathtoUploads);
        if(! new File(realPathtoUploads).exists())
        {
            new File(realPathtoUploads).mkdir();
        }
        String fileName = principal.getName() + "." + file.getContentType().split("/")[1];
        String filePath = realPathtoUploads + "\\" + fileName;
        File dest = new File(filePath);
        file.transferTo(dest);
        UserProfile userProfile = userProfileRepository.findByUsername(principal.getName());
        userProfile.setImageName(fileName);
        userProfileRepository.save(userProfile);
		return "home";
	}
	
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String showLoginForm( Model model){
		return "login";
	}
	
	@RequestMapping(value="/logout", method=RequestMethod.GET)
	public String logoutRequest(HttpSession session){
		sessionRegistry.removeSessionInformation(session.getId());
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
		UserProfile userProfile = userProfileRepository.findByUsername(userNick);
		userProfile.setStatus(isOnline(userNick));
		return userProfile;
	}
	
	@RequestMapping(value="/user/{username}", method=RequestMethod.GET)
	@ResponseBody
	public User getUser(@PathVariable("username") String username, Principal principal, Model model){
		if(principal.getName().equals(username)){
		User user = userRepository.findByUsername(username);
		return user;
		}else{
			return null;
		}
	}
	
	@RequestMapping(value="/contact/{contactUsername}", method=RequestMethod.GET)
	@ResponseBody
	public UserContact showContact(Principal principal, @PathVariable("contactUsername") String contactUsername, Model model){
		String username = principal.getName();
		User contactUser = userRepository.findByUsername(contactUsername);
		UserProfile userProfile = new UserProfile(contactUser);
		userProfile.setStatus(isOnline(contactUsername));
		List<ChatMessage> chatMessagesSended = chatMessageRepository.findAllBySendFromAndSendTo(username, contactUsername);
		List<ChatMessage> chatMessagesReceived = chatMessageRepository.findAllBySendFromAndSendTo(contactUsername, username);
		List<UserMessage> userMessages = groupingMessages(chatMessagesSended, chatMessagesReceived, username);
		UserContact userContact = new UserContact(userProfile, userMessages);
		return userContact;
	}
	
	private List<UserMessage> groupingMessages(
			List<ChatMessage> chatMessagesSended,
			List<ChatMessage> chatMessagesReceived, String username) {
		List<ChatMessage> chatMessages = new ArrayList<ChatMessage>(chatMessagesSended);
		chatMessages.addAll(chatMessagesReceived);
		Collections.sort(chatMessages, new Comparator<ChatMessage>() {
		    @Override
		    public int compare(ChatMessage o1, ChatMessage o2) {
		        return o1.getDateTime().compareTo(o2.getDateTime());
		    }
		});
		List<UserMessage> userMessages = new ArrayList<UserMessage>();
		for(Iterator<ChatMessage> i = chatMessages.iterator(); i.hasNext(); ) {
		    ChatMessage chatMessage = i.next();
		    UserMessage userMessage = new UserMessage(chatMessage, username);
		    userMessages.add(userMessage);
		    System.out.println(userMessage.getMessage());
		}
		return userMessages;
	}

	@RequestMapping(value="/username", method=RequestMethod.GET)
	@ResponseBody
	public String getUsername(Principal principal, Model model){
		return principal.getName();
	}
	
	@RequestMapping(value="/contacts", method=RequestMethod.GET)
	@ResponseBody
	public List<UserProfile> getContacts(Principal principal, Model model){
		String username = principal.getName();
		List<UserProfile> userProfileList = new ArrayList<UserProfile>();
		List<Contact> contactList = contactRepository.findAllByUsername(username);
		for(Iterator<Contact> i = contactList.iterator(); i.hasNext(); ) {
		    Contact item = i.next();
		    String contactUsername = item.getContactUsername();
		    User user = userRepository.findByUsername(contactUsername);
		    UserProfile profile = new UserProfile(user);
		    profile.setStatus(isOnline(contactUsername));
		    userProfileList.add(profile);
		}	
		return userProfileList;
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
			return "register";
		
		user.setEnabled(true);
		userRepository.save(user);
		UserProfile userProfile = new UserProfile(user);
		userProfileRepository.save(userProfile);
		UserRole userRole = new UserRole(user.getUsername(),"ROLE_USER");
		userRoleRepository.save(userRole);
		model.addFlashAttribute(user);		
		return "login";
	}
	
	private boolean isOnline(String username){
		List<Object> principals = sessionRegistry.getAllPrincipals();
		List<String> usersNamesList = new ArrayList<String>();
		for (Object principal: principals) {
		    if (principal instanceof UserDetails) {
		        usersNamesList.add(((UserDetails) principal).getUsername());
		    }
		}
		return usersNamesList.contains(username);
	}
	
}
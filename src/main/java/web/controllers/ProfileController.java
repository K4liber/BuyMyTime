package web.controllers;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import repositories.ChatMessageRepository;
import repositories.ContactRepository;
import repositories.UserProfileRepository;
import repositories.UserRepository;
import data.entities.Contact;
import data.entities.User;
import data.entities.UserProfile;
import data.messages.ChatMessage;
import data.views.UserContact;
import data.views.UserMessage;

@Controller
public class ProfileController {
	
	@Autowired
	UserRepository userRepository;
	@Autowired
	UserProfileRepository userProfileRepository;
	@Autowired
	ContactRepository contactRepository;
	@Autowired
	private SessionRegistry sessionRegistry;
	@Autowired
	ChatMessageRepository chatMessageRepository;
	
	@RequestMapping(value="/contacts", method=RequestMethod.GET)
	@ResponseBody
	public List<UserProfile> getContacts(Principal principal, Model model){
		String username = principal.getName();
		List<UserProfile> userProfileList = new ArrayList<UserProfile>();
		List<Contact> contactList = contactRepository.findAllByUsername(username);
		List<Contact> contactList2 = contactRepository.findAllByContactUsername(username);
		for(Iterator<Contact> i = contactList.iterator(); i.hasNext(); ) {
		    Contact item = i.next();
		    String contactUsername = item.getContactUsername();
		    User user = userRepository.findByUsername(contactUsername);
		    UserProfile profile = new UserProfile(user);
		    profile.setStatus(isOnline(contactUsername));
		    userProfileList.add(profile);
		}	
		for(Iterator<Contact> i = contactList2.iterator(); i.hasNext(); ) {
		    Contact item = i.next();
		    String contactUsername = item.getUsername();
		    User user = userRepository.findByUsername(contactUsername);
		    UserProfile profile = new UserProfile(user);
		    profile.setStatus(isOnline(contactUsername));
		    userProfileList.add(profile);
		}	
		return userProfileList;
	}
	
	@RequestMapping(value="/profile/{username}", method=RequestMethod.GET)
	@ResponseBody
	public UserProfile showProfile(@PathVariable("username") String userNick, Model model){
		UserProfile userProfile = userProfileRepository.findByUsername(userNick);
		userProfile.setStatus(isOnline(userNick));
		return userProfile;
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
	
	@RequestMapping(value="/addContact/{contactUsername}", method=RequestMethod.GET)
	@ResponseBody
	public String addContact(Principal principal,
			@PathVariable("contactUsername") String contactUsername, Model model){
		String username = principal.getName();
		Contact exist = contactRepository.findByUsernameAndContactUsername(username, contactUsername);
		Contact exist2 = contactRepository.findByUsernameAndContactUsername(contactUsername, username);
		boolean contactExist = (exist != null) || (exist2 != null);
		if(contactExist)
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
	
    public boolean isOnline(String username){
		List<Object> principals = sessionRegistry.getAllPrincipals();
		List<String> usersNamesList = new ArrayList<String>();
		for (Object principal: principals) {
		    if (principal instanceof UserDetails) {
		        usersNamesList.add(((UserDetails) principal).getUsername());
		    }
		}
		return usersNamesList.contains(username);
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
  
}

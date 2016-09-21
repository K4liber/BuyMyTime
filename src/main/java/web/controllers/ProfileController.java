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
import data.entities.ChatMessage;
import data.entities.Contact;
import data.entities.User;
import data.entities.UserProfile;
import data.views.ContactInfo;
import data.views.UserContact;

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
	
	@RequestMapping(value="/messagesCount", method=RequestMethod.GET)
	@ResponseBody
	public Long getMessagesCount(Principal principal){
		if(principal == null)
			return null;
		List<ChatMessage> chatMessages = chatMessageRepository
				.findAllBySendToAndOpen(principal.getName(), false);
		return (long) chatMessages.size();
	}
	
	@RequestMapping(value="/messages", method=RequestMethod.GET)
	@ResponseBody
	public List<ContactInfo> getContactsInfo(Principal principal, Model model){
		if(principal == null)
			return null;
		String username = principal.getName();
		List<ContactInfo> contactInfoList = new ArrayList<ContactInfo>();
		List<String> helperList = new ArrayList<String>();
		List<ChatMessage> chatMessages = chatMessageRepository
				.findAllBySendFrom(username);
		chatMessages.addAll(chatMessageRepository.findAllBySendTo(username));
		for (Iterator<ChatMessage> i = chatMessages.iterator(); i.hasNext(); ) {
			ChatMessage item = i.next();
			if (username.equals(item.getSendFrom())){
				if(!helperList.contains(item.getSendTo())){
					ContactInfo contact = new ContactInfo(item.getSendTo(), item.isOpen());
					contactInfoList.add(contact);
					helperList.add(item.getSendTo());
				}
			}
			if (username.equals(item.getSendTo())){
				if(!helperList.contains(item.getSendFrom())){
					ContactInfo contact = new ContactInfo(item.getSendFrom(), item.isOpen());
					contactInfoList.add(contact);
					helperList.add(item.getSendFrom());
				}
			}
		}
		for (int i=0; i<contactInfoList.size(); i++) {
			contactInfoList.get(i).setStatus(isOnline(contactInfoList.get(i).getUsername()));
		}
		return contactInfoList;
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
		List<ChatMessage> userMessages = groupingMessages(chatMessagesSended, chatMessagesReceived, username);
		UserContact userContact = new UserContact(userProfile, userMessages);
		return userContact;
	}
	
	@RequestMapping(value="/edit", method=RequestMethod.POST)
	@ResponseBody
	public UserProfile handleFileUpload(@RequestParam("file") MultipartFile file,
        Principal principal, HttpServletRequest request) throws IllegalStateException, IOException {
		String realPathtoUploads = request.getSession().getServletContext().getRealPath("/resources/img");
        if(! new File(realPathtoUploads).exists())
            new File(realPathtoUploads).mkdir();
        String fileName = principal.getName() + "." + file.getContentType().split("/")[1];
        String filePath = realPathtoUploads + "\\" + fileName;
        File dest = new File(filePath);
        file.transferTo(dest);
        UserProfile userProfile = userProfileRepository.findByUsername(principal.getName());
        userProfile.setImageName(fileName);
        userProfileRepository.save(userProfile);
		return userProfile;
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
    
    private List<ChatMessage> groupingMessages(
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
		for(Iterator<ChatMessage> i = chatMessages.iterator(); i.hasNext(); ) {
		    ChatMessage chatMessage = i.next();
		    if(chatMessage.getSendTo().equals(username)){
			    chatMessage.setOpen(true);
			    chatMessageRepository.save(chatMessage);
		    }
		}
		return chatMessages;
	}
  
}

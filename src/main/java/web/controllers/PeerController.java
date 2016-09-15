package web.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import data.entities.Contact;
import data.entities.PaidConversation;
import data.entities.User;
import data.entities.UserProfile;
import repositories.PaidConversationRepository;

@Controller
public class PeerController {
	
	@Autowired
	private PaidConversationRepository paidConversationRepository;
	
	@RequestMapping(value="/endCall/{callWith}", method=RequestMethod.GET)
	@ResponseBody
	public String endCall(Principal principal, Model model, HttpSession session, 
			@PathVariable("callWith") String callWith){
		String username = principal.getName();
		PaidConversation paidConversation = paidConversationRepository
				.findByPayingAndReceiverAndEnded(username, callWith, false);
		PaidConversation paidConversation2 = paidConversationRepository
				.findByPayingAndReceiverAndEnded(callWith, username, false);
		if(paidConversation != null){
			paidConversation.setEnded(true);
			paidConversationRepository.save(paidConversation);
		}
		if(paidConversation2 != null){
			paidConversation2.setEnded(true);
			paidConversationRepository.save(paidConversation2);
		}
		return null;
	}
}

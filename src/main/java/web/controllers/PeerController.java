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
import data.entities.PeerConnection;
import data.entities.User;
import data.entities.UserProfile;
import repositories.PeerConnectionRepository;

@Controller
public class PeerController {
	
	@Autowired
	private PeerConnectionRepository peerConnectionRepository;
	
	@RequestMapping(value="/endCall/{callWith}", method=RequestMethod.GET)
	@ResponseBody
	public String endCall(Principal principal, Model model, HttpSession session, 
			@PathVariable("callWith") String callWith){
		String username = principal.getName();
		PeerConnection paidConversation = peerConnectionRepository
				.findByPayingAndEnded(username, false);
		if(paidConversation == null){
			paidConversation = peerConnectionRepository
				.findByReceiverAndEnded(username, false);
		}
		if(paidConversation != null){
			paidConversation.setEnded(true);
			peerConnectionRepository.save(paidConversation);
		}
		return null;
	}
}

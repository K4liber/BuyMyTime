package web.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import data.entities.PeerConnection;
import data.entities.User;
import repositories.PeerConnectionRepository;

@Controller
public class PeerController {
	
	@Autowired
	private PeerConnectionRepository peerConnectionRepository;
	
	@RequestMapping(value="/connections/{username}", method=RequestMethod.GET)
	@ResponseBody
	public List<PeerConnection> getUser(@PathVariable("username") String username, Principal principal, Model model){
		if (principal.getName().equals(username)){
			List<PeerConnection> peerConnections = peerConnectionRepository.findByUsername(username);
			return peerConnections;
		} else {
			return null;
		}
	}
	
}

package web.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import repositories.CategoryRepository;
import repositories.ChatMessageRepository;
import repositories.PeerConnectionRepository;
import repositories.TransactionRepository;
import repositories.UserRepository;
import data.entities.Category;
import data.entities.PeerConnection;
import data.entities.Transaction;
import data.entities.User;
import data.messages.ChatMessage;

@RequestMapping
@Controller
public class HomeController {
	
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	private PeerConnectionRepository peerConnectionRepository;
	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	ChatMessageRepository chatMessageRepository;
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String home(Principal principal, Model model){
		List<Category> categories = categoryRepository.findAll();
		model.addAttribute("categories",categories);
		model.addAttribute("user", new User());
		if(principal != null){
			model.addAttribute("username", principal.getName());
			endAllSuspendedConnections(principal.getName());
			model.addAttribute("newMessages", lookForNewMessages(principal.getName()));
		}	
		return "home";
	}
	
	private int lookForNewMessages(String username) {
		List<ChatMessage> notOpenMessages= chatMessageRepository.findAllBySendToAndOpen(username, false);
		System.out.println(notOpenMessages.size());
		return notOpenMessages.size();
	}

	private void endAllSuspendedConnections(String username) {
		List<PeerConnection> peerConnections = peerConnectionRepository
				.findAllByPayingAndEnded(username, false);
		for(PeerConnection peerConnection : peerConnections){
            peerConnection.setEnded(true);
            peerConnectionRepository.save(peerConnection);
            summarizeTransaction(peerConnection);
		}
		peerConnections = peerConnectionRepository
				.findAllByReceiverAndEnded(username, false);
		for(PeerConnection peerConnection : peerConnections){
            peerConnection.setEnded(true);
            summarizeTransaction(peerConnection);
            peerConnectionRepository.save(peerConnection);
		}
	}

	@RequestMapping(value="/categories", method=RequestMethod.GET)
	@ResponseBody
	public List<Category> categories(Model model){
		List<Category> categories = categoryRepository.findAll();
		return categories;
	}
	
	@RequestMapping(value="/about", method=RequestMethod.GET)
	@ResponseBody
	public String getAbout(@RequestParam String username, Model model){
		String about = "COs tam sadasdasd" + username;
		return about;
	}
	
	private void summarizeTransaction(PeerConnection peerConnection) {
		Transaction transaction = transactionRepository.findByPeerConnectionId(peerConnection.getId());
		if(!transaction.getEnded()){
			User userPaying = userRepository.findByUsername(peerConnection.getPaying());
			User userReceiver = userRepository.findByUsername(peerConnection.getReceiver());
			userPaying.setCoins(userPaying.getCoins() - transaction.getCoins());
			userReceiver.setCoins(userReceiver.getCoins() + transaction.getCoins());
			transaction.setEnded(true);
			userRepository.save(userPaying);
			userRepository.save(userReceiver);
			transactionRepository.save(transaction);
		}
	}
	
}

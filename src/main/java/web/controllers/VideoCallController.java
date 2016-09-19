package web.controllers;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import data.entities.PeerConnection;
import data.entities.Transaction;
import data.entities.User;
import data.entities.UserProfile;
import data.messages.AnswerCallMessage;
import data.messages.AnswerPaidMessage;
import data.messages.CallMessage;
import data.messages.ChatMessage;
import data.messages.CommuniqueMessage;
import data.messages.EndCallMessage;
import data.messages.PaidMessage;
import data.messages.TimeUpdateMessage;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.*;

import repositories.ChatMessageRepository;
import repositories.PeerConnectionRepository;
import repositories.TransactionRepository;
import repositories.UserProfileRepository;
import repositories.UserRepository;

@Controller
public class VideoCallController {
	
	@Autowired
	private SimpMessagingTemplate messaging;
	@Autowired
	private ChatMessageRepository chatMessageRepository;
	@Autowired
	private PeerConnectionRepository peerConnectionRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TransactionRepository transactionRepository;
	
	@RequestMapping(value="/fileMessage", method=RequestMethod.POST)
	@ResponseBody
	public String handleFileMessage(@RequestParam("file") MultipartFile file, @RequestParam("username") String sendTo,
        Principal principal, HttpServletRequest request) throws IllegalStateException, IOException {
		ChatMessage message = new ChatMessage();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = formatter.format(new Date());
        String realPathtoUploads = request.getSession().getServletContext().getRealPath("/resources/files");
        if(! new File(realPathtoUploads).exists())
            new File(realPathtoUploads).mkdir();
		message.setMessageContent(file.getOriginalFilename());
		message.setSendTo(sendTo);
		message.setSendFrom(principal.getName());
		message.setType("file");
		message.setDateTime(date);
		chatMessageRepository.save(message);
		ChatMessage chatMessage = chatMessageRepository.findByDateTimeAndSendTo(date, sendTo);
		String fileName = principal.getName() + sendTo + chatMessage.getId() + "." + file.getContentType().split("/")[1];
        String filePath = realPathtoUploads + "\\" + fileName;
        File dest = new File(filePath);
        file.transferTo(dest);
        chatMessage.setFileName(fileName);
		messaging.convertAndSendToUser(sendTo, "/queue/chat", chatMessage);
        chatMessageRepository.save(chatMessage);
		return fileName;
	}
	
    @MessageMapping("/call")
    public void call(Principal principal, CallMessage message) throws InterruptedException{   
        CallMessage callMessage = new CallMessage();
        callMessage.setName(principal.getName());
        messaging.convertAndSendToUser(message.getName(), "/queue/call", callMessage);
    }
    
    @MessageMapping("/cancelCall")
    public void cancelCall(Principal principal, CallMessage message) throws InterruptedException{
        CallMessage callMessage = new CallMessage();
        callMessage.setName(principal.getName());
        messaging.convertAndSendToUser(message.getName(), "/queue/cancelCall", callMessage);
    }
    
    @MessageMapping("/endCall")
    public void callEnd(Principal principal, EndCallMessage message) throws InterruptedException{
    	EndCallMessage endCallMessage = new EndCallMessage();
    	endCallMessage.setCallWith(principal.getName());
        messaging.convertAndSendToUser(message.getCallWith(), "/queue/endCall", endCallMessage);
    }
    
    @MessageMapping("/callAnswer")
    public void answerTheCall(Principal principal, AnswerCallMessage message) throws InterruptedException{
        messaging.convertAndSendToUser(message.getCallingFrom(), "/queue/callAnswer", message);
    }
    
    @MessageMapping("/paidCall")
    public void paidCall(Principal principal, PaidMessage message) throws InterruptedException{ 
        messaging.convertAndSendToUser(message.getToId(), "/queue/paid", message);
    }
    
    @MessageMapping("/paidCallAnswer")
    public void paidCallAnswer(Principal principal, AnswerPaidMessage message) throws InterruptedException{ 
    	boolean haveEnoughCoins = haveEnoughCoins(principal.getName(), message);
    	if(message.isAccept() && haveEnoughCoins){
			PeerConnection peerConnection = new PeerConnection(message);
    		peerConnectionRepository.save(peerConnection);
    		Transaction transaction = new Transaction(peerConnection);
    		transactionRepository.save(transaction);
    		messaging.convertAndSendToUser(message.getReceiver(), "/queue/paidAnswer", message);
            messaging.convertAndSendToUser(message.getPaying(), "/queue/paidAnswer", message);
    	} else if (message.isAccept() && !haveEnoughCoins) {
    		CommuniqueMessage communique = 
    				new CommuniqueMessage("You do not have enought coins to start paid chat.");
    		messaging.convertAndSendToUser(message.getPaying(), "/queue/communique", communique);
    		communique.setCommunique(message.getPaying() 
    				+ " do not have enought coins to start paid chat.");
    		messaging.convertAndSendToUser(message.getReceiver(), "/queue/communique", communique);
    	} else if (!message.isAccept()){
	        messaging.convertAndSendToUser(message.getReceiver(), "/queue/paidAnswer", message);
	        messaging.convertAndSendToUser(message.getPaying(), "/queue/paidAnswer", message);
    	}
    }
    
    @MessageMapping("/message")
    public void chatMessage(Principal principal, ChatMessage message) throws InterruptedException{ 
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = formatter.format(new Date());
        message.setDateTime(date);
        chatMessageRepository.save(message);
        ChatMessage chatMessage = chatMessageRepository.findByDateTimeAndSendTo(date, message.getSendTo());
        messaging.convertAndSendToUser(message.getSendTo(), "/queue/chat", chatMessage);
    }
    
    @MessageMapping("/timeUpdate")
    public void timeUpdate(Principal principal, TimeUpdateMessage message) throws InterruptedException{
    	PeerConnection peerConnection = peerConnectionRepository
    			.findByReceiverAndEnded(principal.getName(), false);
    	if(peerConnection != null){
    		Transaction transaction = transactionRepository.findByPeerConnectionId(peerConnection.getId());
    		updateTimeConnection(peerConnection, transaction);
    	}
    	else {
    		peerConnection = peerConnectionRepository
        			.findByPayingAndEnded(principal.getName(), false);
    		if(peerConnection != null){
    			Transaction transaction = transactionRepository.findByPeerConnectionId(peerConnection.getId());
        		updateTimeConnection(peerConnection, transaction);
        	}
    	}
    }
    
    @MessageMapping("/endPaid")
    public void endPaid(Principal principal, EndCallMessage message) throws InterruptedException{
    	String username = principal.getName();
		PeerConnection peerConnection = peerConnectionRepository
				.findByPayingAndEnded(username, false);
		if(peerConnection == null){
			peerConnection = peerConnectionRepository
				.findByReceiverAndEnded(username, false);
		}
		if(peerConnection != null){
			peerConnection.setEnded(true);
			peerConnectionRepository.save(peerConnection);
			summarizeTransaction(peerConnection);
			CommuniqueMessage communique = 
    				new CommuniqueMessage(username + " ended paid conversation.");
			communique.setAction("endPaid");
    		messaging.convertAndSendToUser(message.getCallWith(), "/queue/communique", communique);
		}
    }
    
    @RequestMapping(value="/endCall/{callWith}", method=RequestMethod.GET)
	@ResponseBody
	public String endCall(Principal principal, Model model, HttpSession session, 
			@PathVariable("callWith") String callWith){
		String username = principal.getName();
		PeerConnection peerConnection = peerConnectionRepository
				.findByPayingAndEnded(username, false);
		if(peerConnection == null){
			peerConnection = peerConnectionRepository
				.findByReceiverAndEnded(username, false);
		}
		if(peerConnection != null){
			peerConnection.setEnded(true);
			peerConnectionRepository.save(peerConnection);
			summarizeTransaction(peerConnection);
		}
		return null;
	}
    
    @RequestMapping(value="/fileName/{id}", method=RequestMethod.GET)
	@ResponseBody
	public String getFileName(Principal principal, Model model, HttpSession session, 
			@PathVariable("id") Long id){
    	ChatMessage chatMessage = chatMessageRepository.findById(id);
    	if(chatMessage != null)
    		return chatMessage.getFileName();
    	else
    		return null;
    }

	private boolean haveEnoughCoins(String username, PeerConnection peerConnection, Transaction transaction) {
    	User payingUser = userRepository.findByUsername(username);
    	Long coins = payingUser.getCoins() - transaction.getCoins();
    	Long enoughtToContinue = (Long)(Long.parseLong(peerConnection.getPrice())/240 + 1);
    	return (coins > enoughtToContinue);
	}

	public boolean haveEnoughCoins(String username, AnswerPaidMessage message){
    	User payingUser = userRepository.findByUsername(username);
    	Long coins = payingUser.getCoins();
    	Long enoughtToStart = (Long)(Long.parseLong(message.getPrice())/60 + 1);
    	return (coins > enoughtToStart);
    }
    
	private void updateTimeConnection(PeerConnection peerConnection, Transaction transaction) {
		if (haveEnoughCoins(peerConnection.getPaying(), peerConnection, transaction)){
			updateTransaction(peerConnection, transaction);
			Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        String date = formatter.format(new Date());
			peerConnection.setEndPoint(date);
			peerConnectionRepository.save(peerConnection);
		} else {
			CommuniqueMessage communique = 
    				new CommuniqueMessage("You do not have enought coins to continue chat.");
			communique.setAction("endPaid");
    		messaging.convertAndSendToUser(peerConnection.getPaying(), "/queue/communique", communique);
    		communique.setCommunique(peerConnection.getPaying() 
    				+ " do not have enought coins to continue chat.");
    		messaging.convertAndSendToUser(peerConnection.getReceiver(), "/queue/communique", communique);
    		peerConnection.setEnded(true);
			peerConnectionRepository.save(peerConnection);
			summarizeTransaction(peerConnection);
		}
	}

	private void updateTransaction(PeerConnection peerConnection, Transaction transaction) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startDate;
		Date endDate;
		try {
			startDate = dateFormat.parse(peerConnection.getStartPoint());
			endDate = dateFormat.parse(peerConnection.getEndPoint());
			Long coins = ((Long.parseLong(peerConnection.getPrice())) 
					* (endDate.getTime() - startDate.getTime()))/3600000;
			transaction.setCoins(coins);
			transactionRepository.save(transaction);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}
	
	private void summarizeTransaction(PeerConnection peerConnection) {
		Transaction transaction = transactionRepository.findByPeerConnectionId(peerConnection.getId());
		if(!transaction.getEnded()){
			data.entities.User userPaying = userRepository.findByUsername(peerConnection.getPaying());
			data.entities.User userReceiver = userRepository.findByUsername(peerConnection.getReceiver());
			userPaying.setCoins(userPaying.getCoins() - transaction.getCoins());
			userReceiver.setCoins(userReceiver.getCoins() + transaction.getCoins());
			transaction.setEnded(true);
			userRepository.save(userPaying);
			userRepository.save(userReceiver);
			transactionRepository.save(transaction);
		}
	}
    
}

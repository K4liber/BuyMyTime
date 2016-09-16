package web.controllers;

import java.security.Principal;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import data.entities.PeerConnection;
import data.messages.AnswerCallMessage;
import data.messages.AnswerPaidMessage;
import data.messages.CallMessage;
import data.messages.ChatMessage;
import data.messages.PaidMessage;
import data.messages.TimeUpdateMessage;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.*;

import repositories.ChatMessageRepository;
import repositories.PeerConnectionRepository;
import repositories.UserRepository;

@Controller
public class VideoCallController {
	
	@Autowired
	private SimpMessagingTemplate messaging;
	
	@Autowired
	private ChatMessageRepository chatMessageRepository;
	
	@Autowired
	private PeerConnectionRepository peerConnectionRepository;
	
    @MessageMapping("/call")
    public void call(Principal principal, CallMessage message) throws InterruptedException{
        System.out.println("Dzwoni do: " + message.getName() + " od: " + principal.getName());   
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
    
    @MessageMapping("/callEnd")
    public void callEnd(Principal principal, CallMessage message) throws InterruptedException{
        CallMessage callMessage = new CallMessage();
        callMessage.setName(principal.getName());
        messaging.convertAndSendToUser(message.getName(), "/queue/callEnd", callMessage);
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
    	if(message.isAccept()){
			PeerConnection peerConnection = new PeerConnection(message);
    		peerConnectionRepository.save(peerConnection);
    	}
        messaging.convertAndSendToUser(message.getReceiver(), "/queue/paidAnswer", message);
    }
    
    @MessageMapping("/message")
    public void chatMessage(Principal principal, ChatMessage message) throws InterruptedException{
    	System.out.println("Message from:" + message.getSendFrom() + " .To: " + message.getSendTo() 
    			+ ". message:" + message.getMessageContent());
        messaging.convertAndSendToUser(message.getSendTo(), "/queue/chat", message);
        message.setDateTime(new Date().toString());
        chatMessageRepository.save(message);
    }
    
    @MessageMapping("/timeUpdate")
    public void timeUpdate(Principal principal, TimeUpdateMessage message) throws InterruptedException{
    	System.out.println("TUTAJ JEST");
    	String username = principal.getName();
    	PeerConnection peerConnection = peerConnectionRepository
    			.findByReceiverAndEnded(username, false);
    	if(peerConnection != null){
    		peerConnection.setEndPoint((new Date()).toString());
    		peerConnectionRepository.save(peerConnection);
    	}
    	peerConnection = peerConnectionRepository
    			.findByPayingAndEnded(username, false);
    	if(peerConnection != null){
    		peerConnection.setEndPoint((new Date()).toString());
    		peerConnectionRepository.save(peerConnection);
    	}
    }
    
}

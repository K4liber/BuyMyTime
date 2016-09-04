package web;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import data.AnswerCallMessage;
import data.ChatMessage;
import data.CallMessage;
import data.PaidMessage;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.*;

import repositories.ChatMessageRepository;
import repositories.UserRepository;

@Controller
public class VideoCallController {
	
	@Autowired
	private SimpMessagingTemplate messaging;
	
	@Autowired
	ChatMessageRepository chatMessageRepository;
	
    @MessageMapping("/call")
    public void greeting(Principal principal, CallMessage message) throws InterruptedException{
        System.out.println("Dzwoni do: " + message.getName() + " od: " + principal.getName());   
        CallMessage callMessage = new CallMessage();
        callMessage.setName(principal.getName());
        messaging.convertAndSendToUser(message.getName(), "/queue/call", callMessage);
    }
    
    @MessageMapping("/answerCall")
    public void answerTheCall(Principal principal, AnswerCallMessage message) throws InterruptedException{
        System.out.println("Akceptacja rozmowy z uzytkownikiem: " 
    + message.getUsername()+ " od: " + principal.getName() + " z ID: " + message.getId());   
        messaging.convertAndSendToUser(message.getUsername(), "/queue/acceptCall", message);
    }
    
    @MessageMapping("/paidCall")
    public void paidCall(Principal principal, PaidMessage message) throws InterruptedException{
        System.out.println("Prosba o platna rozmowe: " 
    + message.getFromId()+ ", " + message.getToId() + ", " + message.getPrice() + ", " + message.getMaxTime());   
        messaging.convertAndSendToUser(message.getToId(), "/queue/paid", message);
    }
    
    @MessageMapping("/message")
    public void chatMessage(Principal principal, ChatMessage message) throws InterruptedException{
    	System.out.println("Message from:" + message.getSendFrom() + " .To: " + message.getSendTo() 
    			+ ". message:" + message.getMessageContent());
        messaging.convertAndSendToUser(message.getSendTo(), "/queue/chat", message);
        chatMessageRepository.save(message);
    }
    
}

package web;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import data.HelloMessage;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.*;

@Controller
public class HelloController {
	
	@Autowired
	private SimpMessagingTemplate messaging;
	
    @MessageMapping("/marco")
    public void greeting(Principal principal, HelloMessage message) throws InterruptedException{
        System.out.println("Dzwoni do: " + message.getName() + " od: " + principal.getName());   
        HelloMessage returnMessage = new HelloMessage();
        returnMessage.setName("Polo");
        HelloMessage exampleMessage = new HelloMessage();
        exampleMessage.setName(principal.getName());
        messaging.convertAndSendToUser(message.getName(), "/queue/reply", exampleMessage);
    }
    
    @SubscribeMapping({"/greetings"})
    public void handleSubscription() {
    	HelloMessage outgoing = new HelloMessage();
    	System.out.println("SUB");
    	outgoing.setName("Jas");
    }
    
}

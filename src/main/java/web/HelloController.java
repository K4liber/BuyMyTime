package web;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import data.HelloMessage;

import org.springframework.messaging.simp.annotation.*;

@Controller
public class HelloController {


    @MessageMapping("/marco")
    @SendTo("/topic/shout")
    public HelloMessage greeting(Principal principal, HelloMessage message) throws InterruptedException{
        System.out.println("Wiadomosc: " + message.getName() + "od: " + principal.getName());   
        HelloMessage returnMessage = new HelloMessage();
        returnMessage.setName("Polo");
        return returnMessage;
    }
    
    @SubscribeMapping({"/greeting"})
    public void handleSubscription() {
    	HelloMessage outgoing = new HelloMessage();
    	System.out.println("SUB");
    	outgoing.setName("Jas");
    }
    
}

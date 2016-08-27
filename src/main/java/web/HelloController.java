package web;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import data.HelloMessage;

import org.springframework.messaging.simp.annotation.*;

@Controller
public class HelloController {


    @MessageMapping("/greeting")
    public void greeting(HelloMessage message) throws InterruptedException{
    	Thread.sleep(60000);
        System.out.println("Wiadomosc: " + message.getName());
    }
    
    @SubscribeMapping({"/greeting"})
    public HelloMessage handleSubscription() {
    	HelloMessage outgoing = new HelloMessage();
    	System.out.println("SUB");
    	outgoing.setName("Jas");
    	return outgoing;
    }
    
}

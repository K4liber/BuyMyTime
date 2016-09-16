package web.services;

import java.util.Iterator;
import java.util.List;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.context.ApplicationContext;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.context.support.WebApplicationContextUtils;

import data.entities.PeerConnection;
import data.messages.CallMessage;
import repositories.PeerConnectionRepository;

@WebListener
public class SessionListener implements HttpSessionListener{
	
	private PeerConnectionRepository peerConnectionRepository;
	private SimpMessagingTemplate messaging;
	
    @Override
    public void sessionCreated(HttpSessionEvent event) {
        System.out.println("==== Session is created ====");
    }
 
    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
    	User user = (User) ((SecurityContext)SecurityContextHolder.getContext())
    			.getAuthentication().getPrincipal();
    	String username = user.getUsername();
        System.out.println("==== Session is destroyed ====");
        final ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(event.getSession().getServletContext());
        final PeerConnectionRepository service = ctx.getBean(PeerConnectionRepository.class);
        final SimpMessagingTemplate service2 = ctx.getBean(SimpMessagingTemplate.class);
        this.peerConnectionRepository = service;
        this.messaging = service2;
        endConnection(username);
    }

	private void endConnection(String username) {
		PeerConnection peerConnection = peerConnectionRepository
				.findByReceiverAndEnded(username, false);
		if(peerConnection != null){
			peerConnection.setEnded(true);
			peerConnectionRepository.save(peerConnection);
			sendEnd(username, peerConnection.getPaying());
		}
		peerConnection = peerConnectionRepository
				.findByPayingAndEnded(username, false);
		if(peerConnection != null){
			peerConnection.setEnded(true);
			peerConnectionRepository.save(peerConnection);
			sendEnd(username, peerConnection.getReceiver());
		}
	}
	
	private void sendEnd(String username, String sendTo){
		CallMessage callMessage = new CallMessage();
        callMessage.setName(username);
        messaging.convertAndSendToUser(sendTo, "/queue/callEnd", callMessage);
	}
	
}

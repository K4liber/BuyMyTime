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
import data.entities.Transaction;
import data.messages.CallMessage;
import repositories.PeerConnectionRepository;
import repositories.TransactionRepository;
import repositories.UserRepository;

@WebListener
public class SessionListener implements HttpSessionListener{
	
	private TransactionRepository transactionRepository;
	private PeerConnectionRepository peerConnectionRepository;
	private SimpMessagingTemplate messaging;
	private UserRepository userRepository;
	
    @Override
    public void sessionCreated(HttpSessionEvent event) {
        System.out.println("==== Session is created ====");
    }
 
    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
    	System.out.println("==== Session is destroyed ====");
    	if((SecurityContext)SecurityContextHolder.getContext() != null){
	    	User user = (User) ((SecurityContext)SecurityContextHolder.getContext())
	    			.getAuthentication().getPrincipal();
	    	String username = user.getUsername();
	    	if(peerConnectionRepository == null || messaging == null 
	    			|| transactionRepository == null)
	    		loadRepositories(event);
	        endConnection(username);
    	}
    }

	private void loadRepositories(HttpSessionEvent event) {
		final ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(event.getSession().getServletContext());
        final PeerConnectionRepository service = ctx.getBean(PeerConnectionRepository.class);
        final SimpMessagingTemplate service2 = ctx.getBean(SimpMessagingTemplate.class);
        final TransactionRepository service3 = ctx.getBean(TransactionRepository.class);
        final UserRepository service4 = ctx.getBean(UserRepository.class);
        this.peerConnectionRepository = service;
        this.messaging = service2;
        this.transactionRepository = service3;
        this.userRepository = service4;
	}

	private void endConnection(String username) {
		PeerConnection peerConnection = peerConnectionRepository
				.findByReceiverAndEnded(username, false);
		if(peerConnection != null){
			peerConnection.setEnded(true);
			peerConnectionRepository.save(peerConnection);
			summarizeTransaction(peerConnection);
			sendEnd(username, peerConnection.getPaying());
		}
		peerConnection = peerConnectionRepository
				.findByPayingAndEnded(username, false);
		if(peerConnection != null){
			peerConnection.setEnded(true);
			peerConnectionRepository.save(peerConnection);
			summarizeTransaction(peerConnection);
			sendEnd(username, peerConnection.getReceiver());
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

	private void sendEnd(String username, String sendTo){
		CallMessage callMessage = new CallMessage();
        callMessage.setName(username);
        messaging.convertAndSendToUser(sendTo, "/queue/callEnd", callMessage);
	}
	
}

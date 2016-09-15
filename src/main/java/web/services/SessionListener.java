package web.services;

import java.util.Iterator;
import java.util.List;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.context.support.WebApplicationContextUtils;

import data.entities.PaidConversation;
import repositories.PaidConversationRepository;

@WebListener
public class SessionListener implements HttpSessionListener{
	
	private PaidConversationRepository paidConversationRepository;
	
    @Override
    public void sessionCreated(HttpSessionEvent event) {
        System.out.println("==== Session is created ====");
    }
 
    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
    	User user = (User) ((SecurityContext)SecurityContextHolder.getContext())
    			.getAuthentication().getPrincipal();
    	String username = user.getUsername();
    	System.out.println("Username: " + username);
        System.out.println("==== Session is destroyed ====");
        final ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(event.getSession().getServletContext());
        final PaidConversationRepository service = ctx.getBean(PaidConversationRepository.class);
        this.paidConversationRepository = service;
        endAllConnections(username);
    }

	private void endAllConnections(String username) {
		List<PaidConversation> paidConversations = paidConversationRepository
				.findAllByPayingAndEnded(username, false);
		if(paidConversations != null)
			endAllConnections(username, paidConversations);
		List<PaidConversation> paidConversations2 = paidConversationRepository
				.findAllByReceiverAndEnded(username, false);
		if(paidConversations2 != null)
			endAllConnections(username, paidConversations2);
	}
	
	private void endAllConnections(String username, List<PaidConversation> paidConversations) {
		for(Iterator<PaidConversation> i = paidConversations.iterator(); i.hasNext(); ) {
			PaidConversation item = i.next();
		    if(!item.isEnded()){
		    	item.setEnded(true);
		    	paidConversationRepository.save(item);
		    }	
		}	
	}
	
}

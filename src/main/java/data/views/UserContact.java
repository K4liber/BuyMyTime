package data.views;

import java.util.List;

import data.entities.ChatMessage;
import data.entities.UserProfile;

public class UserContact {

	private UserProfile profile;
	private List<ChatMessage> chatMessages;
	
	public UserContact(UserProfile profile, List<ChatMessage> chatMessages){
		this.profile = profile;
		this.setChatMessages(chatMessages);
	}
	
	public UserProfile getProfile() {
		return profile;
	}
	public void setProfile(UserProfile profile) {
		this.profile = profile;
	}

	public List<ChatMessage> getChatMessages() {
		return chatMessages;
	}

	public void setChatMessages(List<ChatMessage> chatMessages) {
		this.chatMessages = chatMessages;
	}

	
}

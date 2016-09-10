package data.views;

import java.util.List;

public class UserContact {

	private UserProfile profile;
	private List<UserMessage> userMessages;
	
	public UserContact(UserProfile profile, List<UserMessage> userMessages){
		this.profile = profile;
		this.userMessages = userMessages;
	}
	
	public UserProfile getProfile() {
		return profile;
	}
	public void setProfile(UserProfile profile) {
		this.profile = profile;
	}
	public List<UserMessage> getUserMessages() {
		return userMessages;
	}
	public void setUserMessages(List<UserMessage> userMessages) {
		this.userMessages = userMessages;
	}
	
}

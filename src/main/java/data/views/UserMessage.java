package data.views;

import data.messages.ChatMessage;

public class UserMessage {

	private String message;
	private String dateTime;
	private boolean mine;
	
	public UserMessage(ChatMessage message, String username){
		this.message = message.getMessageContent();
		this.dateTime = message.getDateTime();
		if(message.getSendFrom().equals(username))
			mine = true;
		else
			mine = false;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	public boolean isMine() {
		return mine;
	}
	public void setMine(boolean mine) {
		this.mine = mine;
	}
	
}

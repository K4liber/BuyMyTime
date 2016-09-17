package data.messages;

public class CommuniqueMessage {
	private String communique;
	private boolean acceptation;
	private String action;
	
	public CommuniqueMessage(String communique) {
		this.communique = communique;
	}

	public String getCommunique() {
		return communique;
	}

	public void setCommunique(String communique) {
		this.communique = communique;
	}

	public boolean isAcceptation() {
		return acceptation;
	}

	public void setAcceptation(boolean acceptation) {
		this.acceptation = acceptation;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
}

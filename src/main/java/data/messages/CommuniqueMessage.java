package data.messages;

public class CommuniqueMessage {
	private String communique;
	private boolean acceptation;
	
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
	
}

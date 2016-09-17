package data.messages;

public class EndCallMessage {
	private String callWith;
	private String communique;
	
	public String getCallWith() {
		return callWith;
	}
	public void setCallWith(String callWith) {
		this.callWith = callWith;
	}
	public String getCommunique() {
		return communique;
	}
	public void setCommunique(String communique) {
		this.communique = communique;
	}
}

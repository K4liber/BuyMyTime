package data.messages;

public class AnswerCallMessage {
	
	private String callingFrom;
	private String callingTo;
	private boolean accept;
	
	public boolean isAccept() {
		return accept;
	}
	public void setAccept(boolean accept) {
		this.accept = accept;
	}
	public String getCallingFrom() {
		return callingFrom;
	}
	public void setCallingFrom(String callingFrom) {
		this.callingFrom = callingFrom;
	}
	public String getCallingTo() {
		return callingTo;
	}
	public void setCallingTo(String callingTo) {
		this.callingTo = callingTo;
	}
}

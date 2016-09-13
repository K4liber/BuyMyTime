package data.messages;

public class AnswerPaidMessage {
	
	private String paying;
    private String receiver;
    private String price;
    private String maxTime;
    private boolean accept;
    

	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getMaxTime() {
		return maxTime;
	}
	public void setMaxTime(String maxTime) {
		this.maxTime = maxTime;
	}
	public boolean isAccept() {
		return accept;
	}
	public void setAccept(boolean accept) {
		this.accept = accept;
	}
	public String getPaying() {
		return paying;
	}
	public void setPaying(String paying) {
		this.paying = paying;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

}

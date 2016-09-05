package data.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import data.messages.AnswerPaidMessage;

@Entity
public class PaidConversation {
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String paying;
	private String receiver;
	private String conversationStart;
	private String conversationPoint;
	private Boolean ended;
	private String price;
	private String maxTime;
	
	public PaidConversation(AnswerPaidMessage message) {
		this.paying = message.getFromId();
		this.receiver = message.getToId();
		this.price = message.getPrice();
		this.maxTime = message.getMaxTime();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getConversationStart() {
		return conversationStart;
	}

	public void setConversationStart(String conversationStart) {
		this.conversationStart = conversationStart;
	}

	public String getConversationPoint() {
		return conversationPoint;
	}

	public void setConversationPoint(String conversationPoint) {
		this.conversationPoint = conversationPoint;
	}

	public Boolean getEnded() {
		return ended;
	}

	public void setEnded(Boolean ended) {
		this.ended = ended;
	}

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

}
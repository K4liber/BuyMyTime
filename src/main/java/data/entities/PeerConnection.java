package data.entities;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import data.messages.AnswerCallMessage;
import data.messages.AnswerPaidMessage;

@Entity
public class PeerConnection {
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String paying;
	private String receiver;
	private String startPoint;
	private String endPoint;
	private boolean ended;
	private String price;
	private String maxTime;
	
	public PeerConnection(){};

	public PeerConnection(AnswerPaidMessage message) {
		this.paying = message.getPaying();
		this.receiver = message.getReceiver();
		this.price = message.getPrice();
		this.maxTime = message.getMaxTime();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = formatter.format(new Date());
		this.startPoint = date;
		this.endPoint = date;
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

	public boolean isEnded() {
		return ended;
	}

	public void setEnded(boolean ended) {
		this.ended = ended;
	}

	public String getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(String startPoint) {
		this.startPoint = startPoint;
	}

	public String getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}

}
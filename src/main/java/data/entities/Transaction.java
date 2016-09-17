package data.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Transaction {
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private Long peerConnectionId;
	private Long coins;
	private String receiver;
	private String paying;
	private Boolean ended;
	
	public Transaction(){};

	public Transaction(PeerConnection peerConnection){
		this.peerConnectionId = peerConnection.getId();
		this.coins = Long.parseLong("0");
		this.receiver = peerConnection.getReceiver();
		this.paying = peerConnection.getPaying();
		this.setEnded(false);
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPeerConnectionId() {
		return peerConnectionId;
	}

	public void setPeerConnectionId(Long peerConnectionId) {
		this.peerConnectionId = peerConnectionId;
	}

	public Long getCoins() {
		return coins;
	}

	public void setCoins(Long coins) {
		this.coins = coins;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getPaying() {
		return paying;
	}

	public void setPaying(String paying) {
		this.paying = paying;
	}

	public Boolean getEnded() {
		return ended;
	}

	public void setEnded(Boolean ended) {
		this.ended = ended;
	}

}

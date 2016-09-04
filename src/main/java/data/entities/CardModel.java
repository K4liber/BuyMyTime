package data.entities;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CardModel {
	
	private Card card;
	
	@NotNull
	@Size(min=2, max=128)
	private String tags;
	
	public Card getCard() {
		return card;
	}
	public void setCard(Card card) {
		this.card = card;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	
}

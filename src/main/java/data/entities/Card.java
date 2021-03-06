package data.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Card {
	
	@Id
    @GeneratedValue(strategy=GenerationType.TABLE)
	private Long id;
	private Long userId;
	
	@NotNull
	private String userNick;
	
	@NotNull
	@Size(min=5, max=128)
	private String title;
	
	@NotNull
	@Size(min=5, max=512)
	private String description;
	
	@NotNull
	private String categoryName;
	
	private String authorImageName;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUserNick() {
		return userNick;
	}

	public void setUserNick(String userNick) {
		this.userNick = userNick;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getAuthorImageName() {
		return authorImageName;
	}

	public void setAuthorImageName(String authorImageName) {
		this.authorImageName = authorImageName;
	}
	
	

}

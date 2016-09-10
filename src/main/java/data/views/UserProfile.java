package data.views;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import data.entities.User;

@Entity
public class UserProfile{
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	 
	private String username;
	private String email;
	private Boolean status;
	
	public UserProfile(User user){
		this.username = user.getUsername();
		this.email = user.getEmail();
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}
	
}
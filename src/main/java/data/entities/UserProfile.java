package data.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class UserProfile{
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	 
	private String username;
	private String email;
	private Boolean status;
	private Long quantity;
	private Long price;
	private Long efficiency;
	private String imageName;
	
	public UserProfile(){};
	
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

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public Long getEfficiency() {
		return efficiency;
	}

	public void setEfficiency(Long efficiency) {
		this.efficiency = efficiency;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	
}
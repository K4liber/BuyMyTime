package data.views;

public class ContactInfo {
	
	private String username;
	private Boolean open;
	private Boolean status;
	
	public ContactInfo(String username, boolean open) {
		this.username = username;
		this.setOpen(open);
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public Boolean getOpen() {
		return open;
	}

	public void setOpen(Boolean open) {
		this.open = open;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}
	
}

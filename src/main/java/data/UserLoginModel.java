package data;

import javax.validation.constraints.NotNull;

public class UserLoginModel {
	
	@NotNull
	private String nick;
	
	@NotNull
	private String password;

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}

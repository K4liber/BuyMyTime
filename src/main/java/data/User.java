package data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.Email;

@Entity
public class User implements HttpSessionBindingListener{
	
	private static Map<User, HttpSession> logins = new ConcurrentHashMap<>();
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	 
	@NotNull
	@Size(min=5, max=16)
	private String nick;
	
	@NotNull
	@Size(min=5, max=16)
	private String password;
	
	@NotNull(message="not match")
	private String confirmPassword;
	
	@NotNull
	@Email
	private String email;
	
	private boolean status;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	@Override
    public boolean equals(Object other) {
        return (other instanceof User) && (id != null) ? id.equals(((User) other).getId()) : (other == this);
    }

    @Override
    public int hashCode() {
        return (id != null) ? (this.getClass().hashCode() + id.hashCode()) : super.hashCode();
    }

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        HttpSession session = logins.remove(this);
        if (session != null) {
            session.invalidate();
        }
        logins.put(this, event.getSession());
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        logins.remove(this);
    }
}

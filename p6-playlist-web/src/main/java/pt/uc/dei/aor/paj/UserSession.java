package pt.uc.dei.aor.paj;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import java.io.Serializable;

@Named
@SessionScoped
public class UserSession implements Serializable {

	private static final long serialVersionUID = -8146426169997204925L;

	private String username;
	private String email;
	
	
	public UserSession() {
		username = null;
		email = null;
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
	
	

}

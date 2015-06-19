package pt.uc.dei.aor.paj;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Size;

@Named 
@RequestScoped
public class Usersinterface implements Serializable {
	private static final long serialVersionUID = -8310185641498834904L;

	@Size(min = 3, max=20, message = "Please enter a valid username (3-20 characters)")
	private String username;
	
	private String email;
	private String password;
	private String cpassword;
	private String oldPassword;
	private HttpSession session;

	@Inject 
	private SigninEJB signin;
	
	@Inject 
	private LoginEJB login;
	
	@Inject
	private UserSession userSession;
	

	public Usersinterface() {
		this.session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
	}

	//Getter e Setter associados à variável username
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	//Getter e Setter associados à variável username
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	//Getter e Setter associados à variável password
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	//Getter e Setter associados à variável cpassword
	public String getCpassword() {
		return cpassword;
	}
	public void setCpassword(String cpassword) {
		this.cpassword = cpassword;
	}

	
	//funcao para efectuar logout
	public String userlogout() {
		clearSession();
		return "/index?faces-redirect=true";
	}

	
	//funcao para efectuar registo de utilizador
	public String usersignup() {
		UserDTO user;
		if ((user = signin.register(username, password, cpassword, email)) != null) {
			startSession(user);
			return "/app/playlist?faces-redirect=true";
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error creating user"));
			return null;
		}
	}
	
	//funcao para efectuar login
	public String userlogin() {
		UserDTO user;
		if ((user = login.validateUser(username, password)) != null) {
			startSession(user);
			return "/app/playlist?faces-redirect=true";
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Login or password incorrect"));
			return null;
		}
	}
	
	
	public String delete() {
		if (signin.delete(userSession.getUsername(), password)) {
			clearSession();
			return "/index?faces-redirect=true";
		}
		else {
			addMessage("Account closing failed", FacesMessage.SEVERITY_INFO);
		}
		return null;
	}
	
	
	
	private void clearSession() {
		userSession.setEmail(null);
		userSession.setUsername(null);
		session.setAttribute("loggedin", false);
		session.invalidate();
	}
	
	private void startSession(UserDTO user) {
		userSession.setEmail(user.getEmail());
		userSession.setUsername(user.getUsername());
		session.setAttribute("loggedin", true);
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	
	public void updateUsername() {
		UserDTO u = signin.updateUsername(userSession.getUsername(), username, password);
		if (u != null) {
			userSession.setUsername(u.getUsername());
			username = "";
		}
	}
	
	public void updateEmail() {
		UserDTO u = signin.updateEmail(userSession.getUsername(), email, password);
		if (u != null) {
			userSession.setEmail(u.getEmail());
			email = "";   
		}
	}
	
	public void updatePassword() {
		if (signin.updatePassword(userSession.getUsername(), oldPassword, password))
			addMessage("Password updated", FacesMessage.SEVERITY_INFO);
		else{
			addMessage("Password not updated", FacesMessage.SEVERITY_INFO);
		}
		password = null;
		cpassword = null;
		oldPassword = null;
	}
	
	private void addMessage(String message, Severity type) {
		FacesContext.getCurrentInstance().
        addMessage(null,
            new FacesMessage(type,
            		message, null));
	}
}


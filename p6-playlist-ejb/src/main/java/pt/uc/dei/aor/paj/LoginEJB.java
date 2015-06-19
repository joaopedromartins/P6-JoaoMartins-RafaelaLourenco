package pt.uc.dei.aor.paj;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@LocalBean
public class LoginEJB {
	@PersistenceContext(name = "Utilizador")
	private EntityManager em;
	
	@Inject
	private EncryptEJB crypt;

	
    public LoginEJB() {
        // TODO Auto-generated constructor stub
    }

    
	public User findUserByUsername(String username) {
		
		TypedQuery<User> q = em.createQuery("from User u where u.name like :username", User.class);
    	q.setParameter("username", username);
    	
    	List<User> users = q.getResultList();
    	if (users.isEmpty()) return null;
    	
    	return users.get(0);
	}
	
	public User findUserByEmail(String email) {
		TypedQuery<User> q = em.createQuery("from User u where u.email like :email", User.class);
    	q.setParameter("email", email);
    	
    	List<User> users = q.getResultList();
    	if (users.isEmpty()) return null;
    	
    	return users.get(0);
	}
	
	public UserDTO validateUser(String login, String password) {
		String query;
		String username = null;
		if (login.contains("@")) {
			query = "from User u where u.email like :login and u.password like :password";
			TypedQuery<User> q = em.createQuery("from User u where u.email like :email", User.class);
			q.setParameter("email", login);
			List<User> users = q.getResultList();
			if (users.isEmpty()) return null;
			username = users.get(0).getName();
		}
		else {
			query = "from User u where u.name like :login and u.password like :password"; 
			username = login;
		}
		
		TypedQuery<User> q = em.createQuery(query, User.class);
		q.setParameter("login", login);
		q.setParameter("password", crypt.encrypt(password, username));
		
		List<User> users = q.getResultList();
		
		if (users.isEmpty()) return null;
		
		return new UserDTO(users.get(0).getName(), users.get(0).getEmail());
	}

	
	


	
}

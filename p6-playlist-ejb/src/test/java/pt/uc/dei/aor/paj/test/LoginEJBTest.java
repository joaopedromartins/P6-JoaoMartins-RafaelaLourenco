package pt.uc.dei.aor.paj.test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pt.uc.dei.aor.paj.EncryptEJB;
import pt.uc.dei.aor.paj.LoginEJB;
import pt.uc.dei.aor.paj.User;
import pt.uc.dei.aor.paj.UserDTO;


@RunWith(MockitoJUnitRunner.class)
public class LoginEJBTest {
	@Mock
	EntityManager em;

	@Mock
	TypedQuery<User> mockedQuery;
	
	@Mock
	EncryptEJB crypt;
	
	@InjectMocks
	LoginEJB ejb;

	@Before
	public void init() {
		when(mockedQuery.getResultList()).thenReturn(new ArrayList<User>());
	}
	
	
	@Test
	public void findUserByUsername_should_return_null_with_inexistent_user() {
		final String QUERY = "from User u where u.name like :username";

		// mock invocations
		when(em.createQuery(QUERY, User.class)).thenReturn(mockedQuery);

		// invoke EJB
		User utilizador = ejb.findUserByUsername("testUser");

		// verify that all methods have been called once
		verify(mockedQuery).setParameter("username", "testUser");
		verify(mockedQuery).getResultList();
		verify(em).createQuery(QUERY, User.class);

		Assert.assertThat(utilizador, is(nullValue()));
	}
	
	@Test
	public void findUserByEmail_should_work_correctly() {
		final String QUERY = "from User u where u.email like :email";
		String email = "user@user.pt";
		
		// mock invocations
		when(em.createQuery(QUERY, User.class)).thenReturn(mockedQuery);

		// invoke EJB
		User utilizador = ejb.findUserByEmail(email);

		// verify that all methods have been called once
		verify(mockedQuery).setParameter("email", email);
		verify(mockedQuery).getResultList();
		verify(em).createQuery(QUERY, User.class);

		Assert.assertThat(utilizador, is(nullValue()));
	}

	
	@Test
	public void validate_user_should_work_correctly_with_username() {
		final String QUERY = "from User u where u.name like :login and u.password like :password";
		String username = "testUser";
		String password = "123";
		String encrypt = "123";
		
		// mock invocations
		when(em.createQuery(QUERY, User.class)).thenReturn(mockedQuery);
		when(crypt.encrypt(password, username)).thenReturn(encrypt);
		
		// invoke EJB
		UserDTO user = ejb.validateUser(username, password);
		
		verify(mockedQuery).setParameter("login", username);
		verify(mockedQuery).setParameter("password", encrypt);
		verify(mockedQuery).getResultList();
		verify(em).createQuery(QUERY, User.class);
		
		Assert.assertThat(user, is(nullValue()));
	}
	
	@Test
	public void validate_user_should_work_correctly_with_email() {
		final String QUERY = "from User u where u.email like :email";
		String email = "test@test.pt";
		String password = "123";
		String encrypt = "123";
		
		// mock invocations
		when(em.createQuery(QUERY, User.class)).thenReturn(mockedQuery);
		
		// invoke EJB
		UserDTO user = ejb.validateUser(email, password);
		
		verify(mockedQuery).setParameter("email", email);
		verify(mockedQuery).getResultList();
		verify(em).createQuery(QUERY, User.class);
		
		Assert.assertThat(user, is(nullValue()));
	}

}

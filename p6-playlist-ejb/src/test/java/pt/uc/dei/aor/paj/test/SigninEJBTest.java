package pt.uc.dei.aor.paj.test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

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
import pt.uc.dei.aor.paj.Music;
import pt.uc.dei.aor.paj.MusicDTO;
import pt.uc.dei.aor.paj.MusicEJB;
import pt.uc.dei.aor.paj.Playlist;
import pt.uc.dei.aor.paj.SigninEJB;
import pt.uc.dei.aor.paj.User;
import pt.uc.dei.aor.paj.UserDTO;


@RunWith(MockitoJUnitRunner.class)
public class SigninEJBTest {
	@Mock
	EntityManager em;

	@Mock
	TypedQuery<User> mockedQueryUser;
	@Mock
	TypedQuery<Music> mockedQueryMusic;
	@Mock
	TypedQuery<Playlist> mockedQueryPlaylist;
	
	@Mock
	EncryptEJB crypt;
	
	@InjectMocks
	SigninEJB ejb;
	
	private List<User> users;
	private User u;
	
	@Before
	public void init() {
		users = new ArrayList<>();
		u = new User();
		users.add(u);
		when(mockedQueryUser.getResultList()).thenReturn(users);
		when(mockedQueryMusic.getResultList()).thenReturn(new ArrayList<Music>());
		when(mockedQueryPlaylist.getResultList()).thenReturn(new ArrayList<Playlist>());
	}
	
	
	@Test
	public void delete_should_work_correctly() {
		String username = "user";
		String password = "123";
		
		String query1 = "from User u where u.name like :username and u.password like :password";
		String query2 = "from Music m where m.user = :user";
		String query3 = "from Playlist p where p.user = :user";
		
		// mock invocations
		when(em.createQuery(query1, User.class)).thenReturn(mockedQueryUser);
		when(em.createQuery(query2, Music.class)).thenReturn(mockedQueryMusic);
		when(em.createQuery(query3, Playlist.class)).thenReturn(mockedQueryPlaylist);

		// invoke EJB
		boolean result = ejb.delete(username, password);

		// verify that all methods have been called once
		verify(mockedQueryUser).getResultList();
		verify(em).createQuery(query1, User.class);
		verify(em).createQuery(query2, Music.class);
		verify(em).createQuery(query3, Playlist.class);

		Assert.assertThat(result, is(true));
	}
	
	
	@Test public void updateUsername_should_work_correctly() {
		String oldUsername = "old";
		String password = "123";
		String username = "new";
		String encrypted = "123";
		String encrypted2 = "1234";
		
		String query = "from User u where u.name like :username and u.password like :password";
		
		when(crypt.encrypt(password, oldUsername)).thenReturn(encrypted);
		when(crypt.encrypt(password, username)).thenReturn(encrypted2);
		when(em.createQuery(query, User.class)).thenReturn(mockedQueryUser);
		
		ejb.updateUsername(oldUsername, username, password);
		
		verify(mockedQueryUser).getResultList();
		verify(em).createQuery(query, User.class);
		verify(mockedQueryUser).setParameter("username", oldUsername);
		verify(mockedQueryUser).setParameter("password", encrypted);
		verify(crypt).encrypt(password, username);
		verify(em).merge(u);
		
	}
	
	
	@Test public void updateEmail_should_work_correctly() {
		String oldUsername = "old";
		String password = "123";
		String email = "new@123.pt";
		String encrypted = "123";
		
		String query = "from User u where u.name like :username and u.password like :password";
		
		when(crypt.encrypt(password, oldUsername)).thenReturn(encrypted);
		when(em.createQuery(query, User.class)).thenReturn(mockedQueryUser);
		
		ejb.updateEmail(oldUsername, email, password);
		
		verify(mockedQueryUser).getResultList();
		verify(em).createQuery(query, User.class);
		verify(mockedQueryUser).setParameter("username", oldUsername);
		verify(mockedQueryUser).setParameter("password", encrypted);
		verify(crypt).encrypt(password, oldUsername);
		verify(em).merge(u);
		
	}
	
	@Test public void updatePassword_should_work_correctly() {
		String oldUsername = "old";
		String password = "123";
		String old = "a";
		String encrypted = "123";
		
		String query = "from User u where u.name like :username and u.password like :password";
		
		when(crypt.encrypt(old, oldUsername)).thenReturn(encrypted);
		when(em.createQuery(query, User.class)).thenReturn(mockedQueryUser);
		
		ejb.updatePassword(oldUsername, old, password);
		
		verify(mockedQueryUser).getResultList();
		verify(em).createQuery(query, User.class);
		verify(mockedQueryUser).setParameter("username", oldUsername);
		verify(mockedQueryUser).setParameter("password", encrypted);
		verify(crypt).encrypt(password, oldUsername);
		verify(em).merge(u);
		
	}
	
	
	
	
}

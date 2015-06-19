package pt.uc.dei.aor.paj.test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import pt.uc.dei.aor.paj.EncryptEJB;
import pt.uc.dei.aor.paj.LoginEJB;
import pt.uc.dei.aor.paj.Playlist;
import pt.uc.dei.aor.paj.PlaylistEJB;
import pt.uc.dei.aor.paj.User;
import pt.uc.dei.aor.paj.UserDTO;


@RunWith(MockitoJUnitRunner.class)
public class PlaylistEJBTest {
	@Mock
	EntityManager em;

	@Mock
	LoginEJB loginEJB;
	
	@Mock
	TypedQuery<Playlist> mockedQuery;
	
	@Mock
	TypedQuery<String> mockedQueryString;
	
	@InjectMocks
	PlaylistEJB ejb;

	private User u;
	
	@Before
	public void init() {
		u = new User();
		when(mockedQuery.getResultList()).thenReturn(new ArrayList<Playlist>());
		when(mockedQueryString.getResultList()).thenReturn(new ArrayList<String>());
		when(loginEJB.findUserByUsername(Mockito.anyString())).thenReturn(u);
	}
	
	
	@Test
	public void addPlaylist_should_work_correctly() {
		final String QUERY = "from Playlist l where l.user = :user and l.title like :title";
		String username = "user";
		String playlist = "playlist";
		
		// mock invocations
		when(em.createQuery(QUERY, Playlist.class)).thenReturn(mockedQuery);
		when(mockedQuery.getResultList()).thenReturn(new ArrayList<Playlist>());
		
		// invoke EJB
		boolean result = ejb.addPlaylist(username, playlist);

		// verify that all methods have been called once
		verify(mockedQuery).setParameter("user", u);
		verify(mockedQuery).setParameter("title", playlist);
		verify(mockedQuery).getResultList();
		verify(em).createQuery(QUERY, Playlist.class);

		Assert.assertThat(result, is(true));
	}
	
	@Test
	public void listPlaylist_should_work_correctly() {
		String query = "select pl.title from Playlist pl where pl.user = :user order by pl.title";
		when(em.createQuery(query, String.class)).thenReturn(mockedQueryString);
		when(mockedQueryString.getResultList()).thenReturn(new ArrayList<String>());
		
		ejb.listPlaylist("username");
		
		verify(em).createQuery(query, String.class);
		verify(mockedQueryString).setParameter("user", u);
	}
	

}

package pt.uc.dei.aor.paj.test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
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
import pt.uc.dei.aor.paj.UploadEJB;
import pt.uc.dei.aor.paj.User;


@RunWith(MockitoJUnitRunner.class)
public class UploadEJBTest {
	
	@Mock
	EntityManager em;
	
	@Mock
	Query mockedQuery;
	
	@Mock
	TypedQuery<Music> mockedQueryMusic;
	
	@Mock 
	TypedQuery<User> mockedQueryUser;
	
	@Mock
	MusicEJB musicEJB;
	
	@InjectMocks
	UploadEJB ejb;
	
	
	@Before
	public void init() {
		List<Music> musics = new ArrayList<>();
		when(mockedQueryMusic.getResultList()).thenReturn(musics);
	}
	
	
	@Test
	public void delete_should_work_correctly() {
		String query = "delete from PlaylistEntry pe where pe.music = :music";
		int id = 1;
		Music m = new Music();
		
		when(musicEJB.findMusicListById(id)).thenReturn(m);
		when(em.createQuery(query)).thenReturn(mockedQuery);
		
		boolean result = ejb.removeMusic(id);
		
		verify(mockedQuery).executeUpdate();
		verify(mockedQuery).setParameter("music", m);
		verify(em).remove(m);
		
		Assert.assertThat(result, is(true));
	}
	
	@Test
	public void isEditable_should_work_correctly() {
		String query = "from User u where u.name = :username";
		String query2 = "from Music m where m.id = :id and m.user = :user";
		String user = "testUser";
		int id = 1;
		User u = new User();
		
		when(em.createQuery(query, User.class)).thenReturn(mockedQueryUser);
		when(em.createQuery(query2, Music.class)).thenReturn(mockedQueryMusic);
		when(mockedQueryUser.getSingleResult()).thenReturn(u);
		
		boolean result = ejb.isEditable(id, user);
		
		verify(mockedQueryUser).setParameter("username", user);
		verify(mockedQueryMusic).setParameter("id", id);
		verify(mockedQueryMusic).setParameter("user", u);
		
		Assert.assertThat(result, is(false));
	}
	
	
}

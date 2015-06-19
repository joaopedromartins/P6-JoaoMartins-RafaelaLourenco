package pt.uc.dei.aor.paj.test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import pt.uc.dei.aor.paj.Music;
import pt.uc.dei.aor.paj.MusicDTO;
import pt.uc.dei.aor.paj.MusicEJB;
import pt.uc.dei.aor.paj.PlaylistEntry;
import pt.uc.dei.aor.paj.User;


@RunWith(MockitoJUnitRunner.class)
public class MusicEJBTest {
	@Mock
	EntityManager em;

	@Mock
	TypedQuery<Music> mockedQuery;
	
	@Mock
	TypedQuery<PlaylistEntry> mockedQueryEntry;
	
	@Mock
	LoginEJB loginEJB;
	
	@InjectMocks
	MusicEJB ejb;

	private PlaylistEntry entry;
	private Music music;

	@Before
	public void init() {
		music = new Music();
		List<Music> musics = new ArrayList<>();
		musics.add(music);
		
		when(mockedQuery.getResultList()).thenReturn(musics);
		entry = new PlaylistEntry();
		List<PlaylistEntry> entries = new ArrayList<PlaylistEntry>();
		entries.add(entry);
		when(mockedQueryEntry.getResultList()).thenReturn(entries);
	}
	
	
	@Test
	public void getMusicList_should_work_correctly() {
		final String QUERY = "from Music";

		// mock invocations
		when(em.createQuery(QUERY, Music.class)).thenReturn(mockedQuery);

		// invoke EJB
		List<MusicDTO> musicList = ejb.getMusicList();

		// verify that all methods have been called once
		verify(mockedQuery).getResultList();
		verify(em).createQuery(QUERY, Music.class);

		Assert.assertThat(musicList.size(), is(equalTo(1)));
	}
	
	
	@Test
	public void findMusicListByTitle_should_work_correctly() {
		final String QUERY = "from Music m where lower(m.title) like :title";
		String title = "musicTitle";
		
		when(em.createQuery(QUERY, Music.class)).thenReturn(mockedQuery);
		
		ejb.findMusicListByTitle(title);
		
		verify(mockedQuery).getResultList();
		verify(mockedQuery).setParameter("title", "%"+title.toLowerCase()+"%");
	}
	
	
	@Test
	public void findMusicListByArtist_should_work_correctly() {
		final String QUERY = "from Music m where lower(m.author) like :artist";
		String artist = "artist";
		
		when(em.createQuery(QUERY, Music.class)).thenReturn(mockedQuery);
		
		ejb.findMusicListByArtist(artist);
		
		verify(mockedQuery).getResultList();
		verify(mockedQuery).setParameter("artist", "%"+artist.toLowerCase()+"%");
	}
	
	@Test
	public void remove_music_should_work_correctly() {
		final String QUERY = "from PlaylistEntry pe where pe.music = :music";
		Music m = new Music();
		
		when(em.createQuery(QUERY, PlaylistEntry.class)).thenReturn(mockedQueryEntry);
		
		ejb.remove(m);
		
		verify(em).remove(entry);
		verify(em).remove(m);
		
	}
	
	@Test
	public void findMusicById_should_return_the_correct_music() {
		String query = "from Music m where m.id = :id";
		int id = 1;
		
		when(em.createQuery(query, Music.class)).thenReturn(mockedQuery);
		
		Music m = ejb.findMusicListById(id);
		
		Assert.assertThat(m, is(equalTo(music)));
	}
	
	
	@Test
	public void findMusicByTitleArtistAlbum_should_work_correctly_with_no_album() {
		
		String query = "from Music m where lower(m.title) like :title and lower(m.author)"
				+ " like :artist";
		
		when(em.createQuery(query, Music.class)).thenReturn(mockedQuery);
		
		ejb.findMusicByTitleArtistAlbum("fk", "kksfj", null);
		verify(em).createQuery(query , Music.class);
	}
	
	@Test
	public void findMusicByTitleArtistAlbum_should_work_correctly_with_album() {
		
		String query = "from Music m where lower(m.title) like :title and lower(m.author)"
				+ " like :artist and lower(album) like :album";
		
		when(em.createQuery(query, Music.class)).thenReturn(mockedQuery);
		
		ejb.findMusicByTitleArtistAlbum("fk", "kksfj", "fsd~2");
		verify(em).createQuery(query , Music.class);
	}
	
	
	@Test
	public void getFilteredMusicList_should_work_correctly() {
		String username = "user";
		List<String> filters = Arrays.asList("title", "artist", "album", "genre", "year");
		List<String> activeFilters = new ArrayList<>();
		
		String query = "from Music m where m.user = :user order by m.author, m.album, m.title";
		
		when(em.createQuery(query, Music.class)).thenReturn(mockedQuery);
		ejb.getFilteredMusicList(activeFilters, filters, username);
		
		verify(em).createQuery(query, Music.class);
	}
	
	@Test
	public void getFilteredMusicList_should_work_correctly_with_filters() {
		String username = "user";
		List<String> filters = Arrays.asList("title", "artist", "album", "genre", "year");
		List<String> activeFilters = Arrays.asList("title", "artist", "album", "genre", "1957");
		
		String query = "from Music m where m.user = :user and lower(m.title) like :title and "
				+ "lower(m.artist) like :artist and lower(m.album) like :album and lower(m.genre) like :genre"
				+ " and m.year = :year order by m.author, m.album, m.title";
		
		when(em.createQuery(Mockito.anyString(), Mockito.eq(Music.class))).thenReturn(mockedQuery);
		ejb.getFilteredMusicList(activeFilters, filters, username);
		
		verify(em).createQuery(query, Music.class);
	}
	
	@Test
	public void getAppFilteredMusicList_should_work_correctly() {
		List<String> filters = Arrays.asList("title", "artist", "album", "genre", "year");
		List<String> activeFilters = new ArrayList<>();
		
		String query = "from Music m order by m.author, m.album, m.title";
		
		when(em.createQuery(query, Music.class)).thenReturn(mockedQuery);
		ejb.getAppFilteredMusicList(activeFilters, filters);
		
		verify(em).createQuery(query, Music.class);
	}
	
	@Test
	public void getAppFilteredMusicList_should_work_correctly_with_filters() {
		List<String> filters = Arrays.asList("title", "artist", "album", "genre", "year");
		List<String> activeFilters = Arrays.asList("title", "artist", "album", "genre", "1957");
		
		String query = "from Music m where lower(m.title) like :title and "
				+ "lower(m.artist) like :artist and lower(m.album) like :album and lower(m.genre) like :genre"
				+ " and m.year = :year order by m.author, m.album, m.title";
		
		when(em.createQuery(Mockito.anyString(), Mockito.eq(Music.class))).thenReturn(mockedQuery);
		ejb.getAppFilteredMusicList(activeFilters, filters);
		
		verify(em).createQuery(query, Music.class);
	}
}

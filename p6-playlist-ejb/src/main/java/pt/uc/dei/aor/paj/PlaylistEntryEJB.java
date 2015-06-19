package pt.uc.dei.aor.paj;

import java.util.ArrayList;
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

import pt.uc.dei.aor.paj.Playlist;
import pt.uc.dei.aor.paj.User;

@Stateless
@LocalBean
public class PlaylistEntryEJB {
	private static final Logger log = LoggerFactory.getLogger(PlaylistEntryEJB.class);

	@PersistenceContext(name = "Utilizador")
	private EntityManager em;
	
	@Inject
	private LoginEJB loginEJB;
	
	@Inject
	private PlaylistEJB playlistEJB;
	@Inject 
	private MusicEJB musicEJB;
	
		
	public PlaylistEntryEJB() {
		// TODO Auto-generated constructor stub
	}
	
	private boolean verifyUsername(String username) {
		if (username.length() <= 2) {
			return false;
		}
    	//testar se existe o utilizador com esse nome
    	User loggedUser = loginEJB.findUserByUsername(username);
    	if ( loggedUser == null) {
    		return false;
    	}
    	return true;
	}
	
	private boolean verifyPlaylistName(String username, String playlistname) {
		if (playlistname.length() <= 2) {
			return false;
		}
		//testar se exite playlist com esse nome
		TypedQuery<Playlist> q = em.createQuery("from Playlist l where l.user = :user and l.title like :title", Playlist.class);
		q.setParameter("user", loginEJB.findUserByUsername(username));
		q.setParameter("title", playlistname);
		List<Playlist> l = q.getResultList();
		if (l.isEmpty()) {
			return false;
		}
    	return true;
	}
	
		
	private boolean verifyMusicID(int musicid) {
		//testar se o id da musica e inteiro positivo
		if (musicid<1) {
			return false;
		}
		
		//testar se existe uma musica com esse id
		TypedQuery<Music> qm = em.createQuery("from Music m where m.id = :musicid ", Music.class);
		qm.setParameter("musicid", musicid);
		List<Music> musica = qm.getResultList();
		if (musica.isEmpty()) {
			return false;
		}
    	return true;
	}
	
	public List<PlaylistMusicDTO> findMusicsByUsernameAndPlaylistName(String username, String playlistname) {
		if (! verifyUsername(username)) {
			return null;
		}
		if (! verifyPlaylistName(username, playlistname)) {
			return null;
		} else {
    		TypedQuery<PlaylistEntry> lm = em.createQuery("select ple "
					+ "from PlaylistEntry ple "
					+ "inner join ple.playlist pl " 
					+ "where pl.user = :user"
					+ "	and pl.title like :title "
					+ " order by ple.position", PlaylistEntry.class);
			lm.setParameter("user", loginEJB.findUserByUsername(username));
			lm.setParameter("title", playlistname);
			List<PlaylistEntry> result = lm.getResultList();
			List<PlaylistMusicDTO> retorno = new ArrayList<PlaylistMusicDTO>();
			for (PlaylistEntry i: result) {
				retorno.add(new PlaylistMusicDTO(i.getMusicTitle(), i.getMusicAuthor() , 
					i.getMusicAlbum(), i.getMusicGenre(), i.getMusicDuration(), i.getMusicYear(), i.getMusicId(), i.getPosition(), i.getFilename() ) );
			}
    		return retorno;
    	}
	}
	
	public boolean delMusicfromPlaylistName(String username, String playlistname, int musicid) {
		if (! verifyUsername(username)) {
			return false;
		}
		if (! verifyPlaylistName(username, playlistname)) {
			return false;
		}
		if (! verifyMusicID(musicid)) {
			return false;
		}
    	
    	//seleccionar id da playlistentry
    	TypedQuery<Integer> lm = em.createQuery("select ple.id "
				+ "from PlaylistEntry ple "
				+ "inner join ple.playlist pl " 
				+ "where pl.user = :user"
				+ "	and pl.title like :title"
				+ " and ple.music.id = :musicid", Integer.class);
		lm.setParameter("user", loginEJB.findUserByUsername(username));
		lm.setParameter("title", playlistname);
		lm.setParameter("musicid", musicid);
		List<Integer> result = lm.getResultList();
		
		//apagar playlistentry com o id seleccionado
    	em.createQuery("delete from PlaylistEntry ple where ple.id = :pleid ").
    	setParameter("pleid", result.get(0)).executeUpdate();
    	
		return true;
	}
	
	
	public boolean moveUpMusicfromPlaylistName(String username, String playlistname, int musicid) {
		if (! verifyUsername(username)) {
			return false;
		}
		if (! verifyPlaylistName(username, playlistname)) {
			return false;
		}
		if (! verifyMusicID(musicid)) {
			return false;
		}
    	
		//seleccionar id e posicao da musica da playlist seleccionada
    	TypedQuery<Object[]> lm = em.createQuery("select ple.id, ple.position, ple.playlist.id "
				+ "from PlaylistEntry ple "
				+ "inner join ple.playlist pl " 
				+ "where pl.user = :user "
				+ "	and pl.title like :title "
				+ " and ple.music.id = :musicid ", Object[].class);
		lm.setParameter("user", loginEJB.findUserByUsername(username));
		lm.setParameter("title", playlistname);
		lm.setParameter("musicid", musicid);
		List<Object[]> selectedPLE = lm.getResultList();
		
		
		//seleccionar id da playlistentry acima
    	TypedQuery<Object[]> lup = em.createQuery("select ple.id, ple.position, ple.playlist.id "
				+ "from PlaylistEntry ple " 
				+ "where  ple.playlist.id = :plid "
				+ " and ple.position < :pleposition"
				+ " order by ple.position desc ", Object[].class);
    	lup.setParameter("plid",selectedPLE.get(0)[2]);
    	lup.setParameter("pleposition", selectedPLE.get(0)[1]);
		List<Object[]> upPLE = lup.getResultList();
		
		if (upPLE.isEmpty()) {
			return false;
		} else {
						
			em.createQuery("update PlaylistEntry ple set ple.position = :upposition where ple.id = :selectid and ple.position = :position").
			setParameter("selectid", selectedPLE.get(0)[0]).
			setParameter("position", selectedPLE.get(0)[1]).
			setParameter("upposition", upPLE.get(0)[1]).executeUpdate();
			
			em.createQuery("update PlaylistEntry ple set ple.position = :position where ple.id = :upid and ple.position = :upposition").
			setParameter("position", selectedPLE.get(0)[1]).
			setParameter("upid", upPLE.get(0)[0]).
			setParameter("upposition", upPLE.get(0)[1]).executeUpdate();
			
			return true;
		}
	
	}
	
	public boolean moveDownMusicfromPlaylistName(String username, String playlistname, int musicid) {
		if (! verifyUsername(username)) {
			return false;
		}
		if (! verifyPlaylistName(username, playlistname)) {
			return false;
		}
		if (! verifyMusicID(musicid)) {
			return false;
		}
    	
		
		
    	//seleccionar id e posicao da musica da playlist seleccionada
    	TypedQuery<Object[]> lm = em.createQuery("select ple.id, ple.position, ple.playlist.id "
				+ "from PlaylistEntry ple "
				+ "inner join ple.playlist pl " 
				+ "where pl.user = :user "
				+ "	and pl.title like :title "
				+ " and ple.music.id = :musicid ", Object[].class);
		lm.setParameter("user", loginEJB.findUserByUsername(username));
		lm.setParameter("title", playlistname);
		lm.setParameter("musicid", musicid);
		List<Object[]> selectedPLE = lm.getResultList();
		
		
		
		//seleccionar id da playlistentry abaixo
    	TypedQuery<Object[]> ldown = em.createQuery("select ple.id, ple.position, ple.playlist.id "
				+ "from PlaylistEntry ple " 
				+ "where  ple.playlist.id = :plid "
				+ " and ple.position > :pleposition"
				+ " order by ple.position asc ", Object[].class);
    	ldown.setParameter("plid",selectedPLE.get(0)[2]);
    	ldown.setParameter("pleposition", selectedPLE.get(0)[1]);
		List<Object[]> downPLE = ldown.getResultList();
		
		
		if (downPLE.isEmpty()) {
			return false;
		} else {
			
			em.createQuery("update PlaylistEntry ple set ple.position = :downposition where ple.id = :selectid and ple.position = :position").
			setParameter("selectid", selectedPLE.get(0)[0]).
			setParameter("position", selectedPLE.get(0)[1]).
			setParameter("downposition", downPLE.get(0)[1]).executeUpdate();
			
			em.createQuery("update PlaylistEntry ple set ple.position = :position where ple.id = :downid and ple.position = :downposition").
			setParameter("position", selectedPLE.get(0)[1]).
			setParameter("downid", downPLE.get(0)[0]).
			setParameter("downposition", downPLE.get(0)[1]).executeUpdate();
			
			return true;
		}
	}
	
	public boolean addMusicToPlaylist(String username, String playlistname, int musicid) {
		if (! verifyUsername(username)) {
			return false;
		}
		if (! verifyPlaylistName(username, playlistname)) {
			return false;
		}
		if (! verifyMusicID(musicid)) {
			return false;
		}
    	
		
		//Obter playlist
		TypedQuery<Playlist> q = em.createQuery("from Playlist l where l.user = :user and l.title like :title", Playlist.class);
		q.setParameter("user", loginEJB.findUserByUsername(username));
		q.setParameter("title", playlistname);
		List<Playlist> l = q.getResultList();
		Playlist selectedplaylist=l.get(0);

		TypedQuery<Music> qm = em.createQuery("from Music m where m.id = :musicid ", Music.class);
		qm.setParameter("musicid", musicid);
		List<Music> msc = qm.getResultList();
		Music selectedMusic=msc.get(0);

		
		int newPosition=-1;
		
		TypedQuery<Integer> lmax = em.createQuery("select ple.position "
				+ "from PlaylistEntry ple " 
				+ "where  ple.playlist.id = :plid "
				+ " order by ple.position desc ", Integer.class);
		lmax.setParameter("plid",selectedplaylist.getId());
		List<Integer> max = lmax.getResultList();
		if (max.isEmpty()){
			newPosition=1;
		} else if (max.get(0)>0) {
			newPosition=max.get(0)+1;
		}  
			
		
		//adiciona playlist
    	PlaylistEntry newPlaylistEntry = new PlaylistEntry(selectedplaylist, selectedMusic, newPosition);

    	em.persist( newPlaylistEntry );
    	
    	return true;
	}
}
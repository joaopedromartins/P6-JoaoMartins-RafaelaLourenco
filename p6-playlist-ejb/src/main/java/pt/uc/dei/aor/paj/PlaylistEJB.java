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

@Stateless
@LocalBean
public class PlaylistEJB {

	@PersistenceContext(name = "Utilizador")
	private EntityManager em;
	
	@Inject
	private LoginEJB loginEJB;
	
	public PlaylistEJB() {
		// TODO Auto-generated constructor stub
	}
	
	
	public boolean addPlaylist(String username, String playlistname) {
    	if (username.length() <= 2) return false;
    	if (playlistname.length() <= 2) return false;
    	User loggedUser = loginEJB.findUserByUsername(username);
    	if ( loggedUser == null) return false;
    	
    	//testar se ja exite playlist com esse nome
    	TypedQuery<Playlist> q = em.createQuery("from Playlist l where l.user = :user and l.title like :title", Playlist.class);
		q.setParameter("user", loggedUser);
		q.setParameter("title", playlistname);
		List<Playlist> l = q.getResultList();
		
		
    	if (l.isEmpty()) { 
    		//adiciona playlist
        	Playlist newPlaylist = new Playlist(loggedUser , playlistname);
        	em.persist( newPlaylist );
        	return true;
    	} else {
			return false;
    	}
    	
    }
	
	
	public boolean delPlaylist(String username, String playlistname) {
    	if (username.length() <= 2) return false;
    	if (playlistname.length() <= 2) return false;
    	
    	//testar se existe o utilizador com esse nome
    	User loggedUser = loginEJB.findUserByUsername(username);
    	if ( loggedUser == null) return false;
    	
    	//testar se exite playlist com esse nome
		TypedQuery<Playlist> q = em.createQuery("from Playlist l where l.user = :user and l.title like :title", Playlist.class);
		q.setParameter("user", loggedUser);
		q.setParameter("title", playlistname);
		List<Playlist> l = q.getResultList();
    	if (l.isEmpty()) return false;
    	
    	//apagar registos playlistentry
    	em.createQuery("delete from PlaylistEntry ple where ple.playlist = :playlist").
    	setParameter("playlist", l).executeUpdate();
    	
		//apagar playlist
    	em.createQuery("delete from Playlist l where l.user = :user and l.title like :title").
		setParameter("user", loggedUser).
    	setParameter("title", playlistname).executeUpdate();
    	
    	return true;
    }
	
	
	public boolean renPlaylist(String username, String playlistname, String playlistnewname) {
		if (username.length() <= 2) return false;
    	if (playlistname.length() <= 2) return false;
    	if (playlistnewname.length() <= 2) return false;
    	
    	//testar se existe o utilizador com esse nome
    	User loggedUser = loginEJB.findUserByUsername(username);
    	if ( loggedUser == null) return false;
    	
    	//testar se exite playlist com esse nome
		TypedQuery<Playlist> q = em.createQuery("from Playlist l where l.user = :user and l.title like :title", Playlist.class);
		q.setParameter("user", loggedUser);
		q.setParameter("title", playlistname);
		List<Playlist> l = q.getResultList();
    	if (l.isEmpty()) return false;
    	
    	//testar se exite playlist com esse novo nome
		q.setParameter("title", playlistnewname);
		l = q.getResultList();
    	if (! l.isEmpty()) return false;
		
    	//mudar o nome da playlist
    	em.createQuery("update Playlist l set l.title = :newtitle where l.user = :user and l.title like :title").
		setParameter("user", loggedUser).
		setParameter("newtitle", playlistnewname).
    	setParameter("title", playlistname).executeUpdate();
    	
		return true;
	}
	
	public List<String> listPlaylist(String username) {
    	
    	if (username.length() <= 2) return null;
    	User loggedUser = loginEJB.findUserByUsername(username);
    	
    	if ( loggedUser == null) {
    		return null;
    	}
    	
    	TypedQuery<String> q = em.createQuery("select pl.title from Playlist pl where pl.user = :user order by pl.title", String.class);
		q.setParameter("user", loggedUser);
		List<String> l = q.getResultList();
		
    	return l;
    }
	
	public List<String> listPlaylist(String username, String sortby, String ascdesc) {
    	
    	if (username.length() <= 2) return null;
    	User loggedUser = loginEJB.findUserByUsername(username);
    	
    	if ( loggedUser == null) {
    		return null;
    	}
    	
    	if ( !(sortby.equals("title") || sortby.equals("date")) ) {
    		return null;
    	}
    	
    	if ( !(ascdesc.equals("asc") || ascdesc.equals("desc")) ) {
    		return null;
    	}
    	String myquery= "select pl.title from Playlist pl where pl.user = :user order by pl."+sortby+" "+ascdesc;
    	
    	TypedQuery<String> q = em.createQuery(myquery, String.class);
		q.setParameter("user", loggedUser);
		List<String> l = q.getResultList();
		
		return l;
    }
	
	
public List<String> listPlaylistTamanho(String username, String ascdesc) {
    	
    	if (username.length() <= 2) return null;
    	User loggedUser = loginEJB.findUserByUsername(username);
    	
    	if ( loggedUser == null) {
    		return null;
    	}
    	
    	if ( !(ascdesc.equals("asc") || ascdesc.equals("desc")) ) {
    		ascdesc=""+"asc";
    	}
    	
    	String myquery= "select pl.title, count(*) as tamanho from Playlist pl left join pl.entries ple where pl.user = :user group by pl.title order by tamanho "+ascdesc;
    	
    	TypedQuery<Object[]> q = em.createQuery(myquery, Object[].class);
		q.setParameter("user", loggedUser);
		List<Object[]> l = q.getResultList();
		
		List<String> s = new ArrayList<String>();
		
		for (Object[] i:l) {
			s.add( ""+(String)i[0] );
		}
		return s;
    }
	
	
	public Playlist findPlaylistByUsernameAndPlaylistName(String username, String playlistname) {
		if (username.length() <= 2) return null;
    	if (playlistname.length() <= 2) return null;
    	
    	//testar se existe o utilizador com esse nome
    	User loggedUser = loginEJB.findUserByUsername(username);
    	if ( loggedUser == null) return null;
    	
    	//testar se exite playlist com esse nome
		TypedQuery<Playlist> q = em.createQuery("from Playlist l where l.user = :user and l.title like :title", Playlist.class);
		q.setParameter("user", loggedUser);
		q.setParameter("title", playlistname);
		List<Playlist> l = q.getResultList();
    	if (l.isEmpty()) {
    		return null;
    	} else {
    		return l.get(0);
    	}
	}
	
	public List<PlaylistMusicDTO> findMusicsByUsernameAndPlaylistName(String username, String playlistname) {
		if (username.length() <= 2) {
			return null;
		}
    	
    	if (playlistname.length() <= 2) {
    		return null;
    	}
    	
    	//testar se existe o utilizador com esse nome
    	User loggedUser = loginEJB.findUserByUsername(username);
    	if ( loggedUser == null) {
    		return null;
    	}
    	
    	//testar se exite playlist com esse nome
		TypedQuery<Playlist> q = em.createQuery("from Playlist l where l.user = :user and l.title like :title", Playlist.class);
		q.setParameter("user", loggedUser);
		q.setParameter("title", playlistname);
		List<Playlist> l = q.getResultList();
    	if (l.isEmpty()) {
    		return null;
    	} else {
    		TypedQuery<PlaylistEntry> lm = em.createQuery("select ple "
					+ "from PlaylistEntry ple "
					+ "inner join ple.playlist pl " 
					+ "where pl.user = :user"
					+ "	and pl.title like :title", PlaylistEntry.class);
			lm.setParameter("user", loggedUser);
			lm.setParameter("title", playlistname);
			List<PlaylistEntry> result = lm.getResultList();
			
			
			
			List<PlaylistMusicDTO> retorno = new ArrayList<PlaylistMusicDTO>();
			for (PlaylistEntry i: result) {
				retorno.add(new PlaylistMusicDTO(i.getMusicTitle(), i.getMusicAuthor() , 
					i.getMusicAlbum(), i.getMusicGenre(), i.getMusicDuration(), i.getMusicYear(), i.getMusicId(), i.getPosition(), i.getFilename()) );
			}
    		
			return retorno;
    	}
	}
	
}
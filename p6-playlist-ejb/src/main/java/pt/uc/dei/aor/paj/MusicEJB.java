package pt.uc.dei.aor.paj;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Stateless
@LocalBean
public class MusicEJB {

	@PersistenceContext(name="utilizadores")
	private EntityManager em;
	
	@Inject
	private LoginEJB loginEJB;
	
	
    public MusicEJB() {
        // TODO Auto-generated constructor stub
    }

    
    public List<MusicDTO> getMusicList() {
    	TypedQuery<Music> q = em.createQuery("from Music", Music.class);
    	
    	List<Music> list = q.getResultList();
    	List<MusicDTO> result = new ArrayList<>();
    	for (Music m : list) {
    		result.add(new MusicDTO(m.getTitle(), m.getAuthor(), m.getAlbum(), m.getGenre(), convertMinutes(m.getDuration()), m.getFilename(), m.getYear(), m.getId()));
    	}
    	
    	return result;
    }
    
    
    public List<MusicDTO> findMusicListByTitle(String title) {
    	TypedQuery<Music> q = em.createQuery("from Music m where lower(m.title) like :title", Music.class);
    	q.setParameter("title", "%"+title.toLowerCase()+"%");
    	List<Music> list = q.getResultList();
    	
    	List<MusicDTO> result = new ArrayList<>();
    	for (Music m : list) {
    		result.add(new MusicDTO(m.getTitle(), m.getAuthor(), m.getAlbum(), m.getGenre(), convertMinutes(m.getDuration()), m.getFilename(), m.getYear(), m.getId()));
    	}
    	
    	return result;
    }
    
    public List<MusicDTO> findMusicListByArtist(String artist) {
    	TypedQuery<Music> q = em.createQuery("from Music m where lower(m.author) like :artist", Music.class);
    	q.setParameter("artist", "%"+artist.toLowerCase()+"%");
    	List<Music> list = q.getResultList();
    	
    	List<MusicDTO> result = new ArrayList<>();
    	for (Music m : list) {
    		result.add(new MusicDTO(m.getTitle(), m.getAuthor(), m.getAlbum(), m.getGenre(), convertMinutes(m.getDuration()), m.getFilename(), m.getYear(), m.getId()));
    	}
    	
    	return result;
    }


	public List<MusicDTO> getFilteredMusicList(List<String> activeFilters, List<String> filters, String username) {
		User u = loginEJB.findUserByUsername(username);
		String query = "from Music m where m.user = :user";
		
		int index = 0;
		while (index < activeFilters.size()) {
			if (activeFilters.get(index) != null && activeFilters.get(index).length() > 0) {
				if (filters.get(index).equals("year")) {
					query += " and m."+filters.get(index)+" = :"+filters.get(index);
				}
				else {
					query += " and lower(m."+filters.get(index)+") like :"+filters.get(index);
				}
			}
			index++;
		}
		query += " order by m.author, m.album, m.title";
		
		TypedQuery<Music> q = em.createQuery(query, Music.class);
		
		index = 0;
		while (index < activeFilters.size()) {
			if (activeFilters.get(index) != null && activeFilters.get(index).length() > 0) {
				if (filters.get(index).equals("year")) {
					q.setParameter(filters.get(index), Integer.parseInt(activeFilters.get(index)));
				}
				else {
					q.setParameter(filters.get(index), "%"+activeFilters.get(index).toLowerCase()+"%");					
				}
			}
			index++;
		}
		q.setParameter("user", u);

		List<Music> list = q.getResultList();
    	
    	List<MusicDTO> result = new ArrayList<>();
    	for (Music m : list) {
    		result.add(new MusicDTO(m.getTitle(), m.getAuthor(), m.getAlbum(), m.getGenre(), convertMinutes(m.getDuration()), m.getFilename(), m.getYear(), m.getId()));
    	}
    	
    	return result;
	}
    
	
	public List<MusicDTO> getAppFilteredMusicList(List<String> activeFilters, List<String> filters) {
		String query = "from Music m";
		
		int index = 0;
		boolean first = true;
		while (index < activeFilters.size()) {
			if (activeFilters.get(index) != null && activeFilters.get(index).length() > 0) {
				if (filters.get(index).equals("year")) {
					if (first) {
						query += " where m."+filters.get(index)+" = :"+filters.get(index);
						first = false;
					}
					else {
						query += " and m."+filters.get(index)+" = :"+filters.get(index);						
					}
				}
				else {
					if (first) {
						query += " where lower(m."+filters.get(index)+") like :"+filters.get(index);
						first = false;
					}
					else {
						query += " and lower(m."+filters.get(index)+") like :"+filters.get(index);						
					}
				}
			}
			index++;
		}
		query += " order by m.author, m.album, m.title";
		
		TypedQuery<Music> q = em.createQuery(query, Music.class);
		
		index = 0;
		while (index < activeFilters.size()) {
			if (activeFilters.get(index) != null && activeFilters.get(index).length() > 0) {
				if (filters.get(index).equals("year")) {
					q.setParameter(filters.get(index), Integer.parseInt(activeFilters.get(index)));
				}
				else {
					q.setParameter(filters.get(index), "%"+activeFilters.get(index).toLowerCase()+"%");					
				}
			}
			index++;
		}
		
		List<Music> list = q.getResultList();
    	
    	List<MusicDTO> result = new ArrayList<>();
    	for (Music m : list) {
    		result.add(new MusicDTO(m.getTitle(), m.getAuthor(), m.getAlbum(), m.getGenre(), convertMinutes(m.getDuration()), m.getFilename(), m.getYear(), m.getId()));
    	}
    	
    	return result;
	}
	
	public void remove(Music m) {
		TypedQuery<PlaylistEntry> q = em.createQuery("from PlaylistEntry pe where pe.music = :music", PlaylistEntry.class);
		q.setParameter("music", m);
		List<PlaylistEntry> list = q.getResultList();
		
		for (PlaylistEntry pe : list) {
			em.remove(pe);
		}
		
		em.remove(m);
	}
	
    
	private String convertMinutes(int sec) {
		String m = String.valueOf(sec%60);
		if (m.length() == 1) m = "0"+m;
		return sec/60+"m"+m+"s";
	}


	public List<MusicDTO> findMusicByTitleArtistAlbum(String title, String author, String album) {
		TypedQuery<Music> q;
		
		if (album != null) {
			q = em.createQuery("from Music m where lower(m.title) like :title and lower(m.author)"
					+ " like :artist and lower(album) like :album", Music.class);
			q.setParameter("title", "%"+title.toLowerCase()+"%");
			q.setParameter("album", "%"+album.toLowerCase()+"%");
			q.setParameter("artist", "%"+author.toLowerCase()+"%");
		}
		else {
			q = em.createQuery("from Music m where lower(m.title) like :title and lower(m.author)"
					+ " like :artist", Music.class);
			q.setParameter("title", "%"+title.toLowerCase()+"%");
			q.setParameter("artist", "%"+author.toLowerCase()+"%");
		}
    	
    	List<Music> list = q.getResultList();
    	
    	List<MusicDTO> result = new ArrayList<>();
    	for (Music m : list) {
    		result.add(new MusicDTO(m.getTitle(), m.getAuthor(), m.getAlbum(), m.getGenre(), convertMinutes(m.getDuration()), m.getFilename(), m.getYear(), m.getId()));
    	}
    	
    	return result;
    }


	public Music findMusicListById(int id) {
		TypedQuery<Music> q = em.createQuery("from Music m where m.id = :id", Music.class);
    	q.setParameter("id", id);
    	List<Music> list = q.getResultList();
    	    	
    	return list.get(0);
	}
}

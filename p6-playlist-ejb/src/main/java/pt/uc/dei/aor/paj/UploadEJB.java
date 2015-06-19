package pt.uc.dei.aor.paj;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.servlet.http.Part;
import javax.sound.sampled.AudioFileFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;

@Stateless
public class UploadEJB {
	@PersistenceContext(name="Utilizador")
	private EntityManager em;
	
	@Inject
	private LoginEJB loginEJB;
	@Inject 
	private MusicEJB musicEJB;
	
	private static final Logger logger = LoggerFactory.getLogger(UploadEJB.class);
	
	public MusicDTO upload(Part part, String title, String author, String album, String genre, String username, String year, String path) {
		String filename = "music/"+title+"_"+author+"_"+album+".mp3";

		try {
			InputStream in = part.getInputStream();
			File outFile = new File(filename);
			OutputStream out = new FileOutputStream(outFile);
			
			byte[] buffer = new byte[4096];          
	        int bytesRead = 0;  
	        while(true) {                          
	            bytesRead = in.read(buffer);  
	            if(bytesRead > 0) {  
	            	out.write(buffer, 0, bytesRead);  
	            }else {  
	                break;  
	            }                         
	        }  
	        out.close();  
	        in.close();
	        
	        AudioFileFormat baseFileFormat = new MpegAudioFileReader().getAudioFileFormat(outFile);
			Map<String, Object> properties = baseFileFormat.properties();

			int duration = (int) ((long)properties.get("duration")/1000000);
			filename = path+"/"+filename;
			
	        Music m = new Music(title, author, album, genre, filename, duration, loginEJB.findUserByUsername(username), Integer.parseInt(year));
	        
	        em.persist(m);
	        
	        logger.info("Music created -> "+m);
			return new MusicDTO(title, author, album, genre, convertMinutes(duration), filename, Integer.parseInt(year), m.getId()); 
		}
		catch (Exception e) {
			return null;
		}
		
	}

	public boolean editMusic(int id, String title, String author, String album,
			String genre, String year) {
		TypedQuery<Music> q = em.createQuery("from Music m where m.id = :id", Music.class);
		q.setParameter("id", id);
		Music m = q.getResultList().get(0);
		
		m.setTitle(title);
		m.setAuthor(author);
		m.setAlbum(album);
		m.setGenre(genre);
		try {
			m.setYear(Integer.parseInt(year));
		}
		catch(Exception e) {
			logger.debug("year parsing error");
			return false;
		}
		
		em.persist(m);
		logger.info("Music updated -> "+m);
		return true;
	}

	public boolean removeMusic(int id) {
		Music m = musicEJB.findMusicListById(id);
		Query q = em.createQuery("delete from PlaylistEntry pe where pe.music = :music");
		q.setParameter("music", m);
		q.executeUpdate();
		
		em.remove(m);
		
		logger.info("Music deleted -> "+m);
		return true;
		
	}

	public boolean isEditable(int id, String userLogged) {
		TypedQuery<User> qU = em.createQuery("from User u where u.name = :username", User.class);
		qU.setParameter("username", userLogged);
		User u = qU.getSingleResult();
		
		TypedQuery<Music> qM = em.createQuery("from Music m where m.id = :id and m.user = :user", Music.class);
		qM.setParameter("id", id);
		qM.setParameter("user", u);
		
		List<Music> list = qM.getResultList();
		return !list.isEmpty();
	}
	
	private String convertMinutes(int sec) {
		String m = String.valueOf(sec%60);
		if (m.length() == 1) m = "0"+m;
		return sec/60+"m"+m+"s";
	}
}

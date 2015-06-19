package pt.uc.dei.aor.paj;

import java.time.LocalDate;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;


@Named
@RequestScoped
public class Uploader {
	@Inject
	private UploadEJB ejb;
	
	@Inject
	private UserSession userSession;
	
	@Inject 
	private MusicList musicList;
	
	@Inject
	private MusicFilters musicFilter;
	@Inject AppMusicFilters appFilter;
	
	
	private Part file;
	
	private String title;
	private String author;
	private String album;
	private String genre;
	private String year;
	
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public Uploader() {}
	
	public Part getFile() {
		return file;
	}

	public void setFile(Part file) {
		this.file = file;
	}
	
	
	public void upload() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		String path = session.getServletContext().getContextPath();
		ejb.upload(file, title, author, album, genre, userSession.getUsername(), year, path);
		musicFilter.updateList();
		appFilter.updateList();
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	
	public void update(int id) {
		if (ejb.editMusic(id, title, author, album, genre, year)) {
			musicFilter.updateList();
			appFilter.updateList();
		}
	}
	
	public void remove(int id) {
		if (ejb.removeMusic(id)) {
			musicFilter.updateList();
			appFilter.updateList();
		}
	}
	
	public boolean isEditable(int id) {
		return ejb.isEditable(id, userSession.getUsername());
	}
	
}

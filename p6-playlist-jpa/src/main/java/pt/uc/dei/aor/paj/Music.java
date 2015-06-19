package pt.uc.dei.aor.paj;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"title", "author", "album"}))
public class Music implements Serializable {

	private static final long serialVersionUID = -5753633708037205353L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@NotNull
	@Column(nullable=false)
	private String title;
	
	@NotNull
	@Column(nullable=false)
	private String author;
	
	@Column
	private String album;
	
	@Column
	private String genre;
	
	@NotNull
	@Column(nullable=false)
	private String filename;
	
	@NotNull
	@Column(nullable=false)
	private int duration;
	
	@Column
	private int year;

	@ManyToOne
	private User user;
	
	public Music() {
		super();
	}

	

	public Music(String title, String author, String album,
			String genre, String filename, int duration, User user, int year) {
		this.title = title;
		this.author = author;
		this.album = album;
		this.genre = genre;
		this.filename = filename;
		this.duration = duration;
		this.user = user;
		this.year = year;
	}



	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}

	public String getAlbum() {
		return album;
	}

	public String getGenre() {
		return genre;
	}

	public String getFilename() {
		return filename;
	}

	public int getDuration() {
		return duration;
	}

	public User getUser() {
		return user;
	}
   
	public int getYear() { return year; }
	
	public void setYear(int year) { this.year = year; }



	public void setId(int id) {
		this.id = id;
	}



	public void setTitle(String title) {
		this.title = title;
	}



	public void setAuthor(String author) {
		this.author = author;
	}



	public void setAlbum(String album) {
		this.album = album;
	}



	public void setGenre(String genre) {
		this.genre = genre;
	}



	public void setFilename(String filename) {
		this.filename = filename;
	}



	public void setDuration(int duration) {
		this.duration = duration;
	}



	public void setUser(User user) {
		this.user = user;
	}
	
	@Override
	public String toString() {
		return author+" : "+title+" : "+album+" : "+year;
	}
	
}

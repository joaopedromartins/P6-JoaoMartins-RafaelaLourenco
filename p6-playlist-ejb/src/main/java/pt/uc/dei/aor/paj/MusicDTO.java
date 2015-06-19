package pt.uc.dei.aor.paj;

public class MusicDTO {
	private String title;
	private String author;
	private String album;
	private String genre;
	private String duration;
	private String filename;
	private int year;
	private int id;
	
	public MusicDTO(String title, String author, String album, String genre,
			String duration, String filename, int year, int id) {
		this.title = title;
		this.author = author;
		this.album = album;
		this.genre = genre;
		this.duration = duration;
		this.filename = filename;
		this.year = year;
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

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
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	@Override
	public String toString() {
		String s = author+" - "+title;
		
		return s;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}
	
	public PlaylistMusicDTO toPlaylistMusicDTO() {
		String[] fields = duration.split("m");
		String s = fields[1].substring(0, fields[1].length()-1);
		String m = fields[0];
		
		int time = Integer.valueOf(m)*60+Integer.valueOf(s);
		
		PlaylistMusicDTO pmdto = new PlaylistMusicDTO(title, author, album, genre, time, 
				year, id, 0, filename);
		
		return pmdto;
	}
}

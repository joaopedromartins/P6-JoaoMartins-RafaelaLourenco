package pt.uc.dei.aor.paj;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"title", "user_id"}))
public class Playlist implements Serializable {
	private static final long serialVersionUID = -6391474697846239044L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@NotNull
	@Column(nullable=false)
	private String title;
	
	@NotNull
	@Column(nullable=false)
	private LocalDate date;

	@ManyToOne
	private User user;
	
	@OneToMany(mappedBy="playlist")
	private List<PlaylistEntry> entries;
	
	public Playlist() {
		super();
	}

	public Playlist(User user, String title) {
		this.user = user;
		this.title = title;
		date = LocalDate.now();
	}
   
	@Override
	public String toString() {
		return title+" -> "+date.toString();
	}

	public String getTitle() {
		return title;
	}

	public int getId() {
		return id;
	}

}

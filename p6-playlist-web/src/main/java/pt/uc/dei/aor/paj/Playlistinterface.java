package pt.uc.dei.aor.paj;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

@Named 
@SessionScoped
public class Playlistinterface implements Serializable {
	private static final long serialVersionUID = 2832340869627136905L;

	private String msgerro;
	private String username;
	private String playlistname;
	private String playlistnewname;
	private String operacao;
	private String searchOrder;
	private String searchType;
	private List<String> listaplaylistnames;
	private List<PlaylistMusicDTO> listaplaylistmusics;
	private PlaylistMusicDTO selectedmusic;
	private boolean newMusic = false;
	
	
	@Inject
	private UserSession loggeduser;

	@Inject 
	private PlaylistEJB playlist;
	
	@Inject 
	private PlaylistEntryEJB playlistEntry;
	
	public Playlistinterface() {
		operacao="criar";
		msgerro="";
		setSearchOrder("asc");
		searchType="title";
	}

	
	//Getter associados à variável operacao
	public String getOperacao() {
		return operacao;
	}
	public void setOperacao(String operacao) {
		this.operacao = operacao;
	}

	//Getter associados à variável playlistnewname
	public String getPlaylistnewname() {
		return playlistnewname;
	}
	public void setPlaylistnewname(String playlistnewname) {
		this.playlistnewname = playlistnewname;
	}

	//Getter associados à variável playlistname
	public String getPlaylistname() {
		return playlistname;
	}
	public void setPlaylistname(String playlistname) {
		this.playlistname = playlistname;
	}

	//Getter associados à variável order
	public String getSearchOrder() {
		return searchOrder;
	}
	public void setSearchOrder(String searchOrder) {
		this.searchOrder = searchOrder;
	}

	//Getter associados à variável searchType
	public String getSearchType() {
		return searchType;
	}
	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}


	//Getter associados à variável username
	public String getUsername() {
		this.username=loggeduser.getUsername();
		return username;
	}

	//Getter associados à lista Listaplaylistnames
	public List<String> getListaplaylistnames() {
		//this.listaplaylistnames=playlist.listPlaylist(loggeduser.getUsername());
		//return listaplaylistnames;
		
		if (searchOrder.equals("asc") || searchOrder.equals("desc")) {
			if ( searchType.equals("title") || searchType.equals("date") ) {
				this.listaplaylistnames=playlist.listPlaylist(loggeduser.getUsername(), searchType, searchOrder);
								
				return listaplaylistnames;
			} else if ( searchType.equals("tamanho") ) {
				this.listaplaylistnames=playlist.listPlaylistTamanho(loggeduser.getUsername(), searchOrder);
			} else {
				//ignora seleccoes de searchType invalidas
				this.listaplaylistnames=playlist.listPlaylist(loggeduser.getUsername());
			}
		}
		else {
			//ignora seleccoes de ordem invalidas
			this.listaplaylistnames=playlist.listPlaylist(loggeduser.getUsername());
		}
		return listaplaylistnames;
	}
	
	//Getter associados à lista Listaplaylistmusics
	public List<PlaylistMusicDTO> getListaplaylistmusics() {
		this.listaplaylistmusics = playlistEntry.findMusicsByUsernameAndPlaylistName( loggeduser.getUsername(), playlistname);
		return listaplaylistmusics;
	}

	//Getter associados ao DTO selectedmusic
	public PlaylistMusicDTO getSelectedmusic() {
		return this.selectedmusic;
	}


	//Getter  associados à variável msgerro
	public String getMsgerro() {
		return msgerro;
	}

	

	//metodo que identifica se radiobotton esta activo na posicao criar 
	public boolean typecriar() {
		if (operacao.equals("criar")) {
			return true;
		} else {
			return false;
		}
	}
	
	//metodo que identifica se radiobotton esta activo na posicao editar 
	public boolean typeeditar() {
		if (operacao.equals("editar")) {
			return true;
		} else {
			return false;
		}
	}

	//metodo para criar uma playlist
	public void criaplaylist() {
		if (playlist.addPlaylist( loggeduser.getUsername(), playlistname) ) {
			addMessage("Adicionada a playlist: "+playlistname, FacesMessage.SEVERITY_INFO);
			playlistname=null;
		} else {
			addMessage("ERRO ao adicionar a playlist: "+playlistname, FacesMessage.SEVERITY_ERROR);
		}
	}
	
	//metodo para editar uma playlist
//	public String editaplaylist() {
//		
//		return "/resources/secure/playlist?faces-redirect=true";
//	}
	
	//metodo para apagar uma playlist
	public void apagaplaylist() {
		if (playlist.delPlaylist( loggeduser.getUsername(), playlistname) ) {
			addMessage("Apagada a playlist: "+playlistname, FacesMessage.SEVERITY_INFO);
			setPlaylistname(null);
		} else {
			addMessage("ERRO ao apagar a playlist: "+playlistname, FacesMessage.SEVERITY_ERROR);
		}
		
	}

	
	//metodo para seleccionar uma playlist
	public void selectplaylist(ActionEvent ae) {
		//atribui o nome da playlist correspondente ao botao da linha seleccionada
		playlistname = (String)ae.getComponent().getAttributes().get("selectedline");
		//apaga musica seleccionada
		selectedmusic=null;
	}
	
	//metodo para seleccionar uma musica da playlist
	public void selectmusic(ActionEvent ae) {
		//atribui o nome da playlist correspondente ao botao da linha seleccionada
		int selectedmusicid = (int)(ae.getComponent().getAttributes().get("selectedmusicline"));
		for (PlaylistMusicDTO i:listaplaylistmusics) {
			if (selectedmusicid==i.getId()) {
				this.selectedmusic=i;
			}
		}
		newMusic = false;
	}
	
	
	//metodo para mudar o nome de uma playlist
	public void renameplaylist() {
		//verificar se ja existe a playlist com o novo nome
		boolean existe = false;
		for (String i: listaplaylistnames) {
			if (playlistnewname.equals(i)) {
				existe=true;
				break;
			}
		}
		if (existe) {
			addMessage("Erro: Já existe uma playlist com esse nome: "+playlistnewname, FacesMessage.SEVERITY_ERROR);
		} else if(playlist.renPlaylist( loggeduser.getUsername(), playlistname, playlistnewname)) {
			playlistname = playlistnewname;
		}
		
		
	}
	
	//metodo para apagar uma musica da playlist
	public void delmusicfromplaylist() {
		if ( playlistEntry.delMusicfromPlaylistName( loggeduser.getUsername(), playlistname, selectedmusic.getId()) ) {
			msgerro="";
			selectedmusic=null;
		} else {
			msgerro="Erro ao apagar uma musica da playlist.";
		}
	}
	
	//metodo para mover para cima uma musica na playlist
	public void moveupmusicfromplaylist() {
		if ( playlistEntry.moveUpMusicfromPlaylistName( loggeduser.getUsername(), playlistname, selectedmusic.getId()) ) {
			msgerro="";
			//selectedmusic=null;
		} else {
			msgerro="Erro ao alterar a sequência de musicas da playlist.";
		}
	}
	
	//metodo para mover para baixo uma musica na playlist
	public void movedownmusicfromplaylist() {
		if ( playlistEntry.moveDownMusicfromPlaylistName( loggeduser.getUsername(), playlistname, selectedmusic.getId()) ) {
			msgerro="";
			//selectedmusic=null;
		} else {
			msgerro="Erro ao alterar a sequência de musicas da playlist.";
		}
	}
	
	
	public void showMusicInfo(MusicDTO music) {
		selectedmusic = music.toPlaylistMusicDTO();
		newMusic = true;
	}


	public boolean isNewMusic() {
		return newMusic;
	}


	public void setNewMusic(boolean newMusic) {
		this.newMusic = newMusic;
	}
	
	
	public void addMusicToPlaylist() {

		if (playlistname == null || selectedmusic == null) {
			addMessage("Not possibles to add this music to playlist", FacesMessage.SEVERITY_ERROR);
			return;
		}

		if (!playlistEntry.addMusicToPlaylist(loggeduser.getUsername(), playlistname, selectedmusic.getId())) {
			addMessage("Not possible to add this music to playlist", FacesMessage.SEVERITY_ERROR);
		}
	}


	private void addMessage(String message, Severity type) {
		FacesContext.getCurrentInstance().
        addMessage(null,
            new FacesMessage(type,
            		message, null));
	}
	
	public void reset() {
		playlistname = null;
	}
}

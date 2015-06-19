package pt.uc.dei.aor.paj.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import pt.uc.dei.aor.paj.PlaylistEJB;
import pt.uc.dei.aor.paj.UserSession;


@FacesValidator("playlistValidator")
public class PlaylistValidator implements Validator {
	@Inject 
	private PlaylistEJB ejb;
	@Inject 
	private UserSession user;
	  
    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    	String title = (String) value;
    	
        if (title == null) {
            return;
        }
        
        if (title.length() < 2) {
        	throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Playlist title too short", null));
        }
        
        if (ejb.findPlaylistByUsernameAndPlaylistName(user.getUsername(), title) != null) {
        	throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Playlist with that name already created", null));
        }
    }

}
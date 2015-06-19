package pt.uc.dei.aor.paj.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import pt.uc.dei.aor.paj.LoginEJB;


@FacesValidator("usernameValidator")
public class UsernameValidator implements Validator {
	@Inject LoginEJB ejb;
	  
    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    	String username = (String) value;
    	
        if (username == null) {
            return;
        }
        
        if (username.contains("@")) {
        	throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Username contains invalid characters", null));
        }
        
        if (ejb.findUserByUsername(username) != null) {
        	throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Username already registered", null));
        }
    }

}
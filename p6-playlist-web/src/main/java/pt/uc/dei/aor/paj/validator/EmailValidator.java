package pt.uc.dei.aor.paj.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import pt.uc.dei.aor.paj.LoginEJB;


@FacesValidator("emailValidator")
public class EmailValidator implements Validator {
	@Inject LoginEJB ejb;
	private static final String EMAIL_REGEXP =
	            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	 
    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    	String email = (String) value;
    	
        if (email == null) {
            return;
        }
        
        Pattern mask = Pattern.compile(EMAIL_REGEXP);
        Matcher matcher = mask.matcher(email);
        if (!matcher.matches()) {
        	throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Email format is incorrect", null));
        }
        
        if (ejb.findUserByEmail(email) != null) {
        	throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Email is already registed", null));
        }
    }

}
package pt.uc.dei.aor.paj.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;


@FacesValidator("passwordValidator")
public class PasswordValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    	String password = (String) value;
    	UIInput otherComp = (UIInput) component.getAttributes().get("confirm");
    	String confirm = (String) otherComp.getSubmittedValue();
    			
    	if (password == null || confirm == null) {
            return;
        }
        
        if (!password.equals(confirm)) {
        	throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Passwords don't match", null));
        }
    }

}
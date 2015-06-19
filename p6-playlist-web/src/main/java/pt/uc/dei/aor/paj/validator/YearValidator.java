package pt.uc.dei.aor.paj.validator;

import java.time.LocalDate;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import pt.uc.dei.aor.paj.LoginEJB;


@FacesValidator("yearValidator")
public class YearValidator implements Validator {
	  
    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    	String year = (String) value;
    	
        if (year == null || year == "") {
            return;
        }
        int yearInt;
        
        try {
        	yearInt = Integer.parseInt(year);
        }
        catch (Exception e) {
        	throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Year invalid", null));        	
        }
        
        if (yearInt < 500) {
        	throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Year invalid", null));   
        }
        else if (yearInt > LocalDate.now().getYear()) {
        	throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Year can´t be in the future", null));
        }
       
    }

}
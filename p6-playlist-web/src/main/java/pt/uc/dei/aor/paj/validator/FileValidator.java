package pt.uc.dei.aor.paj.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.servlet.http.Part;

import pt.uc.dei.aor.paj.LoginEJB;


@FacesValidator("fileValidator")
public class FileValidator implements Validator {
	  
    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    	final long MAX_SIZE = 1024*1024*10;
    	Part file = (Part) value;
    	
    	if (file.getSize() > MAX_SIZE) {
        	throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "File too big", null));
        }
        
        String filename = file.getSubmittedFileName();
        String[] fields = filename.split("\\.");
        
        if (fields.length != 2 || !fields[1].equals("mp3")) {
        	throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please upload a mp3 file", null));
        }
    }
}
package pt.uc.dei.aor.paj;

import static org.mockito.Mockito.when;

import javax.faces.validator.ValidatorException;
import javax.servlet.http.Part;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pt.uc.dei.aor.paj.validator.FileValidator;

@RunWith(MockitoJUnitRunner.class)
public class FileValidatorTest {
	
	@Mock
	Part file;
	
	private FileValidator validator = new FileValidator();
	
	
	
	@Test(expected=ValidatorException.class)
	public void files_not_mp3_should_throw_exception() {
		when(file.getSubmittedFileName()).thenReturn("ficheiroNaoMp3");
		when(file.getSize()).thenReturn(1000L);
		
		validator.validate(null, null, file);
	}
	
	@Test
	public void files_mp3_should_not_throw_exception() {
		when(file.getSubmittedFileName()).thenReturn("ficheiroNao.mp3");
		when(file.getSize()).thenReturn(1000L);
		
		validator.validate(null, null, file);
	}
	
	
}

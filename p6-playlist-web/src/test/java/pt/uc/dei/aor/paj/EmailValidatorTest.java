package pt.uc.dei.aor.paj;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import javax.faces.validator.ValidatorException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pt.uc.dei.aor.paj.LoginEJB;
import pt.uc.dei.aor.paj.validator.EmailValidator;

@RunWith(MockitoJUnitRunner.class)
public class EmailValidatorTest {
	
	@Mock
	private LoginEJB ejb;
	
	@InjectMocks
	private EmailValidator validator;
	
	@Before
	public void init() {
		
	}
	
	@Test(expected=ValidatorException.class)
	public void incorrect_format_email_should_throw_exception() {
		validator.validate(null, null, "aa.pt");
	}
	
	@Test
	public void correct_format_email_should_not_throw_exception() {
		validator.validate(null, null, "a@a.pt");
		verify(ejb).findUserByEmail("a@a.pt");
	}
	
	@Test
	public void should_delegate_to_required_empty_email() {
		validator.validate(null, null, null);
		verify(ejb, never()).findUserByEmail(null);
	}
}

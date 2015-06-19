package pt.uc.dei.aor.paj;

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
import pt.uc.dei.aor.paj.validator.UsernameValidator;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UsernameValidatorTest {
	
	@Mock
	private LoginEJB ejb;
	
	@InjectMocks
	private UsernameValidator validator;
	
	@Before
	public void init() {
		
	}
	
	@Test(expected=ValidatorException.class)
	public void incorrect_format_username_should_throw_exception() {
		validator.validate(null, null, "@merico");
	}
	
	@Test
	public void correct_format_username_should_not_throw_exception() {
		validator.validate(null, null, "americo");
		verify(ejb).findUserByUsername("americo");
	}
	
	@Test
	public void should_delegate_to_required_empty_username() {
		validator.validate(null, null, null);
		verify(ejb, never()).findUserByUsername(null);
	}
}

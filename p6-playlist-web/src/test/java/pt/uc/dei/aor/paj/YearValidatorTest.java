package pt.uc.dei.aor.paj;

import java.time.LocalDate;

import javax.faces.validator.ValidatorException;

import org.junit.Test;

import pt.uc.dei.aor.paj.validator.YearValidator;

public class YearValidatorTest {
	
	private YearValidator validator = new YearValidator();
	
	
	
	@Test(expected=ValidatorException.class)
	public void not_numbers_should_throw_exception() {
		validator.validate(null, null, "data");
	}
	
	@Test
	public void numbers_should_not_throw_exception() {
		validator.validate(null, null, "2000");
	}
	
	@Test(expected=ValidatorException.class)
	public void should_throw_an_exception_with_a_future_year() {
		validator.validate(null, null, String.valueOf(LocalDate.now().getYear()+1
				));
	}
	
	@Test
	public void should_delegate_to_required_with_empty_year() {
		validator.validate(null, null, null);
	}
	
}

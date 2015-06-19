package pt.uc.dei.aor.paj.test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pt.uc.dei.aor.paj.EncryptEJB;


public class EncryptEJBTest {
	
	private EncryptEJB crypt;
	
		@Before
		public void init() {
			crypt = new EncryptEJB();
	    }
		
		
		@Test
		public void encryption_should_return_the_correct_result() {
			String username = "user";
			String input = "a";
			String output = "2F905CFC958868484223497B32F4C6EC08C83573A3AA29241AF76907636836A4";
			
			Assert.assertThat(crypt.encrypt(input, username), is(equalTo(output)));
		}
		
		
		@Test
		public void convert_to_hex_should_return_the_correct_result() {
			byte[] input = new byte[]{(byte) 255,0,64,33,15};
			
			Assert.assertThat(EncryptEJB.bytesToHex(input), is(equalTo("FF0040210F")));
		}
}

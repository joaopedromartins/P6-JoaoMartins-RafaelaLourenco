package pt.uc.dei.aor.paj;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class EncryptEJB
 */
@Stateless
@LocalBean
public class EncryptEJB {

    /**
     * Default constructor. 
     */
    public EncryptEJB() {
        // TODO Auto-generated constructor stub
    }

    public String encrypt(String password, String salt) {
    	MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update((password+salt).getBytes());
			return bytesToHex(md.digest());
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
    	
	}
    
    public static String bytesToHex(byte[] bytes) {
    	final char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    
    
}

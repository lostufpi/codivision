package br.ufpi.codivision.core.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Irvayne Matheus
 *
 */
public class GenerateHashPasswordUtil {
	
	/**
	 * <p>
	 * The generateHash method takes a string as a parameter and returns another
	 * string, but with a different value that has a value equivalent to that
	 * received.
	 * </p>
	 * <p>
	 * This value is generated through an algorithm Hash (SHA-256).
	 * </p>
	 * <p>
	 * It transforms the hash formed from the string in a string of hexadecimal
	 * value that represents its value in a different format.
	 * </p>
	 * 
	 * @param password
	 * @return String
	 **/
	public static String generateHash(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		
		MessageDigest algorithm = MessageDigest.getInstance("SHA-256");
		byte messageDigest[] = algorithm.digest(password.getBytes("UTF-8"));
		StringBuilder hexString = new StringBuilder();
		for (byte b : messageDigest) {
			hexString.append(String.format("%02X", 0xFF & b));
		}
		String passwordHex = hexString.toString();
		return passwordHex;
	}
}

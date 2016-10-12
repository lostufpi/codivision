package br.ufpi.codivision.core.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * @author Irvayne Matheus
 *
 */
public class Gravatar {
	
  private static String hex(byte[] array) {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < array.length; ++i) {
      sb.append(Integer.toHexString((array[i]
          & 0xFF) | 0x100).substring(1,3));        
      }
      return sb.toString();
  }
  /**
   * This method generate standard URL gravatar
   * @param email - e-mail to be encrypted
   * @return Url String referring to the image gravatar
   */
  public static String urlGravatar (String email) {
      try {
      MessageDigest md = 
          MessageDigest.getInstance("MD5");
      return "http://www.gravatar.com/avatar/" + hex (md.digest(email.getBytes("CP1252")));
      } catch (NoSuchAlgorithmException e) {
      } catch (UnsupportedEncodingException e) {
      }
      return null;
  }
}

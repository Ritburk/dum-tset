package net.cmacpherson.mud;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

  private static MessageDigest md;
  
  public static String encode(String str) {
    if (md == null)
      try {
        md = MessageDigest.getInstance("MD5");
      } catch (NoSuchAlgorithmException e) {}
    byte[] array = MD5.md.digest(str.getBytes());
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < array.length; i++)
      sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
    return sb.toString();
  }
}


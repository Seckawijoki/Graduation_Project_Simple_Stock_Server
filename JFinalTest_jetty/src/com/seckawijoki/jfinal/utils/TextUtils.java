package com.seckawijoki.jfinal.utils;

import com.sun.istack.internal.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 瑶琴频曲羽衣魂 on 2017/11/23 at 19:10.
 */

public class TextUtils {
  private TextUtils(){
    System.out.println("TextUtils.TextUtils(): ");
  }
  public static boolean isEmailValid(String email){
    String regex = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(email);
    return matcher.matches();
  }
  public static boolean isPhoneValid(String phone){
    String regex = "(13\\d|14[57]|15[^4,\\D]|17[13678]|18\\d)\\d{8}|170[0589]\\d{7}";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(phone);
    return matcher.matches() && phone.length() >= 11;
  }
  /**
   * Returns true if a and b are equal, including if they are both null.
   * <p><i>Note: In platform versions 1.1 and earlier, this method only worked well if
   * both the arguments were instances of String.</i></p>
   * @param a first CharSequence to check
   * @param b second CharSequence to check
   * @return true if a and b are equal
   */
  public static boolean equals(CharSequence a, CharSequence b) {
    if (a == b) return true;
    int length;
    if (a != null && b != null && (length = a.length()) == b.length()) {
      if (a instanceof String && b instanceof String) {
        return a.equals(b);
      } else {
        for (int i = 0; i < length; i++) {
          if (a.charAt(i) != b.charAt(i)) return false;
        }
        return true;
      }
    }
    return false;
  }

  /**
   * Returns true if the string is null or 0-length.
   * @param str the string to be examined
   * @return true if str is null or zero length
   */
  public static boolean isEmpty(@Nullable CharSequence str) {
    return str == null || str.length() == 0;
  }


}

package com.seckawijoki.jfinal.utils;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by 瑶琴频曲羽衣魂 on 2018/1/8 at 16:45.
 */

public class DateUtils {
  private DateUtils(){}
  private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
  public static long dateStringToLong(String date){
    try {
      return format.parse(date).getTime();
    } catch ( ParseException e ) {
      e.printStackTrace();
      return 0;
    }
  }
}

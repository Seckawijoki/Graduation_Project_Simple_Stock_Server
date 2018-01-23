package com.seckawijoki.jfinal.utils;

import com.jfinal.plugin.activerecord.Record;

import org.json.JSONObject;

/**
 * Created by 瑶琴频曲羽衣魂 on 2018/1/19 at 21:35.
 */

public class JsonPackageUtils {
  private JsonPackageUtils(){}
  public static JSONObject parseRecord(Record record, String ...keys){
    JSONObject jsonObject = new JSONObject();
    for ( int i = 0 ; i < keys.length ; i++ ) {
      String value = record.getStr(keys[i]);
      if (TextUtils.isEmpty(value))
        value = "";
      jsonObject.put(keys[i], value);
    }
    return jsonObject;
  }
}

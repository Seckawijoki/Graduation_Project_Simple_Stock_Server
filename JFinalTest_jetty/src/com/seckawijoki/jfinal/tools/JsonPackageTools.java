package com.seckawijoki.jfinal.tools;

import com.jfinal.plugin.activerecord.Record;
import com.seckawijoki.jfinal.utils.TextUtils;

import org.json.JSONObject;

/**
 * Created by 瑶琴频曲羽衣魂 on 2018/1/19 at 21:35.
 */

public class JsonPackageTools {
  private JsonPackageTools(){}
  public static JSONObject addRecord(JSONObject jsonObject, Record record, String ...keys){
    for ( int i = 0 ; i < keys.length ; i++ ) {
      String value = record.getStr(keys[i]);
      if ( TextUtils.isEmpty(value))
        value = "";
      jsonObject.put(keys[i], value);
    }
    return jsonObject;
  }
  public static JSONObject parseRecord(Record record, String ...keys){
    return addRecord(new JSONObject(), record, keys);
  }
}

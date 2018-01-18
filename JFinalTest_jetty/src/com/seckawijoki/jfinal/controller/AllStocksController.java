package com.seckawijoki.jfinal.controller;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by 瑶琴频曲羽衣魂 on 2017/12/5 at 16:08.
 */

public class AllStocksController extends Controller {
  public void getAllStocks(){
    List<Record> list =
            Db.find("select * from all_stocks");
    if (list == null || list.size() <= 0){
      renderText(null);
      return;
    }
    JSONArray idArray = new JSONArray();
    JSONArray nameArray = new JSONArray();
    JSONArray typeArray = new JSONArray();
    for ( int i = 0 ; i < list.size() ; i++ ) {
      Record record = list.get(i);
      JSONObject json = new JSONObject();
      idArray.put( record.getStr("stockId"));
      nameArray.put(record.getStr("stockName"));
      typeArray.put(record.getInt("stockType"));
    }
    JSONObject json = new JSONObject();
    json.put("stockId", idArray);
    json.put("stockName", nameArray);
    json.put("stockType", typeArray);
    renderText(json.toString());
  }
}

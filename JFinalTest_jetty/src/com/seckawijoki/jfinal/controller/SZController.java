package com.seckawijoki.jfinal.controller;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.seckawijoki.jfinal.utils.TextUtils;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by 瑶琴频曲羽衣魂 on 2017/11/26 at 11:04.
 */

public class SZController extends Controller {
  public void getStockIds(){
    String startPara = getPara("start");
    String limitPara = getPara("limit");
    int limit, start;
    if ( TextUtils.isEmpty(limitPara)){
      limit = 1000;
    } else {
      limit = Integer.valueOf(limitPara);
    }
    if (TextUtils.isEmpty(startPara)){
      start = 0;
    } else {
      start = Integer.valueOf(startPara);
    }
    System.out.println("SZController.getStockIds(): start = " + start);
    System.out.println("SZController.getStockIds(): limit = " + limit);
    List<Record> list = Db.find("select stockId from sz limit ?,?", start, limit);
    JSONArray jsonArray = new JSONArray();
    System.out.println("SZController.getStockIds(): list.size() = " + list.size());
    for ( int i = 0 ; i < list.size() ; i++ ) {
      Record record = list.get(i);
      jsonArray.put(record.getStr("stockId"));
    }
    System.out.println("SZController.getStockIds(): jsonArray = " + jsonArray);
    renderJson(jsonArray.toString());
  }
}

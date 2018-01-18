package com.seckawijoki.jfinal.controller;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by 瑶琴频曲羽衣魂 on 2017/12/5 at 16:01.
 */

public class StockTypeController extends Controller{

  public void getStockType(){
    List<Record> stockTypeList =
            Db.find("select * from stock_type");
    if (stockTypeList == null || stockTypeList.size() <= 0){
      renderText(null);
      return;
    }
    JSONArray typeIdArray = new JSONArray();
    JSONArray typeNameArray = new JSONArray();
    for ( int i = 0 ; i < stockTypeList.size() ; i++ ) {
      Record record = stockTypeList.get(i);
      typeIdArray.put(record.getStr("stockTypeId"));
      typeNameArray.put(record.getStr("stockTypeName"));
    }
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("stockTypeId", typeIdArray);
    jsonObject.put("stockTypeName", typeNameArray);
    renderJson(jsonObject.toString());
  }
}

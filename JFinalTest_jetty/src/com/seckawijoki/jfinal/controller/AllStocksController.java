package com.seckawijoki.jfinal.controller;

import com.jfinal.core.Controller;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.seckawijoki.jfinal.constants.server.MoJiReTsu;
import com.seckawijoki.jfinal.tools.SinaRequestTools;
import com.seckawijoki.jfinal.utils.OkHttpUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by 瑶琴频曲羽衣魂 on 2017/12/5 at 16:08.
 */

public class AllStocksController extends Controller {
  public void getHotStocks(){
    int hotStockCount = getParaToInt(MoJiReTsu.HOT_STOCK_COUNT, 9);
    List<Record> list = Db.find(Db.getSqlPara("getHotStocks",
            Kv.by(MoJiReTsu.HOT_STOCK_COUNT, hotStockCount)));
    JSONArray array = new JSONArray();
//    System.out.println("AllStocksController.getHotStocks(): list = " + list);
    for ( int i = 0 ; i < list.size() ; i++ ) {
      Record record = list.get(i);
//      System.out.println("AllStocksController.getHotStocks(): record = " + record);
      String stockId;
      int stockType;
      JSONObject jsonObject = SinaRequestTools.getSinaQuotationToJson(
              stockId = record.getStr(MoJiReTsu.STOCK_ID),
              stockType = record.getInt(MoJiReTsu.STOCK_TYPE)
      );
      jsonObject.put(MoJiReTsu.STOCK_NAME, record.getStr(MoJiReTsu.STOCK_NAME))
              .put(MoJiReTsu.STOCK_TABLE_ID, record.getStr(MoJiReTsu.STOCK_TABLE_ID))
              .put(MoJiReTsu.STOCK_ID, stockId)
              .put(MoJiReTsu.STOCK_TYPE, stockType);
      array.put(jsonObject);
    }
    renderJson(array.toString());
  }

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

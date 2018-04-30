package com.seckawijoki.jfinal.controller;

import com.jfinal.core.Controller;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.seckawijoki.jfinal.constants.client.TopStockOrder;
import com.seckawijoki.jfinal.constants.client.TopStockType;
import com.seckawijoki.jfinal.constants.server.MoJiReTsu;
import com.seckawijoki.jfinal.tools.JsonPackageTools;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by 瑶琴频曲羽衣魂 on 2018/3/25 at 16:52.
 */

public class RecommendationsController extends Controller{
  public void getHotStocks(){
    int hopStockCount = getParaToInt(MoJiReTsu.HOT_STOCK_COUNT, 10);
    List<Record> list = Db.find(Db.getSqlPara("getHotStocks",
            Kv.by(MoJiReTsu.HOT_STOCK_COUNT, hopStockCount)));
    JSONArray jsonArray = new JSONArray();
    for ( int i = 0 ; i < list.size() ; i++ ) {
      Record record = list.get(i);
      JSONObject jsonObject = JsonPackageTools.parseRecord(
              record,
              MoJiReTsu.STOCK_TABLE_ID,
              MoJiReTsu.STOCK_ID,
              MoJiReTsu.STOCK_NAME,
              MoJiReTsu.CURRENT_PRICE,
              MoJiReTsu.CURRENT_POINT,
              MoJiReTsu.FLUCTUATION_RATE,
              MoJiReTsu.TURNOVER,
              MoJiReTsu.VOLUME
      );
      jsonArray.put(jsonObject);
    }
    renderJson(jsonArray.toString());

  }

  public void getTopSeveralStocks(){
    int topStockType = getParaToInt(MoJiReTsu.TOP_STOCK_TYPE);
    boolean topStockOrder = getParaToBoolean(MoJiReTsu.TOP_STOCK_ORDER);
    int topStockCount = getParaToInt(MoJiReTsu.TOP_STOCK_COUNT);
    String sqlKey = "getTopSeveralFluctuationRate";
    if (topStockOrder == TopStockOrder.DESC) {
      switch ( topStockType ) {
        case TopStockType.FLUCTUATION_RATE:
          sqlKey = "getTopSeveralFluctuationRate";
          break;
        case TopStockType.CURRENT_PRICE:
          sqlKey = "getTopSeveralCurrentPrice";
          break;
        case TopStockType.TURNOVER:
          sqlKey = "getTopSeveralTurnover";
          break;
        case TopStockType.VOLUME:
          sqlKey = "getTopSeveralVolume";
          break;
      }
    } else if (topStockOrder == TopStockOrder.ASC){
      switch ( topStockType ){
        case TopStockType.FLUCTUATION_RATE:
          sqlKey = "getBottomSeveralFluctuationRate";
          break;
        case TopStockType.CURRENT_PRICE:
          sqlKey = "getBottomSeveralCurrentPrice";
          break;
      }
    }
    List<Record> list = Db.find(Db.getSqlPara(sqlKey,  Kv.by(MoJiReTsu.TOP_STOCK_COUNT, topStockCount))
    );
    JSONArray jsonArray = new JSONArray();
    for ( Record record : list ) {
      jsonArray.put(JsonPackageTools.parseRecord(
              record,
              MoJiReTsu.STOCK_TABLE_ID,
              MoJiReTsu.STOCK_ID,
              MoJiReTsu.STOCK_NAME,
              MoJiReTsu.CURRENT_PRICE,
              MoJiReTsu.FLUCTUATION_RATE,
              MoJiReTsu.TURNOVER,
              MoJiReTsu.VOLUME
      ));
    }
    renderJson(jsonArray.toString());
  }
}

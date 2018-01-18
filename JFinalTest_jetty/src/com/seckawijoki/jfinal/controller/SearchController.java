package com.seckawijoki.jfinal.controller;

import com.jfinal.core.Controller;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.seckawijoki.jfinal.utils.DateUtils;
import com.seckawijoki.jfinal.utils.MyDbUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by 瑶琴频曲羽衣魂 on 2017/12/16 at 19:37.
 */

public class SearchController extends Controller {
  public void getStockSearchHistory() {
    long userId = getParaToLong("userId");
    List<Record> stockRecordList = Db.find(
            Db.getSqlPara("getStockSearchHistory",
                    Kv.by("userId", userId)
                            .set("limit", 20)
            )
    );
    System.out.println("SearchController.getStockSearchHistory(): stockRecordList = " + stockRecordList);
    JSONArray jsonArray = new JSONArray();
    for ( int i = 0 ; i < stockRecordList.size() ; i++ ) {
      Record stockRecord = stockRecordList.get(i);
      System.out.println("SearchController.getStockSearchHistory(): stockRecord = " + stockRecord);
      long stockTableId = stockRecord.getLong("stockTableId");
      Record recordInFavorite =
              Db.findFirst(
                      Db.getSqlPara("getStockInFavorite",
                              Kv.by("stockTableId", stockTableId)
                                      .set("userId", userId)));
      System.out.println("SearchController.getStockSearchHistory(): recordInFavorite = " + recordInFavorite);
      jsonArray.put(new JSONObject()
              .put("stockTableId", stockRecord.getLong("stockTableId"))
              .put("stockId", stockRecord.getStr("stockId"))
              .put("stockType", stockRecord.getInt("stockType"))
              .put("stockName", stockRecord.getStr("stockName"))
              .put("searchTime", DateUtils.dateStringToLong(
                      stockRecord.getStr("searchTime")))
              .put("favorite", recordInFavorite != null));
    }
    renderJson(jsonArray.toString());
  }

  public void searchForMatchedStocks() {
    String userId = getPara("userId");
    String search = getPara("search");
    int limit = getParaToInt("limit", 20);
    List<Record> stockRecordList =
            Db.find(Db.getSqlPara("searchForMatchedStocks",
                    Kv.by("search", search).set("limit", limit)));
    System.out.println("SearchController.searchForMatchedStocks(): stockRecordList = " + stockRecordList);
    JSONArray jsonArray = new JSONArray();
    for ( int i = 0 ; i < stockRecordList.size() ; i++ ) {
      Record stockRecord = stockRecordList.get(i);
//      System.out.println("SearchController.searchForMatchedStocks(): stockRecord = " + stockRecord);
      long stockTableId = stockRecord.getLong("stockTableId");
      Record recordInFavorite =
              Db.findFirst(
                      Db.getSqlPara("getStockInFavorite",
                              Kv.by("stockTableId", stockTableId)
                                      .set("userId", userId)));
//      System.out.println("SearchController.searchForMatchedStocks(): recordInFavorite = " + recordInFavorite);
      jsonArray.put(new JSONObject()
              .put("stockTableId", stockTableId)
              .put("stockId", stockRecord.getStr("stockId"))
              .put("stockType", stockRecord.getInt("stockType"))
              .put("stockName", stockRecord.getStr("stockName"))
              .put("favorite", recordInFavorite != null)
      );
    }
    renderJson(jsonArray.toString());
  }

  public void saveStockSearchHistory() {
    long userId = getParaToLong("userId");
    long stockTableId = getParaToLong("stockTableId");
    int updateResult = Db.update(
            Db.getSqlPara(
                    "updateStockSearchHistory",
                    Kv.by("userId", userId).set("stockTableId", stockTableId)
            )
    );
    Record record = Db.findFirst(
            Db.getSqlPara("getStockSearchTime",
                    Kv.by("userId", userId).set("stockTableId", stockTableId))
    );
    if ( updateResult == 1 ) {
      renderJson(new JSONObject()
              .put("searchTime", DateUtils.dateStringToLong(
                      record.getStr("searchTime")))
              .toString());
      return;
    }
    boolean saveResult = Db.save("search_history",
            new Record()
                    .set("userId", userId)
                    .set("stockTableId", stockTableId)
    );
    record = Db.findFirst(
            Db.getSqlPara("getStockSearchTime",
                    Kv.by("userId", userId).set("stockTableId", stockTableId))
    );
    renderJson(new JSONObject()
            .put("searchTime", DateUtils.dateStringToLong(
                    record.getStr("searchTime")))
            .toString());
  }

  public void clearStockSearchHistory() {
    String userId = getPara("userId");
    int deleteResult = Db.update(
            Db.getSqlPara("clearStockSearchHistory",
                    Kv.by("userId", userId))
    );
    renderText(deleteResult > 0 ? "true" : "false");
  }

  public void addFavoriteStockFromSearch() {
    long userId = getParaToLong("userId");
    long stockTableId = getParaToLong("stockTableId");
    if ( MyDbUtils.checkUserExistent(userId) == false ) {
      renderJson(new JSONObject());
//      renderText("false");
      return;
    }
    System.out.println("SearchController.addFavoriteStock(): userId = " + userId);
    Record existence = Db.findFirst(
            Db.getSqlPara("getExistentFavoriteStock",
                    Kv.by("userId", userId)
                            .set("stockTableId", stockTableId)
            )
    );
    if ( existence != null ) {
      renderJson(new JSONObject());
//      renderText("false");
      System.out.println("SearchController.addFavoriteStock(): existence = " + existence);
      return;
    }
    int rankWeight = 1 + Db.queryInt(
            Db.getSql("getFavoriteStockMaxRankWeight"),
            userId, userId, userId
    );
    System.out.println("SearchController.addFavoriteStock(): rankWeight = " + rankWeight);
    Record stockRecord = MyDbUtils.getStockRecord(stockTableId);
    Record record;
    boolean result = Db.save("favorite_stock",
            record = new Record()
                    .set("userId", userId)
                    .set("stockTableId", stockTableId)
                    //TODO 2017/12/27 22:58
                    .set("favoriteGroupId", stockRecord.getInt("stockType") + 2)
                    .set("rankWeight", rankWeight)
    );
    System.out.println("SearchController.addFavoriteStockFromSearch(): record = " + record);
//    renderText(String.valueOf(result));
    renderJson(new JSONObject()
            .put("favoriteGroupId", stockRecord.getInt("stockType") + 2)
            .put("rankWeight", rankWeight)
    .toString());
  }

  public void deleteFavoriteStockFromSearch() {
    long userId = getParaToLong("userId");
    String stockTableId = getPara("stockTableId");
    if ( MyDbUtils.checkUserExistent(userId) == false ) {
//      renderJson(new JSONObject());
      renderText("false");
      return;
    }
    System.out.println("SearchController.deleteFavoriteStock(): userId = " + userId);
    Record existence = Db.findFirst(
            Db.getSqlPara("getExistentFavoriteStock",
                    Kv.by("userId", userId)
                            .set("stockTableId", stockTableId))
    );
    if ( existence == null ) {
//      renderJson(new JSONObject());
      renderText("false");
      return;
    }
    System.out.println("SearchController.deleteFavoriteStock(): existence = " + existence);
    int deleteResult = Db.update(
            Db.getSqlPara("deleteFavoriteStockFromSearch",
                    Kv.by("userId", existence.getStr("userId"))
                            .set("stockTableId", stockTableId))
    );
    renderText(deleteResult > 0 ? "true" : "false");
  }

}

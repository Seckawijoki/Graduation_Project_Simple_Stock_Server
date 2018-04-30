package com.seckawijoki.jfinal.controller;

import com.jfinal.core.Controller;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.seckawijoki.jfinal.constants.server.MoJiReTsu;
import com.seckawijoki.jfinal.tools.MyDbTools;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import javafx.util.converter.DateTimeStringConverter;

/**
 * Created by 瑶琴频曲羽衣魂 on 2017/12/16 at 19:37.
 */

public class SearchController extends Controller {
  public void getStockSearchHistory() {
    long userId = getParaToLong("userId");
    long favoriteGroupId = getParaToLong("favoriteGroupId", -1L);
    List<Record> searchRecordList = Db.find(
            Db.getSqlPara("getStockSearchHistory",
                    Kv.by("userId", userId)
                            .set("limit", 20)
            )
    );
//    System.out.println("SearchController.getStockSearchHistory(): searchRecordList = " + searchRecordList);
    JSONArray jsonArray = new JSONArray();
    for ( int i = 0 ; i < searchRecordList.size() ; i++ ) {
      Record searchRecord = searchRecordList.get(i);
//      System.out.println("SearchController.getStockSearchHistory(): searchRecord = " + searchRecord);
      long stockTableId = searchRecord.getLong("stockTableId");
      Record recordInFavorite;
      if (favoriteGroupId <= 1) {
        recordInFavorite = Db.findFirst(
                Db.getSqlPara("getStockInFavorite",
                        Kv.by("stockTableId", stockTableId)
                                .set("userId", userId)));
      } else {
        recordInFavorite = Db.findFirst(
                Db.getSqlPara("getStockInFavoriteWithGroupId",
                        Kv.by("stockTableId", stockTableId)
                                .set("userId", userId)
                .set("favoriteGroupId", favoriteGroupId)));
      }
//      System.out.println("SearchController.getStockSearchHistory(): recordInFavorite = " + recordInFavorite);
      jsonArray.put(new JSONObject()
              .put("stockTableId", searchRecord.getLong("stockTableId"))
              .put("stockId", searchRecord.getStr("stockId"))
              .put("stockType", searchRecord.getInt("stockType"))
              .put("stockName", searchRecord.getStr("stockName"))
              .put("searchTime",
                      searchRecord.getDate("searchTime").getTime())
              .put("favorite", recordInFavorite != null));
    }
    renderJson(jsonArray.toString());
  }

  public void searchForMatchedStocks() {
    String userId = getPara("userId");
    String search = getPara("search");
    long favoriteGroupId = getParaToLong("favoriteGroupId", -1L);
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
      Record recordInFavorite;
      if (favoriteGroupId <= 1) {
        recordInFavorite = Db.findFirst(
                Db.getSqlPara("getStockInFavorite",
                        Kv.by("stockTableId", stockTableId)
                                .set("userId", userId)));
      } else {
        recordInFavorite = Db.findFirst(
                Db.getSqlPara("getStockInFavoriteWithGroupId",
                        Kv.by("stockTableId", stockTableId)
                                .set("userId", userId)
                                .set("favoriteGroupId", favoriteGroupId)));
      }
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
              .put("searchTime",
                      record.getDate("searchTime").getTime())
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
            .put("searchTime",
                    record.getDate("searchTime").getTime())
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
    long favoriteGroupId = getParaToLong("favoriteGroupId", -1L);
    if ( MyDbTools.checkUserExistent(userId) == false ) {
      renderJson(new JSONObject());
//      renderText("false");
      return;
    }
    System.out.println("SearchController.addFavoriteStock(): userId = " + userId);
    Record existence;
    if (favoriteGroupId <= 1) {
      existence = Db.findFirst(
              Db.getSqlPara("getExistentFavoriteStock",
                      Kv.by("userId", userId)
                              .set("stockTableId", stockTableId)
              )
      );
    } else {
      existence = Db.findFirst(
              Db.getSqlPara("getExistentFavoriteStockFromFavoriteGroup",
                      Kv.by("userId", userId)
                              .set("stockTableId", stockTableId)
                      .set("favoriteGroupId", favoriteGroupId)
              )
      );
    }
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
    Record stockRecord = MyDbTools.getStockRecord(stockTableId);
    Record record;
    if (favoriteGroupId < 0){
      favoriteGroupId = stockRecord.getInt("stockType") + 2;
    }
    boolean result = Db.save("favorite_stock",
            record = new Record()
                    .set("userId", userId)
                    .set("stockTableId", stockTableId)
                    //TODO 2017/12/27 22:58
                    .set("favoriteGroupId", favoriteGroupId)
                    .set("rankWeight", rankWeight)
    );
    System.out.println("SearchController.addFavoriteStockFromSearch(): record = " + record);
//    renderText(String.valueOf(result));
    renderJson(new JSONObject()
            .put("favoriteGroupId", favoriteGroupId)
            .put("rankWeight", rankWeight)
    .toString());
  }

  public void deleteFavoriteStockFromSearch() {
    long userId = getParaToLong("userId");
    String stockTableId = getPara("stockTableId");
    if ( MyDbTools.checkUserExistent(userId) == false ) {
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

package com.seckawijoki.jfinal.controller;

import com.jfinal.core.Controller;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.seckawijoki.jfinal.constants.server.DefaultGroups;
import com.seckawijoki.jfinal.constants.server.DefaultStocks;
import com.seckawijoki.jfinal.tools.MyDbTools;
import com.seckawijoki.jfinal.utils.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by 瑶琴频曲羽衣魂 on 2017/12/5 at 12:06.
 */

public class FavoriteController extends Controller {
  private JSONArray getFavoriteGroups(long userId) {
    JSONArray jsonArray = new JSONArray();
    Record userRecord = MyDbTools.getUserRecord(userId);
    if ( userRecord == null ) {
      return jsonArray;
    }
    List<Record> groupNameList = Db.find(
            Db.getSqlPara("getFavoriteGroupName",
                    Kv.by("userId", userId)
            )
    );
    if ( groupNameList == null ) {
      return jsonArray;
    }
    for ( Record record : groupNameList ) {
      long groupId = record.getLong("favoriteGroupId");
      int count = Db.queryInt(
              Db.getSql("countFavoriteStock"),
              userId, groupId
      );
      jsonArray.put(new JSONObject()
              .put("favoriteGroupId", groupId)
              .put("favoriteGroupName", record.getStr("favoriteGroupName"))
              .put("stockCount", count)
              .put("rankWeight", record.getInt("rankWeight"))
      );
    }
    return jsonArray;
  }

  public void getFavoriteGroups() {
    long userId = getParaToLong("userId");
    String result = getFavoriteGroups(userId).toString();
    System.out.println("FavoriteController.getFavoriteGroups(): result = " + result);
    renderJson(result);
  }

  public void addFavoriteGroup() {
    long userId = getParaToLong("userId");
    String favoriteGroupName = getPara("favoriteGroupName");
    Record userRecord = MyDbTools.getUserRecord(userId);
    System.out.println("FavoriteController.addFavoriteGroup(): userRecord = " + userRecord);
    JSONObject jsonObject = new JSONObject();
    if ( userRecord == null ) {
      renderJson(jsonObject.toString());
      return;
    }
    if ( TextUtils.isEmpty(favoriteGroupName) ) {
      renderJson(jsonObject.toString());
      return;
    }
    Record groupTypeRecord = Db.findFirst(
            Db.getSqlPara("getFavoriteGroupTypeByName",
                    Kv.by("favoriteGroupName", favoriteGroupName)
            )
    );
    System.out.println("FavoriteController.addFavoriteGroup(): groupTypeRecord = " + groupTypeRecord);
    if ( groupTypeRecord == null ) {
      groupTypeRecord = new Record()
              .set("favoriteGroupName", favoriteGroupName);
      Db.save("favorite_group_type", groupTypeRecord);
    }
    groupTypeRecord = Db.findFirst(
            Db.getSqlPara("getFavoriteGroupTypeByName",
                    Kv.by("favoriteGroupName", favoriteGroupName)
            )
    );
    String favoriteGroupId = groupTypeRecord.getStr("favoriteGroupId");
    System.out.println("FavoriteController.addFavoriteGroup(): favoriteGroupId = " + favoriteGroupId);
    Record favoriteGroupRecord = Db.findFirst(
            Db.getSqlPara("getFavoriteGroup",
                    Kv.by("userId", userId).set("favoriteGroupId", favoriteGroupId)
            )
    );
    System.out.println("FavoriteController.addFavoriteGroup(): favoriteGroupRecord = " + favoriteGroupRecord);
    if ( favoriteGroupRecord != null ) {
      renderJson(jsonObject.toString());
      return;
    }
    int minRankWeight = Db.queryInt(
            Db.getSql("getMinRankWeightFromFavoriteGroup"), userId
    );
    System.out.println("FavoriteController.addFavoriteGroup(): minRankWeight = " + minRankWeight);
    Db.save("favorite_group",
            new Record()
                    .set("userId", userId)
                    .set("favoriteGroupId", favoriteGroupId)
                    .set("rankWeight", minRankWeight - 1 + ""));
    renderJson(jsonObject
            .put("favoriteGroupId", favoriteGroupId)
            .put("favoriteGroupName", favoriteGroupName)
            .put("rankWeight", minRankWeight - 1)
            .toString()
    );
  }

  public void deleteFavoriteGroups() {
    JSONArray result = new JSONArray();
    long userId = getParaToLong("userId");
    Long[] favoriteGroupIds = getParaValuesToLong("favoriteGroupId");
    Record userRecord = MyDbTools.getUserRecord(userId);
    System.out.println("FavoriteController.deleteFavoriteGroups(): userRecord = " + userRecord);
    if ( userRecord == null ) {
      renderJson(new JSONObject().put("result", result.put(false)).toString());
      return;
    }
    for ( long favoriteGroupId : favoriteGroupIds ) {
      Record groupTypeRecord = Db.findFirst(
              Db.getSqlPara("getFavoriteGroupTypeById",
                      Kv.by("favoriteGroupId", favoriteGroupId)
              )
      );
      System.out.println("FavoriteController.deleteFavoriteGroups(): groupTypeRecord = " + groupTypeRecord);
      if ( groupTypeRecord == null ) {
        result.put(false);
        continue;
      }
      Db.update(Db.getSql("deleteFavoriteGroupByGroupId"),
              userRecord.getStr("userId"),
              groupTypeRecord.getStr("favoriteGroupId"));
      result.put(true);
    }
    renderJson(getFavoriteGroups(userId).toString());
  }

  public void deleteFavoriteGroup() {
    long userId = getParaToLong("userId");
    long favoriteGroupId = getParaToLong("favoriteGroupId");
    Record userRecord = MyDbTools.getUserRecord(userId);
    System.out.println("FavoriteController.deleteFavoriteGroup(): userRecord = " + userRecord);
    if ( userRecord == null ) {
      renderText("false");
      return;
    }
    Record groupTypeRecord = MyDbTools.getFavoriteGroupTypeRecord(favoriteGroupId);
    System.out.println("FavoriteController.deleteFavoriteGroup(): groupTypeRecord = "
            + groupTypeRecord);
    if ( groupTypeRecord == null ) {
      renderText("false");
      return;
    }
    Db.update("delete from favorite_group where userId = ? and favoriteGroupId = ?",
            userRecord.getStr("userId"),
            groupTypeRecord.getStr("favoriteGroupId"));
    renderJson(getFavoriteGroups(userId).toString());
  }

  public void renameFavoriteGroup() {
    //TODO 2017/12/27 20:34
    JSONObject jsonObject = new JSONObject();
    long userId = getParaToLong("userId");
    String oldGroupName = getPara("oldGroupName");
    String newGroupName = getPara("newGroupName");
    Record userRecord = MyDbTools.getUserRecord(userId);
//    System.out.println("FavoriteController.renameFavoriteGroup(): userRecord = " + userRecord);
    if ( userRecord == null ) {
      renderNull();
      return;
    }
    Record oldGroupRecord = MyDbTools.getFavoriteGroupRecord(userId, oldGroupName);
    if ( oldGroupRecord == null ) {
      renderNull();
      return;
    }
    long oldGroupId = oldGroupRecord.getLong("favoriteGroupId");
    int rankWeight = oldGroupRecord.getInt("rankWeight");
    Record newGroupRecord = MyDbTools.getFavoriteGroupRecord(userId, newGroupName);
    if ( newGroupRecord != null ) {
      renderNull();
      return;
    }
    Record groupTypeRecord = MyDbTools.getFavoriteGroupTypeRecord(newGroupName);
    if ( groupTypeRecord == null ) {
      Db.save("favorite_group_type",
              new Record().set("favoriteGroupName", newGroupName));
    }
    int deleteResult =
            Db.update(
                    Db.getSql("deleteFavoriteGroupByGroupId"),
                    userId, oldGroupId
            );
    System.out.println("FavoriteController.renameFavoriteGroup(): deleteResult = " + deleteResult);
    if ( deleteResult <= 0 ) {
      renderNull();
      return;
    }
    groupTypeRecord = MyDbTools.getFavoriteGroupTypeRecord(newGroupName);
    long newGroupId = groupTypeRecord.getLong("favoriteGroupId");
    System.out.println("FavoriteController.renameFavoriteGroup(): newGroupId = " + newGroupId);
    int minWeight = Db.queryInt(
            Db.getSql("getMinRankWeightFromFavoriteGroup"), userId
    );
    System.out.println("FavoriteController.renameFavoriteGroup(): minWeight = " + minWeight);
    Db.save("favorite_group",
            newGroupRecord = new Record()
                    .set("userId", userId)
                    .set("favoriteGroupId", newGroupId)
                    .set("rankWeight", rankWeight - 1)
    );

    renderJson(jsonObject
            .put("favoriteGroupId", newGroupId)
            .put("rankWeight", rankWeight - 1)
            .toString());
  }


  public void getFavoriteStocks() {
    long userId = getParaToLong("userId");
    long favoriteGroupId = getParaToLong("favoriteGroupId");
    JSONArray jsonArray = new JSONArray();
    if ( favoriteGroupId == DefaultGroups.ALL ) {
      getAllFavoriteStocks();
      return;
    }
    if ( favoriteGroupId == DefaultGroups.SPECIAL_ATTENTION ) {
      getSpecialFavoriteStocks();
      return;
    }
    List<Record> favoriteStockList = Db.find(
            Db.getSqlPara("getFavoriteStocks",
                    Kv.by("userId", userId).set("favoriteGroupId", favoriteGroupId))
    );
    for ( int i = 0 ; i < favoriteStockList.size() ; i++ ) {
      Record record = favoriteStockList.get(i);
      jsonArray.put(
              new JSONObject()
                      .put("stockTableId", record.getLong("stockTableId"))
                      .put("rankWeight", record.getInt("rankWeight"))
                      .put("specialAttention", record.getBoolean("specialAttention"))
      );
    }
    System.out.println("FavoriteController.getFavoriteStocks(): jsonArray = " + jsonArray);
    renderJson(jsonArray.toString());
  }

  public void getAllFavoriteStocks() {
    long userId = getParaToLong("userId");
    List<Record> favoriteStockList = Db.find(
            Db.getSqlPara("getAllFavoriteStocks",
                    Kv.by("userId", userId))
    );
    JSONArray jsonArray = new JSONArray();
    for ( int i = 0 ; i < favoriteStockList.size() ; i++ ) {
      Record record = favoriteStockList.get(i);
      jsonArray.put(
              new JSONObject()
                      .put("stockTableId", record.getLong("stockTableId"))
                      .put("rankWeight", record.getInt("rankWeight"))
                      .put("specialAttention", record.getBoolean("specialAttention"))
      );
    }
    System.out.println("FavoriteController.getAllFavoriteStocks(): jsonArray = " + jsonArray);
    renderJson(jsonArray.toString());
  }

  public void getSpecialFavoriteStocks() {
    long userId = getParaToLong("userId");
    List<Record> favoriteStockList = Db.find(
            Db.getSqlPara("getSpecialFavoriteStocks",
                    Kv.by("userId", userId)
            )
    );
    JSONArray jsonArray = new JSONArray();
    for ( int i = 0 ; i < favoriteStockList.size() ; i++ ) {
      Record record = favoriteStockList.get(i);
      jsonArray.put(
              new JSONObject()
                      .put("stockTableId", record.getLong("stockTableId"))
                      .put("rankWeight", record.getInt("rankWeight"))
                      .put("specialAttention", record.getBoolean("specialAttention"))
      );
    }
    System.out.println("FavoriteController.getSpecialFavoriteStocks(): jsonArray = " + jsonArray);
    renderJson(jsonArray.toString());
  }

  public void getDefaultShStocks() {
    JSONArray stockTableIdArray = new JSONArray();
    for ( String defaultShStock : DefaultStocks.defaultShStocks ) {
      Record record = Db.findFirst(
              "select stockTableId from all_stocks where stockName = ?", defaultShStock
      );
      stockTableIdArray.put(record.getLong("stockTableId"));
    }
    renderJson(new JSONObject().put("stockTableId", stockTableIdArray).toString());
  }

  public void getDefaultSzStocks() {
    JSONArray stockTableIdArray = new JSONArray();
    for ( String defaultSzStock : DefaultStocks.defaultSzStocks ) {
      Record record = Db.findFirst(
              "select stockTableId from all_stocks where stockName = ?", defaultSzStock
      );
      stockTableIdArray.put(record.getLong("stockTableId"));
    }
    renderJson(new JSONObject().put("stockTableId", stockTableIdArray).toString());
  }


  public void addFavoriteStocks() {
    long userId = getParaToLong("userId");
    long favoriteGroupId = getParaToLong("favoriteGroupId");
    String[] stockTableIds = getParaValues("stockTableId");
    JSONArray stockTableIdArray = new JSONArray();
    for ( int i = 0 ; i < stockTableIds.length ; ++i ) {
      String stockTableId = stockTableIds[i];
      Record existence = Db.findFirst(
              Db.getSqlPara("getExistentFavoriteStockFromFavoriteGroup",
                      Kv.by("userId", userId)
                              .set("favoriteGroupId", favoriteGroupId)
                              .set("stockTableId", stockTableId)
              )
      );
      System.out.println("FavoriteController.addFavoriteStocks(): existence = " + existence);
      if ( existence != null ) {
        continue;
      }
      int minRankWeight = Db.queryInt(
              Db.getSql("getMinRankWeightFromFavoriteStock"),
              userId
      );
      System.out.println("FavoriteController.addFavoriteStocks(): minRankWeight = " + minRankWeight);
      Db.save("favorite_stock",
              new Record()
                      .set("userId", userId)
                      .set("favoriteGroupId", favoriteGroupId)
                      .set("stockTableId", stockTableId)
                      .set("rankWeight", minRankWeight - 1 + "")
      );
      stockTableIdArray.put(stockTableId);
    }
    renderJson(new JSONObject().put("stockTableId", stockTableIdArray).toString());
  }

  public void deleteFavoriteStocks() {
    long userId = getParaToLong("userId");
    long favoriteGroupId = getParaToLong("favoriteGroupId");
    Long[] stockTableIds = getParaValuesToLong("stockTableId");
    JSONArray stockTableIdArray = new JSONArray();
    for ( int i = 0 ; i < stockTableIds.length ; ++i ) {
      long stockTableId = stockTableIds[i];
      Record existence;
      if (favoriteGroupId != 0 && favoriteGroupId != 1) {
        existence = Db.findFirst(
                Db.getSqlPara("getExistentFavoriteStockFromFavoriteGroup",
                        Kv.by("userId", userId)
                                .set("stockTableId", stockTableId)
                                .set("favoriteGroupId", favoriteGroupId)
                )
        );
      } else {
        existence = Db.findFirst(
                Db.getSqlPara("getExistentFavoriteStock",
                        Kv.by("userId", userId)
                                .set("stockTableId", stockTableId)
                )
        );
      }
      System.out.println("FavoriteController.deleteFavoriteStocks(): existence = " + existence);
      if ( existence == null ) {
        continue;
      }
      int result = Db.update(
              Db.getSql("deleteFavoriteStock"),
              userId, favoriteGroupId, stockTableId);
      System.out.println("FavoriteController.deleteFavoriteStock(): result = " + result);
      if ( result > 0 ) {
        stockTableIdArray.put(stockTableId);
      }
    }
    renderJson(new JSONObject().put("stockTableId", stockTableIdArray).toString());
  }

  public void setSpecialFavoriteStocks() {
    long userId = getParaToLong("userId");
    boolean special = getParaToBoolean("specialAttention", true);
    Long[] stockTableIds = getParaValuesToLong("stockTableId");
    int specialAttention = special ? 1 : 0;
    JSONArray stockTableIdArray = new JSONArray();
    for ( int i = 0 ; i < stockTableIds.length ; ++i ) {
      long stockTableId = stockTableIds[i];
      Record existence = Db.findFirst(
              Db.getSqlPara("getExistentFavoriteStock",
                      Kv.by("userId", userId)
                              .set("stockTableId", stockTableId))
      );
      System.out.println("FavoriteController.setSpecialFavoriteStocks(): existence = " + existence);
      if ( existence == null ) {
        continue;
      }
      int updateCount = Db.update(
              Db.getSqlPara("setSpecialFavoriteStock",
                      Kv.by("userId", userId)
                              .set("stockTableId", stockTableId)
                              .set("specialAttention", specialAttention)
              )
      );
      System.out.println("FavoriteController.setSpecialFavoriteStocks(): updateCount = " + updateCount);
      if ( updateCount > 0 ) {
        stockTableIdArray.put(stockTableId);
      }
    }
    System.out.println("FavoriteController.setSpecialFavoriteStocks(): stockTableIdArray = " + stockTableIdArray);
    renderJson(new JSONObject().put("stockTableId", stockTableIdArray).toString());
  }

  public void setFavoriteStockTop() {
    long userId = getParaToLong("userId");
    long stockTableId = getParaToLong("stockTableId");
    Record existence = Db.findFirst(
            Db.getSqlPara("getExistentFavoriteStock",
                    Kv.by("userId", userId)
                            .set("stockTableId", stockTableId)
            )
    );
    System.out.println("FavoriteController.setFavoriteStockTop(): existence = " + existence);
    if ( existence == null ) {
      renderJson(new JSONObject());
      return;
    }
    int rankWeight = 1 + Db.queryInt(
            Db.getSql("getMaxRankWeightFromFavoriteStock"),
            userId
    );
    System.out.println("FavoriteController.setFavoriteStockTop(): existence = " + existence);
    Db.update(
            Db.getSqlPara("setFavoriteStockTop",
                    Kv.by("userId", userId)
                            .set("stockTableId", stockTableId)
                            .set("rankWeight", rankWeight)
            )
    );
    renderJson(new JSONObject().put("rankWeight", rankWeight).toString());
  }
}

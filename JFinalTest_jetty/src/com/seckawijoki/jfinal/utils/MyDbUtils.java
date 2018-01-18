package com.seckawijoki.jfinal.utils;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * Created by 瑶琴频曲羽衣魂 on 2017/12/5 at 19:20.
 */

public class MyDbUtils {
  private MyDbUtils(){

  }
  public static Record getStockRecord(long stockTableId){
    return Db.findFirst(
            "select * from all_stocks where stockTableId = ?", stockTableId
    );
  }
  public static boolean checkUserExistent(long userId){
    return getUserRecord(userId) != null;
  }
  public static String getUserId(long userId){
    return getUserRecord(userId).getStr("userId");
  }
  public static Record getUserRecord(long userId){
    return Db.findFirst(
            Db.getSqlPara("getUserRecord", Kv.by("userId", userId))
    );
  }
  public static Record getFavoriteGroupRecord(long userId, long favoriteGroupId){
    return Db.findFirst(
            Db.getSqlPara("getFavoriteGroup",
                    Kv.by("userId", userId).set("favoriteGroupId", favoriteGroupId)
            )
    );
  }
  public static Record getFavoriteGroupRecord(long userId, String favoriteGroupName){
    return Db.findFirst(
            Db.getSqlPara("getFavoriteGroupByGroupName",
                    Kv.by("userId", userId).set("favoriteGroupName", favoriteGroupName)
            )
    );
  }
  public static Record getFavoriteGroupTypeRecord(String favoriteGroupName){
    return Db.findFirst(
            "select * from favorite_group_type where favoriteGroupName = ?",
            favoriteGroupName
    );
  }
  public static Record getFavoriteGroupTypeRecord(long favoriteGroupId){
    return Db.findFirst(
            "select * from favorite_group_type where favoriteGroupId = ?",
            favoriteGroupId
    );
  }
  public static boolean checkFavoriteStock(long userId, long stockTableId){
    return getFavoriteStockRecord(userId, stockTableId) != null;
  }
  public static Record getFavoriteStockRecord(long userId, long stockTableId){
    return Db.findFirst(
            "select * from favorite_stock where userId = ? and stockTableId = ?",
            userId, stockTableId
    );
  }
}

package com.seckawijoki.jfinal;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.seckawijoki.jfinal.constants.server.DefaultGroups;

import java.util.List;

/**
 * Created by 瑶琴频曲羽衣魂 on 2017/12/6 at 18:03.
 */

public class AfterJFinalStart {
  public AfterJFinalStart() {
    checkDefaultFavoriteGroup();
    checkUserDefaultFavoriteGroup();
  }

  private void checkDefaultFavoriteGroup() {
    Record record;
    for ( int id : DefaultGroups.IDS ) {
      record = Db.findFirst("select * from favorite_group_type where favoriteGroupId = ?", id);
      if ( record == null ) {
        Db.save("favorite_group_type",
                new Record()
                        .set("favoriteGroupId", id)
                        .set("favoriteGroupName", DefaultGroups.NAMES[id]));
      }
    }
  }

  private void checkUserDefaultFavoriteGroup() {
    List<Record> userRecordList = Db.find(
            "select distinct userId from user"
    );
    for ( Record record : userRecordList ) {
      String userId = record.getStr("userId");
      List<Record> favoriteGroupList = Db.find(
              "select distinct favoriteGroupId " +
                      "from favorite_group " +
                      "where favoriteGroupId in (?,?,?,?)" +
                      "and userId = ?",
              DefaultGroups.ALL,
              DefaultGroups.SPECIAL_ATTENTION,
              DefaultGroups.SHANGHAI_MARKET,
              DefaultGroups.SHENZHEN_MARKET,
              userId
      );
      if ( favoriteGroupList.size() < 4 ) {
        for ( int id : DefaultGroups.IDS ) {
          Db.save("favorite_group",
                  new Record()
                          .set("userId", userId)
                          .set("favoriteGroupId", id)
                          .set("rankWeight", 4-id));
        }
      }
    }
  }
}

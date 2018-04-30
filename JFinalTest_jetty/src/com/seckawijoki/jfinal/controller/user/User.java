package com.seckawijoki.jfinal.controller.user;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;
import com.seckawijoki.jfinal.constants.server.DefaultGroups;
import com.seckawijoki.jfinal.constants.server.DefaultStocks;
import com.seckawijoki.jfinal.constants.server.MoJiReTsu;
import com.seckawijoki.jfinal.constants.server.StockType;
import com.seckawijoki.jfinal.constants.server.LoginStatus;
import com.seckawijoki.jfinal.constants.database.UserStatus;
import com.seckawijoki.jfinal.utils.TextUtils;
import com.sun.istack.internal.NotNull;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by 瑶琴频曲羽衣魂 on 2017/11/21 at 15:47.
 */

public class User extends Model<User> {
  JSONObject requestLogin(String account, String password, String mac) {
    System.out.println("User.requestLogin(): ");
    JSONObject jsonObject = new JSONObject();
    Record record = Db.findFirst
            ("select * from " + MoJiReTsu.USER + " where userName = ? or email = ? or phone = ?",
                    account, account, account);

    if ( record == null ) {
//    if ( userNameError && phoneError && emailError ) {
      System.out.println("User.requestLogin():  Has not registered.");
      /*
      if ( TextUtils.isEmailValid(account) )
        jsonObject.put(LoginStatus.KEY, LoginStatus.EMAIL_ERROR);
      else if ( TextUtils.isPhoneValid(account) )
        jsonObject.put(LoginStatus.KEY, LoginStatus.PHONE_ERROR);
      else
        */
      jsonObject.put(LoginStatus.KEY, LoginStatus.HAS_NOT_REGISTERED);
      return jsonObject;
    }
    jsonObject.put("userId", record.getLong("userId"));
    if ( !TextUtils.equals(record.getStr(MoJiReTsu.PASSWORD), password) ) {
      System.out.println("User.requestLogin():  Password error.");
      //Password error.
      jsonObject.put(LoginStatus.KEY, LoginStatus.PASSWORD_ERROR);
      return jsonObject;
    }
    System.out.println("User.requestLogin(): record = " + record);
    if ( !TextUtils.equals(record.getStr(MoJiReTsu.MAC), mac) ) {
      //TODO: judging different login device
      jsonObject.put(LoginStatus.KEY, LoginStatus.DIFFERENT_MAC);
      loginSuccessful(mac, password, record.getInt(MoJiReTsu.USER_ID));
      return jsonObject;
    }
    if ( record.getInt(MoJiReTsu.USER_STATUS) == UserStatus.INLINE ) {
      System.out.println("User.requestLogin(): Has logged in.");
      //Has logged in.
      if ( TextUtils.equals(record.getStr(MoJiReTsu.MAC), mac) ) {
        jsonObject.put(LoginStatus.KEY, LoginStatus.HAS_LOGGED_IN_ON_THE_PHONE);
      } else {
        jsonObject.put(LoginStatus.KEY, LoginStatus.HAS_LOGGED_IN_ON_ANOTHER_PHONE);
      }
      return jsonObject;
    }
    if ( record.getInt(MoJiReTsu.USER_STATUS) == UserStatus.FROZEN ) {
      System.out.println("User.requestLogin(): Account frozen.");
      //Account frozen.
      jsonObject.put(LoginStatus.KEY, LoginStatus.FROZEN);
      return jsonObject;
    }
    jsonObject.put(LoginStatus.KEY, LoginStatus.SUCCESSFUL);
    loginSuccessful(mac, password, record.getLong(MoJiReTsu.USER_ID));
    return jsonObject;
  }

  private void loginSuccessful(String mac, String password, long userId) {
    Date systemTime = new Date(System.currentTimeMillis());
    Db.update("update user set lastLoginTime = ?, mac = ?, userStatus = ?  where userId = ? and password = ?",
            systemTime, mac, UserStatus.INLINE, userId, password);
    List<Record> list = Db.find(" select * from user");
    Record record = list.get(0);
    System.out.println("User.requestLogin(): record = " + record);
  }

  boolean requestCheckPhoneExistent(String phone) {
    Record record = Db.findFirst(
            "select phone from " + MoJiReTsu.USER + " where " + MoJiReTsu.PHONE + " = ? ", phone);
    System.out.println("User.requestCheckPhoneExistent(): record = " + record);
//    if (phone.equals("13510604840"))return false;
    return record != null;
  }

  boolean requestLogout(String account) {
    /*
    Record record = new Record()
            .set("favoritesListId", "1")
            .set("favoritesListLabel", "我喜欢的股票");
    if (Db.save("favorites_list", record) == false)return false;
    */
    /*
    List<Record> list = Db.find("select * from " + USER + " where email = ? or phone = ? or userName = ?",
            account, account, account);
    if (list.isEmpty() == false){
      Record record = list.get(0);
      System.out.println("User.requestLogout(): record = " + record);
    }
    */
    int result = Db.update("update " + MoJiReTsu.USER
                    + " set userStatus = ? where email = ? or phone = ? or userName = ?",
            UserStatus.OFFLINE, account, account, account);
    System.out.println("User.requestLogout(): result = " + result);
    return true;
  }

  boolean requestRegister(String phone, String password, String mac) {
    if ( TextUtils.isEmpty(phone) || TextUtils.isEmpty(password) ) {
      return false;
    }
    Record existed = Db.findFirst("select phone from user where phone = ?", phone);
    if ( existed != null ) {
      return false;
    }
    Date systemTime = Calendar.getInstance().getTime();
    Record record = new Record()
            .set(MoJiReTsu.PHONE, phone)
            .set(MoJiReTsu.PASSWORD, password)
            .set(MoJiReTsu.USER_STATUS, UserStatus.INLINE)
            .set(MoJiReTsu.MAC, mac)
            .set(MoJiReTsu.REGISTER_TIME, systemTime)
            .set(MoJiReTsu.LAST_LOGIN_TIME, systemTime)
            .set(MoJiReTsu.NICKNAME, phone);
    Db.save(MoJiReTsu.USER, record);
    System.out.println("User.requestRegister(): record = " + record);
    createDefaultFavoriteGroup(phone);
    createDefaultFavoriteStocks(phone);
    return true;
  }

  JSONObject requestGetUserInformation(String account) {
    JSONObject jsonObject = new JSONObject();
    Record record = Db.findFirst("select * from " + MoJiReTsu.USER
            + " where phone = ? or email = ? or userId = ? ", account, account, account);
    if ( record == null )
      return null;
    jsonObject.put(MoJiReTsu.NICKNAME, record.getStr(MoJiReTsu.NICKNAME));
    jsonObject.put(MoJiReTsu.PHONE, record.getStr(MoJiReTsu.PHONE));
    jsonObject.put(MoJiReTsu.EMAIL, record.getStr(MoJiReTsu.EMAIL));
    jsonObject.put(MoJiReTsu.USER_ID, record.getStr(MoJiReTsu.USER_ID));
    return jsonObject;
  }

  /**
   * Called after register successful.
   *
   * @param phone
   */
  private void createDefaultFavoriteGroup(String phone) {
    String userId = Db.findFirst(
            "select userId from user where phone = ?",
            phone
    ).getStr("userId");
    for ( int id : DefaultGroups.IDS ) {
      Db.save("favorite_group",
              new Record()
                      .set("userId", userId)
                      .set("favoriteGroupId", id)
                      .set("rankWeight", id));
    }
  }

  /**
   * Called after register successful.
   *
   * @param phone
   */
  private void createDefaultFavoriteStocks(@NotNull String phone) {
    String userId = Db.findFirst(
            "select userId from user where phone = ?",
            phone
    ).getStr("userId");
    int rankWeight = DefaultStocks.defaultShStocks.length;
    for ( String defaultShStock : DefaultStocks.defaultShStocks ) {
      Db.save("favorite_stock",
              new Record()
                      .set("userId", userId)
                      .set("favoriteGroupId", DefaultGroups.SHANGHAI_MARKET)
                      .set("stockId", defaultShStock)
                      .set("stockType", StockType.SH)
                      .set("rankWeight", rankWeight--));
    }
    rankWeight = DefaultStocks.defaultSzStocks.length;
    for ( String defaultSzStock : DefaultStocks.defaultSzStocks ) {
      Db.save("favorite_stock",
              new Record()
                      .set("userId", userId)
                      .set("favoriteGroupId", DefaultGroups.SHANGHAI_MARKET)
                      .set("stockId", defaultSzStock)
                      .set("stockType", StockType.SZ)
                      .set("rankWeight", rankWeight--));
    }
  }
}

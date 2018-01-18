package com.seckawijoki.jfinal.user;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;
import com.seckawijoki.jfinal.constants.server.DefaultGroups;
import com.seckawijoki.jfinal.constants.server.DefaultStocks;
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
  public static final String TABLE_NAME = "user";
  public static final String USER_ID = "userId";
  static final String PHONE = "phone";
  static final String EMAIL = "email";
  static final String USER_NAME = "userName";
  static final String PASSWORD = "password";
  static final String MAC = "mac";
  static final String NICKNAME = "nickname";
  static final String USER_STATUS = "userStatus";
  static final String REGISTER_TIME = "registerTime";
  static final String LAST_LOGIN_TIME = "lastLoginTime";

  JSONObject requestLogin(String account, String password, String mac) {
    System.out.println("User.requestLogin(): ");
    JSONObject jsonObject = new JSONObject();
    Record record = Db.findFirst
            ("select * from " + TABLE_NAME + " where userName = ? or email = ? or phone = ?",
                    account, account, account);
    /*
    outerLooping:
    for ( int i = 0 ; i < 3 ; ++i ) {
      switch ( i ) {
        default:
        case 0:
          list = Db.find("select * from " + TABLE_NAME + " where " + USER_NAME + " = ?", account);
          if ( !list.isEmpty() ) break outerLooping;
          userNameError = true;
          break;
        case 1:
          list = Db.find("select * from " + TABLE_NAME + " where " + PHONE + " = ?", account);
          if ( !list.isEmpty() ) break outerLooping;
          phoneError = true;
          break;
        case 2:
          list = Db.find("select * from " + TABLE_NAME + " where " + EMAIL + " = ?", account);
          if ( !list.isEmpty() ) break outerLooping;
          emailError = true;
          break;
      }
    }
    */
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
    if ( !TextUtils.equals(record.getStr(PASSWORD), password) ) {
      System.out.println("User.requestLogin():  Password error.");
      //Password error.
      jsonObject.put(LoginStatus.KEY, LoginStatus.PASSWORD_ERROR);
      return jsonObject;
    }
    System.out.println("User.requestLogin(): record = " + record);
    if ( !TextUtils.equals(record.getStr(MAC), mac) ) {
      //TODO: judging different login device
      jsonObject.put(LoginStatus.KEY, LoginStatus.DIFFERENT_MAC);
      loginSuccessful(mac, password, record.getInt(USER_ID));
      return jsonObject;
    }
    if ( record.getInt(USER_STATUS) == UserStatus.INLINE ) {
      System.out.println("User.requestLogin(): Has logged in.");
      //Has logged in.
      if ( TextUtils.equals(record.getStr(MAC), mac) ) {
        jsonObject.put(LoginStatus.KEY, LoginStatus.HAS_LOGGED_IN_ON_THE_PHONE);
      } else {
        jsonObject.put(LoginStatus.KEY, LoginStatus.HAS_LOGGED_IN_ON_ANOTHER_PHONE);
      }
      return jsonObject;
    }
    if ( record.getInt(USER_STATUS) == UserStatus.FROZEN ) {
      System.out.println("User.requestLogin(): Account frozen.");
      //Account frozen.
      jsonObject.put(LoginStatus.KEY, LoginStatus.FROZEN);
      return jsonObject;
    }
    jsonObject.put(LoginStatus.KEY, LoginStatus.SUCCESSFUL);
    loginSuccessful(mac, password, record.getLong(USER_ID));
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
            "select phone from " + TABLE_NAME + " where " + PHONE + " = ? ", phone);
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
    List<Record> list = Db.find("select * from " + TABLE_NAME + " where email = ? or phone = ? or userName = ?",
            account, account, account);
    if (list.isEmpty() == false){
      Record record = list.get(0);
      System.out.println("User.requestLogout(): record = " + record);
    }
    */
    int result = Db.update("update " + TABLE_NAME
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
            .set(PHONE, phone)
            .set(PASSWORD, password)
            .set(USER_STATUS, UserStatus.INLINE)
            .set(MAC, mac)
            .set(REGISTER_TIME, systemTime)
            .set(LAST_LOGIN_TIME, systemTime);
    Db.save(TABLE_NAME, record);
    System.out.println("User.requestRegister(): record = " + record);
    createDefaultFavoriteGroup(phone);
    createDefaultFavoriteStocks(phone);
    return true;
  }

  JSONObject requestGetUserInformation(String account) {
    JSONObject jsonObject = new JSONObject();
    Record record = Db.findFirst("select * from " + TABLE_NAME
            + " where phone = ? or email = ? or userId = ? ", account, account, account);
    if ( record == null )
      return null;
    jsonObject.put(NICKNAME, record.getStr(NICKNAME));
    jsonObject.put(PHONE, record.getStr(PHONE));
    jsonObject.put(EMAIL, record.getStr(EMAIL));
    jsonObject.put(USER_ID, record.getStr(USER_ID));
    return jsonObject;
  }

  boolean requestChangeNickname(String account, String nickname) {
    Record record = Db.findFirst("select * from " + TABLE_NAME
            + " where phone = ? or email = ? or userId = ? ", account, account, account);
    if ( record == null )
      return false;
    Db.update("update " + TABLE_NAME + " set nickname = ?", nickname);
    return true;
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

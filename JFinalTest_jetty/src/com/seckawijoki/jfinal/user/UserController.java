package com.seckawijoki.jfinal.user;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;

import org.json.JSONObject;

/**
 * Created by 瑶琴频曲羽衣魂 on 2017/11/21 at 14:10.
 */

@Before({UserInterceptor.class})
public class UserController extends Controller{
  private static final String ACCOUNT = "account";
  public void login(){
    String account = getPara(ACCOUNT);
    String password = getPara(User.PASSWORD);
    String mac = getPara(User.MAC);
    System.out.println("UserController.login(): account = " + account);
    System.out.println("UserController.login(): password = " + password);
    System.out.println("UserController.login(): mac = " + mac);
    JSONObject jsonObject = getModel(User.class).requestLogin(account, password, mac);
    System.out.println("UserController.login(): jsonObject = " + jsonObject);
    renderJson(jsonObject.toString());
  }

  public void register(){
    String account = getPara(User.PHONE);
    String password = getPara(User.PASSWORD);
    String mac = getPara(User.MAC);
    System.out.println("UserController.register(): account = " + account);
    System.out.println("UserController.register(): password = " + password);
    System.out.println("UserController.register(): mac = " + mac);
    boolean result = getModel(User.class).requestRegister(account, password, mac);
    renderText(String.valueOf(result));
  }

  public void logout(){
    String account = getPara(ACCOUNT);
    System.out.println("UserController.login(): account = " + account);
    renderText(String.valueOf(getModel(User.class).requestLogout(account)));
  }

  public void checkPhoneExistent(){
    String phone = getPara(User.PHONE);
    boolean result = getModel(User.class).requestCheckPhoneExistent(phone);
    System.out.println("UserController.checkPhoneExistent(): result = " + result);
    renderText(String.valueOf(result));
  }

  public void getUserInformation(){
    String account = getPara(ACCOUNT);
    JSONObject jsonObject = getModel(User.class).requestGetUserInformation(account);
    renderJson(jsonObject.toString());
  }

  public void changeNickname(){
    String account = getPara(ACCOUNT);
    String nickname = getPara(User.NICKNAME);
    boolean successful = getModel(User.class).requestChangeNickname(account, nickname);
    renderText(String.valueOf(successful));
  }


}

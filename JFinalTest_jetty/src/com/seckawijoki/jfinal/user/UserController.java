package com.seckawijoki.jfinal.user;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.Kv;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.seckawijoki.jfinal.constants.server.MoJiReTsu;
import com.seckawijoki.jfinal.constants.server.UploadPath;
import com.seckawijoki.jfinal.utils.JsonPackageUtils;
import com.seckawijoki.jfinal.utils.MyDbUtils;
import com.seckawijoki.jfinal.utils.TextUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Created by 瑶琴频曲羽衣魂 on 2017/11/21 at 14:10.
 */

@Before({ UserInterceptor.class })
public class UserController extends Controller {
  public void login() {
    String account = getPara(MoJiReTsu.ACCOUNT);
    String password = getPara(MoJiReTsu.PASSWORD);
    String mac = getPara(MoJiReTsu.MAC);
    System.out.println("UserController.login(): account = " + account);
    System.out.println("UserController.login(): password = " + password);
    System.out.println("UserController.login(): mac = " + mac);
    JSONObject jsonObject = getModel(User.class).requestLogin(account, password, mac);
    System.out.println("UserController.login(): jsonObject = " + jsonObject);
    renderJson(jsonObject.toString());
  }

  public void register() {
    String account = getPara(MoJiReTsu.PHONE);
    String password = getPara(MoJiReTsu.PASSWORD);
    String mac = getPara(MoJiReTsu.MAC);
    System.out.println("UserController.register(): account = " + account);
    System.out.println("UserController.register(): password = " + password);
    System.out.println("UserController.register(): mac = " + mac);
    boolean result = getModel(User.class).requestRegister(account, password, mac);
    renderText(String.valueOf(result));
  }

  public void logout() {
    String account = getPara(MoJiReTsu.ACCOUNT);
    System.out.println("UserController.login(): account = " + account);
    renderText(String.valueOf(getModel(User.class).requestLogout(account)));
  }

  public void checkPhoneExistent() {
    String phone = getPara(MoJiReTsu.PHONE);
    boolean result = getModel(User.class).requestCheckPhoneExistent(phone);
    System.out.println("UserController.checkPhoneExistent(): result = " + result);
    renderText(String.valueOf(result));
  }

  public void getUserInformation() {
    String account = getPara(MoJiReTsu.ACCOUNT);
    JSONObject jsonObject = getModel(User.class).requestGetUserInformation(account);
    renderJson(jsonObject.toString());
  }

  public void changeNickname() {
    long userId = getParaToLong(MoJiReTsu.USER_ID);
    String nickname = getPara(MoJiReTsu.NICKNAME);
    Record userRecord = MyDbUtils.getUserRecord(userId);
    if ( userRecord == null )
      renderNull();
    int update = Db.update("update " + MoJiReTsu.USER + " set nickname = ?", nickname);
    if ( update <= 0 ) {
      renderNull();
      return;
    }
    renderText(MoJiReTsu.TRUE);
  }

  public void changeEmail() {
    long userId = getParaToLong(MoJiReTsu.USER_ID);
    String email = getPara(MoJiReTsu.EMAIL);
    Record userRecord = MyDbUtils.getUserRecord(userId);
    if ( userRecord == null ) {
      System.err.println("UserController.changeEmail(): Invalid email");
      renderNull();
      return;
    }
    if ( !TextUtils.isEmailValid(email) ){
      renderNull();
      return;
    }
    int update = Db.update("update " + MoJiReTsu.USER + " set email = ?", email);
    if ( update <= 0 ) {
      renderNull();
      return;
    }
    renderText(MoJiReTsu.TRUE);
  }

  public void getDefaultPortrait() {
    File file = new File(PathKit.getWebRootPath() + "\\uploadedImages\\ic_default_user_portrait.png");
    System.out.println("UserController.getDefaultPortrait(): PathKit.getWebRootPath() = " + PathKit.getWebRootPath());
    System.out.println("UserController.getDefaultPortrait(): file = " + file);
    renderFile(file);
  }

  public void getUserInfo() {
    long userId = getParaToLong(MoJiReTsu.USER_ID);
    Record userRecord = MyDbUtils.getUserRecord(userId);
    JSONObject jsonObject = JsonPackageUtils.parseRecord(userRecord,
            MoJiReTsu.PHONE,
            MoJiReTsu.EMAIL,
            MoJiReTsu.NICKNAME
    );
    renderJson(jsonObject.toString());
  }

  public void getUserPortrait() {
    long userId = getParaToLong(MoJiReTsu.USER_ID);
    Record userRecord = MyDbUtils.getUserRecord(userId);
    String filePath = userRecord.getStr(MoJiReTsu.PORTRAIT_URI);
    File file = new File(filePath);
    if (file.exists() == false){
      System.out.println("UserController.getUserPortrait(): file = " + file);
      file = new File(UploadPath.DEFAULT_USER_PORTRAIT);
    }
    renderFile(file);
  }

  private void deleteExistentUserPortraitFiles(long userId){
    boolean result;
    File directory = new File(UploadPath.DIRECTORY_USER_PORTRAIT);
    if (!directory.exists()){
      try {
        result = directory.createNewFile();
        if (!result)return;
      } catch ( IOException e ) {
        e.printStackTrace();
      }
    }
    File[] files = directory.listFiles();
    if (files == null)return;
    for ( File file : files ) {
      String fileName = file.getName();
      if (fileName.contains("portrait_user" + userId)){
        result = file.delete();
        System.out.println("UserController.deleteExistentUserPortraitFiles(): deleteFile: " + result);
      }
    }
  }

  public void uploadUserPortrait() {
    boolean result;
    UploadFile uploadFile = getFile();
    long userId = getParaToLong(MoJiReTsu.USER_ID);
    System.out.println("UserController.uploadUserPortrait(): userId = " + userId);
    File file = uploadFile.getFile();
    String fileName = file.getName();
    String postfix = fileName.substring(fileName.lastIndexOf(".") + 1);
    System.out.println("UserController.uploadUserPortrait(): postfix = " + postfix);
    System.out.println("UserController.uploadUserPortrait(): file = " + file);
    String savingFileName = "portrait_user" + userId + "." + postfix;
    deleteExistentUserPortraitFiles(userId);
    File savingFile = new File(UploadPath.DIRECTORY_USER_PORTRAIT, savingFileName);
    if (savingFile.exists()){
      result = savingFile.delete();
      if (!result){
        System.err.println("UserController.uploadUserPortrait(): Failed to delete the file to be overwritten!");
        renderNull();
        return;
      }
    }
    result = file.renameTo(savingFile);
    if (!result){
      System.err.println("UserController.uploadUserPortrait(): Failed to rename file!");
      renderNull();
      return;
    }
    result = Db.update(
      Db.getSqlPara("updateUserPortraitUri",
              Kv.by(MoJiReTsu.USER_ID, userId)
                      .set(MoJiReTsu.PORTRAIT_URI, savingFile.getPath())
      )
    ) > 0;
    System.out.println("UserController.uploadUserPortrait(): savingFile = " + savingFile);
    if (!result) {
      System.err.println("UserController.uploadUserPortrait(): Failed to update database!");
      renderNull();
      return;
    }
    renderJson(new JSONObject().put(MoJiReTsu.RESULT, true).toString());
  }
}

package com.seckawijoki.jfinal.controller;

import com.jfinal.core.Controller;
import com.jfinal.kit.Kv;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.seckawijoki.jfinal.constants.server.MoJiReTsu;
import com.seckawijoki.jfinal.tools.JsonPackageTools;

import java.io.File;

/**
 * Created by 瑶琴频曲羽衣魂 on 2018/2/7 at 22:20.
 */

public class AppController extends Controller {
  public void index(){

  }

  public void getLatestApk(){
    String filePath = PathKit.getWebRootPath() + "//apk//app-demo-debug.apk";
//    String filePath = PathKit.getWebRootPath() + "//apk//futureve-mobile-zhihu-release-5.11.2(619).apk";
    renderFile(new File(filePath));
  }

  public void getApk(){
    int versionCode = getParaToInt(MoJiReTsu.VERSION_CODE);
    Record record = Db.findFirst(Db.getSqlPara("getApkDownloadUri",
            Kv.by(MoJiReTsu.VERSION_CODE, versionCode))
    );
    File apkFile = new File(record.getStr(MoJiReTsu.APK_DOWNLOAD_URI));
    renderFile(apkFile);
  }
  public void getLatestAppVersionCode() {
    Record record = Db.findFirst(Db.getSql("getLatestAppVersion"));
    System.out.println("AppController.getLatestAppVersionCode(): record = " + record);
    renderJson(JsonPackageTools.parseRecord(record,
            MoJiReTsu.VERSION_CODE
            )
            .toString()
    );
  }

  public void getLatestAppVersion(){
    Record record = Db.findFirst(Db.getSql("getLatestAppVersion"));
    System.out.println("AppController.getLatestAppVersion(): record = " + record);
    renderJson(JsonPackageTools.parseRecord(record,
            MoJiReTsu.VERSION_CODE,
            MoJiReTsu.VERSION_NAME,
            MoJiReTsu.VERSION_DESCRIPTION)
            .toString()
    );
  }
}

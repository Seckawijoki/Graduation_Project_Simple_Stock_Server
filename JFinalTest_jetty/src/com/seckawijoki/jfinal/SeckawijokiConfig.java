package com.seckawijoki.jfinal;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.SqlReporter;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.Engine;
import com.seckawijoki.jfinal.controller.AllStocksController;
import com.seckawijoki.jfinal.controller.ChuangYeBanController;
import com.seckawijoki.jfinal.controller.FavoriteController;
import com.seckawijoki.jfinal.controller.SHController;
import com.seckawijoki.jfinal.controller.SZController;
import com.seckawijoki.jfinal.controller.SearchController;
import com.seckawijoki.jfinal.controller.StockController;
import com.seckawijoki.jfinal.controller.StockTypeController;
import com.seckawijoki.jfinal.controller.HelloController;
import com.seckawijoki.jfinal.controller.IndexController;
import com.seckawijoki.jfinal.controller.TransactionController;
import com.seckawijoki.jfinal.interceptor.GlobalInterceptor;
import com.seckawijoki.jfinal.user.User;
import com.seckawijoki.jfinal.user.UserController;
import com.seckawijoki.jfinal.utils.OkHttpUtils;

import okhttp3.OkHttpClient;

/**
 * Created by 瑶琴频曲羽衣魂 on 2017/11/21 at 11:24.
 */

public class SeckawijokiConfig extends JFinalConfig {

  public void configConstant(Constants constants) {
    // 加载少量必要配置，随后可用PropKit.get(...)获取值
    loadPropertyFile("a_little_config.txt");
    constants.setDevMode(true);
    constants.setBaseDownloadPath(PathKit.getRootClassPath()+"\\k_line_chart");
  }

  public void configRoute(Routes routes) {
    routes
            .add("/", IndexController.class)
            .add("/hello", HelloController.class)
            .add("/chuangYeBan", ChuangYeBanController.class)
            .add("/sh", SHController.class)
            .add("/sz", SZController.class)
            .add("/user", UserController.class)
            .add("/allStocks", AllStocksController.class)
            .add("/stockType", StockTypeController.class)
            .add("/favorite", FavoriteController.class)
            .add("/stock", StockController.class)
            .add("/search", SearchController.class)
            .add("/transaction", TransactionController.class)
    ;
  }

  public void configEngine(Engine engine) {

  }

  public void configPlugin(Plugins plugins) {
    // 配置C3p0数据库连接池插件
    DruidPlugin druidPlugin = new DruidPlugin(
            getProperty("jdbcUrl"),
            getProperty("user"),
            getProperty("password").trim());
    plugins.add(druidPlugin);

    // 配置ActiveRecord插件
    ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
//    System.out.println("SeckawijokiConfig.configPlugin(): PathKit.getRootClassPath() = " + PathKit.getRootClassPath());
    arp.setBaseSqlTemplatePath(PathKit.getWebRootPath()+"\\sql");
    arp
            .addSqlTemplate("user.sql")
            .addSqlTemplate("stock.sql")
            .addSqlTemplate("search.sql")
            .addSqlTemplate("favorite.sql");
    arp.setShowSql(true);
    //TODO

    SqlReporter.setLog(true);
    plugins.add(arp);
    // 映射blog 表到 Blog模型

    arp.addMapping(
            User.TABLE_NAME,
            User.USER_ID,
            User.class);
    /*
    arp.addMapping(
            SZ.TABLE_NAME,
            SZ.STOCK_ID,
            SZ.class);
    arp.addMapping(
            SH.TABLE_NAME,
            SH.STOCK_ID,
            SH.class);
    arp.addMapping(
            ChuangYeBan.TABLE_NAME,
            ChuangYeBan.STOCK_ID,
            ChuangYeBan.class);
    */
  }

  public void configInterceptor(Interceptors interceptors) {
    interceptors.add(new GlobalInterceptor());
  }

  public void configHandler(Handlers handlers) {

  }

  @Override
  public void afterJFinalStart() {
    super.afterJFinalStart();
    new AfterJFinalStart();
  }

  @Override
  public void beforeJFinalStop() {
    super.beforeJFinalStop();
    OkHttpUtils.init();
  }

}

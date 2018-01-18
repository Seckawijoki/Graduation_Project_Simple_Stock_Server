package com.seckawijoki.jfinal.controller;

import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Record;
import com.seckawijoki.jfinal.constants.sina.SinaForeignStock;
import com.seckawijoki.jfinal.constants.sina.SinaServerPath;
import com.seckawijoki.jfinal.utils.DownloadUtils;
import com.seckawijoki.jfinal.utils.MyDbUtils;
import com.seckawijoki.jfinal.utils.SinaStockUtils;
import com.seckawijoki.jfinal.utils.SwitchCaseUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 瑶琴频曲羽衣魂 on 2017/12/14 at 14:37.
 */

public class StockController extends Controller {
  private static ExecutorService pool = Executors.newFixedThreadPool(4);

  public void getQuotations() {
    long userId = getParaToLong("userId");//userId
    final Long[] stockTableIds = getParaValuesToLong("stockTableId");
    if ( MyDbUtils.checkUserExistent(userId) == false ) {
      renderText("null");
      return;
    }
    JSONArray jsonArray = new JSONArray();
    for ( int i = 0 ; i < stockTableIds.length ; i++ ) {
      final long stockTableId = stockTableIds[i];
      Record stockRecord = MyDbUtils.getStockRecord(stockTableId);
      String stockId = stockRecord.getStr("stockId");
      int stockType = stockRecord.getInt("stockType");
      String url = SwitchCaseUtils.getSinaQuotationUrl(stockId, stockType);
      String result = getSinaStock(url);
      String[] values = SinaStockUtils.parse(result);
      jsonArray.put(new JSONObject()
              .put("favorite", MyDbUtils.checkFavoriteStock(userId, stockTableId))
              .put("stockId", stockId)
              .put("stockType", stockType)
              .put("values", values));
    }
    renderJson(jsonArray.toString());
  }

  public void getStocks() {
    long userId = getParaToLong("userId");//userId
    final Long[] stockTableIds = getParaValuesToLong("stockTableId");
    if ( MyDbUtils.checkUserExistent(userId) == false ) {
      renderText("null");
      return;
    }
    JSONArray jsonArray = new JSONArray();
    for ( int i = 0 ; i < stockTableIds.length ; i++ ) {
      final long stockTableId = stockTableIds[i];
      Record stockRecord = MyDbUtils.getStockRecord(stockTableId);
      String stockId = stockRecord.getStr("stockId");
      int stockType = stockRecord.getInt("stockType");
      String url = SwitchCaseUtils.getSinaStockUrl(stockId, stockType);
      String result = getSinaStock(url);
      String[] values = SinaStockUtils.parse(result);
      jsonArray.put(new JSONObject()
              .put("favorite", MyDbUtils.checkFavoriteStock(userId, stockTableId))
              .put("stockId", stockId)
              .put("stockType", stockType)
              .put("values", values));
    }
    renderJson(jsonArray.toString());
  }

  public void getForeignStocks() {
    JSONArray jsonArray = new JSONArray();
    List<String> stockNameList = new ArrayList<>();
    for ( int i = 0 ; i < SinaForeignStock.ids.length ; ++i ) {
      String result = getSinaStock(SinaServerPath.STOCK_BASE_PATH + SinaForeignStock.ids[i]);
      String[] values = SinaStockUtils.parse(result);
      JSONArray ja = new JSONArray();
      ja.put(values[0]);
      ja.put(values[1]);
      ja.put(values[2]);
      if ( i == 2 ) {
        System.out.println("StockController.getForeignStocks(): values[3] = " + values[3]);
        ja.put(values[3].substring(0, values[3].length() - 1));
      } else {
        ja.put(values[3]);
      }
      ja.put(SinaForeignStock.ids.length - i);
      stockNameList.add(values[0]);
      jsonArray.put(ja);
    }
    System.out.println("StockController.getForeignStocks(): stockNameList = " + stockNameList);
    renderJson(jsonArray.toString());
  }


  private static String getSinaStock(String url) {
    Callable<String> callable = () -> {
      OkHttpClient okHttpClient = new OkHttpClient();
      Request request = new Request.Builder()
              .url(url)
              .get()
              .build();
      Response response = okHttpClient.newCall(request).execute();
      if ( !response.isSuccessful() ) {
        return null;
      }
      String result = response.body().string();
//      System.out.println("StockController.call(): result = " + result);
      return result;
    };
    Future<String> future = pool.submit(callable);
    try {
      return future.get();
    } catch ( InterruptedException | ExecutionException e ) {
      e.printStackTrace();
      return null;
    }
  }

  public void getKLineChartFileName() {
    long stockTableId = getParaToLong("stockTableId");
    int kLineType = getParaToInt("kLineType");
    renderJson(new JSONObject()
            .put("kLineChartFileName", getKLineChartFileName(stockTableId, kLineType))
            .toString());
  }

  private String getKLineChartFileName(long stockTableId, int kLineType) {
    Record stockRecord = MyDbUtils.getStockRecord(stockTableId);
    if ( stockRecord == null ) {
      return null;
    }
    String stockId = stockRecord.getStr("stockId");
    String sinaKLineType = SwitchCaseUtils.getSinaKLineType(kLineType);
    String sinaStockType = SwitchCaseUtils.getSinaStockType(stockRecord.getInt("stockType"));
    return sinaStockType + stockId + "_" + sinaKLineType + ".gif";
  }

  public void getKLineChart() {
    long stockTableId = getParaToLong("stockTableId");
    int kLineType = getParaToInt("kLineType");
    Record stockRecord = MyDbUtils.getStockRecord(stockTableId);
    if ( stockRecord == null ) {
      renderNull();
      return;
    }
    String savePath = PathKit.getWebRootPath() + "\\k_line_chart\\";
    String stockId = stockRecord.getStr("stockId");
    String sinaKLineType = SwitchCaseUtils.getSinaKLineType(kLineType);
    String sinaStockType = SwitchCaseUtils.getSinaStockType(stockRecord.getInt("stockType"));
    String url = SwitchCaseUtils.getSinaKLineUrl(sinaStockType, stockId, sinaKLineType);
    String fileName = getKLineChartFileName(stockTableId, kLineType);
    File file = DownloadUtils.get().download(url, savePath, fileName);
    renderFile(file);
  }
}

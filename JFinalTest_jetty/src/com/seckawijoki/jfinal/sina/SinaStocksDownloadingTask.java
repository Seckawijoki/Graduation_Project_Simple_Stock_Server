package com.seckawijoki.jfinal.sina;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.seckawijoki.jfinal.constants.server.MoJiReTsu;
import com.seckawijoki.jfinal.tools.SinaRequestTools;
import com.seckawijoki.jfinal.tools.SinaResponseTools;
import com.seckawijoki.jfinal.utils.OkHttpUtils;
import com.sun.javafx.WeakReferenceQueue;

import java.sql.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by 瑶琴频曲羽衣魂 on 2018/3/15 at 16:26.
 */

public class SinaStocksDownloadingTask {
  private static class SinaStocksDownloadingTaskHolder{
    private static SinaStocksDownloadingTask INSTANCE = new SinaStocksDownloadingTask();
  }
  public static SinaStocksDownloadingTask getInstance(){
    return SinaStocksDownloadingTaskHolder.INSTANCE;
  }
  private SinaStocksDownloadingTask(){
    System.out.println("SinaStocksDownloadingTask.SinaStocksDownloadingTask(): ");
    if (SinaStocksDownloadingTaskHolder.INSTANCE != null){
      throw new RuntimeException();
    }
  }
  private int mQuotationRefreshCount = 0;
  private int mStockRefreshCount = 0;
  private ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
  public void start(){
    Runnable r1 = ()->refreshSinaQuotations(30);
    Runnable r2 = this::refreshSinaStocks;
    pool.scheduleAtFixedRate(r1, 0, 45, TimeUnit.SECONDS);
    pool.scheduleAtFixedRate(r2, 0, 60, TimeUnit.SECONDS);
  }

  public SinaStocksDownloadingTask refreshSinaQuotations(int gap){
    List<Record> list = Db.find(Db.getSql("getAllStocks"));
//    Db.update("delete * from sina_quotations");
    for ( int i = 0 ; i < list.size() ; i += gap ) {
      WeakReferenceQueue<Object> reference = new WeakReferenceQueue<>();
      SinaRequestTools.URLBuilder urlBuilder = new SinaRequestTools.URLBuilder();
      reference.add(urlBuilder);
      for ( int j = 0 ; j < gap && i+j < list.size(); ++j ) {
        Record record = list.get(j+i);
        urlBuilder.appendQuotationUrl(
                record.getStr(MoJiReTsu.STOCK_ID),
                record.getInt(MoJiReTsu.STOCK_TYPE)
        );
      }
      String multiResults = OkHttpUtils.get().debug(false).url(urlBuilder.build()).execute().string();
      reference.add(multiResults);
      String[] result = SinaResponseTools.splitMultiResponses(multiResults);
      System.gc();
      for ( int j = 0 ; j < gap && i+j < list.size() ; j++ ) {
        Record record = list.get(j+i);
        String[] values = SinaResponseTools.parse(result[j]);
        Record sinaQuotation = Db.findFirst(Db.getSqlPara("findSinaQuotation",
                Kv.by(MoJiReTsu.STOCK_TABLE_ID, record.getStr(MoJiReTsu.STOCK_TABLE_ID))
        ));
        if (values == null || values.length <= 1)continue;
//        System.out.println("SinaStocksDownloadingTask.refreshSinaQuotations(): sinaQuotation = " + sinaQuotation);
        if (sinaQuotation == null){
          Record quotation = new Record()
                  .set(MoJiReTsu.STOCK_TABLE_ID, record.getStr(MoJiReTsu.STOCK_TABLE_ID))
                  .set(MoJiReTsu.STOCK_ID, record.getStr(MoJiReTsu.STOCK_ID))
                  .set(MoJiReTsu.STOCK_NAME, values.length >= 1 ? values[0] : 0)
                  .set(MoJiReTsu.CURRENT_PRICE, values.length >= 2 ? values[1] : 0)
                  .set(MoJiReTsu.CURRENT_POINT, values.length >= 3 ? values[2] : 0)
                  .set(MoJiReTsu.FLUCTUATION_RATE, values.length >= 4 ? values[3] : 0)
                  .set(MoJiReTsu.TURNOVER, values.length >= 5 ? values[4] : 0)
                  .set(MoJiReTsu.VOLUME, values.length >= 6 ? values[5] : 0)
                  .set(MoJiReTsu.UPDATE_TIME, new Date(System.currentTimeMillis()));
          Db.save("sina_quotations", MoJiReTsu.STOCK_TABLE_ID, quotation);
        } else {
          Kv kv = Kv.by(MoJiReTsu.STOCK_TABLE_ID, record.getStr(MoJiReTsu.STOCK_TABLE_ID))
                  .set(MoJiReTsu.STOCK_NAME, values.length >= 1 ? values[0] : 0)
                  .set(MoJiReTsu.CURRENT_PRICE, values.length >= 2 ? values[1] : 0)
                  .set(MoJiReTsu.CURRENT_POINT, values.length >= 3 ? values[2] : 0)
                  .set(MoJiReTsu.FLUCTUATION_RATE, values.length >= 4 ? values[3] : 0)
                  .set(MoJiReTsu.TURNOVER, values.length >= 5 ? values[4] : 0)
                  .set(MoJiReTsu.VOLUME, values.length >= 6 ? values[5] : 0);
          Db.update(Db.getSqlPara("updateSinaQuotation", kv));
        }
      }
    };
    System.out.println("SinaStocksDownloadingTask.refreshSinaQuotations(): mQuotationRefreshCount = " + ++mQuotationRefreshCount);
    return this;
  }
  public SinaStocksDownloadingTask refreshSinaStocks(){

    System.out.println("SinaStocksDownloadingTask.refreshSinaStocks(): mStockRefreshCount = " + ++mStockRefreshCount);
    return this;
  }
}

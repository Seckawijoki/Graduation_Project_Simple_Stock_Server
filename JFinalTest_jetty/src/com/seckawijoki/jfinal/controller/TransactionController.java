package com.seckawijoki.jfinal.controller;

import com.jfinal.core.Controller;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.seckawijoki.jfinal.constants.server.MoJiReTsu;
import com.seckawijoki.jfinal.utils.JsonPackageUtils;
import com.seckawijoki.jfinal.utils.MyDbUtils;

import org.eclipse.jetty.util.ajax.JSON;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

/**
 * Created by 瑶琴频曲羽衣魂 on 2018/1/16 at 17:15.
 */

public class TransactionController extends Controller{
  public void addTransaction(){
    long userId = getParaToLong(MoJiReTsu.USER_ID);
    long stockTableId = getParaToLong(MoJiReTsu.STOCK_TABLE_ID);
    double tradePrice = Double.valueOf(getPara(MoJiReTsu.TRADE_PRICE));
    int tradeCount = getParaToInt(MoJiReTsu.TRADE_COUNT);
    Date date;
    Time time;
    boolean result = Db.save(
            MoJiReTsu.TRANSACTION,
            new Record()
            .set(MoJiReTsu.USER_ID, userId)
            .set(MoJiReTsu.STOCK_TABLE_ID, stockTableId)
            .set(MoJiReTsu.TRADE_PRICE, tradePrice)
            .set(MoJiReTsu.TRADE_COUNT, tradeCount)
            .set(MoJiReTsu.TRADE_DATE, date = new Date(System.currentTimeMillis()))
            .set(MoJiReTsu.TRADE_TIME, time = new Time(System.currentTimeMillis()))
    );
    if (!result){
      renderNull();
      return;
    }
    int update = Db.update(
            Db.getSqlPara("updateUserPurchasingPower",
                    Kv.by(MoJiReTsu.USER_ID, userId)
                            .set("cost", tradeCount*tradePrice))
    );
    if (update <= 0){
      renderNull();
      return;
    }
    renderJson(new JSONObject().put(MoJiReTsu.RESULT, true));
  }

  public void getUserTransactions(){
    long userId = getParaToLong(MoJiReTsu.USER_ID);
    List<Record> transactionList = Db.find(
            Db.getSqlPara("getUserTransactions",
                    Kv.by(MoJiReTsu.USER_ID, userId)
            )
    );
    JSONArray jsonArray = new JSONArray();
    for ( Record record : transactionList ) {
      Record stockRecord = MyDbUtils.getStockRecord(record.getLong(MoJiReTsu.STOCK_TABLE_ID));
      jsonArray.put(
              JsonPackageUtils.parseRecord(
                      record,
                      MoJiReTsu.TRADE_PRICE,
                      MoJiReTsu.TRADE_COUNT,
                      MoJiReTsu.TRADE_DATE,
                      MoJiReTsu.TRADE_TIME
              )
      );
    }
    renderJson(jsonArray.toString());
  }

  public void getAllTransactions(){
    List<Record> transactionList = Db.find(
            Db.getSql("getAllTransactions")
    );
    JSONArray jsonArray = new JSONArray();
    for ( Record record : transactionList ) {
      Record stockRecord = MyDbUtils.getStockRecord(record.getLong(MoJiReTsu.STOCK_TABLE_ID));

      jsonArray.put(
              JsonPackageUtils.parseRecord(record,
                      MoJiReTsu.TRADE_PRICE,
                      MoJiReTsu.TRADE_COUNT,
                      MoJiReTsu.TRADE_DATE,
                      MoJiReTsu.TRADE_TIME)
      );
    }
    renderJson(jsonArray.toString());
  }

}

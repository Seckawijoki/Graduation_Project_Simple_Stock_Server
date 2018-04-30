package com.seckawijoki.jfinal.controller;


import com.jfinal.core.Controller;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.seckawijoki.jfinal.constants.server.MoJiReTsu;
import com.seckawijoki.jfinal.tools.JsonPackageTools;
import com.seckawijoki.jfinal.tools.SinaRequestTools;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

/**
 * Created by 瑶琴频曲羽衣魂 on 2018/1/16 at 17:15.
 */

public class TransactionController extends Controller {
  public void getAllTransactions() {
    List<Record> transactionList = Db.find(Db.getSqlPara("getAllTransactions"));
    JSONArray jsonArray = new JSONArray();
    for ( Record record : transactionList ) {
      JSONObject jsonObject = JsonPackageTools.parseRecord(
              record,
              MoJiReTsu.TRANSACTION_ID,
              MoJiReTsu.TRADE_PRICE,
              MoJiReTsu.TRADE_COUNT,
              MoJiReTsu.STOCK_TABLE_ID,
              MoJiReTsu.STOCK_NAME,
              MoJiReTsu.STOCK_ID,
              MoJiReTsu.STOCK_TYPE,
              MoJiReTsu.USER_ID,
              MoJiReTsu.NICKNAME
      );
      jsonObject.put(
              MoJiReTsu.TRADE_DATE_TIME,
              record.getDate(MoJiReTsu.TRADE_DATE_TIME).getTime());
      jsonArray.put(jsonObject);
    }
    renderJson(jsonArray.toString());
  }


  public void getUserTransactions() {
    long userId = getParaToLong(MoJiReTsu.USER_ID);
    System.out.println("TransactionController.getUserTransactions(): userId = " + userId);
    List<Record> transactionList = Db.find(
            Db.getSqlPara("getUserTransactions",
                    Kv.by(MoJiReTsu.USER_ID, userId)
            )
    );
    System.out.println("TransactionController.getUserTransactions(): transactionList = " + transactionList);
    JSONArray jsonArray = new JSONArray();
    for ( Record record : transactionList ) {
      JSONObject jsonObject = JsonPackageTools.parseRecord(
              record,
              MoJiReTsu.TRADE_PRICE,
              MoJiReTsu.TRADE_COUNT,
              MoJiReTsu.STOCK_TABLE_ID,
              MoJiReTsu.STOCK_NAME,
              MoJiReTsu.STOCK_ID,
              MoJiReTsu.STOCK_TYPE,
              MoJiReTsu.NICKNAME
      );
      jsonObject.put(
              MoJiReTsu.TRADE_DATE_TIME,
              record.getDate(MoJiReTsu.TRADE_DATE_TIME).getTime());
      jsonArray.put(jsonObject);
    }
    renderJson(jsonArray.toString());
  }

  public void getUserPositions() {
    long userId = getParaToLong(MoJiReTsu.USER_ID);
    List<Record> transactionList = Db.find(
            Db.getSqlPara("getUserTransactions",
                    Kv.by(MoJiReTsu.USER_ID, userId)
            )
    );
    JSONArray jsonArray = new JSONArray();
    for ( Record record : transactionList ) {
      JSONObject jsonObject = JsonPackageTools.parseRecord(
              record,
              MoJiReTsu.TRADE_PRICE,
              MoJiReTsu.TRADE_COUNT,
              MoJiReTsu.STOCK_TABLE_ID,
              MoJiReTsu.STOCK_NAME,
              MoJiReTsu.STOCK_ID,
              MoJiReTsu.STOCK_TYPE,
              MoJiReTsu.NICKNAME
      );
      String[] sinaStockValues = SinaRequestTools.getSinaStock(
              record.getStr(MoJiReTsu.STOCK_ID),
              record.getInt(MoJiReTsu.STOCK_TYPE)
      );
      double currentPrice = Double.parseDouble(sinaStockValues[3]);
      double tradePrice = record.getDouble(MoJiReTsu.TRADE_PRICE);
      jsonObject.put(MoJiReTsu.CURRENT_PRICE, currentPrice);
      jsonObject.put(MoJiReTsu.CLOSING_PRICE_YESTERDAY, tradePrice);
      int tradeCount = record.getInt(MoJiReTsu.TRADE_COUNT);
      double profitOrLoss = tradeCount * ( currentPrice - tradePrice );
      jsonObject.put(MoJiReTsu.PROFIT_OR_LOSS, profitOrLoss);
      jsonObject.put(MoJiReTsu.PROFIT_OR_LOSS_RATE, profitOrLoss * 100 / tradeCount / tradePrice);
      jsonArray.put(jsonObject);
    }
    renderJson(jsonArray.toString());
  }

  public void getUserAsset() {

  }

  public void getUserOrders() {

  }


  public void addTransaction() {
    long userId = getParaToLong(MoJiReTsu.USER_ID);
    long stockTableId = getParaToLong(MoJiReTsu.STOCK_TABLE_ID);
    double tradePrice = Double.valueOf(getPara(MoJiReTsu.TRADE_PRICE));
    int tradeCount = getParaToInt(MoJiReTsu.TRADE_COUNT);
    boolean result = Db.save(
            MoJiReTsu.TRANSACTION,
            new Record()
                    .set(MoJiReTsu.USER_ID, userId)
                    .set(MoJiReTsu.STOCK_TABLE_ID, stockTableId)
                    .set(MoJiReTsu.TRADE_PRICE, tradePrice)
                    .set(MoJiReTsu.TRADE_COUNT, tradeCount)
                    .set(MoJiReTsu.TRADE_DATE_TIME, Calendar.getInstance().getTime())
    );
    if ( !result ) {
      renderJson(new JSONObject().put(MoJiReTsu.RESULT, false).toString());
      return;
    }
    /*
    int update = Db.update(
            Db.getSqlPara("updateUserPurchasingPower",
                    Kv.by(MoJiReTsu.USER_ID, userId)
                            .set("cost", tradeCount * tradePrice))
    );
    if ( update <= 0 ) {
      renderJson(new JSONObject().put(MoJiReTsu.RESULT, false).toString());
      return;
    }
    */
    renderJson(new JSONObject().put(MoJiReTsu.RESULT, true).toString());
  }


}

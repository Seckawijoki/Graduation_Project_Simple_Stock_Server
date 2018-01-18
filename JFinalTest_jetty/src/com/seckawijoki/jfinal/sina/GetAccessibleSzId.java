package com.seckawijoki.jfinal.sina;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.seckawijoki.jfinal.utils.SinaStockUtils;
import com.seckawijoki.jfinal.utils.TextUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 瑶琴频曲羽衣魂 on 2017/11/26 at 10:57.
 */

public class GetAccessibleSzId extends Thread implements Callback {
  private OkHttpClient okHttpClient = new OkHttpClient();
  private static final String BASE_PATH = "http://hq.sinajs.cn/list=s_sz";
  @Override
  public void run() {
    String stockId;
    for ( int i = 0 ; i < 999999 ; ++i ) {
      stockId = String.format("%06d", i);
      Request request = new Request.Builder()
              .url(BASE_PATH + stockId)
              .get()
              .build();
      okHttpClient.newCall(request).enqueue(this);
    }
  }

  @Override
  public void onFailure(Call call, IOException e) {

  }

  @Override
  public void onResponse(Call call, Response response) throws IOException {
    String result = response.body().string();
    String stockId = SinaStockUtils.extractStockId(result);
    String[] values = SinaStockUtils.parse(result);
//    System.out.println("GetAccessibleSzId.onResponse(): values = " + Arrays.toString(values));
    if (values.length != 0 && !TextUtils.isEmpty(values[0])){
      Record savedRecord = new Record()
              .set("stockId", stockId)
              .set("stockName", values[0]);
      try {
        int id = Integer.valueOf(stockId);
        if (id %5000 == 0){
          System.out.println("GetAccessibleSzId.onResponse(): stockId = " + stockId);
        }
      } catch ( NumberFormatException ignored ) {

      }
      double currentPrice = Double.valueOf(values[1]);
      if (currentPrice <= 0)return;
//      System.out.println("GetAccessibleSzId.onResponse(): stockId = " + stockId);
//      System.out.println("GetAccessibleSzId.onResponse(): record = " + record);
      Record existence =
              Db.findFirst("select * from sz where stockId = " + stockId);
      if ( existence == null ) {
        Db.save("sz", savedRecord);
      } else {
        Db.update("sz", "stockId", savedRecord);
      }
    }
  }
}

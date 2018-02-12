package com.seckawijoki.jfinal.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 瑶琴频曲羽衣魂 on 2017/12/14 at 15:32.
 */

public class OkHttpUtils {
  private static final String TAG = "OkHttpUtils";
  private static ExecutorService pool;
  private static final int TIMEOUT_SECONDS = 10;
  private OkHttpUtils(){}
  public static void init(){
    pool = Executors.newFixedThreadPool(8);
  }
  public static GetBuilder get(){
    return new GetBuilder();
  }
  public static PostBuilder post(){
    return new PostBuilder();
  }
  public static class PostBuilder {
    private Request.Builder requestBuilder = new Request.Builder();
    private FormBody.Builder formBodyBuilder = new FormBody.Builder();
    private PostBuilder() {
    }
    public PostBuilder url(String url) {
      requestBuilder.url(url);
      return this;
    }

    public PostBuilder addParam(String name, String value) {
      formBodyBuilder.add(name, value);
      return this;
    }

    public ResponseResult execute() {
      requestBuilder.post(formBodyBuilder.build());
      OkHttpClient okHttpClient = new OkHttpClient.Builder()
              .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
              .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
              .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
              .build();
      Callable<String> callable = () -> {
        Response response =
                okHttpClient.newCall(requestBuilder.build()).execute();
        return (String) response.body().string();
      };
      Future<String> future = pool.submit(callable);
      try {
        return new ResponseResult(future.get());
      } catch ( InterruptedException | ExecutionException e ) {
        return null;
      }
    }
  }
  public static class GetBuilder{
    private Request.Builder requestBuilder = new Request.Builder();
    GetBuilder(){
      requestBuilder.get();
    }
    public GetBuilder url(String url){
      requestBuilder.url(url);
      return this;
    }
    public ResponseResult execute(){
      Callable<String> callable = () -> {
        OkHttpClient okHttpClient = new OkHttpClient();
        Response response = okHttpClient.newCall(requestBuilder.build()).execute();
        String result = response.body().string();
        System.out.println("GetBuilder.execute(): result = " + result);
        return result;
      };
      Future<String> future = pool.submit(callable);
      try {
        return new ResponseResult(future.get());
      } catch ( InterruptedException | ExecutionException e ) {
        e.printStackTrace();
        return null;
      }
    }
  }
  public static class ResponseResult{
    private String result;
    public ResponseResult(String result) {
      this.result = result;
    }
    public JSONObject jsonObject(){
      try {
        return new JSONObject(result);
      } catch ( JSONException e ) {
        return null;
      }
    }
    public JSONArray jsonArray(){
      try {
        return new JSONArray(result);
      } catch ( JSONException e ) {
        return null;
      }
    }
    public Boolean bool(){
      return Boolean.valueOf(result);
    }
    public String string(){
      return result;
    }
  }
}

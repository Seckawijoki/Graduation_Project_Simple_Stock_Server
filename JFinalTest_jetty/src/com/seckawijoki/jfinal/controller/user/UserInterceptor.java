package com.seckawijoki.jfinal.controller.user;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

/**
 * Created by 瑶琴频曲羽衣魂 on 2017/11/21 at 14:10.
 */

public class UserInterceptor implements Interceptor {
  @Override
  public void intercept(Invocation invocation) {
    invocation.invoke();
  }
}

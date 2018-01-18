package com.seckawijoki.jfinal.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

/**
 * Created by 瑶琴频曲羽衣魂 on 2017/12/13 at 21:57.
 */

public class GlobalInterceptor implements Interceptor {
  @Override
  public void intercept(Invocation invocation) {
    System.out.println("========before invoked: " + invocation.getActionKey() + "========");
    invocation.invoke();
    System.out.println("========after invoked: " + invocation.getActionKey() + "========");
  }
}

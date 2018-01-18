package com.seckawijoki.jfinal.controller;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;

/**
 * Created by 瑶琴频曲羽衣魂 on 2017/11/21 at 11:24.
 */

public class HelloController extends Controller {

  public void index(){
    String parameter = getPara("name");
    renderText(parameter);
  }

  public void index(String id){
    renderText(id);
  }

}

package com.seckawijoki.jfinal.controller;

import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;

/**
 * Created by 瑶琴频曲羽衣魂 on 2017/11/21 at 11:23.
 */

public class IndexController extends Controller {

  @ActionKey("/")
  public void index(){
    renderText("index");
  }
}

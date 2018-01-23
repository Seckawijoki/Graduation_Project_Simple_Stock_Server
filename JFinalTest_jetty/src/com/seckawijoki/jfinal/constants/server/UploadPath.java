package com.seckawijoki.jfinal.constants.server;

import com.jfinal.kit.PathKit;

/**
 * Created by 瑶琴频曲羽衣魂 on 2018/1/22 at 21:17.
 */

public interface UploadPath {
  String BASE_PATH = PathKit.getWebRootPath() + "\\";
  String DEFAULT_USER_PORTRAIT = BASE_PATH + "ic_default_user_portrait.png";
  String DIRECTORY_K_LINE_CHART = BASE_PATH + "k_line_chart\\";
  String DIRECTORY_USER_PORTRAIT = BASE_PATH + "user_portrait\\";
}

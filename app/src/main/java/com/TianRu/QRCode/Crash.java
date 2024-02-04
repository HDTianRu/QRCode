package com.TianRu.QRCode;

import android.app.Application;
import android.content.Context;

public class Crash extends Application {

  @Override
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    UserHelper.create(base);
    spUtil.setContext(base);
    ScreenCaptureHelper.setContext(base);
    xcrash.XCrash.init(this);
  }
}

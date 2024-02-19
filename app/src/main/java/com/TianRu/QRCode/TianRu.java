package com.TianRu.QRCode;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

public class TianRu extends Application {

  @Override
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    UserHelper.create(base);
    spUtil.setContext(base);
    ScreenCaptureHelper.setContext(base);
    xcrash.XCrash.init(this);
    createNotificationChannel();
  }
  
  private void createNotificationChannel() {
      CharSequence name = "确认登录";
      String description = "关闭自动确认时，发送确认通知";
      int importance = NotificationManager.IMPORTANCE_HIGH;
      NotificationChannel channel = new NotificationChannel("confirm", name, importance);
      channel.setDescription(description);
      NotificationManager notificationManager = getSystemService(NotificationManager.class);
      notificationManager.createNotificationChannel(channel);
    }
}

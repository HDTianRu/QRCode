package com.TianRu.QRCode;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import com.google.android.material.color.DynamicColors;
import java.util.List;
import java.util.ArrayList;

public class TianRu extends Application {

  @Override
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    UserHelper.create(base);
    spUtil.setContext(base);
    ScreenCaptureHelper.setContext(base);
    xcrash.XCrash.init(this);
    createNotificationChannel();
    DynamicColors.applyToActivitiesIfAvailable(this);
  }
  
  private void createNotificationChannel() {
      List<NotificationChannel> channels = new ArrayList<NotificationChannel>(2);
      channels.add(generateChannel("confirm", "确认登录", "关闭自动确认时，发送确认通知", NotificationManager.IMPORTANCE_HIGH));
      channels.add(generateChannel("fore", "前台服务", "运行前台服务所需通知", NotificationManager.IMPORTANCE_MIN));
      NotificationManager notificationManager = getSystemService(NotificationManager.class);
      notificationManager.createNotificationChannels(channels);
    }
    
  private NotificationChannel generateChannel(String flag, String name, String description, int importance){
    NotificationChannel channel = new NotificationChannel(flag, name, importance);
    channel.setDescription(description);
    return channel;
  }
}

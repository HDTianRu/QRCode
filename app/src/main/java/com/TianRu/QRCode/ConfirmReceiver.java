package com.TianRu.QRCode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationManagerCompat;

public class ConfirmReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    String action = intent.getAction();
    if (action == null) return;
    final String data = intent.getStringExtra("data");
    if (action == "confirm")
      new Thread(new Runnable(){
          @Override
          public void run() {
            Request.confirm(data);
          }
        }).start();
    if (action == "cancel") MainActivity.logger.info("已取消");
    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
    notificationManager.cancel(114);
  }
}


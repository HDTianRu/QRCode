package com.TianRu.QRCode;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import com.TianRu.QRCode.ScreenCaptureHelper;

public class RecordService extends Service {
  public RecordService() {
  }

  @Override
  public void onCreate() {
    super.onCreate();
    startNotification();
  }

  @Override
  public int onStartCommand(final Intent intent, int flags, int startId) {
    int mResultCode = intent.getIntExtra("code", -1);
    Intent mResultData = intent.getParcelableExtra("data");
    MediaProjection mMediaProjection = ((MediaProjectionManager) this.getSystemService(Context.MEDIA_PROJECTION_SERVICE)).getMediaProjection(mResultCode, mResultData);
    ScreenCaptureHelper.setMediaProjection(mMediaProjection);
    return super.onStartCommand(intent, flags, startId);
  }

  public void startNotification() {
    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "fore")
      .setSmallIcon(R.drawable.ic_launcher)
      .setPriority(NotificationCompat.PRIORITY_MIN)
      .setContentTitle("抢码器运行中~~~")
      .setContentText("旅行总有一天会到达终点，不必匆忙");
    Notification notification = notificationBuilder.build();
    notification.defaults = Notification.DEFAULT_SOUND;
    startForeground(233, notification);
  }

  @Override
  public IBinder onBind(Intent intent) {
    throw new UnsupportedOperationException("Not implemented yet");
  }
}

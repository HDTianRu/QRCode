package com.TianRu.QRCode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

  private MediaProjectionManager mMediaProjectionManager;

  private static final int REQUEST_CODE_SCREEN_CAPTURE = 114514;
  private static final int REQUEST_CODE = 1919810;
  public static Logger logger;
  private WindowManager windowManager;
  private WindowManager.LayoutParams params;
  private View floatingView;
  private ScreenCaptureHelper helper =null;
  private Handler mHandler;
  private boolean isAdd = false;
  private boolean isRun = false;
  private UserHelper uh;
  private User user;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mHandler = new Handler();
    uh = UserHelper.getHelper();
    user = uh.get();

// 实例化一个悬浮窗View
    floatingView = LayoutInflater.from(this).inflate(R.layout.floating_view, null);
    Button setting = findViewById(R.id.setting);
    Button request = findViewById(R.id.request);
    Button login = findViewById(R.id.login);
    Button wishlog = findViewById(R.id.wishlog);
    Button start = findViewById(R.id.start);
    Button flv = findViewById(R.id.flv);
    Button confirm = findViewById(R.id.confirm);
    setting.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          startActivity(new Intent(MainActivity.this, Setting.class));
        }
      });
    login.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          startActivity(new Intent(MainActivity.this, Login.class));
        }
      });
    flv.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          startActivity(new Intent(MainActivity.this, Flv.class));
        }
      });
    TextView tv=findViewById(R.id.log);
    TextView floating_tv=floatingView.findViewById(R.id.floating_log);
    logger = new Logger(this, tv, floating_tv, spUtil.getBoolean("debug", false) ? Logger.LEVEL_DEBUG : Logger.LEVEL_INFO);
    wishlog.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          new Thread(new Runnable(){
              @Override
              public void run() {
                Request.getAuthkey(user.stoken, "243523245");
              }
            }).start();
        }
      });
    request.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          String packageName = getPackageName();
          if (!Settings.canDrawOverlays(MainActivity.this)) {
            // 如果应用程序没有被授予悬浮窗口权限，则打开权限授权界面
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + packageName));
            startActivityForResult(intent, REQUEST_CODE);
            return;
          }
          Intent intent = new Intent();
          PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
          if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);
            return;
          }
          Toast.makeText(MainActivity.this, "已授予所需权限，无需再次授予", Toast.LENGTH_SHORT).show();
        }
      });
    confirm.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          final View layout = LayoutInflater.from(MainActivity.this).inflate(R.layout.simple_input, null);
          AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
          builder.setView(layout);
          builder.setTitle("Input Confirm Data");
          builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                final String data = ((EditText)layout.findViewById(R.id.input)).getText().toString();
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                      Request.confirm(data);
                    }
                  }).start();
                dialog.dismiss();
              }
            });
          builder.create().show();
        }
      });
    // 获取WindowManager服务
    windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

// 创建一个悬浮窗布局参数
    params = new WindowManager.LayoutParams(
      WindowManager.LayoutParams.WRAP_CONTENT,
      WindowManager.LayoutParams.WRAP_CONTENT,
      WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
      WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
      PixelFormat.TRANSLUCENT);

// 设置布局参数的位置和大小
    params.x = 0;
    params.y = 0;

// 添加悬浮窗按钮的点击事件
    final Button floatingButton = floatingView.findViewById(R.id.floating_button);
    floatingButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          mHandler.post(new Runnable(){
              @Override
              public void run() {
                if (user == null) {
                  Toast.makeText(MainActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                  return;
                }
                if (helper == null) {
                  Toast.makeText(MainActivity.this, "未授予获取屏幕权限", Toast.LENGTH_SHORT).show();
                  return;
                }
                if (isRun) {
                  Toast.makeText(MainActivity.this, "已关闭", Toast.LENGTH_SHORT).show();
                  isRun = false;
                  floatingButton.setText("开始扫描");
                  helper.pause();
                  return;
                }
                final boolean autoClose = spUtil.getBoolean("autoClose", true);
                final boolean autoConfirm = spUtil.getBoolean("autoConfirm", true);
                Toast.makeText(MainActivity.this, "已开启", Toast.LENGTH_SHORT).show();
                isRun = true;
                floatingButton.setText("关闭扫描");
                user = uh.get();
                logger.info("开启抢码");
                helper.Capture(new ImageReader.OnImageAvailableListener(){
                    @Override
                    public void onImageAvailable(ImageReader reader) {
                      Image image = reader.acquireLatestImage();
                      /*Bitmap bit = ScreenCaptureHelper.imageToBitmap(image);
                       image.close();
                       if (bit == null) return;
                       String url = ScanQRCode.scan(bit);
                       bit.recycle();*/
                      String url = ScanQRCode.image(image, true);
                      logger.debug(url);
                      if (url.startsWith("https://user.mihoyo.com/qr_code_in_game.html")) {
                        logger.info("已扫描到二维码");
                        Map value=new HashMap<String,Object>();
                        for (String e:url.substring(45).split("&")) {
                          String[] v=e.split("=");
                          value.put(v[0], v[1]);
                        }
                        value.put("cookie", user.stoken);
                        value.put("raw", user.raw);
                        boolean succ=Request.login(value, autoConfirm);
                        if (autoClose) {
                          isRun = false;
                          helper.pause();
                          runOnUiThread(new Runnable(){
                              @Override
                              public void run() {
                                floatingButton.setText("开始扫描");
                              }
                            });
                        }
                        value = null;
                        if (succ) {
                          logger.info("扫码成功");
                        }
                      }
                    }
                  });
              }
            });
        }
      });
    
    floatingView.setOnTouchListener(new View.OnTouchListener() {
        private int initialX;
        private int initialY;
        private int initialTouchX;
        private int initialTouchY;
        //private Display display;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
          switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
              initialX = params.x;
              initialY = params.y;
              initialTouchX = (int)event.getRawX();
              initialTouchY = (int)event.getRawY();
              //display = windowManager.getDefaultDisplay();
              return true;
            case MotionEvent.ACTION_UP:
              return true;
            case MotionEvent.ACTION_MOVE:
              // 更新悬浮窗的位置
              int newX = initialX + (int) (event.getRawX() - initialTouchX);
              int newY = initialY + (int) (event.getRawY() - initialTouchY);
              /*int width = display.getWidth();
               int height = display.getHeight();
               int fwidth = floatingView.getWidth();
               int fheight = floatingView.getHeight();
               // 限制悬浮窗不超出屏幕边界
               if (Math.abs(newX) + fwidth / 2 > width / 2) newX = (width / 2 - fwidth / 2) ^ (newX >= 0 ? 1 : -1);
               if (Math.abs(newY) + fheight / 2 > height / 2) newY = (height / 2 - fheight / 2) * (newY >= 0 ? 1 : -1);*/
              params.x = newX;
              params.y = newY;
              windowManager.updateViewLayout(floatingView, params);
              return true;
          }
          return false;
        }
      });
// 将悬浮窗View添加到WindowManager中
    start.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          if (!Settings.canDrawOverlays(MainActivity.this)) {
            Toast.makeText(MainActivity.this, "未授予悬浮窗口权限", Toast.LENGTH_SHORT).show();
            return;
          }
          mHandler.post(new Runnable(){
              @Override
              public void run() {
                if (isAdd) return;
                isAdd = true;
                mMediaProjectionManager = (MediaProjectionManager) MainActivity.this.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE_SCREEN_CAPTURE);
              }
            });
        }
      });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == REQUEST_CODE_SCREEN_CAPTURE) {
      if (!(resultCode == RESULT_OK)) {
        isAdd = false;
        Toast.makeText(this, "未授予获取屏幕权限", Toast.LENGTH_SHORT).show();
        return;
      }
      MediaProjection mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
      // 使用 mediaProjection 进行屏幕截图操作
      helper = new ScreenCaptureHelper(mediaProjection);
      windowManager.addView(floatingView, params);
    }
    if (requestCode == REQUEST_CODE) {
      if (Settings.canDrawOverlays(this)) {
      } else {
        // 用户未授予悬浮窗口权限
        Toast.makeText(this, "未授予悬浮窗口权限", Toast.LENGTH_SHORT).show();
      }
    }
  }

  private long firstBackTime;
  @Override
  public void onBackPressed() {
    if (System.currentTimeMillis() - firstBackTime > 2000) {
      Toast.makeText(this, "再按一次返回键退出程序", Toast.LENGTH_SHORT).show();
      firstBackTime = System.currentTimeMillis();
      return;
    }
    super.onBackPressed();
    System.exit(0);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    windowManager.removeView(floatingView);
    uh.close();
    helper.close();
    this.finish();
    System.exit(0);
  }
}

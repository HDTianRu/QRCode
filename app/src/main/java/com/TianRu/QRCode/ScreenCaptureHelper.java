package com.TianRu.QRCode;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;

public class ScreenCaptureHelper {
  private static final String TAG = "ScreenCaptureHelper";

  private static final int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR;

  private static MediaProjection mMediaProjection;
  private static WindowManager windowManager;
  public static int mScreenWidth;
  public static int mScreenHeight;
  public static int mScreenDensity;
  private static Context context;
  public DisplayMetrics metrics;
  public Display display;
  private ImageReader mImageReader;
  private VirtualDisplay mVirtualDisplay;
  private Handler handler;
  private Bitmap bit;

  public ScreenCaptureHelper(MediaProjection media) {
    this.mMediaProjection = media;
    windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    metrics = new DisplayMetrics();
    display = windowManager.getDefaultDisplay();
    display.getRealMetrics(metrics);
    mScreenWidth = metrics.widthPixels;
    mScreenHeight = metrics.heightPixels;
    mScreenDensity = metrics.densityDpi;
  }

  public static void setContext(Context con) {
    context = con;
  }

  public void Capture(ImageReader.OnImageAvailableListener listener) {
    mImageReader = ImageReader.newInstance(mScreenWidth, mScreenHeight, 0x1, 3); // ImageFormat.RGB_565 = 0x1
    HandlerThread handlerThread = new HandlerThread("CaptureHandler");
    handlerThread.start();
    handler = new Handler(handlerThread.getLooper());
    if (mMediaProjection == null) {
      Toast.makeText(this.context, "未授予录屏权限", Toast.LENGTH_SHORT).show();
      return;
    }
    mVirtualDisplay = mMediaProjection.createVirtualDisplay("ScreenCapture",
                                                            mScreenWidth, mScreenHeight, mScreenDensity, VIRTUAL_DISPLAY_FLAGS,
                                                            mImageReader.getSurface(), null, handler);
    mImageReader.setOnImageAvailableListener(listener, handler);
  }

  public static Bitmap imageToBitmap(Image image) {
    Image.Plane[] planes = image.getPlanes();
    ByteBuffer buffer = planes[0].getBuffer();
    int pixelStride = planes[0].getPixelStride();
    int rowStride = planes[0].getRowStride();
    int rowPadding = rowStride - pixelStride * mScreenWidth;
    Bitmap bitmap = Bitmap.createBitmap(mScreenWidth + rowPadding / pixelStride, mScreenHeight, Bitmap.Config.ARGB_8888);
    bitmap.copyPixelsFromBuffer(buffer);
    planes = null;
    buffer = null;
    return Bitmap.createBitmap(bitmap, 0, 0, mScreenWidth, mScreenHeight);
  }

  public static void saveBitmap(Bitmap bitmap, String filename) {
    try {
      File file = new File("/sdcard/" + filename);
      FileOutputStream fos = new FileOutputStream(file);
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
      fos.flush();
      fos.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void pause(){
    if (handler != null && handler.getLooper() != null) {
      handler.getLooper().quitSafely();
      handler = null;
    }
    if (mImageReader != null) {
      mImageReader.close();
      mImageReader = null;
    }
    if (mVirtualDisplay != null) {
      mVirtualDisplay.release();
      mVirtualDisplay = null;
    }
  }

  public void close() {
    pause();
    if (mMediaProjection != null) {
      mMediaProjection.stop();
      mMediaProjection = null;
    }
  }

}

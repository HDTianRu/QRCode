package com.TianRu.QRCode;

import android.graphics.Bitmap;
import android.media.Image;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class ScanQRCode {
  public static String bitmap(Bitmap bitmap, boolean autoRecycle) {
    if (bitmap == null) return "";
    int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
    int width = bitmap.getWidth();
    int height = bitmap.getHeight();
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
    if (autoRecycle) bitmap.recycle();
    return decodeQRCode(width, height, pixels);
  }

  public static String image(Image image, boolean autoClose) {
    if (image == null) return "";
    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
    int[] pixels = new int[buffer.remaining() / 4];
    buffer.asIntBuffer().get(pixels);
    int width = image.getWidth();
    int height = image.getHeight();
    if (autoClose) image.close();
    return decodeQRCode(width, height, pixels);
  }
  
  protected static String decodeQRCode(int width, int height, int[] pixels) {
    RGBLuminanceSource rgb = new RGBLuminanceSource(width, height, pixels);
    SoftReference<RGBLuminanceSource> refrgb = new SoftReference<>(rgb);
    HybridBinarizer hb = new HybridBinarizer(rgb);
    SoftReference<HybridBinarizer> refhb = new SoftReference<>(hb);
    BinaryBitmap binaryBitmap = new BinaryBitmap(hb);
    SoftReference<BinaryBitmap> refb = new SoftReference<>(binaryBitmap);
    HashMap hints = new HashMap();
    hints.put(DecodeHintType.POSSIBLE_FORMATS, BarcodeFormat.QR_CODE);
    QRCodeReader reader = new QRCodeReader();
    try {
      Result result = reader.decode(binaryBitmap, hints);
      hints = null;
      rgb = null;
      hb = null;
      binaryBitmap = null;
      pixels = null;
      return result.getText();
    } catch (NotFoundException e) {
      //Do nothing
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }
}

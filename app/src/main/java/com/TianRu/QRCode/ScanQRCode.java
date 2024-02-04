package com.TianRu.QRCode;

import android.graphics.Bitmap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import java.lang.ref.SoftReference;
import java.util.HashMap;

public class ScanQRCode {
  public static String scan(Bitmap bitmap) {

    // Convert bitmap to binary array
    int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
    bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
    RGBLuminanceSource rgb = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), pixels);
    SoftReference<RGBLuminanceSource> refrgb = new SoftReference<>(rgb);
    HybridBinarizer hb = new HybridBinarizer(rgb);
    SoftReference<HybridBinarizer> refhb = new SoftReference<>(hb);
    BinaryBitmap binaryBitmap = new BinaryBitmap(hb);
    SoftReference<BinaryBitmap> refb = new SoftReference<>(binaryBitmap);
    // Set decode format to QR code
    HashMap hints = new HashMap();
    hints.put(DecodeHintType.POSSIBLE_FORMATS, BarcodeFormat.QR_CODE);

    // Create QR code reader
    QRCodeReader reader = new QRCodeReader();

    // Decode QR code
    try {
      Result result = reader.decode(binaryBitmap, hints);
      hints = null;
      rgb = null;
      hb = null;
      binaryBitmap = null;
      return result.getText();
    } catch (NotFoundException e) {
      //e.printStackTrace();
    } catch (ChecksumException e) {
      e.printStackTrace();
    } catch (FormatException e) {
      e.printStackTrace();
    }

    return "扫描失败";
  }
}

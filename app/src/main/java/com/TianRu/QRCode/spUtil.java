package com.TianRu.QRCode;

import android.content.Context;
import android.content.SharedPreferences;

public class spUtil {
  public static final String FILE_NAME = "config";
  private static Context context;
  public static SharedPreferences sp;
  public static void setContext(Context con) {
    context = con;
    sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
  }
  public static void remove(String key) {
    sp.edit().remove(key).commit();
  }
  public static void putString(String key, String value) {
    sp.edit().putString(key, value).commit();
  }
  public static String getString(String key) {
    return sp.getString(key, "");
  }
  public static String getString(String key, String normal) {
    return sp.getString(key, normal);
  }
  public static void putBoolean(String key, boolean value) {
    sp.edit().putBoolean(key, value).commit();
  }
  public static boolean getBoolean(String key) {
    return sp.getBoolean(key, false);
  }
  public static boolean getBoolean(String key, boolean normal) {
    return sp.getBoolean(key, normal);
  }
  public static void putInt(String key, int value) {
    sp.edit().putInt(key, value).commit();
  }
  public static int getInt(String key) {
    return sp.getInt(key, 0);
  }
  public static int getInt(String key, int normal) {
    return sp.getInt(key, normal);
  }
}

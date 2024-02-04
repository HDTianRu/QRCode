package com.TianRu.QRCode;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    
  private static final String SQL_CREATE_ENTRIES =
  "CREATE TABLE " + User.table + " (" +
  "uid" + " TEXT PRIMARY KEY," +
  "stoken" + " TEXT," +
  "raw" + " TEXT," +
  "cookie" + " TEXT," +
  "current" + " INTEGER DEFAULT 0);";

  //如果更改数据库架构，则必须增加数据库版本。
  public static final int DATABASE_VERSION = 1;
  public static final String DATABASE_NAME = "account.db";

  public DBHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  //重写创建表的逻辑
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(SQL_CREATE_ENTRIES);
  }

  //重写数据库升级逻辑
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    
  }

    
}

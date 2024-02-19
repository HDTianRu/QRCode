package com.TianRu.QRCode;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;

public class UserHelper {

  private static DBHelper helper;
  private static SQLiteDatabase db;
  private static User current;
  private static UserHelper userHelper;

  private UserHelper(Context context) {
    helper = new DBHelper(context);
    db = helper.getWritableDatabase();
    getCurrent();
  }
  
  public synchronized static void create(Context context) {
    if (userHelper == null) userHelper = new UserHelper(context);
    else throw new RuntimeException("不能重复创建");
  }
  
  public static UserHelper getHelper() {
    if (userHelper != null) return userHelper;
    else throw new RuntimeException("还未创建UserHelper");
  }

  public User getCurrent() {
    Cursor cur = null;
    try {
      cur = myQuery(
        "current = ?",
        new String[] {
          "1"
        }
      );
      if (cur.moveToFirst()) {
        current = new User(
          cur.getString(cur.getColumnIndex("uid")),
          cur.getString(cur.getColumnIndex("stoken")),
          cur.getString(cur.getColumnIndex("cookie")),
          cur.getString(cur.getColumnIndex("raw")),
          cur.getInt(cur.getColumnIndex("current"))
        );
        return current;
      }
    } finally {
      if (cur != null) cur.close();
    }
    return null;
  }

  public boolean addUser(User user) {
    ContentValues value = new ContentValues();
    value.put("uid", user.uid);
    value.put("stoken", user.stoken);
    value.put("cookie", user.cookie);
    value.put("raw", user.raw);
    value.put("current", 0);
    long index;
    if (isExist(user.uid)) {
      index = db.update(
        User.table,
        value,
        "uid = ?",
        new String[] {user.uid}
      );
    } else {
      index = db.insert(
        User.table,
        null,
        value
      );
    }
    boolean success = index >= 1;
    if (success) {
      switchUser(user.uid);
      return true;
    }
    return false;
  }

  public boolean deleteUser(String uid) {
    int index = db.delete(
      User.table,
      "uid = ?",
      new String[] {
        uid
      }
    );
    return index >= 1;
  }

  public Cursor myQuery(String selection, String[] args) {
    return db.query(
      User.table,
      User.columns,
      selection,
      args,
      null,
      null,
      null
    );
  }

  public boolean isExist(String uid) {
    Cursor cur = myQuery(
      "uid = ?",
      new String[] {
        uid
      }
    );
    boolean success = cur.moveToFirst();
    cur.close();
    if (success) return true;
    else return false;
  }
  
  public boolean isCurrent(String uid) {
    Cursor cur = myQuery(
      "uid = ? AND current = ?",
      new String[] {
        uid,
        "1"
      }
    );
    boolean success = cur.moveToFirst();
    cur.close();
    if (success) return true;
    else return false;
  }

  public User queryUser(String uid) {
    Cursor cur = myQuery("uid = ?", new String[] {uid});
    User user = null;
    if (cur != null && cur.moveToFirst()) {
      String stoken = cur.getString(cur.getColumnIndex("stoken"));
      String cookie = cur.getString(cur.getColumnIndex("cookie"));
      String raw = cur.getString(cur.getColumnIndex("raw"));
      int current = cur.getInt(cur.getColumnIndex("current"));
      user = new User(uid, stoken, cookie, raw, current);
      cur.close();
    }
    return user;
  }

  public List<User> queryUsers() {
    List<User> userList = new ArrayList<User>();
    Cursor cur = myQuery(null, null);
    if (cur != null && cur.moveToFirst()) {
      do {
        String uid = cur.getString(cur.getColumnIndex("uid"));
        String stoken = cur.getString(cur.getColumnIndex("stoken"));
        String cookie = cur.getString(cur.getColumnIndex("cookie"));
        String raw = cur.getString(cur.getColumnIndex("raw"));
        int current = cur.getInt(cur.getColumnIndex("current"));
        User user = new User(uid, stoken, cookie, raw, current);
        userList.add(user);
      } while (cur.moveToNext());
      cur.close();
    }
    return userList;
  }

  public boolean switchUser(String uid) {
    int index;
    if (current != null) {
      ContentValues value = new ContentValues();
      value.put("current", 0);
      db.update(
        User.table,
        value,
        "current = ? AND uid = ?",
        new String[] {"1",current.uid}
      );
    }
    ContentValues value = new ContentValues();
    value.put("current", 1);
    index = db.update(
      User.table,
      value,
      "current = ? AND uid = ?",
      new String[] {"0",uid}
    );
    getCurrent();
    return index >= 1;
  }

  public User get() {
    if (current != null) return current;
    return getCurrent();
  }

  public DBHelper getDBHelper() {
    return helper;
  }

  public SQLiteDatabase getDatabase() {
    return db;
  }
  
  public void close() {
    if (db != null) {
      db.close();
      db = null;
    }
    if (helper != null) {
      helper = null;
    }
  }

}

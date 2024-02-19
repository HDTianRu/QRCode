package com.TianRu.QRCode;

public class User {

  public String uid;
  public String stoken;
  public String cookie;
  public String raw;
  public int current = 0;
  
  
  public static String table = "users";
  public static String[] columns = new String[] {
    "uid",
    "stoken",
    "cookie",
    "raw",
    "current"
  };
  
  public User (String uid,String stoken,String cookie,String raw) {
    this.uid = uid;
    this.stoken = stoken;
    this.cookie = cookie;
    this.raw = raw;
  }
  
  public User (String uid,String stoken,String cookie,String raw,int current) {
    this.uid = uid;
    this.stoken = stoken;
    this.cookie = cookie;
    this.raw = raw;
    this.current = current;
  }
  
  public void setCurrent() {
    setCurrent(1);
  }
  
  public void setCurrent(int current) {
    this.current = current;
  }
}

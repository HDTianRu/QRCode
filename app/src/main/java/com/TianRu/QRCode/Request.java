package com.TianRu.QRCode;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.crypto.Cipher;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Request {
  public static String saltWeb = "jEpJb9rRARU2rXDA9qYbZ3selxkuct9a";//lk2
  public static String passSalt = "JwYDpKvLj6MrMqqYU6jTKF17KNO2PXoS";

  public static boolean login(Map<String,Object> value, boolean confirm) {
    String device = randomString(64);
    Map map = new HashMap();
    map.put("app_id", value.get("app_id"));
    map.put("ticket", value.get("ticket"));
    map.put("device", device);
    try {
      String str = post("https://api-sdk.mihoyo.com/" + value.get("biz_key") + "/combo/panda/qrcode/scan", value.get("cookie").toString(), new JSONObject(map).toString());
      JSONObject res=new JSONObject(str);
      if (res.getInt("retcode") != 0) {
        MainActivity.logger.error(res.getString("message"));
        return false;
      }
      Map payload = new HashMap();
      payload.put("proto", "Account");
      payload.put("raw", value.get("raw"));
      map.put("payload", payload);
      if (!confirm) {
        MainActivity.logger.info(
          "Confirm Data:\n" + 
          "https://api-sdk.mihoyo.com/" + value.get("biz_key") + "/combo/panda/qrcode/confirm" + "#" + 
          value.get("cookie").toString() + "#" + 
          new JSONObject(map).toString());
        return true;
      }
      String str2 = post("https://api-sdk.mihoyo.com/" + value.get("biz_key") + "/combo/panda/qrcode/confirm", value.get("cookie").toString(), new JSONObject(map).toString());
      res = new JSONObject(str2);
      if (res.getInt("retcode") != 0) {
        MainActivity.logger.error(res.getString("message"));
        return false;
      }
      MainActivity.logger.info("确认成功");
      return true;
    } catch (JSONException e) {}
    return false;
  }

  public static boolean confirm(String data) {
    String[] datum = data.split("#");
    if (datum.length != 3) {
      MainActivity.logger.error("格式不正确");
      return false;
    }
    try {
      String str2 = post(datum[0], datum[1], datum[2]);
      JSONObject res = new JSONObject(str2);
      if (res.getInt("retcode") != 0) {
        MainActivity.logger.error(res.getString("message"));
        return false;
      }
      return true;
    } catch (Exception e) {}
    return false;
  }

  public static String getUidbyStoken(String stoken) {
    try {
      String url = "https://api-takumi.miyoushe.com/binding/api/getUserGameRolesByStoken";
      JSONObject data = new JSONObject(get(url, stoken));
      if (data.getInt("retcode") != 0) return "";
      JSONArray list = data.getJSONObject("data").getJSONArray("list");
      for (int i =0;i < list.length();i++) {
        JSONObject info = list.getJSONObject(i);
        if (info.getString("game_biz").equals("hk4e_cn")) {
          return info.getString("game_uid");
        }
      }
    } catch (JSONException e) {}
    return "";
  }

  public static String getUidbyCookie(String cookie) {
    try {
      String url = "https://api-takumi.miyoushe.com/binding/api/getUserGameRolesByStoken";
      JSONObject data = new JSONObject(get(url, cookie));
      if (data.getInt("retcode") != 0) return "";
      JSONArray list = data.getJSONObject("data").getJSONArray("list");
      for (int i =0;i < list.length();i++) {
        JSONObject info = list.getJSONObject(i);
        if (info.getString("game_biz").equals("hk4e_cn")) {
          return info.getString("game_uid");
        }
      }
    } catch (JSONException e) {}
    return "";
  }

  public static String getAuthkey(String cookie, String uid) {
    try {
      String url = "https://api-takumi.mihoyo.com/binding/api/genAuthKey";
      JSONObject body = new JSONObject();
      body.put("auth_appid", "webview_gacha");
      body.put("game_biz", "hk4e_cn");
      body.put("game_uid", Integer.parseInt(uid));
      body.put("region", getServer(uid));
      String data=body.toString();
      JSONObject res = new JSONObject(post(url, cookie, data, new Pair("x-rpc-app_version", "2.41.0"),
                                           new Pair("User-Agent", "okhttp/4.8.0"),
                                           new Pair("x-rpc-client_type", "5"),
                                           new Pair("Referer", "https://app.mihoyo.com"),
                                           new Pair("Origin", "https://webstatic.mihoyo.com"),
                                           new Pair("DS", ds2(saltWeb)),
                                           new Pair("x-rpc-sys_version", "12"),
                                           new Pair("x-rpc-channel", "mihoyo"),
                                           new Pair("x-rpc-device_id", randomString(32).toUpperCase()),
                                           new Pair("x-rpc-device_name", randomString(16)),
                                           new Pair("x-rpc-device_model", "Mi 10"),
                                           new Pair("Host", "api-takumi.mihoyo.com")));
      if (res.getInt("retcode") != 0) return "";
      JSONObject auth = res.getJSONObject("data");
    } catch (JSONException e) {} catch (NumberFormatException e) {}

    return "";
  }

  public static String getGameTokenByStoken(String uid, String stoken) {
    try {
      String url = "https://api-takumi.miyoushe.com/auth/api/getGameToken?uid=" + uid;
      JSONObject res = new JSONObject(get(url, stoken));
      if (res.getInt("retcode") == 0) {
        JSONObject token = res.getJSONObject("data");
        return token.getString("game_token");
      }
    } catch (JSONException e) {}
    return "";
  }

  public static String getServer(String uid) {
    switch (uid.charAt(0)) {
      case '1':
        return "cn_gf01"; // 官服
      case '2':
        return "cn_gf01"; // 官服
      case '5':
        return "cn_qd01"; // B服
      case '6':
        return "os_usa"; // 美服
      case '7':
        return "os_euro"; // 欧服
      case '8':
        return "os_asia"; // 亚服
      case '9':
        return "os_cht"; // 港澳台服
    }
    return "cn_gf01";
  }
  public static String request(String url, String cookie, String data, String aigis) {
    return post(url, cookie, data, new Pair("x-rpc-app_version", "2.41.0"),
                new Pair("x-rpc-game_biz", "bbs_cn"),
                new Pair("DS", ds(data, passSalt)),
                new Pair("x-rpc-aigis", aigis),
                new Pair("x-rpc-sys_version", "12"),
                new Pair("x-rpc-device_id", randomString(16)),
                new Pair("x-rpc-device_fp", randomString(13)),
                new Pair("x-rpc-device_name", randomString(16)),
                new Pair("x-rpc-device_model", randomString(16)),
                new Pair("x-rpc-app_id", "bll8iq97cem8"),
                new Pair("x-rpc-client_type", "2"),
                new Pair("User-Agent", "okhttp/4.8.0)"));
  }

  private static final String ALPHANUMERIC_CHARS = "0123456789abcdefghijklmnopqrstuvwxyz";

  public static String randomString(int n) {
    StringBuilder sb = new StringBuilder(n);
    Random random = new Random();
    for (int i = 0; i < n; i++) {
      sb.append(ALPHANUMERIC_CHARS.charAt(random.nextInt(ALPHANUMERIC_CHARS.length())));
    }
    return sb.toString();
  }

  public static String md5(String data) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(data.getBytes());
      byte[] digest = md.digest();
      StringBuilder sb = new StringBuilder();
      for (byte b : digest) {
        sb.append(String.format("%02x", b & 0xff));
      }
      return sb.toString();
    } catch (NoSuchAlgorithmException e) {}
    return "";
  }

  public static String base64(byte[] str) {
    return Base64.getEncoder().encodeToString(str);
  }

  private static final String PUBLIC_KEY =
  "-----BEGIN PUBLIC KEY-----\n"
  + "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDvekdPMHN3AYhm/vktJT+YJr7\n"
  + "cI5DcsNKqdsx5DZX0gDuWFuIjzdwButrIYPNmRJ1G8ybDIF7oDW2eEpm5sMbL9zs\n"
  + "9ExXCdvqrn51qELbqj0XxtMTIpaCHFSI50PfPpTFV9Xt/hmyVwokoOXFlAEgCn+Q\n"
  + "CgGs52bFoYMtyi+xEQIDAQAB\n"
  + "-----END PUBLIC KEY-----\n";

  public static String encrypt(String data) {
    try {
      PublicKey publicKey = getPublicKeyFromString(PUBLIC_KEY);
      Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
      cipher.init(Cipher.ENCRYPT_MODE, publicKey);
      byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
      return base64(encryptedBytes);
    } catch (Exception e) {
      System.exit(1);
    }
    return "";
  }

  private static PublicKey getPublicKeyFromString(String publicKeyString) throws Exception {
    publicKeyString = publicKeyString
      .replace("-----BEGIN PUBLIC KEY-----\n", "")
      .replace("-----END PUBLIC KEY-----\n", "")
      .replaceAll("\\s", "");
    byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    return keyFactory.generatePublic(keySpec);
  }

  public static String ds(String data, String salt) {
    long t = System.currentTimeMillis() / 1000;
    String r = randomString(6);
    String h = md5(String.format("salt=%s&t=%d&r=%s&b=%s&q=", salt, t, r, data));
    return String.format("%d,%s,%s", t, r, h);
  }

  public static String ds2(String salt) {
    long t = System.currentTimeMillis() / 1000;
    String r = randomString(6);
    String h = md5(String.format("salt=%s&t=%d&r=%s", salt, t, r));
    return String.format("%d,%s,%s", t, r, h);
  }

  public static String get(String url, String cookie, Pair... RequestPropertys) {
    OkHttpClient client = new OkHttpClient.Builder()
      .connectTimeout(10, TimeUnit.SECONDS)
      .readTimeout(10, TimeUnit.SECONDS)
      .build();
    try {
      okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder()
        .url(url)
        .addHeader("Cookie", cookie);
      for (Pair p : RequestPropertys) {
        requestBuilder.addHeader(p.k, p.v);
      }
      okhttp3.Request request = requestBuilder.build();
      Response response = client.newCall(request).execute();
      if (response.isSuccessful()) {
        ResponseBody responseBody = response.body();
        if (responseBody != null) {
          return responseBody.string();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }


  public static String post(String url, String cookie, String data, Pair... RequestPropertys) {
    OkHttpClient client = new OkHttpClient.Builder()
      .connectTimeout(20, TimeUnit.SECONDS)
      .readTimeout(20, TimeUnit.SECONDS)
      .build();
    try {
      MediaType mediaType = MediaType.parse("application/json");
      RequestBody body = RequestBody.create(mediaType, data);
      okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder()
        .url(url)
        .addHeader("Content-Type", "application/json")
        .addHeader("Cookie", cookie);
      for (Pair p : RequestPropertys) {
        requestBuilder.addHeader(p.k, p.v);
      }
      okhttp3.Request request = requestBuilder.post(body).build();
      Response response = client.newCall(request).execute();
      if (response.isSuccessful()) {
        ResponseBody responseBody = response.body();
        if (responseBody != null) {
          return responseBody.string();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }

}

package com.TianRu.QRCode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.documentfile.provider.DocumentFile;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;
import androidx.appcompat.app.AppCompatActivity;


public class Login extends AppCompatActivity {
  private Handler handler;
  //private Thread tloginByPass;
  private Thread tloginByQRCode;
  public static String device = Request.randomString(64);
  public int width;
  public UserHelper uh;

  final static int READ_REQUEST_CODE = 114;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.login);
    handler = new Handler();
    uh = UserHelper.getHelper();
    WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
    DisplayMetrics metrics = new DisplayMetrics();
    windowManager.getDefaultDisplay().getRealMetrics(metrics);
    width = metrics.widthPixels;
    //final WebView web=findViewById(R.id.web);
    final ImageView iv=findViewById(R.id.qrcode);
    //final EditText id=findViewById(R.id.userID);
    //final EditText pass=findViewById(R.id.userPass);
    //Button loginByPass=findViewById(R.id.loginByPass);
    Button loginByQRCode=findViewById(R.id.loginByQRCode);
    Button loginByToken=findViewById(R.id.loginByToken);
    Button loginByYAML=findViewById(R.id.loginByYAML);
    Button manageUser=findViewById(R.id.manageUser);
    /*loginByPass.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          tloginByPass = new Thread(new Runnable(){
              @Override
              public void run() {
                try {
                  String data = "{\"account\":" + Request.encrypt(id.getText().toString()) + "\",\"password\":\"" + Request.encrypt(pass.getText().toString()) + "\"}";
                  String res=Request.request("https://passport-api.mihoyo.com/account/ma-cn-passport/app/loginByPassword", "", data, "");
                  JSONObject json =new JSONObject(res);
                  String aigis = "";
                  if (json.getInt("retcode") == -3101) {
                    JSONObject aigisJson =json.getJSONObject("data");
                    String challenge=aigisJson.getString("challenge");
                    String url = "https://challenge.minigg.cn/manual/index.html?gt=" + aigisJson.getString("gt") + "&challenge=" + challenge;
                    web.loadUrl(url);
                    while (true) {
                      SystemClock.sleep(5000);
                      JSONObject aigisData =new JSONObject(Request.get("`https://challenge.minigg.cn/manual/?callback=" + challenge, ""));
                      if (aigisData.getInt("retcode") == 200) {
                        Map map=new HashMap();
                        map.put("geetest_challenge", challenge);
                        map.put("geetest_seccode", aigisData.getString("geetest_validate") + "|jordan");
                        map.put("geetest_validate", aigisData.getString("geetest_validate"));
                        aigis = aigisData.getString("session_id") + ";" + Request.base64(new JSONObject(map).toString().getBytes());
                        break;
                      }
                    }
                  }
                  res = Request.request("https://passport-api.mihoyo.com/account/ma-cn-passport/app/loginByPassword", "", data, aigis);
                } catch (JSONException e) {}
              }
            });
          tloginByPass.start();
        }
      });*/
    loginByQRCode.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Thread tloginByQRCode = new Thread(new Runnable(){
              @Override
              public void run() {
                try {
                  Map map=new HashMap();
                  map.put("app_id", 4);
                  map.put("device", device);
                  JSONObject res = new JSONObject(Request.post("https://hk4e-sdk.mihoyo.com/hk4e_cn/combo/panda/qrcode/fetch", "", new JSONObject(map).toString()));
                  String url =res.getJSONObject("data").getString("url");
                  String ticket=url.substring(url.indexOf("ticket=") + 7);
                  map.put("ticket", ticket);
                  MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                  try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(url, BarcodeFormat.QR_CODE, (int)(width * 0.8), (int)(width * 0.8));
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    final Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                          iv.setImageBitmap(bitmap);
                        }
                      });
                  } catch (WriterException e) {
                    e.printStackTrace();
                  }
                  String raw = null;
                  while (true) {
                    SystemClock.sleep(5000);
                    res = new JSONObject(Request.post("https://hk4e-sdk.mihoyo.com/hk4e_cn/combo/panda/qrcode/query", "", new JSONObject(map).toString()));
                    if (res.getInt("retcode") != 0) {
                      runOnUiThread(new Runnable(){
                          @Override
                          public void run() {
                            iv.setImageDrawable(null);
                            Toast.makeText(Login.this, "二维码已过期", Toast.LENGTH_SHORT).show();
                          }
                        });
                      return;
                    }
                    JSONObject data=res.getJSONObject("data");
                    if (data.getString("stat").equals("Confirmed")) {
                      raw = data.getJSONObject("payload").getString("raw");
                      break;
                    }
                  }
                  runOnUiThread(new Runnable(){
                      @Override
                      public void run() {
                        iv.setImageDrawable(null);
                      }
                    });
                  getAndSave(raw, Login.this);
                } catch (JSONException e) {}
              }
            });
          tloginByQRCode.start();
        }
      });
    loginByToken.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
          builder.setTitle("输入stuid与token");
          View input = LayoutInflater.from(Login.this).inflate(R.layout.input_token, null);
          builder.setView(input);
          final AlertDialog dialog = builder.create();
          final EditText stuidInput = input.findViewById(R.id.stuid);
          final EditText tokenInput = input.findViewById(R.id.token);
          input.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                String stuid = stuidInput.getText().toString();
                String token = tokenInput.getText().toString();
                String raw = String.format("{\"uid\":\"%s\",\"token\":\"%s\"}", stuid, token);
                getAndSave(raw, Login.this);
                dialog.dismiss();
              }
            });
          dialog.show();
        }
      });
    loginByYAML.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
          intent.addCategory(Intent.CATEGORY_OPENABLE);
          intent.setType("*/*");
          startActivityForResult(intent, READ_REQUEST_CODE);
        }
      });
    manageUser.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          startActivity(new Intent(Login.this, Account.class));
        }
      });
  }


  public void getAndSave(final String raw, final Activity con) {
    new Thread(new Runnable(){
        @Override
        public void run() {
          boolean success = false;
          try {
            JSONObject info = new JSONObject(raw);
            if (info.optString("token").equals("")) {
              return;
            }
            JSONObject res = new JSONObject(Request.request("https://passport-api.mihoyo.com/account/ma-cn-session/app/getTokenByGameToken", "", "{\"account_id\":" + Integer.parseInt(info.getString("uid")) + ",\"game_token\":\"" + info.getString("token") + "\"}", "")).getJSONObject("data");
            String stoken =res.getJSONObject("token").getString("token");
            String aid=res.getJSONObject("user_info").getString("aid");
            String Stoken = String.format("stoken=%s;stuid=%s;mid=%s", stoken, aid, res.getJSONObject("user_info").getString("mid"));
            JSONObject cookie = new JSONObject(Request.get("https://api-takumi.mihoyo.com/auth/api/getCookieAccountInfoByGameToken?account_id=" + info.getString("uid") + "&game_token=" + info.getString("token"), ""));
            String Cookie = String.format("ltoken=%s;ltuid=%s;cookie_token=%s", stoken, aid, cookie.getJSONObject("data").getString("cookie_token"));
            success = uh.addUser(new User(aid, Stoken, Cookie, raw));
          } catch (JSONException e) {} catch (NumberFormatException e) {}
          if (success) con.runOnUiThread(new Runnable(){
                @Override
                public void run() {
                  Toast.makeText(con, "登录成功", Toast.LENGTH_SHORT).show();
                }
              });
        }
      }).start();
  }

  public void loginByYAML(final Uri uri) {
    new Thread(new Runnable(){
        @Override
        public void run() {
          DocumentFile documentFile = DocumentFile.fromSingleUri(Login.this, uri);
          InputStream inputStream = null;
          try {
            inputStream = Login.this.getContentResolver().openInputStream(documentFile.getUri());
            Yaml yaml = new Yaml();
            final Map<String, Object> yamlData = yaml.load(inputStream);
            if (null == yamlData) return;
            List<String> items = new ArrayList<String>();
            for (Map.Entry<String, Object> entry : yamlData.entrySet()) {
              String key = entry.getKey();
              items.add(key);
            }
            final String[] item = items.toArray(new String[items.size()]);
            final AlertDialog.Builder dialog = new AlertDialog.Builder(Login.this)
              .setTitle("请选择uid")
              .setItems(item, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dia, final int which) {
                  new Thread(new Runnable(){
                      @Override
                      public void run() {
                        try {
                          Map<String, Object> map = (Map<String, Object>) yamlData.get(item[which]);
                          //if (null == map) return;
                          String stoken = (String) map.get("stoken");
                          final String stuid = (String) map.get("stuid");
                          String mid = (String) map.get("mid");
                          final String Stoken = String.format("stoken=%s;stuid=%s;mid=%s", stoken, stuid, mid);
                          final String uid = (String) map.get("uid");
                          String gameToken = Request.getGameTokenByStoken(uid, Stoken);
                          String url = "https://api-takumi.mihoyo.com/auth/api/getCookieAccountInfoByGameToken?account_id=" + stuid + "&game_token=" + gameToken;
                          JSONObject cookie = new JSONObject(Request.get(url, ""));
                          String cookieToken = cookie.getJSONObject("data").getString("cookie_token");
                          if (null == cookieToken || cookieToken.equals("")) return;
                          String Cookie = String.format("ltoken=%s;ltuid=%s;cookie_token=%s", stoken, stuid, cookieToken);
                          String raw = String.format("{\"uid\":\"%s\",\"token\":\"%s\"}", stuid, gameToken);
                          boolean a=uh.addUser(new User(stuid, Stoken, Cookie, raw));
                          if (a) runOnUiThread(new Runnable(){
                                @Override
                                public void run() {
                                  Toast.makeText(Login.this, "登录成功", Toast.LENGTH_SHORT).show();
                                }
                              });
                        } catch (JSONException e) {}
                      }
                    }).start();
                }
              });
            runOnUiThread(new Runnable(){
                @Override
                public void run() {
                  dialog.show();
                }
              });
          } catch (IOException e) {
            e.printStackTrace();
          } finally {
            try {
              if (inputStream != null) inputStream.close();
            } catch (IOException e) {}
          }
        }
      }).start();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK) {
      if (data == null) return;
      Uri uri = data.getData();
      loginByYAML(uri);
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    //if (tloginByPass != null) tloginByPass.interrupt();
    if (tloginByQRCode != null) tloginByQRCode.interrupt();
    try {
    //  if (tloginByPass != null) tloginByPass.join();
      if (tloginByQRCode != null) tloginByQRCode.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    //tloginByPass = null;
    tloginByQRCode = null;
  }
}

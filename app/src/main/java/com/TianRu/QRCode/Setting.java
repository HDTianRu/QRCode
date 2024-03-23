package com.TianRu.QRCode;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import androidx.appcompat.app.AppCompatActivity;

public class Setting extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_setting);
    LinearLayout list = findViewById(R.id.list);
    try {
      JSONArray settingList = new JSONArray(getAssetFile(this, "settingList.json"));
      for (int i=0;i < settingList.length();i++) {
        final JSONObject item = settingList.getJSONObject(i);
        switch (item.getString("type")) {
          case "switch":{
              list.addView(getSwitchL(item.getString("title"), item.getString("name"), item.getBoolean("normal")));
              break;
            }
          case "input":{
              list.addView(getInputL(item.getString("title"), item.getString("name"), item.getString("normal")));
              break;
            }
          default:{
              break;
            }
        }
      }
    } catch (JSONException e) {}

  }

  public RelativeLayout getSwitchL(String title, final String name, boolean normal) {
    boolean checked = spUtil.getBoolean(name, normal);
    RelativeLayout switchL = (RelativeLayout) getLayoutInflater().from(this).inflate(R.layout.switch_layout, null);
    ((TextView) switchL.findViewById(R.id.text)).setText(title);
    Switch s = ((Switch) switchL.findViewById(R.id.switch1));
    s.setChecked(checked);
    OnCheckedChangeListener listener = new OnCheckedChangeListener(){
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        spUtil.putBoolean(name, isChecked);
      }
    };
    s.setOnCheckedChangeListener(listener);
    return switchL;
  }

  public LinearLayout getInputL(String title, final String name, String normal) {
    String text = spUtil.getString(name, normal);
    LinearLayout inputL = (LinearLayout) getLayoutInflater().from(this).inflate(R.layout.input_layout, null);
    ((TextView) inputL.findViewById(R.id.text)).setText(title);
    final EditText edit = (EditText) inputL.findViewById(R.id.input);
    edit.setText(text);
    OnClickListener listener = new OnClickListener() {
      @Override
      public void onClick(View view) {
        String input = edit.getText().toString();
        spUtil.putString(name, input);
      }
    };
    ((Button) inputL.findViewById(R.id.button)).setOnClickListener(listener);
    return inputL;
  }

  public View getDivider() {
    return getLayoutInflater().from(this).inflate(R.layout.divide, null);
  }

  public String getAssetFile(Context con, String fileName) {
    AssetManager mAssetManger = con.getAssets();
    StringBuffer buffer = new StringBuffer();
    InputStream is = null;
    InputStreamReader isr = null;
    try {
      is = mAssetManger.open(fileName);
      isr = new InputStreamReader(is, "utf-8");
      BufferedReader reader = new BufferedReader(isr); // 缓冲
      String line;
      while ((line = reader.readLine()) != null) {
        buffer.append(line + "\n");
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (null != is) {
          isr.close();
        }
        if (null != isr) {
          isr.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
        return "";
      }
    }
    if (buffer.length() == 0) return "";
    buffer.delete(buffer.length() - 1, buffer.length());
    return buffer.toString();
  }
}

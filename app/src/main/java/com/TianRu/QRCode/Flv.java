package com.TianRu.QRCode;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;

public class Flv extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_flv);
    EditText input = findViewById(R.id.flv_input);
    Button start = findViewById(R.id.start);
    Button stop = findViewById(R.id.stop);
  }

}

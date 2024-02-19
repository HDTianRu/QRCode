package com.TianRu.QRCode;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.app.Activity;

public class Logger {
  public static final int LEVEL_DEBUG = 1;
  public static final int LEVEL_INFO = 2;
  public static final int LEVEL_WARN = 3;
  public static final int LEVEL_ERROR = 4;
  public static final int CHAR_LIMIT = 16;

  private final TextView mTextView;
  private final TextView mfloating_TextView;
  private final int mLogLevel;
  private final Activity mContext;

  public Logger(Activity context, TextView textView, TextView floating_tv,int logLevel) {
    mTextView = textView;
    mfloating_TextView = floating_tv;
    mLogLevel = logLevel;
    mContext = context;
  }

  public void debug(String message) {
    if (mLogLevel > LEVEL_DEBUG) {
      return;
    }
    log("DEBUG", message, Color.BLACK);
  }

  public void info(String message) {
    if (mLogLevel > LEVEL_INFO) {
      return;
    }
    log("INFO", message, Color.BLUE);
  }

  public void warn(String message) {
    if (mLogLevel > LEVEL_WARN) {
      return;
    }
    log("WARN", message, Color.rgb(255, 165, 0));
  }

  public void error(String message) {
    if (mLogLevel > LEVEL_ERROR) {
      return;
    }
    log("ERROR", message, Color.RED);
  }

  private void log(String level, String message, int color) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    String time = dateFormat.format(new Date());
    String logText = "[" + time + "][" + level + "]" + message + "\n";
    message = message.length() <= CHAR_LIMIT ? message : message.substring(0, CHAR_LIMIT) + "...";
    final SpannableStringBuilder builder = new SpannableStringBuilder(logText);
    final SpannableStringBuilder builder2 = new SpannableStringBuilder(message+"\n");
    builder.setSpan(new ForegroundColorSpan(color), 0, logText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    builder2.setSpan(new ForegroundColorSpan(color), 0, message.length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    mContext.runOnUiThread(new Runnable(){
        @Override
        public void run() {
          mTextView.append(builder);
          mfloating_TextView.setText(builder2);
        }
      });
  }
}

package com.TianRu.QRCode;

import android.service.quicksettings.TileService;
import android.content.Intent;
import android.content.Context;

public class QuickStart extends TileService {

  @Override
  public void onClick() {
    super.onClick();
    // 在点击事件中启动Activity
    Intent intent = new Intent(this, MainActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivityAndCollapse(intent);
  }
  
  @Override
  public void onTileAdded() {
    super.onTileAdded();
  }

  @Override
  public void onStartListening() {
    super.onStartListening();
  }

  @Override
  public void onStopListening() {
    super.onStopListening();
  }
  
}


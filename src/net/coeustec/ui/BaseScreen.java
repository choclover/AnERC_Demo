package net.coeustec.ui;

import net.coeustec.app.ResourceManager;
import net.coeustec.engine.ClientEngine;
import net.coeustec.engine.request.Response;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;

public class BaseScreen extends Activity {
  private ProgressDialog processDialog;
  
  public void addToManager(BaseScreen screen) {
    ScreenManager.addActivity(screen);
  }
  
  public void showConfirmDialog(String title, String msgStr) {
    AlertDialog.Builder builder = new AlertDialog.Builder(BaseScreen.this);
    builder.setTitle(title);
    builder.setMessage(msgStr);
    builder.setCancelable(false);
    builder.setPositiveButton(ResourceManager.RES_STR_OK, null);
    
    AlertDialog alert = builder.create();
    alert.show();
  }
  
  public void showQuitAppDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(BaseScreen.this);
    builder.setTitle(ResourceManager.RES_STR_QUITAPP).setMessage(
        ResourceManager.RES_STR_QUITAPP+"?");
    builder.setPositiveButton(ResourceManager.RES_STR_OK,
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
            finish();  
            ClientEngine.getInstance().exitApp();
          }
        }).setNegativeButton(ResourceManager.RES_STR_CANCEL, null);

    builder.create().show();
  }
  
  /**
   * 显示等待框
   */
  public void showProgress(final String message) {
    this.runOnUiThread(new Runnable() {

      @Override
      public void run() {
        processDialog = new ProgressDialog(BaseScreen.this);
        processDialog.setMessage(message);
        processDialog.setIndeterminate(true);
        processDialog.setCancelable(false);
        processDialog.show();
      }
    });

  }

  /**
   * 关闭等待框
   */
  public void closeProgress() {
    this.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (processDialog != null && processDialog.isShowing()) {
          processDialog.dismiss();
        }
      }
    });
  }
  
  public void handleResponseMessage(Response response) {
    return;
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    switch (keyCode) {
    case KeyEvent.KEYCODE_HOME: 
      ClientEngine.getInstance().exitApp();
      return true;
    }

    return super.onKeyDown(keyCode, event);
  }
}

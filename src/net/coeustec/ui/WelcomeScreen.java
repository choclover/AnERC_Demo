package net.coeustec.ui;

import net.coeustec.R;
import net.coeustec.engine.ClientEngine;
import net.coeustec.model.exception.STDException;
import net.coeustec.util.ActivityUtil;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class WelcomeScreen extends BaseScreen {
  private Handler mHandler;
  private ClientEngine engine;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ActivityUtil.setNoTitle(this);
    ActivityUtil.setFullscreen(this);
    setContentView(R.layout.welcome);

    engine = ClientEngine.getInstance();
    try {
      engine.initialize(this);
      engine.launch();
    } catch (STDException e) {
      e.printStackTrace();
    }
    
    mHandler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        ActivityUtil.directToIntent(WelcomeScreen.this, LoginScreen.class);
        finish();
        
        super.handleMessage(msg);

      }
    };
    
    mHandler.sendMessageDelayed(Message.obtain(), 4000);
  }

  /*
   * * 响应按键函数
   */
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    switch (keyCode) {
    case KeyEvent.KEYCODE_BACK:
      finish();
      return true;
    }

    return false;
  }
  
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    mHandler.removeMessages(0);
    mHandler.sendEmptyMessage(0);
    
    return super.onTouchEvent(event);
  }
}

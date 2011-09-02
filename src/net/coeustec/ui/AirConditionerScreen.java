package net.coeustec.ui;

import net.coeustec.model.exception.STDException;
import net.coeustec.util.logger.Logger;

import net.coeustec.R;
import net.coeustec.app.ResourceManager;
import net.coeustec.engine.ClientEngine;
import net.coeustec.engine.Event;
import net.coeustec.engine.request.Response;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class AirConditionerScreen extends BaseScreen {

  protected ProgressBar pbar = null;
  protected PopupWindow popup = null;

  // protected int[] resArray = new int[] { R.drawable.icon, R.drawable.icon,
  // R.drawable.icon, R.drawable.icon };

  // protected String[] title = new String[]{"清除登录信息", "推荐给好友", "帮助", "退出"};

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.airscreen);
    addToManager(this);

    Button btnControl = (Button) findViewById(R.id.btnControl);
    btnControl.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        Logger.i("btnControl is clicked!");
        handleRequestMessage();
      }
    });
  }

  public void handleRequestMessage() {
    try {
      StringBuffer reqBuff = new StringBuffer();
      reqBuff.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
      reqBuff.append("<erc operator=\"control\" direction=\"request\">");
      reqBuff.append("<ercsn>" +ClientEngine.getInstance().getErcSN() +"</ercsn>");
      reqBuff.append("<clientimsi>359426002899056</clientimsi>");
      reqBuff.append("<device><deviceid>1</deviceid>");
//          <keys>
//              <keyvalue>0101</keyvalue>
//              <keyvalue>020A</keyvalue>
//          </keys>
//      </device>
//      <device>
//          <deviceid>2</deviceid>
//          <keys>
//              <keyvalue>0101</keyvalue>
//              <keyvalue>0202</keyvalue>
//              <keyvalue>030A</keyvalue>
//          </keys>
//      </device>
      
      reqBuff.append("</erc>");
      reqBuff.append("\n");
      
      ClientEngine.getInstance().getIoHandler().sendMsgStr(reqBuff.toString() );
     

    } catch (Exception ex) {
      Logger.w("In handleRequestMessage() got an error:" + ex.toString());
    }
  }

  /**
   * 处理服务器返回消息
   */
  @Override
  public void handleResponseMessage(Response response) {
    // 登录界面只处理login事件的返回值
    if (response.getName().equalsIgnoreCase(Event.TASKNAME_CONTROL)) {
      if (response.getErrcode() != Event.ERRCODE_NOERROR) {
        showConfirmDialog(ResourceManager.RES_STR_LOGIN+ResourceManager.RES_STR_FAIL,
            "错误代码" + response.getErrcode());
      } else {
        showConfirmDialog(ResourceManager.RES_STR_LOGIN+ResourceManager.RES_STR_SUCCESS, 
            ResourceManager.RES_STR_LOGIN+ResourceManager.RES_STR_SUCCESS);
      }
    }
  }

  /*
   * 响应按键函数
   */
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    switch (keyCode) {
    case KeyEvent.KEYCODE_BACK: 
      if (popup != null && popup.isShowing()) {
        popup.dismiss();
        return true;
      } else {
        showQuitAppDialog();
        return true;
      }
    }

    return super.onKeyDown(keyCode, event);
  }

  // class LoginMenuAdapter extends MenuAdapter {
  // public LoginMenuAdapter(Context context) {
  // super(context);
  // }
  //		
  // @Override
  // public View getView(final int position, View arg1, ViewGroup arg2) {
  // LinearLayout linear = (LinearLayout) super.getView(position, arg1, arg2);
  // linear.setOnClickListener(new OnClickListener(){
  // @Override
  // public void onClick(View v) {
  // if (position == 0) {
  // // TODO
  // } else if (position == 1) {
  // // TODO
  // } else if (position == 2) {
  // // TODO
  // } else if (position == 3) {
  // DialogUtil.QuitAppDialog(LoginScreen.this);
  // }
  // pw.dismiss();
  // }
  // });
  //			
  // return linear;
  // }
  //		
  // }

}

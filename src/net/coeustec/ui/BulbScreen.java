package net.coeustec.ui;

import net.coeustec.R;
import net.coeustec.app.ResourceManager;
import net.coeustec.engine.ClientEngine;
import net.coeustec.engine.Event;
import net.coeustec.engine.request.Response;
import net.coeustec.util.ActivityUtil;
import net.coeustec.util.logger.Logger;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class BulbScreen extends BaseScreen {
  private String deviceId;

  public static boolean bBulbOn = false;
  
  public ImageView btnBulb;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ActivityUtil.setNoTitle(this);
    ActivityUtil.setFullscreen(this);
    setContentView(R.layout.bulbscreen);
    addToManager(this);
    
    Intent intent = getIntent();
    String value = intent.getStringExtra("TITLE");
    this.deviceId = intent.getStringExtra("DEVICEID");
    TextView title = (TextView) findViewById(R.id.title);
    title.setText(value);
    
    btnBulb = (ImageView)findViewById(R.id.btnAlarm);
    int id = bBulbOn ? R.drawable.bulb_on : R.drawable.bulb_off; 
    btnBulb.setImageDrawable(getResources().getDrawable(id));
    btnBulb.invalidate();
    
    btnBulb.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        Logger.i("Button is clicked!");
        
        bBulbOn = !bBulbOn;

        int id = bBulbOn ? R.drawable.bulb_on : R.drawable.bulb_off; 
        ((ImageView)view).setImageDrawable(view.getResources().getDrawable(id));
        view.invalidate();
        
        updateStatus(bBulbOn);
        
        handleRequestMessage();
      }
    });
    
    updateStatus(bBulbOn);
  }

  public void handleRequestMessage() {
    try {
      StringBuffer reqBuff = new StringBuffer();
      reqBuff.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
      
      reqBuff.append("<erc operator=\"control\" direction=\"request\">");
      reqBuff.append("<ercsn>" +ClientEngine.getInstance().getErcSN() +"</ercsn>");
      reqBuff.append("<clientimsi>" +ClientEngine.getInstance().getIMSI() +"</clientimsi>");
      reqBuff.append("<device>");
      reqBuff.append("<deviceid>").append(this.deviceId).append("</deviceid>");
      reqBuff.append("<keys>");
      
      String keyVal = bBulbOn ? "0101" : "0102";  
      reqBuff.append("<keyvalue>").append(keyVal).append("</keyvalue>");
    
      reqBuff.append("</keys>");
      reqBuff.append("</device>");
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
        showConfirmDialog("开关电灯失败", "错误代码" + response.getErrcode());
      } else {
        showConfirmDialog(ResourceManager.RES_STR_REMOTECONTROL
            + ResourceManager.RES_STR_SUCCESS,
            ResourceManager.RES_STR_REMOTECONTROL
                + ResourceManager.RES_STR_SUCCESS);
      }
    }
  }

  /*
   * 响应按键函数
   */
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {

    return super.onKeyDown(keyCode, event);
  }

  private void updateStatus(boolean bBulb_on) {
    TextView status = (TextView) findViewById(R.id.bulb_status);
    StringBuffer msg = new StringBuffer();
    msg.append("状态：  ").append(bBulbOn ? "开灯" : "关灯");
    msg.append("\n历史：  ").append("1 / 3");
    status.setText(msg.toString());
    
    status.invalidate();
  }

}

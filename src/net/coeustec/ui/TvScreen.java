package net.coeustec.ui;

import java.util.HashMap;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TvScreen extends BaseScreen {

  private String deviceId;
  private static boolean bPower = false;
  private static boolean bMute = false;
  
  public ImageView btnPower, btnMute;
  public Button btnVolup, btnVoldown, btnProgramdown, btnProgramup;
  
  public ImageView imgMenu, imgExit, imgSignal;
  public ImageView imgUp, imgDown, imgLeft, imgRight, imgOK;
  public ImageView imgNum1, imgNum2, imgNum3, imgNum4, imgNum5, imgNum6,
      imgNum7, imgNum8, imgNum9, imgNum0;
  public ImageView imgShow, imgReview;
  
  public static HashMap<Integer, String> buttonValMap 
    = new HashMap<Integer, String>();
  static {
    buttonValMap.put(R.id.btnPower, "0101");
    buttonValMap.put(R.id.btnMute, "0201");
    
    buttonValMap.put(R.id.btnVolup, "0202");
    buttonValMap.put(R.id.btnVoldown, "0203");
    buttonValMap.put(R.id.btnProgramup, "030A");
    buttonValMap.put(R.id.btnProgramdown, "030B");

    buttonValMap.put(R.id.btnMenu, "0401");
    buttonValMap.put(R.id.btnExit, "0402");
    buttonValMap.put(R.id.btnSignal, "0403");
    buttonValMap.put(R.id.btnOK, "0404");
    buttonValMap.put(R.id.btnShow, "0405");
    
    buttonValMap.put(R.id.arrowUp, "0600");
    buttonValMap.put(R.id.arrowDown, "0601");
    buttonValMap.put(R.id.arrowLeft, "0602");
    buttonValMap.put(R.id.arrowRight, "0603"); 
     
    buttonValMap.put(R.id.btnNum0, "0500");    
    buttonValMap.put(R.id.btnNum1, "0501");
    buttonValMap.put(R.id.btnNum2, "0502");
    buttonValMap.put(R.id.btnNum3, "0503");
    buttonValMap.put(R.id.btnNum4, "0504");
    buttonValMap.put(R.id.btnNum5, "0505");
    buttonValMap.put(R.id.btnNum6, "0506");
    buttonValMap.put(R.id.btnNum7, "0507");
    buttonValMap.put(R.id.btnNum8, "0508");
    buttonValMap.put(R.id.btnNum9, "0509");

    buttonValMap.put(R.id.btnReview, "");  //no code mapping
  }
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ActivityUtil.setNoTitle(this);
    ActivityUtil.setFullscreen(this);
    setContentView(R.layout.tvscreen);
    addToManager(this);
    
    Intent intent = getIntent();
    String value = intent.getStringExtra("TITLE");
    this.deviceId = intent.getStringExtra("DEVICEID");
    TextView title = (TextView) findViewById(R.id.title);
    title.setText(value);
    
    btnPower = (ImageView)findViewById(R.id.btnPower);
    int id = bPower ? R.drawable.poweron : R.drawable.poweroff;
    btnPower.setImageDrawable(btnPower.getResources().getDrawable(id));
    btnPower.invalidate();
    btnPower.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        Logger.i("View ID is: "+view.getId()+"\t btnPower is:"+R.id.btnPower);
        bPower = !bPower;
        
        enableAllButton(bPower);
        
        int id = bPower ? R.drawable.poweron : R.drawable.poweroff;
        ((ImageView) view)
            .setImageDrawable(view.getResources().getDrawable(id));
        view.invalidate();
        
        handleRequestMessage(buttonValMap.get(R.id.btnPower));
      }
    });
    
    btnMute = (ImageView)findViewById(R.id.btnMute);
    id = bMute ? R.drawable.mute : R.drawable.unmute;
    btnMute.setImageDrawable(btnMute.getResources().getDrawable(id));
    btnMute.invalidate();
    btnMute.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        Logger.i("View ID is: "+view.getId()+"\tbtnMute is:"+R.id.btnMute);
        bMute = !bMute;
        
        int id = bMute ? R.drawable.mute : R.drawable.unmute;
        ((ImageView) view)
            .setImageDrawable(view.getResources().getDrawable(id));
        view.invalidate();
        
        handleRequestMessage(buttonValMap.get(R.id.btnMute));
      }
    });
    
    View.OnClickListener listener = new View.OnClickListener() {
      public void onClick(View view) {
        int viewId = view.getId();
        Logger.i("View ID is: "+view.getId());
        
        String butCode = buttonValMap.get(viewId).trim();
        if (butCode!=null && butCode.length()>0) {
          handleRequestMessage(butCode);
        }
      }
    };
    
    initButtons(listener);
    enableAllButton(bPower);
    
  }

  private void enableAllButton(boolean enable) {
    btnMute.setClickable(enable);
    
    btnVolup.setClickable(enable);
    btnVoldown.setClickable(enable);
    btnProgramdown.setClickable(enable);
    btnProgramup.setClickable(enable);
    
    imgMenu.setClickable(enable);
    imgExit.setClickable(enable);
    imgSignal.setClickable(enable);
    imgUp.setClickable(enable);
    imgDown.setClickable(enable);
    imgLeft.setClickable(enable);
    imgRight.setClickable(enable);
    imgOK.setClickable(enable);
    
    imgNum1.setClickable(enable);
    imgNum2.setClickable(enable);
    imgNum3.setClickable(enable);
    imgNum4.setClickable(enable);
    imgNum5.setClickable(enable);
    imgNum6.setClickable(enable);
    imgNum7.setClickable(enable);
    imgNum8.setClickable(enable);
    imgNum9.setClickable(enable);
    imgNum0.setClickable(enable);
   
    imgShow.setClickable(enable);
    imgReview.setClickable(enable);
  }
  
  private void initButtons(View.OnClickListener listener) {
    btnVolup = (Button) findViewById(R.id.btnVolup);
    btnVoldown = (Button) findViewById(R.id.btnVoldown);
    btnProgramdown = (Button) findViewById(R.id.btnProgramdown);
    btnProgramup = (Button) findViewById(R.id.btnProgramup);

    imgMenu = (ImageView) findViewById(R.id.btnMenu);
    imgExit = (ImageView) findViewById(R.id.btnExit);
    imgSignal = (ImageView) findViewById(R.id.btnSignal);
    imgUp = (ImageView) findViewById(R.id.arrowUp);
    imgDown = (ImageView) findViewById(R.id.arrowDown);
    imgLeft = (ImageView) findViewById(R.id.arrowLeft);
    imgRight = (ImageView) findViewById(R.id.arrowRight);
    imgOK = (ImageView) findViewById(R.id.btnOK);

    imgNum1 = (ImageView) findViewById(R.id.btnNum1);
    imgNum2 = (ImageView) findViewById(R.id.btnNum2);
    imgNum3 = (ImageView) findViewById(R.id.btnNum3);
    imgNum4 = (ImageView) findViewById(R.id.btnNum4);
    imgNum5 = (ImageView) findViewById(R.id.btnNum5);
    imgNum6 = (ImageView) findViewById(R.id.btnNum6);
    imgNum7 = (ImageView) findViewById(R.id.btnNum7);
    imgNum8 = (ImageView) findViewById(R.id.btnNum8);
    imgNum9 = (ImageView) findViewById(R.id.btnNum9);
    imgNum0 = (ImageView) findViewById(R.id.btnNum0);

    imgShow = (ImageView) findViewById(R.id.btnShow);
    imgReview = (ImageView)findViewById(R.id.btnReview);
    
    if (listener == null) return;
    
    btnVolup.setOnClickListener(listener);
    btnVoldown.setOnClickListener(listener);
    btnProgramdown.setOnClickListener(listener);
    btnProgramup.setOnClickListener(listener);
    
    imgMenu.setOnClickListener(listener);
    imgExit.setOnClickListener(listener);
    imgSignal.setOnClickListener(listener);
    imgUp.setOnClickListener(listener);
    imgDown.setOnClickListener(listener);
    imgLeft.setOnClickListener(listener);
    imgRight.setOnClickListener(listener);
    imgOK.setOnClickListener(listener);
    
    imgNum1.setOnClickListener(listener);
    imgNum2.setOnClickListener(listener);
    imgNum3.setOnClickListener(listener);
    imgNum4.setOnClickListener(listener);
    imgNum5.setOnClickListener(listener);
    imgNum6.setOnClickListener(listener);
    imgNum7.setOnClickListener(listener);
    imgNum8.setOnClickListener(listener);
    imgNum9.setOnClickListener(listener);
    imgNum0.setOnClickListener(listener);
   
    imgShow.setOnClickListener(listener);
    imgReview.setOnClickListener(listener);
    
  }
  
  public void handleRequestMessage(String keyVal) {
    try {
      StringBuffer reqBuff = new StringBuffer();
      reqBuff.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
      reqBuff.append("<erc operator=\"control\" direction=\"request\">");
      reqBuff.append("<ercsn>" +ClientEngine.getInstance().getErcSN() +"</ercsn>");
      reqBuff.append("<clientimsi>" +ClientEngine.getInstance().getIMSI() +"</clientimsi>");
      reqBuff.append("<device>");
      reqBuff.append("<deviceid>").append(this.deviceId).append("</deviceid>");
      reqBuff.append("<keys>");
      
//      if (bPower) {
//        reqBuff.append("<keyvalue>0101</keyvalue>");
//      } else {
//        reqBuff.append("<keyvalue>0102</keyvalue>");
//      }
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
        showConfirmDialog("遥控失败", "错误代码" + response.getErrcode());

      } else {
        Toast.makeText(TvScreen.this, "遥控成功",
             Toast.LENGTH_SHORT).show(); 
        
//        showConfirmDialog(ResourceManager.RES_STR_REMOTECONTROL
//            + ResourceManager.RES_STR_SUCCESS,
//            ResourceManager.RES_STR_REMOTECONTROL
//                + ResourceManager.RES_STR_SUCCESS);
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

  private void updateStatus(boolean bFortify) {
//    TextView status = (TextView) findViewById(R.id.alarm_status);
//    StringBuffer msg = new StringBuffer();
//    msg.append("状态：  ").append(bFortify ? "设防" : "撤防");
//    msg.append("\n历史：  ").append("1 / 3");
//    status.setText(msg.toString());
//    
//    status.invalidate();
  }

}

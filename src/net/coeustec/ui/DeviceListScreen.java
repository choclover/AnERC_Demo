package net.coeustec.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.w3c.dom.NodeList;

import net.coeustec.R;
import net.coeustec.app.ResourceManager;
import net.coeustec.engine.ClientEngine;
import net.coeustec.engine.Event;
import net.coeustec.engine.request.Response;
import net.coeustec.util.ActivityUtil;
import net.coeustec.util.XmlNode;
import net.coeustec.util.XmlNodeList;
import net.coeustec.util.logger.Logger;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;

public class DeviceListScreen extends BaseScreen {
  private List<Map<String, Object>> groupAry;
  private List<List<Map<String, Object>>> childAry;

  SimpleExpandableListAdapter mAdapter = null;
  
  Timer timer;
  TimerTask timeoutTask;
  Handler handler;
  
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ActivityUtil.setNoTitle(this);
    ActivityUtil.setFullscreen(this);
    setContentView(R.layout.devicelist);
    addToManager(this);

    groupAry = new ArrayList<Map<String, Object>>();
    childAry = new ArrayList<List<Map<String, Object>>>();

    addInfo("空调", new String[] {/*"空调1", "空调2"*/  });
    addInfo("电视机", new String[]{/*"电视机1"*/  });
    addInfo("报警器", new String[] { /*"安防1", "安防2"*/ });
    addInfo("机顶盒", new String[]{});  
    addInfo("热水器", new String[]{});  
    addInfo("DVD", new String[]{});  
    
    
    mAdapter = new SimpleExpandableListAdapter(
        this,
        groupAry,
        //android.R.layout.simple_expandable_list_item_1,
        R.layout.devicelist_tv,
        new String []{"TITLE"},
        new int []{android.R.id.text1},
        childAry,
        //android.R.layout.simple_expandable_list_item_2,
        R.layout.devicelist_tv,
        new String []{"TITLE"},
        new int []{android.R.id.text1}
        );
    
    ExpandableListView devListView = (ExpandableListView) findViewById(R.id.deviceList);
    //devListView.setBackgroundColor(this.getResources().getColor(R.color.white));
    //devListView.setGroupIndicator(this.getResources().getDrawable(R.drawable.arrow_right));
    devListView.setAdapter(mAdapter);
    
    devListView.setOnChildClickListener(new OnChildClickListener() {
      @Override
      public boolean onChildClick(ExpandableListView arg0, View arg1, int groupPos,
          int childPos, long arg4) {
        Logger.w("[Child Click]:" + groupPos + ":" + childPos + ":" + arg4 + ":" + arg1);
        
        Intent i = null;
        switch (groupPos) {
        case 0: {//空调
          i = new Intent(DeviceListScreen.this, AirConditionerScreen.class);
          break;
        }
        
        case 1: {//电视机
          i = new Intent(DeviceListScreen.this, TvScreen.class);
          break;
        }
        case 2: {  //报警器
          i = new Intent(DeviceListScreen.this, AlarmScreen.class);
          break;
        }

        default:
          break;
        }
            
        String value = (String)childAry.get(groupPos).get(childPos).get("TITLE");
        i.putExtra("TITLE",  value);
        value = (String)childAry.get(groupPos).get(childPos).get("DEVICEID");
        i.putExtra("DEVICEID",  value);
        Logger.i("DEVICEID is:"+value);
        //i.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        
        startActivity(i);
        
        return true;
      }
    });
    
    sendRequest();

    handler = new Handler() {
      public void handleMessage(Message msg) {
        switch (msg.what) {
        case 1:
          showConfirmDialog("同步失败", "没有收到设备同步数据！");
          sendRequest();
          break;
        }
        super.handleMessage(msg);
      }
    };
           
  }

  public void sendRequest() {
    StringBuffer reqBuff = new StringBuffer();
    reqBuff.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
    
    if (true) {
      reqBuff.append("<erc operator=\"synchronize\" direction=\"request\">");
      reqBuff.append("<ercsn>434954D31107</ercsn>");
      reqBuff.append("<clientimsi>359426002899056</clientimsi>");
      
    } else {
      reqBuff.append("<erc operator=\"control\" direction=\"request\">");
      reqBuff.append("<ercsn>434954D31107</ercsn>");
      reqBuff.append("<clientimsi>359426002899056</clientimsi>");
      reqBuff.append("<device>");
      reqBuff.append("<deviceid>0101</deviceid>");
      reqBuff.append("<keys>");
      reqBuff.append("<keyvalue>0419</keyvalue>");
      reqBuff.append("<keyvalue>0202</keyvalue>");
      reqBuff.append("<keyvalue>0101</keyvalue>");
      reqBuff.append("</keys>");
      reqBuff.append("</device>");
    }
    
    reqBuff.append("</erc>");
    reqBuff.append("\n");
    
    
    ClientEngine.getInstance().getIoHandler().sendMsgStr(reqBuff.toString() );
    
    timer = new Timer();
    timeoutTask = new TimerTask() {
      @Override
      public void run() {
        Message message = new Message();      
        message.what = 1;      
        handler.sendMessage(message);   
      }
    };
    timer.schedule(timeoutTask, 3000);
  }
  
  @Override
  public void handleResponseMessage(Response response) {
    if (response.getName().equalsIgnoreCase(Event.TASKNAME_SYNC)) {
      timeoutTask.cancel();
      timer.cancel();
      
      if (response.getErrcode() != Event.ERRCODE_NOERROR) {
        showConfirmDialog("\u83b7\u53d6\u8bbe\u5907\u5217\u8868"+ResourceManager.RES_STR_FAIL,   //获取设备列表
            "错误代码" + response.getErrcode());
        return;
      }
      
      XmlNode xmlRoot = (XmlNode)response.getData();
      String scrTitle = xmlRoot.selectSingleNodeText("ercname");
      TextView tv = (TextView)findViewById(R.id.tv_title);
      tv.setText(scrTitle);

      this.groupAry.clear();
      this.childAry.clear();
      ArrayList<String> arr1 = new ArrayList<String>();
      ArrayList<String> arr2 = new ArrayList<String>();
      ArrayList<String> arr3 = new ArrayList<String>();
      ArrayList<String> arr4 = new ArrayList<String>();
      ArrayList<String> arr5 = new ArrayList<String>();
      ArrayList<String> arr6 = new ArrayList<String>();
      
      XmlNodeList devList = xmlRoot.selectSingleNode("devicelist")
                            .selectChildNodes("device");
      for (int i=0; i<devList.count(); i++) {
        XmlNode node = devList.get(i);
        
        String deviceName = node.selectSingleNodeText("devicename");
        String deviceId = node.selectSingleNodeText("deviceid");
        int deviceType = Integer.parseInt(node.selectSingleNodeText("devtypeid"));
        switch (deviceType) {
        case 1:
          arr1.add(deviceId+"_"+deviceName);
          break;
        case 2:
          arr2.add(deviceId+"_"+deviceName);
          break;
        case 3:
          arr3.add(deviceId+"_"+deviceName);
          break;
        case 4:
          arr4.add(deviceId+"_"+deviceName);
          break;
        case 5:
          arr5.add(deviceId+"_"+deviceName);
          break;
        case 6:
        case 7:
          arr6.add(deviceId+"_"+deviceName);
          break;          
        default:
          break;
        }
      }
      
      addInfo("空调", arr1.toArray());
      addInfo("电视机", arr2.toArray());
      addInfo("报警器",arr3.toArray());
      addInfo("机顶盒",arr4.toArray());
      addInfo("热水器", arr5.toArray());
      addInfo("DVD", arr6.toArray());
      
      mAdapter.notifyDataSetChanged();
      
    }
  }
  
  private void addInfo(String str, Object[] c) {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("TITLE", str);
    groupAry.add(map);
    
    List<Map<String, Object>> item = new ArrayList<Map<String, Object>>();
    for (int i = 0; i < c.length; i++) {
      map = new HashMap<String, Object>();
      String value = (String)c[i];
      int idx = value.indexOf('_');
      if (idx != -1) {
        map.put("DEVICEID", value.substring(0, idx));
        map.put("TITLE", value.substring(idx+1));
      } else {
        map.put("DEVICEID", value);
      }
      
      item.add(map);
    }
    childAry.add(item);
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    switch (keyCode) {
    case KeyEvent.KEYCODE_BACK: 
      showQuitAppDialog();
      return true;
    }

    return super.onKeyDown(keyCode, event);
  }
}

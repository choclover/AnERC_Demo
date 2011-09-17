package net.coeustec.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.coeustec.R;
import net.coeustec.app.ResourceManager;
import net.coeustec.engine.ClientEngine;
import net.coeustec.engine.Event;
import net.coeustec.engine.request.Response;
import net.coeustec.util.ActivityUtil;
import net.coeustec.util.XmlNode;
import net.coeustec.util.XmlNodeList;
import net.coeustec.util.logger.Logger;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
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
  
  private final String DEVICE_LIST_INFO = "DEVICE_LIST";
  
  private final static String STR_ERCNAME = "ERC名称";
  private final static String STR_AC = "空调";
  private final static String STR_TV = "电视机";
  private final static String STR_ALARM = "报警器";
  private final static String STR_TOPSET = "机顶盒";
  private final static String STR_HEATER = "热水器";
  private final static String STR_DVD = "DVD";
  
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ActivityUtil.setNoTitle(this);
    ActivityUtil.setFullscreen(this);
    setContentView(R.layout.devicelist);
    addToManager(this);

    groupAry = new ArrayList<Map<String, Object>>();
    childAry = new ArrayList<List<Map<String, Object>>>();

    addInfo(STR_AC, new String[] {/*"空调1", "空调2"*/  });
    addInfo(STR_TV, new String[]{/*"电视机1"*/  });
    addInfo(STR_ALARM, new String[] { /*"安防1", "安防2"*/ });
    addInfo(STR_TOPSET, new String[]{});  
    addInfo(STR_HEATER, new String[]{});  
    addInfo(STR_DVD, new String[]{});  
    
    
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
    
    ImageView syncBtn = (ImageView)findViewById(R.id.sync_btn);
    syncBtn.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        Logger.i("Sync Button is clicked!");
        sendRequest();
      }
    });
    
    loadDeviceList();
    
    ///////////////////////
    handler = new Handler() {
      public void handleMessage(Message msg) {
        switch (msg.what) {
        case 1:
          showConfirmDialog("同步失败", "没有收到设备同步数据！");
          //sendRequest();
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
      reqBuff.append("<ercsn>" +ClientEngine.getInstance().getErcSN() +"</ercsn>");
      reqBuff.append("<clientimsi>" +ClientEngine.getInstance().getIMSI() +"</clientimsi>");
      
    } else {
//      reqBuff.append("<erc operator=\"control\" direction=\"request\">");
//      reqBuff.append("<ercsn>434954D31107</ercsn>");
//      reqBuff.append("<clientimsi>359426002899056</clientimsi>");
//      reqBuff.append("<device>");
//      reqBuff.append("<deviceid>0101</deviceid>");
//      reqBuff.append("<keys>");
//      reqBuff.append("<keyvalue>0419</keyvalue>");
//      reqBuff.append("<keyvalue>0202</keyvalue>");
//      reqBuff.append("<keyvalue>0101</keyvalue>");
//      reqBuff.append("</keys>");
//      reqBuff.append("</device>");
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
    
    //some seconds later, if timeout, will show error message and resend the synchronize request
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
      String ercName = xmlRoot.selectSingleNodeText("ercname");
      setErcName(ercName);

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
      
      addInfo(STR_AC, arr1.toArray());
      addInfo(STR_TV, arr2.toArray());
      addInfo(STR_ALARM,arr3.toArray());
      addInfo(STR_TOPSET,arr4.toArray());
      addInfo(STR_HEATER, arr5.toArray());
      addInfo(STR_DVD, arr6.toArray());
      
      mAdapter.notifyDataSetChanged();
      
      JSONObject rootObj = new JSONObject();
      try {
        rootObj.put(STR_ERCNAME, ercName);
        rootObj.put(STR_AC, new JSONArray(arr1));
        rootObj.put(STR_TV, new JSONArray(arr2));
        rootObj.put(STR_ALARM, new JSONArray(arr3));
        rootObj.put(STR_TOPSET, new JSONArray(arr4));
        rootObj.put(STR_HEATER, new JSONArray(arr5));
        rootObj.put(STR_DVD, new JSONArray(arr6));
        
        String listStr = rootObj.toString();
        Logger.i( "Device List is: "+listStr);
        
        saveDeviceList(listStr);
        
      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      

    }
  }
  
  private void setErcName(String ercName) {
    TextView tv = (TextView)findViewById(R.id.tv_title);
    tv.setText(ercName);
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
  
  private void saveDeviceList(String listStr) {
    SharedPreferences settings = getSharedPreferences(DEVICE_LIST_INFO, Context.MODE_PRIVATE);
    Editor edit = settings.edit();
    edit.putString("device_list", listStr);
    edit.commit();
  }
  
  private void loadDeviceList() {
    try {
      SharedPreferences settings = getSharedPreferences(DEVICE_LIST_INFO, Context.MODE_PRIVATE);
      String listStr = settings.getString("device_list", "");
      if (listStr == null || listStr.trim().length()<=0) {
        Logger.v("List Str is EMPTY");
        return;
      } else {
        Logger.v("List Str is: "+listStr);
      }
     
      this.groupAry.clear();
      this.childAry.clear();
      JSONObject rootObj = new JSONObject(listStr);
      
      if (rootObj.has(STR_ERCNAME)) {
        String ercName = rootObj.getString(STR_ERCNAME);
        if (ercName != null && ercName.trim().length()>0) {
          setErcName(ercName);
        }
      }
      addInfo(STR_AC, getObjectAry(rootObj.getJSONArray(STR_AC)));
      addInfo(STR_TV, getObjectAry(rootObj.getJSONArray(STR_TV)));
      addInfo(STR_ALARM, getObjectAry(rootObj.getJSONArray(STR_ALARM)));
      addInfo(STR_TOPSET, getObjectAry(rootObj.getJSONArray(STR_TOPSET)));
      addInfo(STR_HEATER, getObjectAry(rootObj.getJSONArray(STR_HEATER)));
      addInfo(STR_DVD, getObjectAry(rootObj.getJSONArray(STR_DVD)));
      
      mAdapter.notifyDataSetChanged();
      
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
  
  private Object[] getObjectAry(JSONArray jsonAry) throws JSONException {
    ArrayList<String> arr = new ArrayList<String>();
    for (int i=0; i<jsonAry.length(); i++) {
      arr.add(jsonAry.getString(i));
    }
    return arr.toArray();
  }
}

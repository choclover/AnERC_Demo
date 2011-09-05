package net.coeustec.ui;

import net.coeustec.R;
import net.coeustec.app.ResourceManager;
import net.coeustec.engine.ClientEngine;
import net.coeustec.engine.Event;
import net.coeustec.engine.request.Response;
import net.coeustec.util.logger.Logger;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class AirConditionerScreen extends BaseScreen {

  protected ProgressBar pbar = null;
  protected PopupWindow popup = null;

  public boolean ac_poweron = false;
  public int ac_mode = 1;
  
  final String[] ac_modeStr = {"制冷", "制热 ", "除湿 ", "通风", "自动" };
  // 1. 制冷 //2. 制热 //3. 除湿 //4. 通风 //5. 自动
  public int ac_temp = 25;

  // public ImageView btnPower;
  public ImageView btnMode;
  public TextView tvTemprature;

  public Gallery modeGa;
  public SeekBar tempratureBar;
  public ImageAdapter modeGaAdapter;
  public RelativeLayout seekLayout;
  
  // protected int[] resArray = new int[] { R.drawable.icon, R.drawable.icon,
  // R.drawable.icon, R.drawable.icon };

  // protected String[] title = new String[]{"清除登录信息", "推荐给好友", "帮助", "退出"};

  private String deviceId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.airscreen);
    addToManager(this);

    Intent intent = getIntent();
    String value = intent.getStringExtra("TITLE");
    this.deviceId = intent.getStringExtra("DEVICEID");
    TextView title = (TextView) findViewById(R.id.title);
    title.setText(value);

    Button btnControl = (Button) findViewById(R.id.btnControl);
    btnControl.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        Logger.i("btnControl is clicked!");
        handleRequestMessage();
      }
    });

    ImageView btnPower = (ImageView) findViewById(R.id.btnPower);
    btnPower.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        Logger.i("btnPower is clicked!");
        ac_poweron = !ac_poweron;

        int id = ac_poweron ? R.drawable.poweron : R.drawable.poweroff;
        ((ImageView) view)
            .setImageDrawable(view.getResources().getDrawable(id));
        view.invalidate();
        
        btnMode.setClickable(ac_poweron);
        tvTemprature.setClickable(ac_poweron);
        
        updateStatus();
      }
    });

    btnMode = (ImageView) findViewById(R.id.modebtn);
    btnMode.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        Logger.i("btnMode is clicked!");
        showAllMode();
      }
    });

    tvTemprature = (TextView) findViewById(R.id.temeratureTxt);
    tvTemprature.setText(String.valueOf(ac_temp));
    tvTemprature.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        Logger.i("tvTemp is clicked!");
        showTempratureBar();
      }
    });

    btnMode.setClickable(ac_poweron);
    tvTemprature.setClickable(ac_poweron);
    
    modeGa = (Gallery) findViewById(R.id.mode_gallery);
    tempratureBar = (SeekBar) findViewById(R.id.tempratureBar);

    seekLayout = (RelativeLayout) findViewById(R.id.seekbarLayout);
    seekLayout.removeAllViews();
    
    updateStatus();
  }

  public void handleRequestMessage() {
    try {
      StringBuffer reqBuff = new StringBuffer();
      reqBuff.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");

      reqBuff.append("<erc operator=\"control\" direction=\"request\">");
      reqBuff.append("<ercsn>434954D31107</ercsn>");
      reqBuff.append("<clientimsi>359426002899056</clientimsi>");
      reqBuff.append("<device>");
      reqBuff.append("<deviceid>").append(this.deviceId).append("</deviceid>");
      reqBuff.append("<keys>");
      
      if (ac_poweron) {
        reqBuff.append("<keyvalue>04").append(
            Integer.toHexString(ac_temp).toUpperCase()).append("</keyvalue>");
        reqBuff.append("<keyvalue>020").append(ac_mode).append("</keyvalue>");
        reqBuff.append("<keyvalue>0101</keyvalue>");
      } else {
        reqBuff.append("<keyvalue>0102</keyvalue>");
      }
      
      reqBuff.append("</keys>");
      reqBuff.append("</device>");
      reqBuff.append("</erc>");

      reqBuff.append("\n");

      ClientEngine.getInstance().getIoHandler().sendMsgStr(reqBuff.toString());

    } catch (Exception ex) {
      Logger.w("In handleRequestMessage() got an error:" + ex.toString());
    }
  }

  /**
   * 处理服务器返回消息
   */
  @Override
  public void handleResponseMessage(Response response) {
    if (response.getName().equalsIgnoreCase(Event.TASKNAME_CONTROL)) {
      if (response.getErrcode() != Event.ERRCODE_NOERROR) {
        showConfirmDialog(ResourceManager.RES_STR_REMOTECONTROL
            + ResourceManager.RES_STR_FAIL, "错误代码" + response.getErrcode());
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
    switch (keyCode) {
    case KeyEvent.KEYCODE_BACK:
    }

    return super.onKeyDown(keyCode, event);
  }

  private void showTempratureBar() {
    seekLayout.removeAllViews();
    seekLayout.addView(tempratureBar);

    // 定义 seekbar 控件
    tempratureBar.setMax(30 - 17); // 最大温度30，最小温度17
    tempratureBar.setProgress(ac_temp - 17);

    tempratureBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress,
          boolean fromTouch) {
//        Logger.v("onProgressChanged()", String.valueOf(progress) + ", "
//            + String.valueOf(fromTouch));

        ac_temp = progress + 17;
        tvTemprature.setText(String.valueOf(ac_temp));
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
//        Logger.v("onStartTrackingTouch()", String.valueOf(seekBar.getProgress()));
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
//        Logger.v("onStopTrackingTouch()", String.valueOf(seekBar.getProgress()));
        
        seekLayout.removeAllViews();
        
        updateStatus();
      }
    });
  }

  private void showAllMode() {
    seekLayout.removeAllViews();
    seekLayout.addView(modeGa);

    // 定义 Gallery 控件
    modeGaAdapter = new ImageAdapter(this);
    modeGa.setAdapter(modeGaAdapter);
    // 设置 Gallery 控件的图片源
    modeGa.setOnItemSelectedListener(new OnItemSelectedListener() {
      public void onNothingSelected(AdapterView<?> parent) {
        Logger.i("onNothingSelected()! ");
      }

      public void onItemSelected(AdapterView<?> parent, View v, int position,
          long id) {// 点击事件
      // Toast.makeText(AirConditionerScreen.this, "" + position,
      // Toast.LENGTH_SHORT).show(); // Toast显示图片位置
      // refreshAirMode(position+1);
      }
    });

    modeGa.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> paramAdapterView, View paramView,
          int paramInt, long paramLong) {
        Logger.i("onItemClick()! ");
        refreshAirMode(paramInt + 1);
        
        updateStatus();
      }
    });
  }

  public void refreshAirMode(int mode) {
    ac_mode = mode;

    int resId = 0;
    switch (ac_mode) {
    case 1:
      resId = R.drawable.mode_cooling;
      break;
    case 2:
      resId = R.drawable.mode_heating;
      break;
    case 3:
      resId = R.drawable.mode_dehumidify;
      break;
    case 4:
      resId = R.drawable.mode_ventilate;
      break;
    case 5:
      resId = R.drawable.mode_auto;
      break;

    default:
      break;
    }

    btnMode.setImageDrawable(btnMode.getResources().getDrawable(resId));
    btnMode.invalidate();

    if (false) {
      modeGaAdapter.removeAll();
    } else {
      seekLayout.removeView(modeGa);
    }
  }
  
  private void updateStatus() {
    updateStatus(ac_poweron, ac_mode, ac_temp);
  }
  
  private void updateStatus(boolean bPoweron, int mode, int temprature) {
    TextView status = (TextView) findViewById(R.id.air_status);
    StringBuffer msg = new StringBuffer();
    msg.append("开关：  ").append(bPoweron ? "开" : "关").append("\n");
    msg.append("模式：  ").append(ac_modeStr[mode-1]).append("\n");
    msg.append("温度：  ").append(temprature).append("℃");
    status.setText(msg.toString());
    
    status.invalidate();
  }

}

class ImageAdapter extends BaseAdapter {
  private Context mContext;
  // 定义Context
  private Integer[] mImageIds = {
      // 定义整型数组 即图片源
      R.drawable.mode_cooling, R.drawable.mode_heating,
      R.drawable.mode_dehumidify, R.drawable.mode_ventilate,
      R.drawable.mode_auto };

  public ImageAdapter(Context c) {
    // 声明 ImageAdapter
    mContext = c;
  }

  public void removeAll() {
    mImageIds = new Integer[0];
    notifyDataSetChanged();
  }

  public int getCount() { // 获取图片的个数
    return mImageIds.length;
  }

  public Object getItem(int position) {
    // 获取图片在库中的位置
    return position;
  }

  public long getItemId(int position) {
    // 获取图片在库中的位置
    return position;
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    ImageView i = new ImageView(mContext);
    i.setImageResource(mImageIds[position]);
    // 给ImageView设置资源
    i.setLayoutParams(new Gallery.LayoutParams(64, 64));
    // 设置布局 图片200×200显示
    i.setScaleType(ImageView.ScaleType.FIT_CENTER);
    // 设置比例类型
    return i;
  }
}

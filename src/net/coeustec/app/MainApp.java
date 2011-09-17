package net.coeustec.app;

import net.coeustec.engine.ClientEngine;
import net.coeustec.model.exception.STDException;
import net.coeustec.ui.AirConditionerScreen;
import net.coeustec.ui.AlarmScreen;
import net.coeustec.ui.LoginScreen;
import net.coeustec.ui.TvScreen;
import net.coeustec.ui.WelcomeScreen;
import net.coeustec.util.ActivityUtil;
import net.coeustec.util.logger.Logger;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

public class MainApp extends Activity {
  private ClientEngine engine;
  public static final String SETTING_INFO = "setting_infos";
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    try {
      engine = ClientEngine.getInstance();
      engine.initialize(this);
      engine.launch();
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    Logger.i("ready to launch welcome screen!");
    ActivityUtil.directToIntent(this, WelcomeScreen.class);
    
    finish();
  }
  
  public void saveSettings() {
    SharedPreferences settings = getSharedPreferences(SETTING_INFO, Activity.MODE_PRIVATE);
    Editor edit = settings.edit();
    edit.putBoolean("ac_poweron", AirConditionerScreen.ac_poweron);
    edit.putInt("ac_mode", AirConditionerScreen.ac_mode);
    edit.putInt("ac_temp", AirConditionerScreen.ac_temp);
    
    edit.putBoolean("alarm_fortify", AlarmScreen.bFortify);
    
    edit.putBoolean("tv_poweron", TvScreen.bPoweron);
    edit.putBoolean("tv_mute", TvScreen.bMute);
    
    edit.commit();
  }
  
  public void loadSettings() {
    try {
      SharedPreferences settings = getSharedPreferences(SETTING_INFO, Context.MODE_PRIVATE);
      if (settings != null) {
        AirConditionerScreen.ac_poweron = settings.getBoolean("ac_poweron",
            AirConditionerScreen.ac_poweron);
        AirConditionerScreen.ac_mode = settings.getInt("ac_mode",
            AirConditionerScreen.ac_mode);
        AirConditionerScreen.ac_temp = settings.getInt("ac_temp",
            AirConditionerScreen.ac_temp);

        AlarmScreen.bFortify = settings.getBoolean("alarm_fortify",
            AlarmScreen.bFortify);

        TvScreen.bPoweron = settings
            .getBoolean("tv_poweron", TvScreen.bPoweron);
        TvScreen.bMute = settings.getBoolean("tv_mute", TvScreen.bMute);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

}

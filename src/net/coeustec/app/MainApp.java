package net.coeustec.app;

import net.coeustec.engine.ClientEngine;
import net.coeustec.model.exception.STDException;
import net.coeustec.ui.LoginScreen;
import net.coeustec.ui.WelcomeScreen;
import net.coeustec.util.ActivityUtil;
import net.coeustec.util.logger.Logger;
import android.app.Activity;
import android.os.Bundle;

public class MainApp extends Activity {
  private ClientEngine engine;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    try {
      engine = ClientEngine.getInstance();
      engine.initialize(this);
      engine.launch();
      
      Thread.sleep(2000);
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    Logger.i("ready to launch welcome screen!");
    ActivityUtil.directToIntent(this, WelcomeScreen.class);
  }

}
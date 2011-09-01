package net.coeustec.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.coeustec.app.MessageHandler;
import net.coeustec.app.io.IoHandler;
import net.coeustec.engine.request.LoginRequest;
import net.coeustec.engine.request.Request;
import net.coeustec.model.exception.STDException;
import net.coeustec.util.Utils;
import net.coeustec.util.logger.Logger;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

public class ClientEngine implements AppHandler {
  
  private static final String TAG = "ClientEngine";
  
  /* 
   * Field Members
   */
  private static ClientEngine instance = null;
  
  private Context launcher;
  private ActivityManager activityManager = null;
  private TelephonyManager teleManager = null;
  private MessageHandler msgHandler = null;
  private IoHandler ioHandler = null;
  
  private ClientEngine() {
  }
  
  public static ClientEngine getInstance() {
    if (instance == null) {
      instance = new ClientEngine();
    }
    return instance;
  }

  public void initialize(Context context) throws STDException {
    if (context == null) {
      throw new STDException("Context launcher should NOT be NULL");
    } else {
      this.launcher = context;
    }
    
    this.activityManager = (ActivityManager)this.launcher.getSystemService(Context.ACTIVITY_SERVICE);
    
//    //Register System State Broadcast receiver
//    IntentFilter intentFilter = new IntentFilter();
//    intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
//    intentFilter.addAction(Intent.ACTION_SCREEN_ON);
//    this.sysStateReceiver = new SystemStateReceiver();
//    this.launcher.registerReceiver(sysStateReceiver, intentFilter);
//
//    //Create Telephony Manager
//    this.teleManager = (TelephonyManager)this.launcher.getSystemService(Context.TELEPHONY_SERVICE);
    
    //Create MessageHandler instance
    this.msgHandler = MessageHandler.getInstance();
    
    //Create IoHandler instance
    this.ioHandler =  IoHandler.getInstance();
    
    //Create AccessController instance
//    this.accController = AccessController.getInstance();
    
  }
  
  //////////////////////////////////////////////////////////////////////////////
  @Override
  public void terminate() {
    this.ioHandler.terminate();
    this.msgHandler.terminate();
  }
  
  @Override
  public void launch() {
    this.ioHandler.launch();
    this.msgHandler.launch();
  }
  
  public String getPhoneNum() {
    return this.teleManager.getLine1Number();
  }

  public ActivityManager getActivityManager() {
    return activityManager;
  }

  public MessageHandler getMsgHandler() {
    return msgHandler;
  }

  public IoHandler getIoHandler() {
    return ioHandler;
  }
  
  public PackageManager getPackageManager() {
    return launcher.getPackageManager();
  }
  
  public int getApiVersion() {
    return android.os.Build.VERSION.SDK_INT;
  }
  
  public void launchActivity(Class activity) {
    if (activity != null) {
      Intent i = new Intent(this.launcher, activity);
      i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      this.launcher.startActivity(i);
    }
  }
  
  

  public void loginServer() throws STDException {
    Logger.i(TAG, "enter loginServer");
    
    String phoneNum = getPhoneNum();
    if (! Utils.isValidPhoneNumber(phoneNum)) {
      throw new STDException("Got invalid phone number of " + phoneNum
          + ", unable to login!");
    }
    
    Request request = new LoginRequest(phoneNum);
    msgHandler.sendRequest(request);
  }
}
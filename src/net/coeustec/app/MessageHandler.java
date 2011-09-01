package net.coeustec.app;

import net.coeustec.app.io.IoHandler;
import net.coeustec.engine.AppHandler;
import net.coeustec.engine.ClientEngine;
import net.coeustec.engine.Event;
import net.coeustec.engine.request.Request;
import net.coeustec.model.exception.STDException;
import net.coeustec.util.logger.Logger;

import android.os.Handler;
import android.os.Message;

public class MessageHandler extends android.os.Handler implements AppHandler {
  
  private static final String TAG = "MessageHandler";
  
  private static MessageHandler instance = null;
  private IoHandler     ioHandler = null;
  private ClientEngine  engine = null;
  
  private MessageHandler() {
  }
  
  public static MessageHandler getInstance() {
    if (instance == null) {
      instance  = new MessageHandler();
    }
    return instance;
  }
  
  @Override
  public void launch() {
    this.engine = ClientEngine.getInstance();    
    this.ioHandler = IoHandler.getInstance();
  }
  
  @Override
  public void terminate() {
    removeMessages(0);
  }
  
  public void sendRequest(Request req) {
    Message msg = this.obtainMessage(Event.SIGNAL_TYPE_REQACK, req);
    this.sendMessage(msg);
  }
  
  @Override
  public void handleMessage(android.os.Message message) {
    Object msg = message.obj;
    int sigType = message.what;
    Logger.i(TAG, "msg type:" /*+msg.getClass().getName()+ "id:"*/ +sigType);
    
    switch(sigType) {
    case Event.SIGNAL_TYPE_REQACK:
      if (msg instanceof Request) {
        Request req = (Request)msg;
        if (req.isIncomingReq()) {
          //Execute this request in the main thread, 
          //and then append the processed request to message queue again
          req.execute();
          this.sendRequest(req);   
        
        } else if (req.isOutgoingReq() && req.isOutputContentReady()) {
          String replyStr = req.getOutputContent();
          if (replyStr != null && replyStr.trim().length() > 0) {
            this.ioHandler.sendMsgStr(replyStr);
          } else {
            Logger.d(TAG, "Outgoing reply is NULL or empty for request "+req.getName());
          }
          
        } else {
          Logger.w(TAG, "Unhandled a request: "+req.getName());
        }
      }
      break;
        
    case Event.SIGNAL_TYPE_OUTSTREAM_READY:
      // Start to login server
      try {
        engine.loginServer();
      } catch (STDException e1) {
        Logger.w(TAG, e1.toString());
      }
      break;
      
    default:
      break;
      
    } 
    
    super.handleMessage(message);
  }

  //////////////////////////////////////////////////////////////////////////////

}
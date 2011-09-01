package net.coeustec.app.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import net.coeustec.app.MessageHandler;
import net.coeustec.engine.AppHandler;
import net.coeustec.engine.ClientEngine;
import net.coeustec.engine.Event;
import net.coeustec.engine.request.Request;
import net.coeustec.model.exception.STDException;
import net.coeustec.util.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;


import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;


public class IoHandler implements AppHandler {
  
  private static final String TAG = "IoHandler";
  private static final int SLEEP_TIME = 100;  //mill-seconds
  
  /*
   * Field members
   */
  private static IoHandler instance = null;

  private ClientEngine  engine = null;
  private MessageHandler msgHandler = null;
  
  private Socket socketConn = null;
  private BufferedInputStream bis = null;
  private BufferedOutputStream bos = null;
  //private boolean isLogin = false;  //TODO how to use this flag in client?
  
  private InputConnectionThread inputConnThread  = null;
  private OutputConnectionThread outputConnThread  = null;

  /*
   * Methods
   */
  private IoHandler() {
    
  }
  
  public static IoHandler getInstance() {
    if (instance == null) {
      instance = new IoHandler();
    }
    return instance;
  }

  private void initialize() {
    if (socketConn != null) {
      try { socketConn.close(); } 
      catch (IOException e) { e.printStackTrace(); }
      socketConn = null;
    }

    while (socketConn == null) {
      socketConn = constructSocketConnection();
      if (socketConn == null) {
        Logger.w(TAG, "Creating Socket returns NULL");
        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      } 
    }

    if (socketConn != null) {
      try {
        bis = new BufferedInputStream(socketConn.getInputStream());
        bos = new BufferedOutputStream(socketConn.getOutputStream());
        
        inputConnThread = new InputConnectionThread(); 
        inputConnThread.start();
        outputConnThread = new OutputConnectionThread(); 
        outputConnThread.start(); 
        
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  
  public void launch() {
    this.engine = ClientEngine.getInstance();  
    this.msgHandler = MessageHandler.getInstance();
    
    initialize();
  }
  
  @Override
  public void terminate() {
    if (inputConnThread != null) {
      inputConnThread.terminate();
    }
    if (outputConnThread != null) {
      outputConnThread.terminate();
    }
    
    if (bis != null)
      try {
        bis.close();
      } catch (IOException e) {
        Logger.w(TAG, e.toString());
      }

    if (bos != null)
      try {
        bos.close();
      } catch (IOException e) {
        Logger.w(TAG, e.toString());
      }

    if (socketConn != null)
      try {
        socketConn.close();
      } catch (IOException e) {
        Logger.w(TAG, e.toString());
      }
  }
  //////////////////////////////////////////////////////////////////////////////
  public String getRemoteSvrAddr() {
    // TODO read from config
    String addr = "192.168.1.250";
    //addr = "192.168.1.108";
    return addr;
  }

  public int getRemoteSvrPort() {
    return 9177;  //9123;
  }

  public String getEncoding() {
    return "UTF-8";
  }
  
  public void sendMsgStr(String msg) {
    try {
      if (outputConnThread != null) {
        outputConnThread.sendMsgStr(msg);
      } else {
        Logger.w(TAG, "IO connection is NULL");
      }
    } catch (STDException e) {
      Logger.w(TAG, "SendMsgStr() got error of "+e.getMessage());
    }
  }
  
  public void handleResponseMessage(JSONObject msgObj) {
    //TODO
  }
  
  private Socket constructSocketConnection() {
    Socket aSock = null;
    try {
      // The IP here should NOT be localhost which is the phone itself
      aSock = new Socket(InetAddress.getByName(getRemoteSvrAddr()),
          getRemoteSvrPort());
      aSock.setKeepAlive(true);
      // TODO handle re-connnect and exception handling

    } catch (UnknownHostException e) {
      Logger.w(TAG, e.toString());
    } catch (IOException e) {
      Logger.w(TAG, e.toString());
    }

    return aSock;
  }
  
  //////////////////////////////////////////////////////////////////////////////
  //Connection thread
  class OutputConnectionThread extends Thread/*HandlerThread*/ {
    private static final String TAG = "OutputConnectionThread";
    private Handler outputMsgHandler = null;
    private boolean isReady = false;
    
    public OutputConnectionThread() {
      super(TAG);
    }
    
    public void terminate() {
      this.interrupt();

      if (outputMsgHandler != null) {
        outputMsgHandler.removeMessages(0);
        outputMsgHandler = null;
      }
    }
    
//    public boolean isReady() {
//      return isReady;
//    }
    
    public void run() {
      /*
       * Output message handler
       */
      Looper.prepare();
      
      this.outputMsgHandler = new Handler(/*this.getLooper()*/) {
        @Override
        public void handleMessage(android.os.Message message) {
          String msgStr = (String) message.obj;

          try {
            sendMsgStr_internal(msgStr);
          } catch (STDException e) {
            Logger.w(TAG, e.toString());
          }
        }
      };
      
      Message signal = msgHandler.obtainMessage(Event.SIGNAL_TYPE_OUTSTREAM_READY);
      msgHandler.sendMessage(signal);
      
      Looper.loop();
    }
    
    public void sendMsgStr(String msgStr) throws STDException {
      if (this.outputMsgHandler == null) {
        throw new STDException("Output Msg handler should NOT be null!");
      }
      Message msg = this.outputMsgHandler.obtainMessage(0, msgStr);
      this.outputMsgHandler.sendMessage(msg);
    }

    private void sendMsgStr_internal(String msg) throws STDException {
      if (bos == null) {
        throw new STDException("Output stream should NOT be null!");
      }

      try {
        byte[] msgBytes;
        msgBytes = msg.getBytes(getEncoding());

        bos.write(msgBytes);
        bos.flush();

      } catch (UnsupportedEncodingException e) {
        Logger.w(TAG, e.toString());
      } catch (IOException e) {
        Logger.w(TAG, e.toString());
      }
    }
  }//class OutputConnectionThread
  
  
  class InputConnectionThread extends Thread {
    private static final String TAG = "@@ InputConnectionThread";

    private boolean isStop = false;
    private boolean isReady = false;
    
    public void terminate() {
      isStop = true;
      this.interrupt();
    }

//    public boolean isReady() {
//      return isReady;
//    }
    
    public void run() {
      try {
        /*
         * Input message receiver
         */
        byte[] buffer = null;

        while (!isStop) {
          if (bis.available() > 0) {
            buffer = new byte[bis.available()];
            bis.read(buffer);

            String msgStr = new String(buffer, getEncoding());
            Logger.i(TAG, "AndrClient Got a message:\n" + msgStr);
            
            try {
              JSONObject msgObjRoot = new JSONObject(msgStr);
              String msgType = msgObjRoot.getString(Event.TAGNAME_MSG_TYPE);

              if (msgType.equals(Event.MESSAGE_HEADER_REQ)) {
                //This is a incoming request
                int msgId = msgObjRoot.getInt(Event.TAGNAME_MSG_ID);
                String reqType = msgObjRoot.getString(Event.TAGNAME_CMD_TYPE);
                
                String reqClazName = Request.class.getName();
                if (reqClazName.indexOf('.') != -1) {
                  reqClazName = reqClazName.substring(0, reqClazName.lastIndexOf('.')+1);
                } else {
                  reqClazName = "";
                }
                reqClazName = reqClazName + reqType + "Request";
                Logger.i(TAG, "Ready to new instance of:"+reqClazName);
                
                Request request;
                request = (Request) Class.forName(reqClazName).newInstance();

                if (request != null) {
                  // send incoming request to MessageHandler to handle
                  request.setRequestSeq(msgId);
                  msgHandler.sendRequest(request);
                }
                
              } else if (msgType.equals(Event.MESSAGE_HEADER_ACK)) {
                //This is a response message
                handleResponseMessage(msgObjRoot);
                
              } else {
                Logger.i(TAG, "Unsupported Incoming MESSAGE type(" + msgType
                    + ") in this version.");
              }
              
            } catch (JSONException ex) {
              Logger.w(TAG, "JSON paring error for request:\n" + msgStr);
              Logger.w(TAG, ex.toString());
            } catch (InstantiationException ex) {
              Logger.w(TAG, ex.toString());
            } catch (IllegalAccessException e) {
              Logger.w(TAG, e.toString());
            } catch (ClassNotFoundException e) {
              Logger.w(TAG, e.toString());
            }
          }

          Thread.sleep(SLEEP_TIME);
        }// while !stopped

      } catch (InterruptedException e) {
        Logger.w(TAG, e.toString());
      } catch (UnsupportedEncodingException e) {
        Logger.w(TAG, e.toString());
      } catch (IOException e) {
        Logger.w(TAG, e.toString());
      }
    }
    
  }// class RemoteConnectionThread

}//class IoHandler




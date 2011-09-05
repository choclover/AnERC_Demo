package net.coeustec.app.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;

import net.coeustec.app.MessageHandler;
import net.coeustec.app.ResourceManager;
import net.coeustec.engine.AppHandler;
import net.coeustec.engine.ClientEngine;
import net.coeustec.engine.Event;
import net.coeustec.engine.request.Response;
import net.coeustec.model.exception.STDException;
import net.coeustec.ui.BaseScreen;
import net.coeustec.util.XmlNode;
import net.coeustec.util.logger.Logger;
import android.os.Handler;
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
    Runnable r = new Runnable() {
      @Override
      public void run() {
        init_network();
      }
    };
    new Thread(r).start();
  }
  
  
  @Override
  public void launch() {
    this.engine = ClientEngine.getInstance();  
    this.msgHandler = this.engine.getMsgHandler();
    
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
  
  public String getRemoteSvrIP() {
    /* 
     * Do NOT use localhost/127.0.0.1 for machine itself 
     */
    // TODO read from config
    String addr = "192.168.1.250";
    //addr = "192.168.1.108";
    return addr;
  }

  public String getRemoteSvrDomainName() {
    // TODO read from config
    String addr = "coeustec.gicp.net";
    return addr;
  }

  public int getRemoteSvrPort() {
    return 9999;  //9123;
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
//        BaseScreen.showConfirmDialog("网络故障",
//            "网络连接故障，请退出程序重试！");
            
      }
    } catch (STDException e) {
      Logger.w(TAG, "SendMsgStr() got error of "+e.getMessage());
    }
  }
  
  public void handleResponseMessage(XmlNode xmlRoot) {
    String respType = xmlRoot.getAttribute(Event.TAGNAME_MSG_TYPE);
    
    String val = "";
    val = xmlRoot.selectSingleNodeText(Event.TAGNAME_RESULT);
    int errcode = Integer.parseInt(val);
    
    Response resp = new Response();
    resp.setName(respType);
    resp.setErrcode(errcode);
    resp.setData(xmlRoot);
    
    Message message = this.msgHandler.obtainMessage(Event.SIGNAL_TYPE_REQACK, resp);
    this.msgHandler.sendMessage(message);
  }
  
  //////////////////////////////////////////////////////////////////////////////
  private void init_network() {
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
      
      break;  //FIXME: design a re-connect or retry method
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
        Logger.w(TAG, e.toString());
      }
    }
  }
  
  private Socket constructSocketConnection() {
    Socket aSock = null;
    final int RETRY_TIMES = 5;
    
    for (int i=0; i<RETRY_TIMES; i++) {
      try {
        // The IP here should NOT be localhost which is the phone itself
        aSock = new Socket(getRemoteSvrDomainName(),
            getRemoteSvrPort());
        aSock.setKeepAlive(true);
  
      } catch (UnknownHostException e) {
        Logger.w(TAG, e.toString());
      } catch (IOException e) {
        Logger.w(TAG, e.toString());
      }
      
      if (aSock != null) break;
      
      try {
        Thread.sleep((i*10+5) * 1000);
      } catch (InterruptedException e) {
        Logger.w(TAG, e.toString());
      }
    }

    return aSock;
  }
  
  //**************************************************************************//
  //Connection thread
  class OutputConnectionThread extends Thread/*HandlerThread*/ {
    private static final String TAG = "@@ OutputConnectionThread";
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
      
      //we can start to login server now since OUTPUT stream is ready
      Message signal = msgHandler.obtainMessage(Event.SIGNAL_TYPE_OUTSTREAM_READY);
      msgHandler.sendMessage(signal);
      
      Looper.loop();
    }
    
    private void sendMsgStr_internal(String msg) throws STDException {
      if (bos == null) {
        throw new STDException("Output stream should NOT be null!");
      }

      Logger.i(TAG, "Ready to send msg:\n"+msg);
      
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
    
    ////////////////////////////////////////////////////////////////////////////
    public void sendMsgStr(String msgStr) throws STDException {
      if (this.outputMsgHandler == null) {
        throw new STDException("Output Msg handler should NOT be null!");
      }
      Message msg = this.outputMsgHandler.obtainMessage(0, msgStr);
      this.outputMsgHandler.sendMessage(msg);
    }
    
  }//class OutputConnectionThread
  
  //**************************************************************************//
  class InputConnectionThread extends Thread {
    private static final String TAG = "@@ InputConnectionThread";

    private boolean isStop = false;
//    private boolean isReady = false;
    
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
            if (msgStr == null || msgStr.trim().length() == 0) {
              continue;
            }
            
            try {
              XmlNode msgObjRoot = new XmlNode();

              if (!msgObjRoot.loadXml(msgStr)) {
                Logger.w("Fail to load raw data:\n"+msgStr);
              }

              String evtType = msgObjRoot.getAttribute(Event.TAGNAME_MSG_DIRE);
              if (evtType.equals(Event.MESSAGE_HEADER_REQ)) {
                //This is a incoming request
                
              } else if (evtType.equals(Event.MESSAGE_HEADER_ACK)) {
                //This is a response message
                handleResponseMessage(msgObjRoot);
                
              } else {
                Logger.i(TAG, "Unsupported Incoming MESSAGE type(" + evtType
                    + ") in this version.");
              }
              
            } catch (Exception ex) {
              Logger.w(TAG, ex.toString());
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




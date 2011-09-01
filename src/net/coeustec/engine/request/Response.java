package net.coeustec.engine.request;

import net.coeustec.engine.Event;

public class Response /*extends Message*/ {
  private String respName;
  private int errcode = Event.ERRCODE_NOERROR;
  private int respType = 0;
  
  /*
   * Constants
   */
  
  /*
   * Field Members
   */
//  protected boolean bIncoming = true;
//  protected boolean bOutputContentReady = false;
//  protected String outputContent = null;
//  protected int req_seq = Event.MSG_ID_INVALID;
//  
//  public void execute(MessageHandler msgHandler) {
//  }
  
  public String getName() {
    return respName;
  }
  
  public void setName(String name) {
    this.respName = name;
  }

  public int getErrcode() {
    return errcode;
  }

  public void setErrcode(int errcode) {
    this.errcode = errcode;
  }

  public int getRespType() {
    return respType;
  }

  public void setRespType(int respType) {
    this.respType = respType;
  }
  
}
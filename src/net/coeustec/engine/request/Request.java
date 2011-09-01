package net.coeustec.engine.request;

import net.coeustec.app.MessageHandler;
import net.coeustec.engine.Event;

import org.json.JSONException;
import org.json.JSONObject;



public abstract class Request /*extends Message*/ {
  /*
   * Constants
   */
  public static final int RESP_TYPE_LOGIN = 1001;
  
  /*
   * Field Members
   */
  protected boolean bIncoming = true;
  protected boolean bOutputContentReady = false;
  protected String outputContent = null;
  protected int req_seq = Event.MSG_ID_INVALID;
  
  public void execute(MessageHandler msgHandler) {
  }
  
  public abstract void execute();
  
  public String getName() {
    return "Request";  //Event.TASKNAME_Generic;
  }
  
  public boolean isIncomingReq() {
    return bIncoming;
  }
  
  public boolean isOutgoingReq() {
    return !bIncoming;
  }
  
  public boolean isOutputContentReady() {
    return bOutputContentReady;
  }
  
  public String getOutputContent() {
    return outputContent;
  }
  
  public void setOutputContent(String content) {
    outputContent = content;
    this.bOutputContentReady = true;
  }
  
//  public JSONObject generateGenericReplyHeader(String cmd_type) 
//    throws JSONException {
//    JSONObject header = new JSONObject();
//    header.put(Event.TAGNAME_MSG_TYPE, Event.MESSAGE_HEADER_ACK);
//    header.put(Event.TAGNAME_MSG_ID, req_seq);
//    header.put(Event.TAGNAME_CMD_TYPE, cmd_type);
//    
//    return header;
//  }
  
  public void setRequestSeq(int seq) {
    this.req_seq = seq;
  }
}
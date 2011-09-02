package net.coeustec.engine.request;

import net.coeustec.engine.Event;
import net.coeustec.util.logger.Logger;

public class LoginRequest extends Request {
  private String phoneNum = null;
  private String passwd = null;
  
  public LoginRequest(String phoneNum, String passwd) {
    this.phoneNum  = phoneNum;
    this.passwd = passwd;
  }
  
  public String getName() {
    return Event.TASKNAME_LOGIN;
  }
  
  public void execute() {
    try {
      StringBuffer reqBuff = new StringBuffer();
      reqBuff.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
      reqBuff.append("<erc operator=\"login\" direction=\"request\">");
      reqBuff.append("<clienttype>2</clienttype>");
      reqBuff.append("<clientimsi>359426002899056</clientimsi>");
      reqBuff.append("<ercsn>434954D31107</ercsn>");
      reqBuff.append("<clientconfig>0</clientconfig>");
      reqBuff.append("<clientsoftware>V0.8</clientsoftware>");
      reqBuff.append("<clientos>Android</clientos>");
      reqBuff.append("<clientosversion>2.2.0</clientosversion>");
      reqBuff.append("<clientpwd>" +passwd+ "</clientpwd>");
      reqBuff.append("<clientmsisdn>" +phoneNum+ "</clientmsisdn>");
      reqBuff.append("</erc>");
      reqBuff.append("\n");
      
      this.bIncoming = false;
      setOutputContent(reqBuff.toString());

    } catch (Exception ex) {
      Logger.w(getName(), "In execute() got an error:" + ex.toString());
    }
  }
  
  public boolean isOutputContentReady() {
    return true;
  }

}
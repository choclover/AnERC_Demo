package net.coeustec.ui;

import net.coeustec.R;
import net.coeustec.engine.ClientEngine;
import net.coeustec.util.ActivityUtil;
import net.coeustec.util.logger.Logger;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class SettingScreen extends BaseScreen {
  EditText editErcSn;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ActivityUtil.setNoTitle(this);
    ActivityUtil.setFullscreen(this);
    setContentView(R.layout.settingscreen);
    
    this.editErcSn = (EditText )findViewById(R.id.editErcSn);
    editErcSn.setText(ClientEngine.getErcSN().trim());
    
    Button btnConfirm = (Button) findViewById(R.id.btnSettingConfirm);
    btnConfirm.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        Logger.i("btnLogin is clicked!");
        ClientEngine.setErcSN(editErcSn.getText().toString().trim());
        finish();
      }
    });
    
    Button btnCancel = (Button) findViewById(R.id.btnSettingCancel);
    btnCancel.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        Logger.i("btnCancel is clicked!");
        finish();
      }
    });
  }
}

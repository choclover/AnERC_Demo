package net.coeustec.ui;

import net.coeustec.engine.Event;
import net.coeustec.engine.request.Response;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class LoginScreen extends BaseScreen {

	protected ProgressBar pbar = null;
	protected PopupWindow popup = null;
	
//  protected int[] resArray = new int[] { R.drawable.icon, R.drawable.icon,
//      R.drawable.icon, R.drawable.icon };

//	protected String[] title = new String[]{"清除登录信息", "推荐给好友", "帮助", "退出"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		setTopBarButton();
//		setBottomBarButton();
//		setBodyView();
//		initCallBack();

		// 特别处理，隐藏状态条
//		RelativeLayout topbar = (RelativeLayout) findViewById(R.id.headerBar);
//		topbar.setVisibility(View.GONE);

		addToManager(this);
	}

	//@Override
	public void setBodyView() {
//		super.setBodyView();
//		LayoutInflater inflater = (LayoutInflater) this
//				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		View view = inflater.inflate(R.layout.loginscreen, null);
//		bodyView.addView(view, new LayoutParams(LayoutParams.FILL_PARENT,
//				LayoutParams.FILL_PARENT));
	}

	/*
	 * 添加特别的按钮处理
	 */
	public void initCallBack() {
//		bottomRightImage.setVisibility(View.GONE);
//		bottomRighttext.setVisibility(View.VISIBLE);
//		bottomRighttext.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//
//				String msg = getString(R.string.login_promt);
//				showProgress(msg);
//				// 进行登录动作
//				SocketUtil.loginSystem("12345678", "15365185898");
//			}
//		});
	}

	/*
	 * 创建菜单
	 */
	//@Override
	public boolean onCreatePopMenu() {
//		LayoutInflater inflater = (LayoutInflater) this
//				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		View view = inflater.inflate(R.layout.popmenu, null);
//		grid = (GridView) view.findViewById(R.id.menuGrid);
//		menuadapter = new LoginMenuAdapter(this);
//		menuadapter.setMenuContent(title, resArray);
//		grid.setAdapter(menuadapter);
//
//		pw = new PopupWindow(view, LayoutParams.FILL_PARENT,
//				LayoutParams.WRAP_CONTENT);
//		pw.setOutsideTouchable(true);
//		
//		grid.setFocusable(true);
//				
//		pw.showAtLocation(findViewById(R.id.footerBar), Gravity.CENTER
//				| Gravity.BOTTOM, 0, 75);
		return true;
	}

	/**
	 * 处理服务器返回消息
	 */
	@Override
	public void handleResponseMessage(Response response)
	{
		// 登录界面只处理login事件的返回值
		if (response.getName().equalsIgnoreCase(Event.TASKNAME_LOGIN)) {
			// 得到登录的返回消息
			// TODO 需要处理各种情况，这里暂且假定登录成功
			closeProgress();
			if (response.getErrcode() != Event.ERRCODE_NOERROR) {
				showConfirmDialog("登录失败", "错误代码"+response.getErrcode());
			} else {
				ScreenManager.removeTopActivity();

        // MainScreen.temperature = ((LoginEventRsp) login)
        // .getTemperature();
        // MainScreen.humidity = ((LoginEventRsp) login).getHumidity();
        // ActivityUtil.directToIntent(LoginScreen.this, MainScreen.class);
				
				finish();
			}
		}
	}

	/*
	 * 响应按键函数
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK: {
			if (popup != null && popup.isShowing()) {
			  popup.dismiss();
				return true;
			} else {
				showQuitAppDialog();
				return true;
			}
		}
		}

		return super.onKeyDown(keyCode, event);
	}
	
//	class LoginMenuAdapter extends MenuAdapter {
//		public LoginMenuAdapter(Context context) {
//			super(context);
//		}
//		
//		@Override
//		public View getView(final int position, View arg1, ViewGroup arg2) {
//			LinearLayout linear = (LinearLayout) super.getView(position, arg1, arg2);
//			linear.setOnClickListener(new OnClickListener(){
//				@Override
//				public void onClick(View v) {
//					if (position == 0) {
//						// TODO
//					} else if (position == 1) {
//						// TODO
//					} else if (position == 2) {
//						// TODO
//					} else if (position == 3) {
//						DialogUtil.QuitAppDialog(LoginScreen.this);
//					}	
//					pw.dismiss();
//				}
//			});
//			
//			return linear;
//		}
//		
//	}

}

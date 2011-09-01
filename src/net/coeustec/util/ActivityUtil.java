package net.coeustec.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class ActivityUtil {

  public static final String PREFS_NAME = "net.coeustec";

  private ActivityUtil() {

  }

  /*
   * 设置全屏
   */
  public static void setFullscreen(Activity activity) {
    activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
  }

  /*
   * 设置没有标题
   */
  public static void setNoTitle(Activity activity) {
    activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
  }

  /*
   * 获得屏幕参数
   */
  public static DisplayMetrics getDisplayInfo(Activity activity) {
    DisplayMetrics displayMetric = activity.getResources().getDisplayMetrics();
    return displayMetric;
  }

  /*
   * 保存key value对到preference中 key： key参数 value：key值
   */
  public static void savePreference(Context activity, String key, String value) {

    SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, 0);

    SharedPreferences.Editor editor = settings.edit();

    editor.putString(key, value);

    editor.commit();
  }

  /*
   * 取得保存好的数据 key: key值 value：如果不存在时的默认值
   */
  public static String getPreference(Context activity, String key, String value) {

    SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, 0);

    String res = settings.getString(key, value);

    return res;
  }

  /*
   * 隐藏输入法
   */
  public static void setInputMethodHidden(Activity activity) {
    activity.getWindow().setSoftInputMode(
        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
  }

  // 获取AndroidManifest.xml中android:versionName
  public static String getSoftwareVersion(Context context) {
    String packageName = PREFS_NAME;
    String sv = "";
    try {
      PackageInfo pkinfo = context.getPackageManager().getPackageInfo(
          packageName, PackageManager.GET_CONFIGURATIONS);
      sv = pkinfo.versionName;
      return sv;
    } catch (NameNotFoundException e) {

    }
    return "";
  }

  // 获取AndroidManifest.xml中android:versionCode
  public static int getVersionCode(Context context) {
    String packageName = PREFS_NAME;
    int sv = 0;
    try {
      PackageInfo pkinfo = context.getPackageManager().getPackageInfo(
          packageName, PackageManager.GET_CONFIGURATIONS);
      sv = pkinfo.versionCode;
      return sv;
    } catch (NameNotFoundException e) {

    }

    return 0;
  }

  // 启动新Intent,带参数
  public static void directToIntent(Context context, Class classname,
      String param) {
    Intent intent = new Intent(context, classname);
    intent.putExtra("param", param);
    context.startActivity(intent);
  }

  // 启动新Intent,不带参数
  public static void directToIntent(Context context, Class classname) {
    Intent intent = new Intent(context, classname);
    context.startActivity(intent);
  }

  public static void directToExit(Context context, Class classname, String param) {
    Intent intent = new Intent(context, classname);
    context.startActivity(intent);
    ((Activity) context).finish();
  }

  public static void exitApp(Context context) {
    ActivityManager activityManager = (ActivityManager) context
        .getSystemService("activity");
    String str = context.getPackageName();
    activityManager.restartPackage(str);
  }

  public static View inflate(Activity context, int layoutId) {
    return context.getLayoutInflater().inflate(layoutId, null);
  }

  public static View inflate(Context context, int layoutId) {
    return ((Activity) context).getLayoutInflater().inflate(layoutId, null);
  }

  public static void showToast(Context context, String param) {
    Toast localToast = Toast.makeText(context, param, Toast.LENGTH_LONG);
    localToast.show();
  }

}

package net.coeustec.ui;

import java.util.ArrayList;

import net.coeustec.util.logger.Logger;
import android.app.Activity;

public class ScreenManager {

	private static ArrayList<Activity> actiList_ = null;

	public static void addActivity(Activity activity) {
		if (activity != null) {
			if (actiList_ == null)
				actiList_ = new ArrayList<Activity>(0);
			actiList_.add(activity);
		}
	}

	public static void removeAllActivity() {
		try {
			if (actiList_ == null || actiList_.size() == 0)
				return;

			int sz = actiList_.size();
			for (int i = 0; i < (sz); i++) {
				Activity act = actiList_.get(i);
				if (act != null) {
					act = actiList_.remove(i);
					act.finish();
				}
			}

		} catch (Exception e) {
			Logger.i("error when remove activity");
		}
	}

	public static BaseScreen getTopActivity() {
		if (actiList_ == null || actiList_.size() == 0)
			return null;
		return (BaseScreen) actiList_.get(actiList_.size() - 1);
	}

	public static void removeTopActivity() {
		if (actiList_ == null || actiList_.size() == 0)
			return;
		actiList_.remove(actiList_.size() - 1);
		return;
	}

	public static ArrayList<Activity> getActivitys() {
		return actiList_;
	}
}

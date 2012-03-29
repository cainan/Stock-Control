package br.csoliveira.stockcontrol.util;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.util.Log;

public class Utils {

    private static final String APP_TAG = "Stock_Control";

    public static void doLog(String text) {
        Log.d(APP_TAG, text);
    }

    public static void doLog(String tag, String text) {
        Log.d(tag, text);
    }

    /**
     * Sets screen rotation as fixed to current rotation setting
     * 
     * @param activity
     */
    public static void lockScreenRotation(Activity activity) {
        switch (activity.getResources().getConfiguration().orientation) {
        case Configuration.ORIENTATION_PORTRAIT:
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            break;
        case Configuration.ORIENTATION_LANDSCAPE:
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            break;
        }
    }

    /**
     * Allow screen rotation
     * 
     * @param activity
     */
    public static void unlockScreenRotation(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
}

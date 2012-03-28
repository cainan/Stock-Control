package br.csoliveira.stockcontrol.util;

import android.util.Log;

public class Utils {

    private static final String APP_TAG = "Stock_Control";

    public static void doLog(String text) {
        Log.d(APP_TAG, text);
    }

    public static void doLog(String tag, String text) {
        Log.d(tag, text);
    }
}

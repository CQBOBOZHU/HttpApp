package com.bobozhu.httpapp;

import android.app.Activity;
import android.content.SharedPreferences;



/**
 * Created by Administrator
 */

public class SharePreferenceUtils {

    public static void save(String key, Object value) {
        SharedPreferences sharedPreferences = AppContext.getAppContext().getSharedPreferences(AppContext.getAppContext().getPackageName(),
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        }
        editor.commit();
    }


    public static Integer getInt(String key) {
        SharedPreferences sharedPreferences = AppContext.getAppContext().getSharedPreferences(AppContext.getAppContext().getPackageName(),
                Activity.MODE_PRIVATE);
        if (sharedPreferences != null) {
            int value = sharedPreferences.getInt(key, 0);
            return value;
        }
        return 0;
    }

    public static String getString(String key) {
        SharedPreferences sharedPreferences = AppContext.getAppContext().getSharedPreferences(AppContext.getAppContext().getPackageName(),
                Activity.MODE_PRIVATE);
        if (sharedPreferences != null) {
            String value = sharedPreferences.getString(key, "");
            return value;
        }
        return "";
    }

    public static boolean getBoolean(String key) {
        SharedPreferences sharedPreferences = AppContext.getAppContext().getSharedPreferences(AppContext.getAppContext().getPackageName(),
                Activity.MODE_PRIVATE);
        if (sharedPreferences != null) {
            boolean value = sharedPreferences.getBoolean(key, true);
            return value;
        }
        return true;
    }
}

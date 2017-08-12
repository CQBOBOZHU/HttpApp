package com.bobozhu.httpapp;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2017/8/12.
 */

public class AppContext extends Application {
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
    }

    public static Context getAppContext()
    {
        return appContext;
    }
}

package com.coolweather.android.util;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePal;

/**
 * 项目基础的 Application 类，可以提供全局的 Context 对象。
 */

public class BaseApplication extends Application {

    private static Context context;


    public static Context getContext() {
        return context;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        LitePal.initialize(context);
    }
}

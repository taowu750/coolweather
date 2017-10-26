package com.coolweather.android.app;

import android.app.Application;
import android.content.Context;

import com.coolweather.android.util.LogUtil;

import org.litepal.LitePal;

/**
 * 项目基础的 Application 类，可以提供全局的 Context 对象。
 */

public class BaseApplication extends Application {

    private static Context context;

    private static final String TAG = LogUtil.TAG_HEAD + "BaseApplication";


    public static Context getContext() {
        return context;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        LitePal.initialize(context);
        LogUtil.v(TAG, "onCreate: 创建 BaseApplication，并初始化 LitePalApplication");
    }
}

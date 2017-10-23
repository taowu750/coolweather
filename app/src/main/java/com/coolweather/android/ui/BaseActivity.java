package com.coolweather.android.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.coolweather.android.R;
import com.coolweather.android.util.ActivityCollector;
import com.coolweather.android.util.LogUtil;

/**
 * 项目基础的 Activity 类，用来提供一些特殊功能。它会在每次创建时在日志中打印类名，其中 tag 为"BaseActivity"<br/>
 * 还提供了强制结束应用的方法。
 */

public class BaseActivity extends AppCompatActivity {

    public static final String FORCE_FINISH = "com.coolweather.android.FORCE_FINISH";


    private ForceOfflineReceiver receiver;
    private LocalBroadcastManager localBroadcastManager;

    private static final String TAG = "BaseActivity";


    /**
     * 当活动处于活动栈栈顶时，该方法将会弹出一个对话框询问用户是否终结应用
     */
    public void forceFinish() {
        if (receiver != null) {
            Intent intent = new Intent(FORCE_FINISH);
            localBroadcastManager.sendBroadcast(intent);
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, getClass().getSimpleName());
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(FORCE_FINISH);
        receiver = new ForceOfflineReceiver();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (receiver != null) {
            localBroadcastManager.unregisterReceiver(receiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }


    class ForceOfflineReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("警告");
            builder.setMessage(R.string.app_name + "将被强制终结，是否继续？");
            builder.setCancelable(false);
            builder.setPositiveButton("是", (dialog, which) -> {
                ActivityCollector.finishAll();
                Toast.makeText(context, R.string.app_name + "已经结束！", Toast.LENGTH_SHORT).show();
            });
            builder.setNegativeButton("否", null);
            builder.show();
        }
    }
}

package com.coolweather.android.util;

import android.app.Activity;

import com.coolweather.android.exception.NotCreatedObjectException;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动收集器，用于存储活动或移除存在的活动，并且可以终结存在于其中的所有活动。<br/>
 * 需要注意的是，该类只会存在一个。
 */

public class ActivityCollector {

    private static List<Activity> activities = new ArrayList<>();

    private static final String TAG = LogUtil.TAG_HEAD + "ActivityCollector";


    private ActivityCollector() {
        throw new NotCreatedObjectException();
    }


    /**
     * 向 ActivityCollector 中添加 activity。
     *
     * @param activity Activity 对象
     * @return true
     */
    public static boolean addActivity(Activity activity) {
        LogUtil.v(TAG, "addActivity: 添加 " + activity.getClass().getSimpleName() + " 对象");
        return activities.add(activity);
    }

    /**
     * 删除存在于 ActivityCollector 中的 activity。
     *
     * @param activity 需要删除的 Activity 对象
     * @return 如果 acitivity 存在于 ActivityCollector 中，返回 true，否则返回 false
     */
    public static boolean removeActivity(Activity activity) {
        LogUtil.v(TAG, "removeActivity: 移除 " + activity.getClass().getSimpleName() + " 对象");
        return activities.remove(activity);
    }

    /**
     * 销毁所有存在于 ActivityCollector 中的 Activity 对象。
     */
    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
                LogUtil.v(TAG, "finishAll: 终结 " + activity.getClass().getSimpleName() + " 对象");
            }
        }
    }
}

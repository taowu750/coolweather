package com.coolweather.android.util;

import com.coolweather.android.exception.NotCreatedObjectException;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 发送 Http 请求的工具类
 */

public class HttpUtil {

    private static final String TAG = LogUtil.TAG_HEAD + "HttpUtil";


    private HttpUtil() {
        throw new NotCreatedObjectException();
    }


    public static void sendRequest(String address, Callback callback) {
        LogUtil.v(TAG, "sendRequest: 地址-" + address);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }
}

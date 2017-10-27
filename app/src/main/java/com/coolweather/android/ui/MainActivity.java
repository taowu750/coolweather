package com.coolweather.android.ui;

import android.os.Bundle;

import com.coolweather.android.R;
import com.coolweather.android.app.BaseActivity;
import com.coolweather.android.util.WeatherInfoCache;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (WeatherInfoCache.hasWeatherInfoCache()) {
            WeatherActivity.activityStart(this);
        }
    }
}

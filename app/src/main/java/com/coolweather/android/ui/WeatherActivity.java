package com.coolweather.android.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.R;
import com.coolweather.android.app.BaseActivity;
import com.coolweather.android.app.BaseApplication;
import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.service.AutoUpdateService;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.LogUtil;
import com.coolweather.android.util.WeatherDataParseUtil;
import com.coolweather.android.util.WeatherInfoCache;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class WeatherActivity extends BaseActivity {

    private static final String INTENT_WEATHER_ID = "weather_id";

    private static final String TAG = LogUtil.TAG_HEAD + "WeatherActivity";


    private ScrollView weatherLayout;

    // weather_title 部分
    private TextView titleCity;
    private TextView titleUpdateTime;

    // weather_now 部分
    private TextView degreeText;
    private TextView weatherInfoText;

    // weather_forecast 部分
    private LinearLayout forecastLayout;

    // weather_aqi 部分
    private TextView aqiText;
    private TextView pm25Text;

    // weather_suggestion 部分
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;

    // 下拉刷新天气数据
    private SwipeRefreshLayout swipeRefresh;

    // 通过滑动菜单的方式，使得用户能够在滑动菜单中自由选择要查看的城市
    private DrawerLayout drawerLayout;
    private Button navButton;

    // WeatherActivity 的主界面，用来设置背景图
    @Deprecated
    private ViewGroup mainLayout;

    // 用来绑定服务
    @Deprecated
    private AutoUpdateService.WeatherBinder binder;
    @Deprecated
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder = (AutoUpdateService.WeatherBinder) iBinder;
            binder.setWeatherActivity(WeatherActivity.this);

            LogUtil.v(TAG, "ServiceConnection.onServiceConnected: 绑定了服务，并设置了活动对象");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    // 用来设置背景图
    private ImageView backgroundView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTransparent();
        setContentView(R.layout.activity_weather);

        // bindService 会调用 Service 的 onCreate 方法，但是不会调用 onStartCommand 方法
//        Intent bindIntent = new Intent(this, AutoUpdateService.class);
//        bindService(bindIntent, connection, BIND_AUTO_CREATE);
        initView();
        viewSetting();
        setBackground();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unbindService(connection);
    }

    /**
     * 当另一个活动需要启动 WeatherActivity 时，且没有缓存过天气数据时，需要调用这个方法。
     *
     * @param context   启动 WeatherActivity 的活动对象
     * @param weatherId 天气 id
     */
    public static void activityStart(Context context, String weatherId) {
        Intent intent = new Intent(context, WeatherActivity.class);
        intent.putExtra(INTENT_WEATHER_ID, weatherId);
        context.startActivity(intent);
    }

    /**
     * 当另一个活动需要启动 WeatherActivity 时，且缓存过天气数据时，需要调用这个方法。
     *
     * @param context 启动 WeatherActivity 的活动对象
     */
    public static void activityStart(Context context) {
        Intent intent = new Intent(context, WeatherActivity.class);
        context.startActivity(intent);
    }


    /**
     * 如果 Android 系统版本大于等于 5.0，就将它的系统状态栏设置为透明。
     */
    public void setStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            // 这两个标志表示活动的布局会显示在系统的状态栏上
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 关闭 WeatherActivity 的滑动菜单
     */
    public void closeDrawer() {
        drawerLayout.closeDrawers();
    }

    /**
     * 刷新天气数据
     */
    public void refreshData(String weatherId) {
        swipeRefresh.setRefreshing(true);
        requestWeather(weatherId);
    }

    /**
     * 设置 WeatherActivity 的背景图。需要注意的是，该方法只能在 Android 4.1 及以上的版本中才能够起作用。
     *
     * @param pic 背景图
     */
    @Deprecated
    public void setBackground(Drawable pic) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mainLayout.setBackground(pic);
            LogUtil.v(TAG, "setBackground: 设置背景图片成功");
        } else {
            LogUtil.w(TAG, "setBackground: 当前 Android 系统版本低于 4.1，无法使用该方法");
        }
    }

    /**
     * 设置 WeatherActivity 的背景图片。
     */
    public void setBackground() {
        String backgroundPic = WeatherInfoCache.getBingPic();
        if (backgroundPic != null) {
            Glide.with(this).load(backgroundPic).into(backgroundView);
        } else {
            loadBackgroundPic();
        }
    }


    /**
     * 初始化视图
     */
    private void initView() {
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);

        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);

        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);

        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);

        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_txt);

        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton = (Button) findViewById(R.id.nav_button);

        mainLayout = (ViewGroup) findViewById(R.id.weather_main_layout);

        backgroundView = (ImageView) findViewById(R.id.background_view);
    }

    /**
     * 对控件进行设置
     */
    private void viewSetting() {
        Intent intent = getIntent();
        String weatherId = intent.getStringExtra(INTENT_WEATHER_ID);
        if (weatherId != null) {
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);

            LogUtil.d(TAG, "viewSetting: 天气 id 从 MainActivity 传来-" + weatherId);
        } else {
            String weatherInfo = WeatherInfoCache.getWeatherInfo();
            if (weatherInfo != null) {
                Weather weather = WeatherDataParseUtil.handleWeatherResponse(weatherInfo);
                weatherId = weather.basic.weatherId;
                showWeatherInfo(weather);

                LogUtil.d(TAG, "viewSetting: 天气 id 从缓存中取出-" + weatherId);
            } else {
                Toast.makeText(this, "很遗憾，没有接收到任何对天气数据的请求", Toast.LENGTH_SHORT).show();

                LogUtil.w(TAG, "handleWeatherInfo: 出现 bug，WeatherActivity 没有被正常启动");
            }
        }

        swipeRefresh.setOnRefreshListener(() -> {
            String cacheWeatherId = getCacheWeatherId();
            if (cacheWeatherId != null) {
                refreshData(cacheWeatherId);
            } else {
                Toast.makeText(this, "刷新失败，请重新选择", Toast.LENGTH_SHORT).show();
            }
        });

        navButton.setOnClickListener((view) -> drawerLayout.openDrawer(GravityCompat.START));
    }

    /**
     * 根据天气 id 请求城市数据
     *
     * @param weatherId 天气 id
     */
    private void requestWeather(final String weatherId) {
        String weatherUrl = WeatherDataParseUtil.WEATHER_ADDRESS + "?cityid=" + weatherId + "&key=" +
                BaseApplication.HE_WEATHER_KEY;
        HttpUtil.sendRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(WeatherActivity.this, "获取天气信息失败！", Toast.LENGTH_SHORT).show());
                swipeRefresh.setRefreshing(false);

                LogUtil.e(TAG, "requestWeather: IOException-" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = WeatherDataParseUtil.handleWeatherResponse(responseText);
                runOnUiThread(() -> {
                    if (weather != null && WeatherDataParseUtil.WEATHER_STATUS_OK.equals(weather.status)) {
                        WeatherInfoCache.putWeatherInfo(responseText);

                        showWeatherInfo(weather);
                    } else {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败！", Toast.LENGTH_SHORT).show();
                    }
                    swipeRefresh.setRefreshing(false);
                });
            }
        });
    }

    /**
     * 将 weather 中的信息显示出来。
     *
     * @param weather Weather 对象
     */
    private void showWeatherInfo(Weather weather) {
        if (weather != null && WeatherDataParseUtil.WEATHER_STATUS_OK.equals(weather.status)) {
            String cityName = weather.basic.cityName;
            String updateTime = weather.basic.update.updateTime.split(" ")[1];
            String degree = weather.now.temperature + "度";
            String weatherInfo = weather.now.more.info;

            // weather_title 部分
            titleCity.setText(cityName);
            titleUpdateTime.setText(updateTime);

            // weather_now 部分
            degreeText.setText(degree);
            weatherInfoText.setText(weatherInfo);

            // weather_forecast 部分
            // 之所以采用这种动态添加 View 的方式，是因为预测的数据数量是变化的，无法知道有多少
            forecastLayout.removeAllViews();
            for (Forecast forecast : weather.forecastList) {
                View view = LayoutInflater.from(this).inflate(R.layout.weather_forecast_item, forecastLayout, false);
                TextView dateText = view.findViewById(R.id.date_text);
                TextView infoText = view.findViewById(R.id.info_text);
                TextView maxText = view.findViewById(R.id.max_text);
                TextView minText = view.findViewById(R.id.min_text);

                dateText.setText(forecast.date);
                infoText.setText(forecast.more.info);
                maxText.setText(forecast.temperature.max);
                maxText.setText(forecast.temperature.min);

                forecastLayout.addView(view);
            }

            // weather_aqi 部分
            if (weather.aqi != null) {
                aqiText.setText(weather.aqi.city.aqi);
                pm25Text.setText(weather.aqi.city.pm25);
            }

            // weather_suggestion 部分
            String comfort = "舒适度：" + weather.suggestion.comfort.info;
            String carWash = "洗车指数：" + weather.suggestion.carWash.info;
            String sport = "运动建议：" + weather.suggestion.sport.info;
            comfortText.setText(comfort);
            carWashText.setText(carWash);
            sportText.setText(sport);

            weatherLayout.setVisibility(View.VISIBLE);

            // 启动定时更新服务
            Intent intent = new Intent(this, AutoUpdateService.class);
            startService(intent);
        } else {
            Toast.makeText(WeatherActivity.this, "获取天气信息失败！", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadBackgroundPic() {
        HttpUtil.sendRequest(AutoUpdateService.BING_PIC_ADDR, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.w(TAG, "loadBackgroundPic: IOException-" + Log.getStackTraceString(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String picAddr = response.body().string();
                if (picAddr != null) {
                    WeatherInfoCache.putBingPic(picAddr);
                    runOnUiThread(() -> Glide.with(WeatherActivity.this).load(picAddr).into(backgroundView));

                    LogUtil.v(TAG, "loadBackgroundPic: 设置背景图片成功");
                } else {
                    onFailure(call, new IOException("获取必应图片路径失败！"));
                }
            }
        });
    }

    /**
     * 返回缓存的天气 id。
     *
     * @return 缓存的天气 id
     */
    private String getCacheWeatherId() {
        String weatherInfo = WeatherInfoCache.getWeatherInfo();
        if (weatherInfo != null) {
            Weather weather = WeatherDataParseUtil.handleWeatherResponse(weatherInfo);

            return weather.basic.weatherId;
        }

        return null;
    }
}

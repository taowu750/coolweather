package com.coolweather.android.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.R;
import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.util.AreaDataParseUtil;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.LogUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 用于遍历省市县数据信息的碎片
 */

public class ChooseAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;


    private static final String TAG = LogUtil.TAG_HEAD + "ChooseAreaFragment";


    private ProgressDialog progressDialog;

    private TextView titleText;
    private Button backButton;
    private ListView areaDataListView;

    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countyList;

    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中的城市
     */
    private City selectedCity;
    /**
     * 当前选中的级别
     */
    private int currentLevel = LEVEL_PROVINCE;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);

        titleText = view.findViewById(R.id.title_text);
        backButton = view.findViewById(R.id.back_button);
        areaDataListView = view.findViewById(R.id.area_data_list_view);

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        areaDataListView.setAdapter(adapter);

        LogUtil.v(TAG, "onCreateView: 创建视图成功");

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        areaDataListView.setOnItemClickListener((parent, view, position, id) -> {
            if (currentLevel == LEVEL_PROVINCE) {
                selectedProvince = provinceList.get(position);
                queryCities();
            } else if (currentLevel == LEVEL_CITY) {
                selectedCity = cityList.get(position);
                queryCounties();
            } else if (currentLevel == LEVEL_COUNTY) {
                Activity activity = getActivity();
                String weatherId = countyList.get(position).getWeatherId();
                if (activity instanceof MainActivity) {
                    WeatherActivity.activityStart(getActivity(), weatherId);
                    activity.finish();
                } else if (activity instanceof WeatherActivity) {
                    WeatherActivity weatherActivity = (WeatherActivity) activity;
                    weatherActivity.closeDrawer();
                    weatherActivity.refreshData(weatherId);
                }
            }

            LogUtil.d(TAG, "onActivityCreated: areaDataListView 点击事件响应");
        });
        backButton.setOnClickListener((view) -> {
            if (currentLevel == LEVEL_CITY) {
                queryProvinces();
            } else if (currentLevel == LEVEL_COUNTY) {
                queryCities();
            }

            LogUtil.d(TAG, "onActivityCreated: backButton 点击事件响应");
        });

        queryProvinces();
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            areaDataListView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;

            LogUtil.d(TAG, "queryProvinces: 从数据库查询省数据");
        } else {
            queryFromServer(AreaDataParseUtil.PROVINCE_DATA_ADDRESS, LEVEL_PROVINCE);

            LogUtil.d(TAG, "queryProvinces: 从网络查询省数据");
        }
    }

    /**
     * 查询全国所有的市，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        // ORM 类的域会变为小写格式
        cityList = DataSupport.where("provinceid=?", String.valueOf(selectedProvince.getId()))
                .find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            areaDataListView.setSelection(0);
            currentLevel = LEVEL_CITY;

            LogUtil.d(TAG, "queryCities: 从数据库查询城市数据");
        } else {
            queryFromServer(AreaDataParseUtil.PROVINCE_DATA_ADDRESS + "/" + selectedProvince.getProvinceCode(),
                    LEVEL_CITY);

            LogUtil.d(TAG, "queryCities: 从网路查询城市数据");
        }
    }

    /**
     * 查询全国所有的县，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid=?", String.valueOf(selectedCity.getId()))
                .find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            areaDataListView.setSelection(0);
            currentLevel = LEVEL_COUNTY;

            LogUtil.d(TAG, "queryCounties: 从数据库查询县城数据");
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            queryFromServer(AreaDataParseUtil.PROVINCE_DATA_ADDRESS + "/" + provinceCode + "/" + cityCode,
                    LEVEL_COUNTY);

            LogUtil.d(TAG, "queryCounties: 从网络查询县城数据");
        }
    }

    /**
     * 从服务器查询省市县数据，并将查找到的数据存入数据库中
     *
     * @param address 服务器地址
     * @param type    需要查询的数据类型
     */
    private void queryFromServer(String address, final int type) {
        showProgressDialog();
        HttpUtil.sendRequest(address, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // 通过 runOnUiThread() 方法回到主线程处理逻辑
                getActivity().runOnUiThread(() -> {
                    closeProgressDialog();
                    Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();

                    LogUtil.e(TAG, "queryFromServer: IOException-" + Log.getStackTraceString(e));
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if (type == LEVEL_PROVINCE) {
                    result = AreaDataParseUtil.handleProvinceResponse(responseText);
                } else if (type == LEVEL_CITY) {
                    result = AreaDataParseUtil.handleCityResponse(responseText, selectedProvince.getId());
                } else if (type == LEVEL_COUNTY) {
                    result = AreaDataParseUtil.handleCountyResponse(responseText, selectedCity.getId());
                }

                if (result) {
                    // 通过 runOnUiThread() 方法回到主线程处理逻辑
                    getActivity().runOnUiThread(() -> {
                        closeProgressDialog();
                        if (type == LEVEL_PROVINCE) {
                            queryProvinces();
                        } else if (type == LEVEL_CITY) {
                            queryCities();
                        } else if (type == LEVEL_COUNTY) {
                            queryCounties();
                        }
                    });
                }
            }
        });
    }

    /**
     * 显示进度条对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度条对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}

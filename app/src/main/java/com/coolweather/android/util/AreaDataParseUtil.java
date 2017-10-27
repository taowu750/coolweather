package com.coolweather.android.util;

import android.text.TextUtils;
import android.util.Log;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.exception.NotCreatedObjectException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用来解析服务器返回的省市县数据，并将结果数据存入数据库中。
 */

public class AreaDataParseUtil {

    public static final String PROVINCE_DATA_ADDRESS = "http://guolin.tech/api/china";


    private static final String TAG = LogUtil.TAG_HEAD + "AreaDataParseUtil";

    private AreaDataParseUtil() {
        throw new NotCreatedObjectException();
    }


    /**
     * 解析处理服务器返回的省份数据并将结果保存在数据库中。
     *
     * @param response 服务器返回的信息
     * @return 如果 response 不为空，返回 true，否则返回 false
     */
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();

                    LogUtil.v(TAG, "handleProvinceResponse: 省份名-" + province.getProvinceName() + ", 省份代码-" +
                            province.getProvinceCode());
                }

                return true;
            } catch (JSONException e) {
                LogUtil.e(TAG, "handleProvinceResponse: JSONException-" + Log.getStackTraceString(e));
            }
        }

        return false;
    }

    /**
     * 解析处理服务器返回的城市数据并将结果保存在数据库中。
     *
     * @param response 服务器返回的信息
     * @param provinceId 包含城市的省份 id
     * @return 如果 response 不为空，返回 true，否则返回 false
     */
    public static boolean handleCityResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();

                    LogUtil.v(TAG, "handleCityResponse: 城市名-" + city.getCityName() + ", 城市代码-" + city.getCityCode());
                }

                return true;
            } catch (JSONException e) {
                LogUtil.e(TAG, "handleCityResponse: JSONException-" + e.getMessage());
            }
        }

        return false;
    }

    /**
     * 解析处理服务器返回的县镇数据并将结果保存在数据库中。
     *
     * @param response 服务器返回的信息
     * @param cityId 包含县镇的城市 id
     * @return 如果 response 不为空，返回 true，否则返回 false
     */
    public static boolean handleCountyResponse(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCounty = new JSONArray(response);
                for (int i = 0; i < allCounty.length(); i++) {
                    JSONObject countyObject = allCounty.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();

                    LogUtil.v(TAG, "handleCountyResponse: 县镇名-" + county.getCountyName() + ", 天气代码-" + county.getWeatherId());
                }

                return true;
            } catch (JSONException e) {
                LogUtil.e(TAG, "handleCountyResponse: JSONException-" + e.getMessage());
            }
        }

        return false;
    }
}

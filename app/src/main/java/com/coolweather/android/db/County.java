package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * 县的 ORM。
 */

public class County extends DataSupport {

    /**
     * 每个实体类都必须有的字段
     */
    private int id;
    /**
     * 县的名称
     */
    private String countyName;
    /**
     * 和风天气的天气信息访问 id
     */
    private String weatherId;
    /**
     * 所属城市的 id
     */
    private int cityId;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}

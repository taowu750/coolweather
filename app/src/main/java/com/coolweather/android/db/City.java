package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * 表示城市的 ORM。
 */

public class City extends DataSupport {

    /**
     * 每个实体类都应该有的字段
     */
    private int id;
    /**
     * 城市名称
     */
    private String cityName;
    /**
     * 城市代号
     */
    private int cityCode;
    /**
     * 所属省份的 id
     */
    private int provinceId;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}

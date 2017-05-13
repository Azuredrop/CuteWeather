package com.azuredrop.cuteweather.db;

/**
 * Created by AzureDrop on 2017/5/9.
 */

/**
 * 城市
 * @author AzureDrop
 */
public class City extends BaseArea {
    /**
     * 所属省的ID
     */
    private int provinceId;
    /**
     * 天气编码
     */
    private String weatherCode;

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public String getWeatherCode() {
        return weatherCode;
    }

    public void setWeatherCode(String weatherCode) {
        this.weatherCode = weatherCode;
    }
}

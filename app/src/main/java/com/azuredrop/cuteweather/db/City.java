package com.azuredrop.cuteweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by AzureDrop on 2017/5/9.
 */

/**
 * 城市类，用于保存市相关的信息
 * @author AzureDrop
 */
public class City extends DataSupport {
    /**
     * ID
     */
    private int id;
    /**
     * 城市名称
     */
    private String cityName;
    /**
     * 城市编码
     */
    private int cityCode;
    /**
     * 所属省的ID
     * {@linkplain com.azuredrop.cuteweather.db.Province 省}
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

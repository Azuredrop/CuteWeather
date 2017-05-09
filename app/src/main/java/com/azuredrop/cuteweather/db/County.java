package com.azuredrop.cuteweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by AzureDrop on 2017/5/9.
 */

/**
 * 县类，用于保存市相关的信息
 * @author AzureDrop
 */
public class County extends DataSupport {
    /**
     * ID
     */
    private int id;
    /**
     * 县名称
     */
    private String countyName;
    /**
     * 县对应的天气ID
     */
    private String weatherId;
    /**
     * 县所属市的ID
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

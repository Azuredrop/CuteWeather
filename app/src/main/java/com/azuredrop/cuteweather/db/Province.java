package com.azuredrop.cuteweather.db;

/**
 * Created by AzureDrop on 2017/5/9.
 */

/**
 * 省份
 * @author AzureDrop
 */
public class Province extends BaseArea {
    /**
     * 所属国家Id
     */
    private int countryId;

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }
}

package com.azuredrop.cuteweather.db;

/**
 * Created by AzureDrop on 2017/5/13.
 */

/**
 * 国家
 * @author AzureDrop
 */
public class Country extends BaseArea {
    /**
     * 所属洲Id
     */
    private int continentId;

    public int getContinentId() {
        return continentId;
    }

    public void setContinentId(int continentId) {
        this.continentId = continentId;
    }
}

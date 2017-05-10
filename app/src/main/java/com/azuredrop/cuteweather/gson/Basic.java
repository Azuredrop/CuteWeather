package com.azuredrop.cuteweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by AzureDrop on 2017/5/10.
 */

/**
 * 基本信息
 * @author AzureDrop
 */
public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update {
        @SerializedName("loc")
        public String updateTime;
    }
}

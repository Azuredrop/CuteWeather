package com.azuredrop.cuteweather.gson;

/**
 * Created by AzureDrop on 2017/5/10.
 */

/**
 * 空气质量
 * @author AzureDrop
 */
public class AQI {
    public AQICity city;

    public class AQICity {
        public String aqi;

        public String pm25;
    }
}

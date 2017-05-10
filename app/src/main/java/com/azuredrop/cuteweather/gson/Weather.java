package com.azuredrop.cuteweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by AzureDrop on 2017/5/10.
 */

/**
 * 天气情况
 * @author AzureDrop
 */
public class Weather {
    public String status;

    public Basic basic;

    public AQI aqi;

    public Now now;

    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}

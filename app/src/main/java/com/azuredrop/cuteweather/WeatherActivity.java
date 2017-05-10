package com.azuredrop.cuteweather;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.azuredrop.cuteweather.gson.Forecast;
import com.azuredrop.cuteweather.gson.Weather;
import com.azuredrop.cuteweather.util.HttpUtil;
import com.azuredrop.cuteweather.util.Utility;

import java.io.IOException;
import java.io.PipedReader;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    /**
     * Intent中WeatherID
     */
    public static final String WEATHER_ID = "weather_id";
    /**
     * 存放进SharedPreferences里返回的城市数据
     */
    public static final String SHARED_PREF_WEATHER = "weather";

    /**
     * 申请的APIKey
     */
    private static final String HEFENG_WEATHER_APIKEY = "f28feb687e594050bc145b72e92e298a";

    // 控件
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        // 初始化控件
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);

        // 填充数据
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = preferences.getString(SHARED_PREF_WEATHER, null);
        if (weatherString != null) {
            // 有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        } else {
            // 无缓存时去服务器查询天气
            String weatherId = getIntent().getStringExtra(WEATHER_ID);
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
    }

    /**
     * 根据天气ID请求城市天气信息
     *
     * @param weatherId 天气ID
     */
    public void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=" + HEFENG_WEATHER_APIKEY;

        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, R.string.err_fetch_weather_failed, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString(SHARED_PREF_WEATHER, responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, R.string.err_fetch_weather_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    /**
     * 处理并展示Weather实体类中的数据
     *
     * @param weather eather实体类
     */
    private void showWeatherInfo(Weather weather) {
        // 城市名
        titleCity.setText(weather.basic.cityName);
        // 更新时间
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        titleUpdateTime.setText(updateTime);
        // 温度
        String degree = weather.now.temperature + getString(R.string.centigrade_symbol);
        degreeText.setText(degree);
        // 天气情况
        weatherInfoText.setText(weather.now.more.info);

        forecastLayout.removeAllViews();
        for (Forecast forcast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forcast_item, forecastLayout, false);
            // 日期
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            dateText.setText(forcast.date);
            // 信息
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            infoText.setText(forcast.more.info);
            // 最高温度
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            maxText.setText(forcast.temperature.max);
            // 最低温度
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            minText.setText(forcast.temperature.min);

            forecastLayout.addView(view);
        }

        // 空气质量
        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        // 建议
        String comfort = getString(R.string.suggestion_comfort) + weather.suggestion.comfort.info;
        comfortText.setText(comfort);
        String carWash = getString(R.string.suggestion_car_wash) + weather.suggestion.carWash.info;
        carWashText.setText(carWash);
        String sport = getString(R.string.suggestion_sport) + weather.suggestion.sport.info;
        sportText.setText(sport);

        weatherLayout.setVisibility(View.VISIBLE);
    }
}

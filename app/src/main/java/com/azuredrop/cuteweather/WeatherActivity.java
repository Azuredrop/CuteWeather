package com.azuredrop.cuteweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.azuredrop.cuteweather.gson.Forecast;
import com.azuredrop.cuteweather.gson.Weather;
import com.azuredrop.cuteweather.service.AutoUpdateService;
import com.azuredrop.cuteweather.util.HttpUtil;
import com.azuredrop.cuteweather.util.Utility;
import com.bumptech.glide.Glide;

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
     * 存放进SharedPreferences里背景图片
     */
    public static final String SHARED_PREF_BG_IMG = "background_img";

    /**
     * 申请的APIKey
     */
    public static final String HEFENG_WEATHER_APIKEY = "f28feb687e594050bc145b72e92e298a";

    /**
     * 当前城市的WeatherId
     */
    private String mWeatherId;

    // 控件
    private ImageView backgoundImg;
    public DrawerLayout drawerLayout;
    public SwipeRefreshLayout swipeRefresh;
    private ScrollView weatherLayout;
    private Button navButton;
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

        // 使背景扩展到系统状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_weather);

        // 初始化控件
        backgoundImg = (ImageView) findViewById(R.id.bg_img);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // SwipeRefreshLayout
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        navButton = (Button) findViewById(R.id.nav_button);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
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
        // 填充背景图片
        String backgroundPic = preferences.getString(SHARED_PREF_BG_IMG, null);
        if (backgroundPic != null) {
            Glide.with(this).load(backgroundPic).into(backgoundImg);
        } else {
            loadBackgoundPic();
        }
        // 填充天气数据
        String weatherString = preferences.getString(SHARED_PREF_WEATHER, null);
        if (weatherString != null) {
            // 有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            // 无缓存时去服务器查询天气
            mWeatherId = getIntent().getStringExtra(WEATHER_ID);
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
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
                        swipeRefresh.setRefreshing(false);
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
                            mWeatherId = weather.basic.weatherId;

                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString(SHARED_PREF_WEATHER, responseText);
                            editor.apply();

                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, R.string.err_fetch_weather_failed, Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });

        // 请求天气信息的时候，刷新背景图片
        loadBackgoundPic();
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

        // 启动自动更新服务
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    /**
     * 加载背景图片
     */
    private void loadBackgoundPic(){
        String requestPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bgPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString(SHARED_PREF_BG_IMG, bgPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bgPic).into(backgoundImg);
                    }
                });
            }
        });
    }
}

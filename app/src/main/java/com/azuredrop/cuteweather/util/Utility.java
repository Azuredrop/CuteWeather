package com.azuredrop.cuteweather.util;

/**
 * Created by AzureDrop on 2017/5/9.
 */

import android.text.TextUtils;

import com.azuredrop.cuteweather.db.Continent;
import com.azuredrop.cuteweather.db.Country;
import com.azuredrop.cuteweather.db.Province;
import com.azuredrop.cuteweather.db.City;
import com.azuredrop.cuteweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.List;

/**
 *
 * @author AzureDrop
 */
public class Utility {
    /**
     * 解析和处理服务器返回的城市数据
     *
     * @param response 服务器返回的数据
     * @return 是否处理成功
     */
    public static boolean handleCityResponse(String response) {
        if (TextUtils.isEmpty(response)) {
            return false;
        }

        try {
            Continent continent = new Continent();
            continent.setNameZh("亚洲");
            continent.setNameEn("Asia");
            continent.save();

            JSONArray allProvinces = new JSONArray(response);
            for (int i = 0; i < allProvinces.length(); i++) {
                JSONObject object = allProvinces.getJSONObject(i);

                // 获取洲Id
                List<Continent> continentList = DataSupport.where("nameZh = ?", "亚洲").find(Continent.class);
                if (continentList.size() == 0)
                    return false;
                int continentId = continentList.get(0).getId();

                // 保存国家
                List<Country> countryList = DataSupport
                        .where("continentId = ? and nameZh = ?", String.valueOf(continentId), object.getString("countryZh"))
                        .find(Country.class);
                if (countryList.size() == 0) {
                    Country country = new Country();
                    country.setNameZh(object.getString("countryZh"));
                    country.setNameEn(object.getString("countryEn"));
                    country.setContinentId(continentId);
                    country.save();
                }
                // 获取国家Id
                countryList = DataSupport
                        .where("continentId = ? and nameZh = ?", String.valueOf(continentId), object.getString("countryZh"))
                        .find(Country.class);
                if (countryList.size() == 0)
                    return false;
                int countryId = countryList.get(0).getId();

                // 保存省
                List<Province> provinceList = DataSupport
                        .where("countryId = ? and nameZh = ?", String.valueOf(countryId), object.getString("provinceZh"))
                        .find(Province.class);
                if (provinceList.size() == 0) {
                    Province province = new Province();
                    province.setNameZh(object.getString("provinceZh"));
                    province.setNameEn(object.getString("provinceEn"));
                    province.setCountryId(countryId);
                    province.save();
                }
                // 获取省Id
                provinceList = DataSupport
                        .where("countryId = ? and nameZh = ?", String.valueOf(countryId), object.getString("provinceZh"))
                        .find(Province.class);
                if (provinceList.size() == 0)
                    return false;
                int provinceId = provinceList.get(0).getId();

                // 保存市
                List<City> cityList = DataSupport
                        .where("provinceId = ? and nameZh = ?", String.valueOf(provinceId), object.getString("cityZh"))
                        .find(City.class);
                if (cityList.size() == 0) {
                    City city = new City();
                    city.setNameZh(object.getString("cityZh"));
                    city.setNameEn(object.getString("cityEn"));
                    city.setWeatherCode(object.getString("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
            }

            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 获取指定地点的天气编码
     *
     * @param country  地点所在国家
     * @param province 地点所在省
     * @param city     地点所在市
     * @return 指定地点的天气编码
     */
    public static String getWeatherCode(String country, String province, String city) {
        if (province.lastIndexOf("省") > 0)
            province = province.substring(0, province.lastIndexOf("省"));
        if (city.lastIndexOf("市") > 0)
            city = city.substring(0, city.lastIndexOf("市"));

        // 获取洲Id
        List<Continent> continentList = DataSupport.where("nameZh = ?", "亚洲").find(Continent.class);
        if (continentList.size() == 0)
            return null;

        // 获取国家Id
        List<Country> countryList = DataSupport
                .where("continentId = ? and nameZh = ?", String.valueOf(continentList.get(0).getId()), country)
                .find(Country.class);
        if (countryList.size() == 0)
            return null;

        // 获取省Id
        List<Province> provinceList = DataSupport
                .where("countryId = ? and nameZh like ?", String.valueOf(countryList.get(0).getId()), "%" + province + "%")
                .find(Province.class);
        if (provinceList.size() == 0)
            return null;

        // 获取市
        List<City> cityList = DataSupport
                .where("provinceId = ? and nameZh like ?", String.valueOf(provinceList.get(0).getId()), "%" + city + "%")
                .find(City.class);
        if (cityList.size() == 0) {
            return null;
        }

        return cityList.get(0).getWeatherCode();
    }

    /**
     * 讲返回的JSON数据解析成Weather实体类
     *
     * @param response 返回的JSON数据
     * @return Weather实体类
     */
    public static Weather handleWeatherResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather5");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, Weather.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

package com.azuredrop.cuteweather;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.azuredrop.cuteweather.db.BaseArea;
import com.azuredrop.cuteweather.db.City;
import com.azuredrop.cuteweather.db.Continent;
import com.azuredrop.cuteweather.db.Country;
import com.azuredrop.cuteweather.db.Province;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AzureDrop on 2017/5/9.
 */

public class ChooseAreaFragment extends Fragment {
    // 标识当前级别
    private enum AreaLevel {
        LEVEL_CONTINENT,
        LEVEL_COUNTRY,
        LEVEL_PROVINCE,
        LEVEL_CITY
    }

    // 控件
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;

    /**
     * ListView适配器
     */
    private ArrayAdapter<String> adapter;

    /**
     * ListView绑定的数据
     */
    private List<String> dataList = new ArrayList<>();
    /**
     * 当前选中的级别
     */
    private AreaLevel currentLevel = AreaLevel.LEVEL_CONTINENT;
    /**
     * 区域堆栈
     */
    private List<BaseArea> areaList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_area, container, false);
        // TitleText
        titleText = (TextView) view.findViewById(R.id.title_text);
        // BackButtion
        backButton = (Button) view.findViewById(R.id.back_button);
        // ListView
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (currentLevel) {
                    case LEVEL_CONTINENT:
                        // 获取洲Id
                        List<Continent> continentList = DataSupport
                                .where("nameZh = ?", dataList.get(position))
                                .find(Continent.class);
                        if (continentList.size() == 0)
                            return;
                        else
                            areaList.add(continentList.get(0));

                        // 查询洲下的国家
                        queryCountries();
                        break;
                    case LEVEL_COUNTRY:
                        // 获取洲Id
                        String continentId = String.valueOf(areaList.get(areaList.size() - 1).getId());
                        // 获取点击的国家Id
                        List<Country> countryList = DataSupport
                                .where("continentId = ? and nameZh = ? ", continentId, dataList.get(position))
                                .find(Country.class);
                        if (countryList.size() == 0)
                            return;
                        else
                            areaList.add(countryList.get(0));

                        // 查询国家下的省
                        queryProvinces();
                        break;
                    case LEVEL_PROVINCE:
                        // 获取国家Id
                        String countryId = String.valueOf(areaList.get(areaList.size() - 1).getId());
                        // 获取点击的省Id
                        List<Province> provinceList = DataSupport
                                .where("countryId = ? and nameZh = ? ", countryId, dataList.get(position))
                                .find(Province.class);
                        if (provinceList.size() == 0)
                            return;
                        else
                            areaList.add(provinceList.get(0));

                        // 查下省下的市
                        queryCities();
                        break;
                    case LEVEL_CITY:
                        // 获取省Id
                        String provinceId = String.valueOf(areaList.get(areaList.size() - 1).getId());
                        // 获取点击的省Id
                        List<City> cityList = DataSupport
                                .where("provinceId = ? and nameZh = ? ", provinceId, dataList.get(position))
                                .find(City.class);
                        if (cityList.size() == 0)
                            return;

                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        activity.requestWeather(cityList.get(0).getWeatherCode());
                        break;
                    default:
                        break;
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 根据当前级别，重新填充列表
                if (currentLevel == AreaLevel.LEVEL_CITY) {
                    areaList.remove(areaList.size() - 1);

                    queryProvinces();
                } else if (currentLevel == AreaLevel.LEVEL_PROVINCE) {
                    areaList.remove(areaList.size() - 1);

                    queryCountries();
                } else if (currentLevel == AreaLevel.LEVEL_COUNTRY) {
                    areaList.clear();

                    queryContinents();
                }
            }
        });

        // 初始填充洲级数据
        queryContinents();
    }

    /**
     * 查询所有洲级数据
     */
    private void queryContinents() {
        titleText.setText("地球");
        backButton.setVisibility(View.GONE);

        dataList.clear();
        dataList.add("亚洲");
        adapter.notifyDataSetChanged();
        listView.setSelection(0);
        currentLevel = AreaLevel.LEVEL_CONTINENT;
    }

    /**
     * 查询所有国家级数据
     */
    private void queryCountries() {
        BaseArea baseArea = areaList.get(areaList.size() - 1);
        if (!(baseArea instanceof Continent)) {
            return;
        }

        titleText.setText(baseArea.getNameZh());
        backButton.setVisibility(View.VISIBLE);

        // 获取市级数据
        List<Country> countryList = DataSupport.where("continentId = ?", String.valueOf(baseArea.getId())).find(Country.class);
        if (countryList.size() > 0) {
            dataList.clear();
            for (Country country : countryList) {
                dataList.add(country.getNameZh());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = AreaLevel.LEVEL_COUNTRY;
        }
    }

    /**
     * 查询所有省级数据
     */
    private void queryProvinces() {
        BaseArea baseArea = areaList.get(areaList.size() - 1);
        if (!(baseArea instanceof Country)) {
            return;
        }

        titleText.setText(baseArea.getNameZh());
        backButton.setVisibility(View.VISIBLE);

        // 获取省级数据
        List<Province> provinceList = DataSupport.where("countryId = ?", String.valueOf(baseArea.getId())).find(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getNameZh());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = AreaLevel.LEVEL_PROVINCE;
        }
    }

    /**
     * 查询所有市级数据
     */
    private void queryCities() {
        BaseArea baseArea = areaList.get(areaList.size() - 1);
        if (!(baseArea instanceof Province)) {
            return;
        }

        titleText.setText(baseArea.getNameZh());
        backButton.setVisibility(View.VISIBLE);

        // 获取市级数据
        List<City> cityList = DataSupport.where("provinceId = ?", String.valueOf(baseArea.getId())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getNameZh());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = AreaLevel.LEVEL_CITY;
        }
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}

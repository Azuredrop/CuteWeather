package com.azuredrop.cuteweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by AzureDrop on 2017/5/9.
 */

/**
 * 省份类，用于保存省份相关的信息
 * @author AzureDrop
 */
public class Province extends DataSupport {
    /**
     * ID
     */
    private int id;
    /**
     * 省份名称
     */
    private String provinceName;
    /**
     * 省份代号
     */
    private int provinceCode;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }
    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }
    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}

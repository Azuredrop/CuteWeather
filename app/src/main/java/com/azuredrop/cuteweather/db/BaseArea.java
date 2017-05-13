package com.azuredrop.cuteweather.db;

/**
 * Created by AzureDrop on 2017/5/13.
 */

import org.litepal.crud.DataSupport;

/**
 * 地区基类
 * @author AzureDrop
 */
public abstract class BaseArea extends DataSupport {
    /**
     * ID
     */
    private int id;
    /**
     * 中文名称
     */
    private String nameZh;
    /**
     * 英文名称
     */
    private String nameEn;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameZh() {
        return nameZh;
    }

    public void setNameZh(String nameZh) {
        this.nameZh = nameZh;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }
}

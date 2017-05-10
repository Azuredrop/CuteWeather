package com.azuredrop.cuteweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by AzureDrop on 2017/5/10.
 */

/**
 * 当前信息
 * @author AzureDrop
 */
public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More {
        @SerializedName("txt")
        public String info;
    }
}

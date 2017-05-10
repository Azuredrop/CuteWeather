package com.azuredrop.cuteweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by AzureDrop on 2017/5/10.
 */

/**
 * 根据天气相关的建议
 * @author AzureDrop
 */
public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    public Sport sport;

    public class Comfort {
        @SerializedName("txt")
        public String info;
    }

    public class CarWash {
        @SerializedName("txt")
        public String info;
    }

    public class Sport {
        @SerializedName("txt")
        public String info;
    }
}

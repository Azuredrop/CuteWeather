package com.azuredrop.cuteweather.util;

/**
 * Created by AzureDrop on 2017/5/9.
 */

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Http类
 * @author AzureDrop
 */
public class HttpUtil {
    /**
     * 发送Http请求
     *
     * @param address  url地址
     * @param callback 用来处理响应的回调
     */
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        okHttpClient.newCall(request).enqueue(callback);
    }
}

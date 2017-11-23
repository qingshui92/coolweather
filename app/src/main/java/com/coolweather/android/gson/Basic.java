package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by danshui on 2017/11/22.
 */

public class Basic {//用@SerializedName注解的方式，让JSON字段与Java字段之间建立映射关系

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;
    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}

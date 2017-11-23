package com.coolweather.android.gson;

/**
 * Created by danshui on 2017/11/22.
 */

public class AQI {//AQI是指“空气质量指数”

    public AQICity city;

    public class AQICity{
        public String aqi;
        public String pm25;
    }
}

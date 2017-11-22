package com.coolweather.android.util;

import android.text.TextUtils;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by danshui on 2017/11/21.
 */

public class Utility {
    //解析和处理服务器返回的省级数据

    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            //用JSONArray， JSONObject把数据解析出来，然后通过save（）方法把数据存储到实体类对象上
            try {
                JSONArray allProvinces = new JSONArray(response);//一个数组
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);//通过getJSONObject()获取到JSONObject对象
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));//通过getString(参数是string类型，不用转换)把
                    province.setProvinceCode(provinceObject.getInt("id"));//getInt(参数是String类型)，getInt()把别的类型转成Int
                    province.save();//save()方法是DataSupport的，该行代码表示把“数据添加到数据库中相对应的那个表中”也就是province表中
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    //解析和处理服务器返回的市级数据

    public static boolean handleCityResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);

                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();

                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
    //解析和处理服务器返回的县级数据

    public static boolean handleCountyResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allCounties=new JSONArray(response);
                for(int i=0;i<allCounties.length();i++){
                    JSONObject countyObject = allCounties.getJSONObject(i);

                    County county=new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();

                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }

        }
        return false;
    }
}
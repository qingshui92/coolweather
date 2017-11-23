package com.coolweather.android;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;

  private ImageView bingPicImg;

  public SwipeRefreshLayout swipeRefresh;

  public DrawerLayout drawerLayout;
  private Button navButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        //实现背景图和状态栏融合的效果，如下。  还有一个更复杂的方法也可以实现，12.7.2节，引入Design Support库，嵌套CoordinatorLayout,AppBarLayout,CollapsingToolbarLayout等

        if (Build.VERSION.SDK_INT>=21){//Android系统5.0及以上，会执行后面的代码

           View decorView= getWindow().getDecorView();//拿到当前活动的DecorView

                  //调用DecorView的setSystemUiVisibility（）方法，改变系统UI的显示，以下两个值表示“活动的布局会显示在状态栏上”
           decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
           getWindow().setStatusBarColor(Color.TRANSPARENT);//将状态栏设置为透明色
        }

        //初始化各控件
        weatherLayout=(ScrollView)findViewById(R.id.weather_layout);
        titleCity=(TextView)findViewById(R.id.title_city);
        titleUpdateTime=(TextView)findViewById(R.id.title_update_time);
        degreeText=(TextView)findViewById(R.id.degree_text);
        weatherInfoText=(TextView)findViewById(R.id.weather_info_text);
        forecastLayout=(LinearLayout)findViewById(R.id.forecast_layout);
        aqiText=(TextView)findViewById(R.id.aqi_text);
        pm25Text=(TextView)findViewById(R.id.pm25_text);
        comfortText=(TextView)findViewById(R.id.comfort_text);
        carWashText=(TextView)findViewById(R.id.car_wash_text);
        sportText=(TextView)findViewById(R.id.sport_text);

        bingPicImg=(ImageView)findViewById(R.id.bing_pic_img);

        swipeRefresh=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);//利用setColorSchemeResources（）方法，设置下拉进度条的颜色

        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        navButton=(Button)findViewById(R.id.nav_button);//因为activity_weather布局因，引入了碎片布局


        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);//获取SharedPreferences的实例
        String weatherString=prefs.getString("weather",null);//get数据，读取数据

        final String weatherId;

       String bingPic= prefs.getString("bing_pic",null);
       if(bingPic!=null){
           Glide.with(this).load(bingPic).into(bingPicImg);
       }else {
           loadBingPic();
       }

        if(weatherString!=null){
            //有缓存时，直接解析天气数据
            Weather weather= Utility.handleWeatherResponse(weatherString);

            weatherId=weather.basic.weatherId;

            showWeatherInfo(weather);
        }else {
            //无缓存时，去服务器查询天气

            weatherId=getIntent().getStringExtra("weather_id");

            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);//打开滑动菜单
            }
        });
    }

    //根据weatherId请求对应城市的天气信息

    public void requestWeather(final String weatherId){

        String weatherUrl="http://guolin.tech/api/weather?cityid="+weatherId+"&key=bc0418b57b2d4918819d3974ac1285d9";

        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {//new Callback()回掉处理
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();

                        swipeRefresh.setRefreshing(false);//false表示刷新事件结束，该方法表示隐藏“刷新进度条”
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String responseText=response.body().string();

                final Weather weather=Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null&&"ok".equals(weather.status)){
                            //存储数据
                           SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                        editor.putString("weather",responseText);
                        editor.apply();
                        showWeatherInfo(weather);
                        }else {
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }

                        swipeRefresh.setRefreshing(false);//false表示刷新事件结束，该方法表示隐藏“刷新进度条”
                    }
                });
            }
        });
        loadBingPic();
    }
    //处理并展示Weather实体类中的数据

    private void showWeatherInfo(Weather weather) {

        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;

        titleCity.setText(cityName);
        titleUpdateTime.setText(degree);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);

        forecastLayout.removeAllViews();

        for (Forecast forecast : weather.forecastList) {
            //inflate（）方法：3参，值为false时，2参不是一参的父布局，但1参 生成布局的时候，会参考2参，把2参当作是父布局一样
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);

            TextView dateText = (TextView)view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView)view.findViewById(R.id.max_text);
            TextView minText=(TextView)view.findViewById(R.id.min_text);

            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);

            forecastLayout.addView(view);
        }
        if(weather.aqi!=null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }

        String comfort="舒适度"+weather.suggestion.comfort.info;
        String carWash="洗车指数"+weather.suggestion.carWash.info;
        String sport="运动指数"+weather.suggestion.sport.info;

        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);

        weatherLayout.setVisibility(View.VISIBLE);
    }

    //加载“必应”的每日一图

    private void loadBingPic(){

        String requestBingPic="http://guolin.tech/api/bing_pic";
        //HttpUtil.sendOkHttpRequest（）获取背景图的链接
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String bingPic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
            editor.putString("bing_pic",bingPic);//并将背景图的链接缓存到SharedPreferences当中
            editor.apply();
            runOnUiThread(new Runnable() {//通过runOnUiThread（）方法，切换到主线程
                @Override
                public void run() {
                    Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                }
            });
            }
        });
    }

    }
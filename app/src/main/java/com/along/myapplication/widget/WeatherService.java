package com.along.myapplication.widget;


import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.along.myapplication.R;
import com.amap.api.services.weather.LocalDayWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WeatherService extends Service implements WeatherSearch.OnWeatherSearchListener {
    private String weather;
    private String reporttime1;
    private String Temperature;
    private String wind;
    private String humidity;
    private String totalWeather;
    private WeatherSearchQuery mquery;
    private WeatherSearch mweathersearch;
    private LocalWeatherLive weatherlive;
    private LocalWeatherForecast weatherforecast;
    private List<LocalDayWeatherForecast> forecastlist = null;
    private String cityname = "上海市";//天气搜索的城市，可以写名称或adcode；
    private Timer mTimer;


    public WeatherService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        searchliveweather();
        searchforcastsweather();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateViews();
            }
        }, 0, 10000);
    }

    private void updateViews() {

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.weather_widget);
        searchliveweather();
        searchforcastsweather();
        remoteViews.setTextViewText(R.id.weather, totalWeather);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        ComponentName componentName = new ComponentName(getApplicationContext(), WeatherWidget.class);
        appWidgetManager.updateAppWidget(componentName, remoteViews);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void init() {
//        TextView city =(TextView)findViewById(R.id.city);
//        city.setText(cityname);
//        forecasttv=(TextView)findViewById(R.id.forecast);
//        reporttime1 = (TextView)findViewById(R.id.reporttime1);
//        reporttime2 = (TextView)findViewById(R.id.reporttime2);
//         weather = (TextView)findViewById(R.id.weather);
//        Temperature = (TextView)findViewById(R.id.temp);
//        wind=(TextView)findViewById(R.id.wind);
//        humidity = (TextView)findViewById(R.id.humidity);
    }

    private void searchforcastsweather() {
        mquery = new WeatherSearchQuery(cityname, WeatherSearchQuery.WEATHER_TYPE_FORECAST);//检索参数为城市和天气类型，实时天气为1、天气预报为2
        mweathersearch = new WeatherSearch(this);
        mweathersearch.setOnWeatherSearchListener(this);
        mweathersearch.setQuery(mquery);
        mweathersearch.searchWeatherAsyn(); //异步搜索
    }

    private void searchliveweather() {
        mquery = new WeatherSearchQuery(cityname, WeatherSearchQuery.WEATHER_TYPE_LIVE);//检索参数为城市和天气类型，实时天气为1、天气预报为2
        mweathersearch = new WeatherSearch(this);
        mweathersearch.setOnWeatherSearchListener(this);
        mweathersearch.setQuery(mquery);
        mweathersearch.searchWeatherAsyn(); //异步搜索
    }

    /**
     * 实时天气查询回调
     */
    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult weatherLiveResult, int rCode) {
        if (rCode == 1000) {
            if (weatherLiveResult != null && weatherLiveResult.getLiveResult() != null) {
                weatherlive = weatherLiveResult.getLiveResult();
                reporttime1 = (weatherlive.getReportTime() + "发布");//发布时间
                weather = weatherlive.getWeather();//天气
                Temperature = (weatherlive.getTemperature() + "°");//气温
                wind = (weatherlive.getWindDirection() + "风  " + weatherlive.getWindPower() + "级");//风力
                humidity = ("湿度     " + weatherlive.getHumidity() + "%");//湿度
                totalWeather = reporttime1 + "\n" + weather + "\n" + "气温：" + Temperature + "\n" + wind + "\n" + humidity;
            } else {
//                ToastUtil.show(WeatherSearchActivity.this, R.string.no_result);
            }
        } else {
//            ToastUtil.showerror(WeatherSearchActivity.this, rCode);
        }
    }

    /**
     * 天气预报查询结果回调
     */
    @Override
    public void onWeatherForecastSearched(
            LocalWeatherForecastResult weatherForecastResult, int rCode) {
        if (rCode == 1000) {
            if (weatherForecastResult != null && weatherForecastResult.getForecastResult() != null
                    && weatherForecastResult.getForecastResult().getWeatherForecast() != null
                    && weatherForecastResult.getForecastResult().getWeatherForecast().size() > 0) {
                weatherforecast = weatherForecastResult.getForecastResult();
                forecastlist = weatherforecast.getWeatherForecast();
                fillforecast();

            } else {
//                ToastUtil.show(WeatherSearchActivity.this, R.string.no_result);
            }
        } else {
//            ToastUtil.showerror(WeatherSearchActivity.this, rCode);
        }
    }

    private void fillforecast() {
//        reporttime2.setText(weatherforecast.getReportTime()+"发布");
        String forecast = "";
        for (int i = 0; i < forecastlist.size(); i++) {
            LocalDayWeatherForecast localdayweatherforecast = forecastlist.get(i);
            String week = null;
            switch (Integer.valueOf(localdayweatherforecast.getWeek())) {
                case 1:
                    week = "周一";
                    break;
                case 2:
                    week = "周二";
                    break;
                case 3:
                    week = "周三";
                    break;
                case 4:
                    week = "周四";
                    break;
                case 5:
                    week = "周五";
                    break;
                case 6:
                    week = "周六";
                    break;
                case 7:
                    week = "周日";
                    break;
                default:
                    break;
            }
            String temp = String.format("%-3s/%3s",
                    localdayweatherforecast.getDayTemp() + "°",
                    localdayweatherforecast.getNightTemp() + "°");
            String date = localdayweatherforecast.getDate();
            forecast += date + "  " + week + "                       " + temp + "\n\n";
        }
//        forecasttv.setText(forecast);
    }
}

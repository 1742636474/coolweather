package com.example.administrator.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018\7\3 0003.
 */
public class Forecast {

    public  String date;

    @SerializedName("tmp")
    public  Temperature temperature;

    @SerializedName("cond")
    public  Moer moer;

    public  class  Temperature{
        public  String max;
        public  String min;


    }

    public  class  Moer{
        @SerializedName("txt_d")
        public  String info;

    }




}
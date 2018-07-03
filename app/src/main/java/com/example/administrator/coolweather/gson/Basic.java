package com.example.administrator.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018\7\3 0003.
 */
public class Basic {

    @SerializedName("city")
    public  String cityName;

    @SerializedName("id")
    public  String weatherId;

    public  Update update;
    public class  Update{

        @SerializedName("loc")
        public  String updateTime;
    }
}

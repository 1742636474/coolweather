package com.example.administrator.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018\7\3 0003.
 */
public class Now {

    @SerializedName("tmp")
    public  String temperature;

    @SerializedName("cond")
    public  More more;

    public class  More{

        @SerializedName("txt")
        public  String info;

    }
}
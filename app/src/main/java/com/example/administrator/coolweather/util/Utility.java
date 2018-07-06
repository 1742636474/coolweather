package com.example.administrator.coolweather.util;

import android.text.TextUtils;

import com.example.administrator.coolweather.db.City;
import com.example.administrator.coolweather.db.County;
import com.example.administrator.coolweather.db.Province;
import com.example.administrator.coolweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018\6\25 0025.
 */
public class Utility {

    public  static  boolean handleP(String respone){


        if(!TextUtils.isEmpty(respone)){

            try{
                JSONArray allp=new JSONArray(respone);
                for(int i=0;i<allp.length();i++){
                    JSONObject pro=allp.getJSONObject(i);
                    Province province=new Province();
                    province.setProvinceName(pro.getString("name"));
                    province.setProvinceCode(pro.getInt("id"));
                    province.save();

                }
                return  true;


            }catch (JSONException e){

                e.printStackTrace();
            }

        }
        return  false;

    }

    public  static  boolean handleC(String response,int provinceId){

        if(!TextUtils.isEmpty(response)){

            try{
                JSONArray allc=new JSONArray(response);
                for(int i=0;i<allc.length();i++) {
                    JSONObject city = allc.getJSONObject(i);
                    City city1=new City();
                    city1.setCityName(city.getString("name"));
                    city1.setCityCode(city.getInt("id"));
                    city1.setProvinceId(provinceId);
                    city1.save();


                }

               return  true;
            }catch (JSONException e){

                e.printStackTrace();
            }


        }

        return  false;
    }


    public  static  boolean handleCon(String response,int cityId){

        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allcount=new JSONArray(response);
                for(int i=0;i<allcount.length();i++){

                    JSONObject cont=allcount.getJSONObject(i);
                     County county=new County();
                    county.setCountyName(cont.getString("name"));
                    county.setWeatherId(cont.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }




                return  true;

            }catch (JSONException e){
                e.printStackTrace();
            }


        }


        return  false;
    }

    public  static Weather handle(String response){
        try {
            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather");
            String  content=jsonArray.getJSONObject(0).toString();
            return  new Gson().fromJson(content,Weather.class);


        }catch (Exception e){
            e.printStackTrace();
        }



     return  null;
    }


}

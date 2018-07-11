package com.example.administrator.coolweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.coolweather.db.City;
import com.example.administrator.coolweather.db.County;
import com.example.administrator.coolweather.db.Province;
import com.example.administrator.coolweather.util.HttpUtil;
import com.example.administrator.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2018\6\26 0026.
 */
public class Choose_area  extends Fragment{

    public static  final  int LEVEL_PROVINCE=0;
    public static  final  int LEVEL_CITY=1;
    public static  final  int LEVEL_COUNTY=2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> datalist=new ArrayList<>();
    private  List<Province> provincesList;
    private  List<City> cityList;
    private  List<County> countyList;
    private  Province selectProvince;
    private  City selectCity;
    private  int currentLevel;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.choose_area,container,false);
        titleText= (TextView) view.findViewById(R.id.title_text);
        backButton= (Button) view.findViewById(R.id.back_button);
        listView= (ListView) view.findViewById(R.id.list_view);
        adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,datalist);
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(currentLevel==LEVEL_PROVINCE){
                    selectProvince =provincesList.get(i);
                   queryCities();



                }else  if(currentLevel==LEVEL_CITY){
                    selectCity=cityList.get(i);
                    queryCounties();


                }else  if (currentLevel==LEVEL_COUNTY){
                    String weatherId=countyList.get(i).getWeatherId();
                    if (getActivity() instanceof MainActivity){
                        Intent intent=new Intent(getActivity(),WeatherActivity.class);
                        intent.putExtra("weather_id",weatherId);
                        startActivity(intent);
                        getActivity().finish();

                    }else if (getActivity() instanceof  WeatherActivity){

                        WeatherActivity activity= (WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipe.setRefreshing(true);
                        activity.requestWeather(weatherId);


                    }


                }
            }




        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentLevel==LEVEL_COUNTY){

                    queryCities();

                }else if(currentLevel==LEVEL_CITY){

                   queryProvinces();
                }
            }
        });
        queryProvinces();

    }

    private void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provincesList= DataSupport.findAll(Province.class);
        if(provincesList.size()>0){
            datalist.clear();
            for(Province province : provincesList){
             datalist.add(province.getProvinceName());

            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_PROVINCE;

        }else {
            String address="http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }


    }
    private void queryCities() {
        titleText.setText(selectProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList=DataSupport.where("provinceid=?",String.valueOf(selectProvince.getId())).find(City.class);
        if(cityList.size()>0){
           datalist.clear();
            for(City city :cityList){
                datalist.add(city.getCityName());


            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_CITY;

        }else {
            int provincecode=selectProvince.getProvinceCode();
            String address="http://guolin.tech/api/china/"+provincecode;
            queryFromServer(address,"city");

        }


    }
    private void queryCounties() {


        titleText.setText(selectCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList=DataSupport.where("cityid=?",String.valueOf(selectCity.getId())).find(County.class);
        if(countyList.size()>0){
            datalist.clear();
            for(County county : countyList){
                datalist.add(county.getCountyName());



            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_COUNTY;


        }else {
            int provincecode=selectProvince.getProvinceCode();
            int citycode=selectCity.getCityCode();
            String address="http://guolin.tech/api/china/"+provincecode+"/"+citycode;
            queryFromServer(address,"county");


        }


    }

    private void queryFromServer(String address,final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closePrigressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }


                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText=response.body().string();
                boolean result=false;
                if("province".equals(type)){

                    result= Utility.handleP(responseText);


                }else  if ("city".equals(type)){

                    result=Utility.handleC(responseText,selectProvince.getId());
                }else  if("county".equals(type)){
                    result=Utility.handleCon(responseText,selectCity.getId());


                }
                if(result){

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closePrigressDialog();
                            if ("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();


                            }else  if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });



                }

            }
        });

    }

    private void showProgressDialog() {
        if(progressDialog==null){
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);

        }
        progressDialog.show();
    }
    private void closePrigressDialog() {
        if(progressDialog!=null){
            progressDialog.dismiss();


        }


    }


}

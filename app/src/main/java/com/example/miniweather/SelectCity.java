package com.example.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import Bean.City;


public class SelectCity extends Activity implements View.OnClickListener {
    private ImageView mBackBtn;
    private ListView cityListLv;
    private static String updateCode="-1";

    private List<City> mCityList;
    private MyApplication myApplication;
    private ArrayList<String> mArrayList;
    private EditText mEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);
        mBackBtn=(ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

        myApplication=(MyApplication)getApplication();
        mCityList=myApplication.getCityList();
        mArrayList=new ArrayList<String>();
        for(int i=0;i<mCityList.size();i++){
            String No_=Integer.toString(i+1);
            String number=mCityList.get(i).getNumber();
            String provinceName=mCityList.get(i).getProvince();
            String cityName=mCityList.get(i).getCity();
            mArrayList.add("No."+No_+":"+number+"-"+provinceName+"-"+cityName);
        }
        cityListLv=(ListView)findViewById(R.id.selectcity_lv);
        ArrayAdapter<String>adapter=new ArrayAdapter<String>(SelectCity.this,R.layout.support_simple_spinner_dropdown_item,mArrayList);
        cityListLv.setAdapter(adapter);

        AdapterView.OnItemClickListener itemClickListener=new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String updateCityCode=mCityList.get(position).getNumber();
//                updateCode = Integer.toString(updateCityCode);
                Log.d("update city code",updateCityCode);


                Intent i = new Intent();
                i.putExtra("cityCode",updateCityCode);
                setResult(RESULT_OK, i);
                finish();

                SharedPreferences sharedPreferences=getSharedPreferences("CityCodePreference",Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("citycode",updateCityCode);
                editor.commit();

            }
        };
        cityListLv.setOnItemClickListener(itemClickListener);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back:
                Intent i=new Intent(this,MainActivity.class);
                i.putExtra("cityCode","updateCode");
                startActivity(i);
                break;
            default:break;
        }
    }


}

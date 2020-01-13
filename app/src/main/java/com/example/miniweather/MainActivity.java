package com.example.miniweather;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import Bean.TodayWeather;
import cn.example.miniweather.util.NetUtil;

public class MainActivity extends Activity implements View.OnClickListener {
    private ImageView mUpdateBtn;

    private ImageView mCitySelect;
    private String updateCode;
    private TextView day,warm,weather_now,warm_range,weather,wind,city,title_city_name;
    private ImageView image_view;

    private static final int UPDATE_TODAY_WEATHER=1;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);
        if(NetUtil.getNetworkState(this)!=NetUtil.NETWORN_NONE){
            Log.d("myWeather","网络ok");
            Toast.makeText(MainActivity.this,"网络OK!",Toast.LENGTH_LONG).show();
        }else
        {
            Log.d("myWeather","网络挂了");
            Toast.makeText(MainActivity.this,"网络挂了！",Toast.LENGTH_LONG).show();
        }
        mCitySelect=(ImageView)findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);

        initView();


//        updateCode=getIntent().getStringExtra("cityCode");
//        if(!updateCode.equals("-1")){
//            queryWeatherCode(updateCode);
//        }

    }

    @Override
    protected void onStart() {
        super.onStart();
//        Intent intent = getIntent();
//        if(intent!=null){
//            updateCode=intent.getStringExtra("cityCode");
//            if(!updateCode.equals("-1")){
//                queryWeatherCode(updateCode);
//            } else {
//                queryWeatherCode("101010100");
//            }
//        }

    }

    private void queryWeatherCode(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather", address);
        new Thread(new Runnable() {
            @Override
            public void run() {

                HttpURLConnection con = null;
                TodayWeather todayWeather=null;
                try {
                    URL url = new URL(address);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {
                        response.append(str);
                        Log.d("myWeather", str);
                    }
                    String responseStr = response.toString();
                    Log.d("myWeather", responseStr);

                    todayWeather=parseXML(responseStr);
                    if(todayWeather!=null){
                        Log.d("myWeather",todayWeather.toString());

                        Message msg =new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj=todayWeather;
                        mHandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                }
            }
        }).start();
    }
    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.title_update_btn){
            SharedPreferences mySharePre=getSharedPreferences("CityCodePreference",Activity.MODE_PRIVATE);
            String sharecode=mySharePre.getString("citycode","");
            if(!sharecode.equals("")){
                Log.d("sharecode",sharecode);
                queryWeatherCode(sharecode);
            }
            else
            {
                queryWeatherCode("101010100");
            }
        }
        if(view.getId()==R.id.title_city_manager){
            Intent i=new Intent(this,SelectCity.class);
            startActivityForResult(i,1);
        }
        if(view.getId()==R.id.title_update_btn){
            SharedPreferences sharedPreferences=getSharedPreferences("config",MODE_PRIVATE);
            //  读取存储的内容，默认值是北京
            String cityCode=sharedPreferences.getString("main_city_code","101010100");
            Log.d("myWeather",cityCode);

            if(NetUtil.getNetworkState(this)!=NetUtil.NETWORN_NONE){
                Log.d("myWeather","网络OK");
                queryWeatherCode(cityCode);

            }else{
                Log.d("myWeather","网络挂了");
                Toast.makeText(MainActivity.this,"网络挂了",Toast.LENGTH_LONG).show();

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1 && resultCode==RESULT_OK){
            String newCityCode=data.getStringExtra("cityCode");
            Log.d("myWeather","选择的城市代码为"+newCityCode);
            queryWeatherCode(newCityCode);

            if(NetUtil.getNetworkState(this)!=NetUtil.NETWORN_NONE){
                Log.d("myWeather","网络OK");
                queryWeatherCode(newCityCode);
            }else {
                Log.d("myWeather","网络挂了");
                Toast.makeText(MainActivity.this,"网络挂了!",Toast.LENGTH_LONG).show();
            }
        }
    }

    private TodayWeather parseXML(String xmldata){
        TodayWeather todayWeather = null;
        int fengxiangCount=0;
        int fengliCount =0;
        int dateCount=0;
        int highCount =0;
        int lowCount=0;
        int typeCount =0;
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather", "parseXML");
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
// 判断当前事件是否为文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                        break;
// 判断当前事件是否为标签元素开始事件
                    case XmlPullParser.START_TAG:
                        if(xmlPullParser.getName().equals("resp"
                        )){
                            todayWeather= new TodayWeather();
                        }
                        if (todayWeather != null) {
                            if (xmlPullParser.getName().equals("city")) {
                                    eventType = xmlPullParser.next();
                            todayWeather.setCity(xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("updatetime")) {
                        eventType = xmlPullParser.next();
                        todayWeather.setUpdatetime(xmlPullParser.getText());
                    }  else if (xmlPullParser.getName().equals("wendu")) {
                        eventType = xmlPullParser.next();
                        todayWeather.setWendu(xmlPullParser.getText());
                    }   else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                        eventType = xmlPullParser.next();
                        todayWeather.setFengxiang(xmlPullParser.getText());
                        fengxiangCount++;
                    } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                        eventType = xmlPullParser.next();
                        todayWeather.setFengli(xmlPullParser.getText());
                        fengliCount++;
                    } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                        eventType = xmlPullParser.next();
                        todayWeather.setDate(xmlPullParser.getText());
                        dateCount++;
                    } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                        eventType = xmlPullParser.next()
                        ;
                        todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                        highCount++;
                    } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                        eventType = xmlPullParser.next();
                        todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                        lowCount++;
                    } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                        eventType = xmlPullParser.next();
                        todayWeather.setType(xmlPullParser.getText());
                        typeCount++;
                    }
                }
                break;
// 判断当前事件是否为标签元素结束事件
                case XmlPullParser.END_TAG:
                    break;
            }
// 进入下一个元素并触发相应事件
            eventType = xmlPullParser.next();
        }
    } catch (XmlPullParserException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
return todayWeather;
}

void initView(){
    //用于初始化控件的函数
    day=(TextView) findViewById(R.id.day);
    warm=(TextView) findViewById(R.id.warm);
    weather_now=(TextView) findViewById(R.id.weather_now);
    warm_range=(TextView) findViewById(R.id.warm_range);
    weather=(TextView) findViewById(R.id.weather);
    wind=(TextView) findViewById(R.id.wind);
    city=(TextView)findViewById(R.id.city);
    title_city_name=(TextView)findViewById(R.id.title_city_name);
    image_view=(ImageView) findViewById(R.id.image_view);

    day.setText("N/A");
    warm.setText("N/A");
    weather_now.setText("N/A");
    warm_range.setText("N/A");
    weather.setText("N/A");
    wind.setText("N/A");
    city.setText("N/A");
    title_city_name.setText("N/A");
}
void updateTodayWeather(TodayWeather todayWeather){
        day.setText(todayWeather.getDate());
        warm.setText(todayWeather.getWendu());
        weather_now.setText(todayWeather.getType()+"实时");
        warm_range.setText(todayWeather.getLow()+"-"+todayWeather.getHigh());
        weather.setText(todayWeather.getType());
        wind.setText(todayWeather.getFengxiang()+todayWeather.getFengli());
        city.setText(todayWeather.getCity());
        title_city_name.setText(todayWeather.getCity()+"天气");
        if(todayWeather.getType()!=null){
            Log.d("type",todayWeather.getType());
            switch (todayWeather.getType()){
                case"晴":
                    image_view.setImageResource(R.drawable.sun);
                    break;
                case"阴":
                    image_view.setImageResource(R.drawable.cloudy);
                    break;
                case"雾":
                    image_view.setImageResource(R.drawable.frowgy);
                    break;
                case"多云":
                    image_view.setImageResource(R.drawable.cloud);
                    break;
                case"小雨":
                    image_view.setImageResource(R.drawable.smallrain);
                    break;
                case"中雨":
                    image_view.setImageResource(R.drawable.midrain);
                    break;
                case"大雨":
                    image_view.setImageResource(R.drawable.bigrain);
                    break;
                case"小雪":
                    image_view.setImageResource(R.drawable.smallsnow);
                    break;
                case"中雪":
                    image_view.setImageResource(R.drawable.midsnow);
                    break;
                case"大雪":
                    image_view.setImageResource(R.drawable.bigsnow);
                    break;
                    default:
                        image_view.setImageResource(R.drawable.sun);
                        break;
            }
        }
        Toast.makeText(MainActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();

}
}

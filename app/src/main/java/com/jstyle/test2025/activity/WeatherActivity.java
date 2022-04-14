package com.jstyle.test2025.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.jstyle.blesdk2025.Util.BleSDK;
import com.jstyle.blesdk2025.constant.BleConst;
import com.jstyle.blesdk2025.model.WeatherData;


import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.jstyle.test2025.R;

public class WeatherActivity extends BaseActivity {


    @BindView(R.id.cityname)
    EditText cityname;
    @BindView(R.id.ed_temperature_1)
    EditText ed_temperature_1;
    @BindView(R.id.ed_temperature_two)
    EditText ed_temperature_two;
    @BindView(R.id.ed_temperature_there)
    EditText ed_temperature_there;
    @BindView(R.id.weathid)
    EditText weathid;

    WeatherData weatherData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);
        ButterKnife.bind(this);
        weatherData=new WeatherData();

    }




  @OnClick({R.id.Settemperature})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.Settemperature:


                if(TextUtils.isEmpty(weathid.getText())){
                    showToast("Please enter the weathid");
                    return;
                }

                if(Integer.valueOf(weathid.getText().toString())<=38){

                }else if(Integer.valueOf(weathid.getText().toString())==99){

                } else{
                    showToast("weathid error");
                    return;
                }

                if(TextUtils.isEmpty(ed_temperature_1.getText())){
                    showToast("Please enter the current temperature");
                    return;
                }
                if(TextUtils.isEmpty(ed_temperature_two.getText())){
                    showToast("Please enter the current maximum temperature");
                    return;
                }
                if(TextUtils.isEmpty(ed_temperature_there.getText())){
                    showToast("Please enter the current minimum temperature");
                    return;
                }
                if(TextUtils.isEmpty(cityname.getText())){
                    showToast("Please enter city name");
                return;
                }
                weatherData.setWeatherId(Integer.valueOf(weathid.getText().toString()));//0-38 or 99
                weatherData.setTempNow(Integer.valueOf(ed_temperature_1.getText().toString()));
                weatherData.setTempHigh(Integer.valueOf(ed_temperature_two.getText().toString()));
                weatherData.setTempLow(Integer.valueOf(ed_temperature_there.getText().toString()));
                weatherData.setCityName(cityname.getText().toString());
                sendValue(BleSDK.setWeather(weatherData));
                break;

        }

    }
    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType= getDataType(maps);
        switch (dataType){
            case BleConst.Weather:
                showDialogInfo(maps.toString());
                break;
        }}

}

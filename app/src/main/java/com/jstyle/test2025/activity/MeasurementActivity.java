package com.jstyle.test2025.activity;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jstyle.blesdk2025.Util.BleSDK;
import com.jstyle.blesdk2025.constant.BleConst;
import com.jstyle.test2025.R;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeasurementActivity extends BaseActivity {

    @BindView(R.id.radioGroup_mian)
    RadioGroup radioGroup_mian;
    @BindView(R.id._switch)
    SwitchCompat _switch;
    @BindView(R.id.data_info)
    TextView data_info;
    @BindView(R.id.send)
    Button send;
    int type=1;
    boolean open=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);
        ButterKnife.bind(this);
        radioGroup_mian.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.hrv:
                        type=1;
                        break;
                    case R.id.heart:
                        type=2;
                        break;
                    case R.id.oxygen:
                        type=3;
                        break;
                }

            }
        });
        _switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                open=isChecked;
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendValue(BleSDK.StartDeviceMeasurementWithType(type,open));
            }
        });

    }




    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType= getDataType(maps);
        Log.e("dataCallback",maps.toString());
        switch (dataType){
            case BleConst.MeasurementHrvCallback:
            case BleConst.MeasurementHeartCallback:
            case BleConst.MeasurementOxygenCallback:
                data_info.setText("");
                data_info.setText(maps.toString());
                break;
        }}

}

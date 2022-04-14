package com.jstyle.test2025.activity;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.jstyle.blesdk2025.Util.BleSDK;
import com.jstyle.blesdk2025.constant.BleConst;
import com.jstyle.test2025.R;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EcginfoActivity extends BaseActivity {

    @BindView(R.id._switchCompat)
    SwitchCompat _switchCompat;
    @BindView(R.id.data_info)
    TextView data_info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getppg);
        ButterKnife.bind(this);
        _switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sendValue(BleSDK.enableEcg(isChecked));
            }
        });

    }




    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType= getDataType(maps);
        Log.e("dataCallback",maps.toString());
        switch (dataType){
            case BleConst.EcgppGstatus:
            case BleConst.ECG:
                data_info.setText(maps.toString());
                break;
        }}

}

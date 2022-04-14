package com.jstyle.test2025.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.jstyle.blesdk2025.Util.BleSDK;
import com.jstyle.blesdk2025.constant.BleConst;
import com.jstyle.blesdk2025.constant.DeviceKey;
import com.jstyle.blesdk2025.model.AutoMode;
import com.jstyle.blesdk2025.model.MyAutomaticHRMonitoring;
import com.jstyle.test2025.R;


import java.util.Map;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 自动监测设置
 */
public class HeartRateSetActivity extends BaseActivity {

    @BindView(R.id.edit_minute)
    EditText editMinute;




    private static final String TAG = "HeartRateSetActivity";



    private void init() {
        sendValue(BleSDK.GetAutomatic(AutoMode.AutoHeartRate));
    }

    @OnClick({R.id.button_set_activitytime, R.id.button_get_activitytime})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_set_activitytime:
                setActivityTimeAlarm();
                break;
            case R.id.button_get_activitytime:
                sendValue(BleSDK.GetAutomatic(AutoMode.AutoHeartRate));
                break;
        }
    }



    private void setActivityTimeAlarm() {
        // TODO Auto-generated method stub
        if (TextUtils.isEmpty(editMinute.getText().toString())) return;;
        int minInterval = Integer.parseInt(editMinute.getText().toString());
        sendValue(BleSDK.SetAutomatic(true,minInterval, AutoMode.AutoHeartRate));
    }

    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        Log.e("info",maps.toString());
        String dataType= getDataType(maps);
        Map<String,String>data= getData(maps);
        switch (dataType){
            case BleConst.SetAutomatic:
                showSetSuccessfulDialogInfo(dataType);
                break;
            case BleConst.GetAutomatic:
                String timeInterval = data.get(DeviceKey.IntervalTime);
                editMinute.setText(timeInterval);

                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate_set);
        ButterKnife.bind(this);
        init();
    }
}

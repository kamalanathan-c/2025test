package com.jstyle.test2025.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.jstyle.blesdk2025.Util.BleSDK;
import com.jstyle.blesdk2025.constant.BleConst;
import com.jstyle.blesdk2025.constant.DeviceKey;
import com.jstyle.blesdk2025.model.MyDeviceInfo;
import com.jstyle.test2025.R;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeviceInfoActivity extends BaseActivity {

    @BindView(R.id.button_reset)
    Button buttonReset;
    @BindView(R.id.button_battery)
    Button buttonBattery;
    @BindView(R.id.button_mac)
    Button buttonMac;
    @BindView(R.id.button_read_version)
    Button buttonReadVersion;
    @BindView(R.id.button_mcu_rest)
    Button buttonMcuRest;
    @BindView(R.id.radio_12h)
    RadioButton radio12h;
    @BindView(R.id.radio_24h)
    RadioButton radio24h;
    @BindView(R.id.radioGroup1)
    RadioGroup radioGroupTimeMode;
    @BindView(R.id.radio_km)
    RadioButton radioKm;
    @BindView(R.id.radio_mile)
    RadioButton radioMile;
    @BindView(R.id.radioGroup3)
    RadioGroup radioGroup_distanceUnit;



    @BindView(R.id.SwitchCompat_hand)
    SwitchCompat SwitchCompatHand;
    @BindView(R.id.left_or_light)
    SwitchCompat left_or_light;
    @BindView(R.id.Night_mode)
    SwitchCompat Night_mode;

    @BindView(R.id.Social_distance_switch)
    SwitchCompat Social_distance_switch;
    @BindView(R.id.Chinese_English_switch)
    SwitchCompat Chinese_English_switch;

    @BindView(R.id.button_deviceinfo_set)
    Button buttonDeviceinfoSet;
    @BindView(R.id.button_deviceinfo_get)
    Button buttonDeviceinfoGet;
    @BindView(R.id.ed_name)
    EditText edName;
    @BindView(R.id.ed_MotorVibration)
    EditText edMotorVibration;
    @BindView(R.id.ed_heartbeatPacketsInterval)
    EditText edHeartbeatPacketsInterval;
    @BindView(R.id.Dial_switch)
    EditText Dial_switch;


    @BindView(R.id.radio_temp_c)
    RadioButton radioTempC;
    @BindView(R.id.radio_temp_f)
    RadioButton radioTempF;
    @BindView(R.id.radioGroup_tempUnit)
    RadioGroup radioGroupTempUnit;

    @BindView(R.id.AppCompatEditText_baseHr)
    AppCompatEditText AppCompatEditText_baseHr;
    @BindView(R.id.Dialinterface)
    AppCompatEditText Dialinterface;
    @BindView(R.id.BASE_heart)
    AppCompatEditText BASE_heart;

    int index=1;
    int sensitivityid=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        ButterKnife.bind(this);

        radioGroup_distanceUnit.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                sendValue(BleSDK.SetDistanceUnit(checkedId==R.id.radio_km));
            }
        });
        radioGroupTimeMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                sendValue(BleSDK.SetTimeModeUnit(checkedId==R.id.radio_12h));
            }
        });
        SwitchCompatHand.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sendValue(BleSDK.setWristOnEnable(isChecked));
            }
        });

        radioGroupTempUnit.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                sendValue(BleSDK.setTemperatureUnit(checkedId!=R.id.radio_temp_c));
            }
        });
        Night_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sendValue(BleSDK.setLightMode(isChecked));
            }
        });
        Chinese_English_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sendValue(BleSDK.setLauage(isChecked));
            }
        });

    }

    @OnClick({R.id.setDialinterface,R.id.button_heartbeaPacketsInterval_set, R.id.button_ota, R.id.button_MotorVibration_set, R.id.button_name_get, R.id.button_name_set, R.id.button_reset, R.id.button_battery, R.id.button_mac, R.id.button_read_version, R.id.button_mcu_rest, R.id.button_deviceinfo_set, R.id.button_deviceinfo_get})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.setDialinterface:
                if(TextUtils.isEmpty(Dialinterface.getText())){
                    showToast("Dialinterface is null");
                    return;
                }
                sendValue(BleSDK.SetDialinterface(Integer.valueOf(Dialinterface.getText().toString())));
                break;
            case R.id.button_heartbeaPacketsInterval_set:
                String heartInterval = edHeartbeatPacketsInterval.getText().toString();
                if (!TextUtils.isEmpty(heartInterval)) {
                    sendValue(BleSDK.SetHeartbeatPackets(Integer.valueOf(heartInterval)));
                }
                break;
            case R.id.button_ota:
                sendValue(BleSDK.enterOTA());
                break;
            case R.id.button_MotorVibration_set:
                String times = edMotorVibration.getText().toString();
                if (!TextUtils.isEmpty(times)) {
                    sendValue(BleSDK.MotorVibrationWithTimes(Integer.valueOf(times)));
                }

                break;
            case R.id.button_name_get:
                sendValue(BleSDK.GetDeviceName());
                break;
            case R.id.button_name_set:
                String name = edName.getText().toString();
                if (!TextUtils.isEmpty(name)) {
                    sendValue(BleSDK.SetDeviceName(name));
                }
                break;
            case R.id.button_reset:
                showResetDialog();

                break;
            case R.id.button_battery:
                sendValue(BleSDK.GetDeviceBatteryLevel());
                break;
            case R.id.button_mac:
                sendValue(BleSDK.GetDeviceMacAddress());
                break;
            case R.id.button_read_version:
                sendValue(BleSDK.GetDeviceVersion());
                break;
            case R.id.button_mcu_rest:
                sendValue(BleSDK.MCUReset());
                break;
            case R.id.button_deviceinfo_set:
                setDeviceInfo();
                break;
            case R.id.button_deviceinfo_get:
                sendValue(BleSDK.GetDeviceInfo());
                break;

        }
    }

    private void showResetDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.Restore_factory))
                .setMessage(getString(R.string.Restore_factory_tips))
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendValue(BleSDK.Reset());
                    }
                })
                .setNegativeButton("Cancel", null)
                .create().show();

    }

    private void setDeviceInfo() {
        String brightness = AppCompatEditText_baseHr.getText().toString();
        MyDeviceInfo deviceBaseParameter = new MyDeviceInfo();
        deviceBaseParameter.setDistanceUnit(radioGroup_distanceUnit.getCheckedRadioButtonId() == R.id.radio_mile);
        deviceBaseParameter.setIs12Hour(radioGroupTimeMode.getCheckedRadioButtonId() == R.id.radio_12h);
        deviceBaseParameter.setTemperature_unit(radioGroupTempUnit.getCheckedRadioButtonId() == R.id.radio_temp_c);
        deviceBaseParameter.setBright_screen(SwitchCompatHand.isChecked());
        deviceBaseParameter.setFahrenheit_or_centigrade(left_or_light.isChecked());
        deviceBaseParameter.setNight_mode(Night_mode.isChecked());
        if(!TextUtils.isEmpty(Dial_switch.getText())){
            deviceBaseParameter.setDialinterface(Integer.parseInt(Dial_switch.getText().toString()));
        }
        deviceBaseParameter.setSocial_distance_switch(Social_distance_switch.isChecked());
        deviceBaseParameter.setChinese_English_switch(Chinese_English_switch.isChecked());
        String base = BASE_heart.getText().toString();
        if(!TextUtils.isEmpty(base)&&Integer.valueOf(base)>40){
            deviceBaseParameter.setBaseheart(Integer.valueOf(base));
        }else{
            showToast("The input value must be greater than 40");
            return;
        }
        if (!TextUtils.isEmpty(brightness)) {
            deviceBaseParameter.setScreenBrightness(Integer.valueOf(brightness));
        }

        sendValue(BleSDK.SetDeviceInfo(deviceBaseParameter));
    }

    boolean isRecevier;

    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        Log.e("info",maps.toString());
        String dataType = getDataType(maps);
        Map<String, String> data = getData(maps);
        switch (dataType) {
            case BleConst.GetDeviceName:
                String deviceName = data.get(DeviceKey.DeviceName);
                showDialogInfo(deviceName);
                break;
            case BleConst.GetDeviceBatteryLevel:
                String battery = data.get(DeviceKey.BatteryLevel);
                showDialogInfo(battery);
                break;
            case BleConst.GetDeviceMacAddress:
                String mac = data.get(DeviceKey.MacAddress);
                showDialogInfo(mac);
                break;
            case BleConst.GetDeviceVersion:
                String version = data.get(DeviceKey.DeviceVersion);
                showDialogInfo(version);
                break;
            case BleConst.GetDeviceInfo:
                showDialogInfo(maps.toString());
                break;
            case BleConst.SetDeviceInfo:
            case BleConst.SetMotorVibrationWithTimes:
                default:
                    //showDialogInfo(maps.toString());
                    break;
          /*  case BleConst.GetTemperatureCorrectionValue:
                String tempValue=data.get(DeviceKey.TemperatureCorrectionValue);
                showSetSuccessfulDialogInfo(tempValue);
                break;*/
        }
    }
}

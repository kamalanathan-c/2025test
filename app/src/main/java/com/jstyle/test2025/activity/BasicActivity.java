package com.jstyle.test2025.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jstyle.blesdk2025.Util.BleSDK;
import com.jstyle.blesdk2025.constant.BleConst;
import com.jstyle.blesdk2025.constant.DeviceKey;
import com.jstyle.blesdk2025.model.MyPersonalInfo;
import com.jstyle.blesdk2025.model.MyDeviceTime;
import com.jstyle.test2025.R;


import java.util.Calendar;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BasicActivity extends BaseActivity {

    @BindView(R.id.button_settime)
    Button buttonSettime;
    @BindView(R.id.button_Gettime)
    Button buttonGettime;
    @BindView(R.id.textView1)
    TextView textView1;
    @BindView(R.id.radio_female)
    RadioButton radioFemale;
    @BindView(R.id.radio_male)
    RadioButton radioMale;
    @BindView(R.id.radioGroup_gender)
    RadioGroup radioGroup_gender;
    @BindView(R.id.textView_age)
    TextView textViewAge;
    @BindView(R.id.editText_age)
    EditText editTextAge;
    @BindView(R.id.textView_height)
    TextView textViewHeight;
    @BindView(R.id.editText_height)
    EditText editTextHeight;
    @BindView(R.id.textView_heightb)
    TextView textViewHeightb;
    @BindView(R.id.textView_weight)
    TextView textViewWeight;
    @BindView(R.id.editText_weight)
    EditText editTextWeight;
    @BindView(R.id.textView_weight2)
    TextView textViewWeight2;
    @BindView(R.id.textView_stride)
    TextView textViewStride;
    @BindView(R.id.editText_stride)
    EditText editTextStride;
    @BindView(R.id.textView_stride2)
    TextView textViewStride2;
    @BindView(R.id.button_setinfo)
    Button buttonSetinfo;
    @BindView(R.id.button_getinfo)
    Button buttonGetinfo;
    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.radio_enable_sleep)
    RadioButton radioEnableSleep;
    @BindView(R.id.radio_disable_sleep)
    RadioButton radioDisableSleep;
    @BindView(R.id.radioGroup5)
    RadioGroup radioGroup5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.button_settime, R.id.button_Gettime, R.id.button_setinfo, R.id.button_getinfo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_settime:
                setTime();
                
                break;
            case R.id.button_Gettime:
                sendValue(BleSDK.GetDeviceTime());
                break;
            case R.id.button_setinfo:
                setUserInfo();
                break;
            case R.id.button_getinfo:
                sendValue(BleSDK.GetPersonalInfo());
                break;
        }
    }

    private static final String TAG = "BasicActivity";
    private void setTime() {
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int min=calendar.get(Calendar.MINUTE);
        int second=calendar.get(Calendar.SECOND);
        MyDeviceTime setTime=new MyDeviceTime();
        setTime.setYear(year);
        setTime.setMonth(month);
        setTime.setDay(day);
        setTime.setHour(hour);
        setTime.setMinute(min);
        setTime.setSecond(second);
        sendValue(BleSDK.SetDeviceTime(setTime));
    }

    private void setUserInfo() {
        if(TextUtils.isEmpty(editTextAge.getText().toString())||
                TextUtils.isEmpty(editTextHeight.getText().toString())||
                TextUtils.isEmpty(editTextWeight.getText().toString())||
                TextUtils.isEmpty(editTextStride.getText().toString()))return;
        int age=Integer.valueOf(editTextAge.getText().toString());
        int height=Integer.valueOf(editTextHeight.getText().toString());
        int weight=Integer.valueOf(editTextWeight.getText().toString());
        int stepLength=Integer.valueOf(editTextStride.getText().toString());
        int gender= radioGroup_gender.getCheckedRadioButtonId()==R.id.radio_male?1:0;
        MyPersonalInfo setPersonalInfo=new MyPersonalInfo();
        setPersonalInfo.setAge(age);
        setPersonalInfo.setHeight(height);
        setPersonalInfo.setWeight(weight);
        setPersonalInfo.setStepLength(stepLength);
        setPersonalInfo.setSex(gender);
        sendValue(BleSDK.SetPersonalInfo(setPersonalInfo));
    }



    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType= getDataType(maps);
        Map<String,String>data= getData(maps);
        switch (dataType){
            case BleConst.GetDeviceTime:
                showDialogInfo(maps.toString());
                break;
            case BleConst.SetPersonalInfo:
            case BleConst.SetDeviceTime:
                showSetSuccessfulDialogInfo(dataType);
                break;
            case BleConst.GetPersonalInfo:
                String age=data.get(DeviceKey.Age);
                String height=data.get(DeviceKey.Height);
                String weight=data.get(DeviceKey.Weight);
                String stepLength=data.get(DeviceKey.Stride);
                int gender= Integer.parseInt(data.get(DeviceKey.Gender));
                editTextStride.setText(stepLength);
                editTextHeight.setText(height);
                editTextWeight.setText(weight);
                editTextAge.setText(age);
                radioGroup_gender.check(gender==1?R.id.radio_male:R.id.radio_female);
                break;

        }
    }
}

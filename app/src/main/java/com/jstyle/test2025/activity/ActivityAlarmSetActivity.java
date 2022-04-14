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
import android.widget.TextView;
import android.widget.TimePicker;

import com.jstyle.blesdk2025.Util.BleSDK;
import com.jstyle.blesdk2025.constant.BleConst;
import com.jstyle.blesdk2025.constant.DeviceKey;
import com.jstyle.blesdk2025.model.MySedentaryReminder;
import com.jstyle.test2025.R;


import java.util.Map;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityAlarmSetActivity extends BaseActivity {

    @BindView(R.id.textView1)
    TextView textView1;
    @BindView(R.id.timePicker_start)
    TimePicker timePickerStart;
    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.timePicker_stop)
    TimePicker timePickerStop;
    @BindView(R.id.linearLayout1)
    LinearLayout linearLayout1;
    @BindView(R.id.button_weekchoose)
    Button buttonWeekchoose;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.edit_minute)
    EditText editMinute;
    @BindView(R.id.textView4)
    TextView textView4;
    @BindView(R.id.ll2)
    LinearLayout ll2;
    @BindView(R.id.textVew)
    TextView textVew;
    @BindView(R.id.edit_step)
    EditText editStep;
    @BindView(R.id.textVie)
    TextView textVie;
    @BindView(R.id.ll3)
    LinearLayout ll3;
    @BindView(R.id.button_set_activitytime)
    Button buttonSetActivitytime;
    @BindView(R.id.button_get_activitytime)
    Button buttonGetActivitytime;
    @BindArray(R.array.weekarray)
            String[]weekArray;
    int[] weekPosition;
    private static final String TAG = "ActivityAlarmSetActivit";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_set2);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        weekPosition=new int[7];
        timePickerStart.setIs24HourView(true);
        timePickerStop.setIs24HourView(true);
        sendValue(BleSDK.GetSedentaryReminder());
    }

    @OnClick({R.id.button_weekchoose, R.id.button_set_activitytime, R.id.button_get_activitytime})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_weekchoose:
                showWeekDialog();
                break;
            case R.id.button_set_activitytime:
                setActivityTimeAlarm();
                break;
            case R.id.button_get_activitytime:
                sendValue(BleSDK.GetSedentaryReminder());
                break;
        }
    }

    AlertDialog weekDialog;
    private void showWeekDialog() {
        boolean[]checked=new boolean[7];
        for(int i=0;i<7;i++){
            checked[i]=weekPosition[i]==1;
        }
            weekDialog=new AlertDialog.Builder(this)
                    .setMultiChoiceItems(weekArray,checked, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                           weekPosition[which]=isChecked?1:0;
                        }
                    })
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setNegativeButton("Cancel",null).create();

        weekDialog.show();
    }

    private void setActivityTimeAlarm() {
        // TODO Auto-generated method stub
        if (TextUtils.isEmpty(editMinute.getText().toString())
                || TextUtils.isEmpty(editStep.getText().toString())) return;
        int hourStart = timePickerStart.getCurrentHour();
        int minStart = timePickerStart.getCurrentMinute();
        int hourEnd = timePickerStop.getCurrentHour();
        int minEnd = timePickerStop.getCurrentMinute();
        int minInterval = Integer.parseInt(editMinute.getText().toString());
        int minStep = Integer.parseInt(editStep.getText().toString());
        int week = 0;
        for (int  i =0;i<7;i++) {
            if(weekPosition[i]==1)
            week += Math.pow(2, i);
        }
        MySedentaryReminder sportPeriod = new MySedentaryReminder();
        sportPeriod.setStartHour(hourStart);
        sportPeriod.setStartMinute(minStart);
        sportPeriod.setEndHour(hourEnd);
        sportPeriod.setEndMinute(minEnd);
        sportPeriod.setIntervalTime(minInterval);
        sportPeriod.setLeastStep(minStep);
        sportPeriod.setWeek(week);
        sendValue(BleSDK.SetSedentaryReminder(sportPeriod));
    }

    @Override
    public void dataCallback(Map<String, Object> data) {
        super.dataCallback(data);
        String dataType= getDataType(data);
        Map<String,String>maps= getData(data);
        switch (dataType){
            case BleConst.SetSedentaryReminder:
                showSetSuccessfulDialogInfo(dataType);
                break;
            case BleConst.GetSedentaryReminder:
                int startHour = Integer.valueOf(maps.get(DeviceKey.StartTimeHour));
                int startMin = Integer.valueOf(maps.get(DeviceKey.StartTimeMin));
                int endHour = Integer.valueOf(maps.get(DeviceKey.EndTimeHour));
                int endMin = Integer.valueOf(maps.get(DeviceKey.EndTimeMin));
                String timeInterval = maps.get(DeviceKey.IntervalTime);
                String week = maps.get(DeviceKey.Week);
                String step = maps.get(DeviceKey.LeastSteps);
                String[]weekStrings=week.split("-");
                for(int i=0;i<7;i++){
                    weekPosition[i]=Integer.valueOf(weekStrings[i]);
                }
                timePickerStart.setCurrentHour(startHour);
                timePickerStart.setCurrentMinute(startMin);
                timePickerStop.setCurrentHour(endHour);
                timePickerStop.setCurrentMinute(endMin);
                editMinute.setText(timeInterval);
                editStep.setText(step);
                break;
        }
    }
}

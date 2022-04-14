package com.jstyle.test2025.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.jstyle.blesdk2025.Util.BleSDK;
import com.jstyle.blesdk2025.constant.BleConst;
import com.jstyle.test2025.R;
import com.jstyle.test2025.adapter.GpsDataAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TemperatureHistoryActivity extends BaseActivity {
    @BindView(R.id.bt_getData)
    Button btGetData;
    @BindView(R.id.RecyclerView_exerciseHistory)
    RecyclerView RecyclerViewExerciseHistory;
    @BindView(R.id.bt_deleteData)
    Button btDeleteData;
    byte ModeStart=0;
    byte ModeContinue=2;
    byte ModeDelete=(byte)0x99;
    private List< String> list;
    private GpsDataAdapter activityModeDataAdapter;
    private int dataCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_data);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        list=new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerViewExerciseHistory.setLayoutManager(linearLayoutManager);
        activityModeDataAdapter = new GpsDataAdapter();
        RecyclerViewExerciseHistory.setAdapter(activityModeDataAdapter);
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        RecyclerViewExerciseHistory.addItemDecoration(dividerItemDecoration);
    }

    @OnClick({R.id.bt_getData, R.id.bt_deleteData,R.id.bt_getyexiaData,R.id.bt_deleteData_auto})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_getData:
                list.clear();
                dataCount=0;
                getData(ModeStart);
                break;
            case R.id.bt_deleteData:
                list.clear();
                dataCount=0;
                getData(ModeDelete);
                break;
            case R.id.bt_getyexiaData:
                list.clear();
                dataCount=0;
                getDatafortemp(ModeStart);
                break;
            case R.id.bt_deleteData_auto:
                list.clear();
                dataCount=0;
                getDatafortemp(ModeDelete);
                break;

        }
    }


    private void getDatafortemp(byte mode){
        //showProgressDialog("同步数据(Synchronize data)");
        sendValue(BleSDK.GetAxillaryTemperatureDataWithMode(mode,""));
    }

    private void getData(byte mode){
        // showProgressDialog("同步数据(Synchronize data)");
        sendValue(BleSDK.GetTemperature_historyDataWithMode(mode,""));
    }

    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType= getDataType(maps);
        boolean finish=getEnd(maps);
        switch (dataType){
            case BleConst.deleteGetTemperature_historyDataWithMode:
            case BleConst.deleteGetAxillaryTemperatureDataWithMode:
                list.add(maps.toString());
                activityModeDataAdapter.setData(list);
                break;
            case BleConst.GetAxillaryTemperatureDataWithMode:
                list.add(maps.toString());
                dataCount++;
                if(finish){
                    dataCount=0;
                    disMissProgressDialog();
                    activityModeDataAdapter.setData(list);
                }
                if(dataCount==50){
                    Log.e("sdadaa","sssssssssssssssssssss");
                    dataCount=0;
                    if(finish){
                        disMissProgressDialog();
                        activityModeDataAdapter.setData(list);
                    }else{
                        getDatafortemp(ModeContinue);
                    }
                }
                break;
            case BleConst.Temperature_history:
                list.add(maps.toString());
                dataCount++;
                if(finish){
                    dataCount=0;
                    disMissProgressDialog();
                    activityModeDataAdapter.setData(list);
                }
                if(dataCount==50){
                    Log.e("sdadaa","sssssssssssssssssssss");
                    dataCount=0;
                    if(finish){
                        disMissProgressDialog();
                        activityModeDataAdapter.setData(list);
                    }else{
                        getData(ModeContinue);
                    }
                }

                break;
        }
    }
}

package com.jstyle.test2025.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.jstyle.blesdk2025.Util.BleSDK;
import com.jstyle.blesdk2025.constant.BleConst;
import com.jstyle.blesdk2025.constant.DeviceKey;
import com.jstyle.test2025.R;
import com.jstyle.test2025.adapter.ActivityModeDataAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/11/19.
 */

public class ExerciseHistoryDataActivity extends BaseActivity {
    @BindView(R.id.bt_getData)
    Button btGetData;
    @BindView(R.id.RecyclerView_exerciseHistory)
    RecyclerView RecyclerViewExerciseHistory;
    @BindView(R.id.bt_deleteData)
    Button btDeleteData;
    @BindArray(R.array.mode_name)
            String[]modeNames;
    byte ModeStart=0;
    byte ModeContinue=2;
    byte ModeDelete=(byte)0x99;
    private List<Map<String, String>> list;
    private ActivityModeDataAdapter activityModeDataAdapter;
    private int dataCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercisehistory);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        list=new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerViewExerciseHistory.setLayoutManager(linearLayoutManager);
        activityModeDataAdapter = new ActivityModeDataAdapter(modeNames);
        RecyclerViewExerciseHistory.setAdapter(activityModeDataAdapter);
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        RecyclerViewExerciseHistory.addItemDecoration(dividerItemDecoration);
    }

    @OnClick({R.id.bt_getData, R.id.bt_deleteData})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_getData:
                list.clear();
                dataCount=0;
                getData(ModeStart);
                break;
            case R.id.bt_deleteData:
                getData(ModeDelete);
                break;
        }
    }
    private void getData(byte mode){
        sendValue(BleSDK.GetActivityModeDataWithMode(mode));
    }

    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType= getDataType(maps);
        boolean finish=getEnd(maps);
        switch (dataType){
            case BleConst.Delete_ActivityModeData:
                showDialogInfo(maps.toString());
                break;
            case BleConst.Temperature_history:
                list.addAll((List<Map<String,String>>)maps.get(DeviceKey.Data));
                dataCount++;
                if(finish){
                    dataCount=0;
                    disMissProgressDialog();
                    activityModeDataAdapter.setData(list);
                }
                if(dataCount==50){
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

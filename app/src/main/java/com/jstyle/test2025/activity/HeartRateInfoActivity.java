package com.jstyle.test2025.activity;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.jstyle.blesdk2025.Util.BleSDK;
import com.jstyle.blesdk2025.constant.BleConst;
import com.jstyle.blesdk2025.constant.DeviceKey;
import com.jstyle.test2025.R;
import com.jstyle.test2025.adapter.HeartRateDataAdapter;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HeartRateInfoActivity extends BaseActivity {


    @BindView(R.id.radio_history)
    RadioButton radioHistory;
    @BindView(R.id.radio_once)
    RadioButton radioOnce;
    @BindView(R.id.radioGroup1)
    RadioGroup radioGroup1;
    @BindView(R.id.bt_readData)
    Button btReadData;
    @BindView(R.id.bt_DeleteData)
    Button btDeleteData;
    @BindView(R.id.RecyclerView_heartData)
    RecyclerView RecyclerViewHeartData;
    private HeartRateDataAdapter heartRateDataAdapter;
    byte ModeStart=0;
    byte ModeContinue=2;
    byte ModeDelete=(byte)0x99;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate_info);
        ButterKnife.bind(this);
        init();
    }
    private void init() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerViewHeartData.setLayoutManager(linearLayoutManager);
        heartRateDataAdapter = new HeartRateDataAdapter();
        RecyclerViewHeartData.setAdapter(heartRateDataAdapter);
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        RecyclerViewHeartData.addItemDecoration(dividerItemDecoration);

    }

    @OnClick({R.id.bt_readData, R.id.bt_DeleteData})
    public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.bt_readData:
                    list.clear();
                    dataCount=0;
                    getHeartHistoryData(ModeStart);
                    break;
                case R.id.bt_DeleteData:
                    getHeartHistoryData(ModeDelete);
                    break;
        }
    }
    List<Map<String,String>>list=new ArrayList<>();
    int dataCount=0;
    private static final String TAG = "HeartRateInfoActivity";
    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType= getDataType(maps);
        boolean finish=getEnd(maps);
        switch (dataType){
            case BleConst.Delete_GetDynamicHR:
                showDialogInfo(maps.toString());
                break;
            case BleConst.GetDynamicHR:
                dataCount++;
                list.addAll((List<Map<String,String>>)maps.get(DeviceKey.Data));
                if(finish){
                    heartRateDataAdapter.setData(list,HeartRateDataAdapter.GET_HEART_DATA);
                }
                if(dataCount==50){
                    dataCount=0;
                    if(finish){
                        heartRateDataAdapter.setData(list,HeartRateDataAdapter.GET_HEART_DATA);
                    }else{
                        getHeartHistoryData(ModeContinue);
                    }
                }

                break;
            case BleConst.GetStaticHR:
                dataCount++;
                list.addAll((List<Map<String,String>>)maps.get(DeviceKey.Data));
                if(finish){
                    heartRateDataAdapter.setData(list,HeartRateDataAdapter.GET_ONCE_HEARTDATA);
                }
                if(dataCount==50){
                    dataCount=0;
                    if(finish){
                        heartRateDataAdapter.setData(list,HeartRateDataAdapter.GET_ONCE_HEARTDATA);
                    }else{
                        getHeartHistoryData(ModeContinue);
                    }
                }
                break;
        }
    }


    private void getHeartHistoryData(byte mode){
        sendValue(radioGroup1.getCheckedRadioButtonId()==R.id.radio_history?
                BleSDK.GetDynamicHRWithMode(mode,""):BleSDK.GetStaticHRWithMode(mode,""));

    }
}

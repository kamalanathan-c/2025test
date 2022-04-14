package com.jstyle.test2025.activity;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.jstyle.blesdk2025.Util.BleSDK;
import com.jstyle.blesdk2025.constant.BleConst;
import com.jstyle.test2025.R;
import com.jstyle.test2025.adapter.GpsDataAdapter;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



public class BloodOxygenActivity extends BaseActivity {

    @BindView(R.id.bt_readData)
    Button btReadData;
    @BindView(R.id.bt_DeleteData)
    Button btDeleteData;
    @BindView(R.id.RecyclerView_heartData)
    RecyclerView RecyclerViewHeartData;
    private GpsDataAdapter heartRateDataAdapter; ;
    byte ModeStart=0;
    byte ModeContinue=2;
    byte ModeDelete=(byte) 0x99;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oxy_info);
        ButterKnife.bind(this);
        init();
    }
    private void init() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerViewHeartData.setLayoutManager(linearLayoutManager);
        heartRateDataAdapter = new GpsDataAdapter();
        RecyclerViewHeartData.setAdapter(heartRateDataAdapter);
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        RecyclerViewHeartData.addItemDecoration(dividerItemDecoration);

    }

    @OnClick({R.id.bt_readData, R.id.bt_DeleteData,R.id.GetAutomaticSpo2Monitoring,R.id.bt_DeleteDataGetAutomaticSpo2Monitoring})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_readData:
                heartRateDataAdapter.Clear();
                getHeartHistoryData(ModeStart);
                break;
            case R.id.bt_DeleteData:
                heartRateDataAdapter.Clear();
                getHeartHistoryData(ModeDelete);
                break;
            case R.id.GetAutomaticSpo2Monitoring:
                heartRateDataAdapter.Clear();
                sendValue(BleSDK.Obtain_The_data_of_manual_blood_oxygen_test(ModeStart));
                break;
            case R.id.bt_DeleteDataGetAutomaticSpo2Monitoring:
                heartRateDataAdapter.Clear();
                sendValue(BleSDK.Obtain_The_data_of_manual_blood_oxygen_test(ModeDelete));
                break;
        }
    }




    int dataCount=0;
    private static final String TAG = "HeartRateInfoActivity";
    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType= getDataType(maps);
        boolean finish=getEnd(maps);
        switch (dataType){
            case BleConst.Blood_oxygen:
            case BleConst.GetAutomaticSpo2Monitoring:
            case BleConst.Delete_Obtain_The_data_of_manual_blood_oxygen_test:
                heartRateDataAdapter.ADDData(maps.toString());
                break;

        }
    }


    private void getHeartHistoryData(byte mode){
        sendValue(BleSDK.GetBloodOxygen(mode,""));

    }
}

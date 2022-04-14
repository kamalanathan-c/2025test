package com.jstyle.test2025.activity;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.jstyle.blesdk2025.Util.BleSDK;
import com.jstyle.blesdk2025.constant.BleConst;
import com.jstyle.blesdk2025.constant.DeviceKey;
import com.jstyle.test2025.R;
import com.jstyle.test2025.adapter.TotalDataAdapter;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TotalDataActivity extends BaseActivity {


    @BindView(R.id.bt_readData)
    Button btReadData;
    @BindView(R.id.bt_DeleteData)
    Button btDeleteData;
    @BindView(R.id.RecyclerView_totalData)
    RecyclerView RecyclerViewDetailData;
    private TotalDataAdapter toatlDataAdapter;
    byte ModeStart=0;
    byte ModeContinue=2;
    byte ModeDelete=(byte)0x99;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_data);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerViewDetailData.setLayoutManager(linearLayoutManager);
        toatlDataAdapter = new TotalDataAdapter();
        RecyclerViewDetailData.setAdapter(toatlDataAdapter);
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        RecyclerViewDetailData.addItemDecoration(dividerItemDecoration);
    }

    @OnClick({R.id.bt_readData, R.id.bt_DeleteData})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_readData:
                dataCount=0;
                list.clear();
               getTotalData(ModeStart);
                break;
            case R.id.bt_DeleteData:
                getTotalData(ModeDelete);
                break;
        }
    }
    List<Map<String,String>>list=new ArrayList<>();
    int dataCount=0;
    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType= getDataType(maps);
        boolean finish=getEnd(maps);
        switch (dataType){
            case BleConst.Delete_GetTotalActivityData:
                showDialogInfo(maps.toString());
                break;
            case BleConst.GetTotalActivityData:
                dataCount++;
                list.addAll((List<Map<String,String>>)maps.get(DeviceKey.Data));
                if(finish){
                    toatlDataAdapter.setData(list);
                }
                if(dataCount==50){
                   dataCount=0;
                    if(finish){
                        toatlDataAdapter.setData(list);
                    }else{
                        getTotalData(ModeContinue);
                    }
                }

                break;

        }

    }
    private void getTotalData(byte mode){
        sendValue(BleSDK.GetTotalActivityDataWithMode(mode,""));
    }
}

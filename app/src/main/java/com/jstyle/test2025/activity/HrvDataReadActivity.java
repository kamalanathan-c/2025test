package com.jstyle.test2025.activity;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.jstyle.blesdk2025.Util.BleSDK;
import com.jstyle.blesdk2025.constant.BleConst;
import com.jstyle.blesdk2025.constant.DeviceKey;
import com.jstyle.test2025.R;
import com.jstyle.test2025.adapter.HrvDataAdapter;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HrvDataReadActivity extends BaseActivity {


    @BindView(R.id.bt_readData)
    Button btReadData;
    @BindView(R.id.bt_DeleteData)
    Button btDeleteData;
    @BindView(R.id.RecyclerView_hrvData)
    RecyclerView RecyclerViewHrvData;
    private HrvDataAdapter hrvDataAdapter;
    byte ModeStart = 0;
    byte ModeContinue = 2;
    byte ModeDelete =(byte) 0x99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hrv_data_read);
        ButterKnife.bind(this);
        init();
    }

    int dataCount = 0;

    @OnClick({R.id.bt_readData, R.id.bt_DeleteData})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_readData:
                list.clear();
                dataCount=0;
                getHrvData(ModeStart);
                break;
            case R.id.bt_DeleteData:
                getHrvData(ModeDelete);
                break;
        }
    }

    List<Map<String, String>> list = new ArrayList<>();

    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        Log.e("info",maps.toString());
        String dataType = getDataType(maps);
        boolean finish = getEnd(maps);
        switch (dataType) {
            case BleConst.GetHRVData:
                dataCount++;
                list.addAll((List<Map<String, String>>) maps.get(DeviceKey.Data));
                if (finish) {
                    hrvDataAdapter.setData(list);
                }
                if (dataCount == 50) {
                    if (finish) {
                        hrvDataAdapter.setData(list);
                    } else {
                        getHrvData(ModeContinue);
                    }
                }
                break;
        }
    }

    private void init() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerViewHrvData.setLayoutManager(linearLayoutManager);
        hrvDataAdapter = new HrvDataAdapter();
        RecyclerViewHrvData.setAdapter(hrvDataAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        RecyclerViewHrvData.addItemDecoration(dividerItemDecoration);

    }

    private void getHrvData(byte mode) {
        sendValue(BleSDK.GetHRVDataWithMode(mode,""));
    }
}

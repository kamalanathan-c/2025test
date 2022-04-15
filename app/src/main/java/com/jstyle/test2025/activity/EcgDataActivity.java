package com.jstyle.test2025.activity;

import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.jstyle.blesdk2025.Util.BleSDK;
import com.jstyle.blesdk2025.constant.BleConst;
import com.jstyle.blesdk2025.constant.DeviceKey;
import com.jstyle.test2025.R;
import com.jstyle.test2025.adapter.ECGDataAdapter;
import com.jstyle.test2025.daomananger.EcgDataDaoManager;
import com.jstyle.test2025.model.EcgHistoryData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EcgDataActivity extends BaseActivity {

    @BindView(R.id.RecyclerView_exerciseHistory)
    RecyclerView ECGDATA;
    private ECGDataAdapter ecgDataAdapter;
    int index=0;

    private  String ecgDate;
    private String lastEcgDate;
    StringBuffer   stringBuffer;
    EcgHistoryData healthEcgData;
    String address="";
    List<EcgHistoryData> ecgDataList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecghisdata);
        ButterKnife.bind(this);
        address=getIntent().getStringExtra("address");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ECGDATA.setLayoutManager(linearLayoutManager);
        ecgDataAdapter = new ECGDataAdapter();
        ECGDATA.setAdapter(ecgDataAdapter);
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        ECGDATA.addItemDecoration(dividerItemDecoration);
        EcgHistoryData lastHealthData = EcgDataDaoManager.getLastEcgData(address);
        lastEcgDate = "";
        if (lastHealthData != null) {
            lastEcgDate = lastHealthData.getTime();
            Log.e("jsjsjsj",lastEcgDate);
        }
    }


    @OnClick({R.id.readECG,R.id.delete_ECG})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.readECG:
                index=0;
                ecgDataAdapter.Clear();
                ecgDataList.clear();
                sendValue(BleSDK.GetECGwaveform(true,0x00));
                break;
            case R.id.delete_ECG:
                sendValue(BleSDK.GetECGwaveform(false,0x00));
                ecgDataAdapter.Clear();
                break;
        }
    }

    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        if(null!=maps){
            String dataType= getDataType(maps);
            Log.e("dataCallback",maps.toString());
            switch (dataType){
                case BleConst.ECGdata:
                    boolean finish=getEnd(maps);
                    Map<String,String>ecgmaps= getData(maps);
                    if (null!=ecgmaps.get(DeviceKey.Date)) {
                        String KDate = ecgmaps.get(DeviceKey.Date);
                        ecgDate = KDate;
                        stringBuffer = new StringBuffer();
                        healthEcgData = new EcgHistoryData();
                        int hrv = Integer.parseInt(ecgmaps.get(DeviceKey.HRV));
                        int hr = Integer.parseInt(ecgmaps.get(DeviceKey.HeartRate));
                        int moodValue = Integer.parseInt(ecgmaps.get(DeviceKey.ECGMoodValue));
                        healthEcgData.setTime(ecgDate);
                        healthEcgData.setHrv(hrv);
                        healthEcgData.setHeartRate(hr);
                        healthEcgData.setBreathValue(moodValue);
                        healthEcgData.setAddress(address);
                    }
                    if (null!=ecgmaps.get(DeviceKey.ECGValue)) {
                        String ecgData = ecgmaps.get(DeviceKey.ECGValue);
                        stringBuffer.append(ecgData);
                    }
                    if (finish) {
                        if (!TextUtils.isEmpty(ecgDate)) {
                            Log.e("jsjsjsj","isEmpty(ecgDate)"+ecgDate+"***"+lastEcgDate);
                            healthEcgData.setArrayECGData(stringBuffer.toString());
                            ecgDataList.add(healthEcgData);
                            if ( !ecgDate.equals(lastEcgDate) &&index < 9) {
                                index++;
                                ecgDate = "";
                                sendValue(BleSDK.GetECGwaveform(true,index,lastEcgDate));
                            } else {
                                Log.e("jsjsjsj","end__ecgDate");
                                ecgDate = "";
                                index = 0;
                                ecgDataAdapter.setData(ecgDataList);
                                EcgDataDaoManager.insertData(ecgDataList);
                            }
                        } else {

                            Log.e("jsjsjsj","end__ecgDate");
                            ecgDate = "";
                            index = 0;
                            ecgDataAdapter.setData(ecgDataList);
                            EcgDataDaoManager.insertData(ecgDataList);
                        }
                    }

                    break;
                case BleConst.DeleteECGdata:
                    showDialogInfo(maps.toString());
                    break;
            }
        }
      }

}

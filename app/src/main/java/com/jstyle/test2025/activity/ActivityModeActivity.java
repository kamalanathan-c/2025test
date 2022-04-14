package com.jstyle.test2025.activity;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jstyle.blesdk2025.Util.BleSDK;
import com.jstyle.blesdk2025.constant.BleConst;
import com.jstyle.blesdk2025.constant.DeviceKey;
import com.jstyle.blesdk2025.model.ExerciseMode;
import com.jstyle.test2025.R;
import com.jstyle.test2025.adapter.ActivityModeAdapter;

import java.util.Map;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityModeActivity extends BaseActivity {

    @BindView(R.id.bt_startMode)
    Button btStartMode;
    @BindView(R.id.bt_stopMode)
    Button btStopMode;
    @BindView(R.id.tv_activityModeData)
    TextView tvActivityModeData;
    @BindView(R.id.RecyclerView_mode)
    RecyclerView RecyclerViewMode;
    @BindArray(R.array.mode_name)
    String[] modeName;
    private ActivityModeAdapter activityModeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        GridLayoutManager linearLayoutManager = new GridLayoutManager(this, 3);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerViewMode.setLayoutManager(linearLayoutManager);
        activityModeAdapter = new ActivityModeAdapter(modeName);
        RecyclerViewMode.setAdapter(activityModeAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL);
        DividerItemDecoration dividerItemDecorationVERTICAL = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        RecyclerViewMode.addItemDecoration(dividerItemDecoration);
        RecyclerViewMode.addItemDecoration(dividerItemDecorationVERTICAL);
    }

    int mode;

    @OnClick({R.id.bt_startMode, R.id.suspend, R.id.continues,R.id.bt_stopMode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_startMode:
                mode = activityModeAdapter.getSelectPosition();
                if(mode==-1)return;
                sendValue(BleSDK.EnterActivityMode(mode, ExerciseMode.Status_START));
                break;
            case R.id.suspend:
                mode = activityModeAdapter.getSelectPosition();
                if(mode==-1)return;
                sendValue(BleSDK.EnterActivityMode(mode, ExerciseMode.Status_PAUSE));
                break;

            case R.id.continues:
                mode = activityModeAdapter.getSelectPosition();
                if(mode==-1)return;
                sendValue(BleSDK.EnterActivityMode(mode, ExerciseMode.Status_CONTUINE));
                break;
            case R.id.bt_stopMode:
                mode = activityModeAdapter.getSelectPosition();
                if(mode==-1)return;
                sendValue(BleSDK.EnterActivityMode(mode, ExerciseMode.Status_FINISH));
                break;
        }

    }



    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType= getDataType(maps);
        boolean finish=getEnd(maps);
        switch (dataType){
            case BleConst.EnterActivityMode:
                Map<String,String>mapEnterActivityMode= getData(maps);
                int status = Integer.parseInt(mapEnterActivityMode.get(DeviceKey.enterActivityModeSuccess));
                if(0==status){
                    showToast("Please quit the exercise");
                }
                break;
            case BleConst.SportData:
                Map<String,String>map= getData(maps);
                String step = map.get(DeviceKey.Step);
                String cal = map.get(DeviceKey.Calories);
                String heart = map.get(DeviceKey.HeartRate);
                StringBuffer stringBuffer=new StringBuffer();
                stringBuffer.append("Step: "+step).append("\n")
                        .append("Cal: "+cal).append("\n")
                        .append("HeartRate: "+heart);
                tvActivityModeData.setText(stringBuffer.toString());
                break;
    }}


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}

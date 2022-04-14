package com.jstyle.test2025.activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.jstyle.blesdk2025.Util.BleSDK;
import com.jstyle.blesdk2025.constant.BleConst;
import com.jstyle.test2025.R;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MacActivity extends BaseActivity {

    @BindView(R.id.mac)
    AppCompatEditText mac;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get3d);
        ButterKnife.bind(this);


    }

    @OnClick({R.id.send})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.send:
                if(!TextUtils.isEmpty(mac.getText())){
                    sendValue(BleSDK.SetDeviceID(mac.getText().toString()));
                }
                break;

        }

    }


    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType= getDataType(maps);
        Log.e("dataCallback",maps.toString());
        switch (dataType){
            case BleConst.GetMAC:

                break;
        }}

}

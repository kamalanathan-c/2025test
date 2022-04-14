 package com.jstyle.test2025.activity;

 import android.os.Bundle;
 import android.util.Log;
 import android.view.View;


 import com.jstyle.blesdk2025.Util.BleSDK;
 import com.jstyle.blesdk2025.constant.BleConst;
 import com.jstyle.test2025.R;

 import java.util.Map;

 import butterknife.ButterKnife;
 import butterknife.OnClick;

 public class QRActivity extends BaseActivity {


     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_qr);
         ButterKnife.bind(this);
     }

     @OnClick({R.id.set, R.id.get})
     public void onViewClicked(View view) {
         switch (view.getId()) {
             case R.id.set:
                 sendValue(BleSDK.unlockScreen());
                 break;
             case R.id.get:
                 sendValue(BleSDK.lockScreen());
                 break;
         }
     }



     @Override
     public void dataCallback(Map<String, Object> maps) {
         super.dataCallback(maps);
         Log.e("info",maps.toString());
         String dataType= getDataType(maps);
         switch (dataType){
             case BleConst.ExitQRcode:
             case BleConst.EnterQRcode:
             case BleConst.QRcodebandBack:
                 showSetSuccessfulDialogInfo(maps.toString());
                 break;
         }
     }
 }

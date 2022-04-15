package com.jstyle.test2025.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.jstyle.blesdk2025.Util.BleSDK;
import com.jstyle.blesdk2025.constant.BleConst;
import com.jstyle.blesdk2025.constant.DeviceKey;
import com.jstyle.test2025.R;
import com.jstyle.test2025.Util.BleData;
import com.jstyle.test2025.Util.RxBus;
import com.jstyle.test2025.adapter.MainAdapter;
import com.jstyle.test2025.ble.BleManager;
import com.jstyle.test2025.ble.BleService;
import java.util.Map;
import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements MainAdapter.onItemClickListener {
    private static final String TAG = "MainActivity";
    @BindView(R.id.main_recyclerview)
    RecyclerView mainRecyclerview;
    @BindArray(R.array.item_options)
    String[] options;
    @BindView(R.id.BT_CONNECT)
    Button btConnect;
    @BindView(R.id.button_startreal)
    Button buttonStartreal;
    @BindView(R.id.textView_step)
    TextView textViewStep;
    @BindView(R.id.textView_cal)
    TextView textViewCal;
    @BindView(R.id.textView_distance)
    TextView textViewDistance;
    @BindView(R.id.textView_time)
    TextView textViewTime;
    @BindView(R.id.textView1)
    TextView textView1;
    @BindView(R.id.textView_heartValue)
    TextView textViewHeartValue;
    @BindView(R.id.textView_tempValue)
    TextView textViewTempValue;
    @BindView(R.id.textView_ActiveTime)
    TextView textViewActiveTime;
    @BindView(R.id.SwitchCompat_temp)
    SwitchCompat SwitchCompatTemp;
    private ProgressDialog progressDialog;
    private Disposable subscription;
    private String address;
    boolean isStartReal;
    public static int phoneDataLength = 200;//手机一个包能发送的最多数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
        connectDevice();
    }

    private void connectDevice() {
        address = getIntent().getStringExtra("address");
        if (TextUtils.isEmpty(address)) {
            Log.i(TAG, "onCreate: address null ");
            return;
        }
        Log.i(TAG, "onCreate: ");
        BleManager.getInstance().connectDevice(address);
        showConnectDialog();
    }

    private void showConnectDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.connectting));
        if (!progressDialog.isShowing()) progressDialog.show();

    }

    private void dissMissDialog() {
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
    }

    private void init() {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        mainRecyclerview.setLayoutManager(gridLayoutManager);
        final MainAdapter mainAdapter = new MainAdapter(options, this);
        mainRecyclerview.setAdapter(mainAdapter);
        subscription = RxBus.getInstance().toObservable(BleData.class).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<BleData>() {
            @Override
            public void accept(BleData bleData) throws Exception {
                String action = bleData.getAction();
                if (action.equals(BleService.ACTION_GATT_onDescriptorWrite)) {
                    mainAdapter.setEnable(true);
                    btConnect.setEnabled(false);
                    buttonStartreal.setEnabled(true);
                    SwitchCompatTemp.setEnabled(true);
                    dissMissDialog();
                } else if (action.equals(BleService.ACTION_GATT_DISCONNECTED)) {
                    mainAdapter.setEnable(false);
                    btConnect.setEnabled(true);
                    buttonStartreal.setEnabled(false);
                    SwitchCompatTemp.setEnabled(false);
                    isStartReal = false;
                    dissMissDialog();
                }
            }
        });
        SwitchCompatTemp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sendValue(BleSDK.RealTimeStep(isStartReal,isChecked));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unsubscribe();
        if (BleManager.getInstance().isConnected()) BleManager.getInstance().disconnectDevice();
    }

    private void unsubscribe() {
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
            Log.i(TAG, "unSubscribe: ");
        }
    }

    @OnClick({R.id.BT_CONNECT, R.id.button_startreal,R.id.EnterPhotoMode,R.id.Exitphotomode
    ,R.id.ClearBraceletData})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ClearBraceletData:
                sendValue(BleSDK.ClearBraceletData());
                break;
            case R.id.BT_CONNECT:
                connectDevice();
                break;
            case R.id.button_startreal:
                isStartReal = !isStartReal;
                buttonStartreal.setText(isStartReal ? "Stop" : "Start");

                sendValue(BleSDK.RealTimeStep(isStartReal,SwitchCompatTemp.isChecked()));
                break;
            case R.id.EnterPhotoMode:
                sendValue(BleSDK.EnterPhotoMode());
                break;
            case R.id.Exitphotomode:
                sendValue(BleSDK.ExitPhotoMode());
                break;
        }

    }

    @Override
    public void onItemClick(int position) {
        Intent intent;
        switch (position) {
            case 0:
                startActivity(new Intent(MainActivity.this, BasicActivity.class));
                break;
            case 1:
                startActivity(new Intent(MainActivity.this, DeviceInfoActivity.class));
                break;
            case 2:
                startActivity(new Intent(MainActivity.this, DetailDataActivity.class));
                break;
            case 3:
                startActivity(new Intent(MainActivity.this, TotalDataActivity.class));
                break;
            case 4:
                startActivity(new Intent(MainActivity.this, HeartRateInfoActivity.class));
                break;
            case 5:
                startActivity(new Intent(MainActivity.this, HeartRateSetActivity.class));
                break;
            case 6:
                startActivity( new Intent(MainActivity.this, AlarmListActivity.class));
                break;
            case 7:
                startActivity(new Intent(MainActivity.this, ActivityAlarmSetActivity.class));
                break;
            case 8:
                startActivity(new Intent(MainActivity.this, ExerciseHistoryDataActivity.class));
                break;
            case 9:
                startActivity( new Intent(MainActivity.this, HrvDataReadActivity.class));
                break;
            case 10:
                startActivity(new Intent(MainActivity.this, ActivityModeActivity.class));
                break;
            case 11:
                startActivity( new Intent(MainActivity.this, NotifyActivity.class));
                break;
            case 12:
                startActivity(new Intent(MainActivity.this, SetGoalActivity.class));
                break;
            case 13:
                startActivity(new Intent(MainActivity.this, BloodOxygenActivity.class));
                break;
            case 14:
                startActivity(new Intent(MainActivity.this, SocialDistanceActivity.class));
                break;
            case 15:
                startActivity(new Intent(MainActivity.this, TemperatureHistoryActivity.class));

                break;
            case 16:
                startActivity(new Intent(MainActivity.this, MacActivity.class));
                break;
            case 17:
                startActivity(new Intent(MainActivity.this, EcginfoActivity.class));
                break;
            case 18:
                startActivity(new Intent(MainActivity.this, EcgActivity.class));
                break;
            case 19:
                startActivity(new Intent(MainActivity.this, EcgPPgStatusActivity.class));
                break;
             case 20:
                 Intent intent1=      new Intent(MainActivity.this, EcgDataActivity.class);
                 intent1.putExtra("address",address) ;
                startActivity(intent1);
                break;
             case 21:
                startActivity(new Intent(MainActivity.this, MeasurementActivity.class));
                break;
             case 22:
                startActivity(new Intent(MainActivity.this, QRActivity.class));
                break;
            case 23:
                startActivity(new Intent(MainActivity.this, WeatherActivity.class));
                break;

            default:
                break;
        }

    }

    @Override
    public void dataCallback(Map<String, Object> map) {
        super.dataCallback(map);
        String dataType = getDataType(map);
        Log.e("info",map.toString());
        switch (dataType) {
            case BleConst.ReadSerialNumber:
                showDialogInfo(map.toString());
                break;
            case BleConst.RealTimeStep:
                Map<String, String> maps = getData(map);
                String step = maps.get(DeviceKey.Step);
                String cal = maps.get(DeviceKey.Calories);
                String distance = maps.get(DeviceKey.Distance);
                String time = maps.get(DeviceKey.ExerciseMinutes);
                String ActiveTime = maps.get(DeviceKey.ActiveMinutes);
                String heart = maps.get(DeviceKey.HeartRate);
                String TEMP= maps.get(DeviceKey.TempData);
                textViewCal.setText(cal);
                textViewStep.setText(step);
                textViewDistance.setText(distance);
                textViewTime.setText(time);
                textViewHeartValue.setText(heart);
                textViewActiveTime.setText(ActiveTime);
                textViewTempValue.setText(TEMP);
                break;
            case BleConst.DeviceSendDataToAPP:
                //showDialogInfo(BleConst.DeviceSendDataToAPP);
                break;
            case BleConst.FindMobilePhoneMode:
                //showDialogInfo(BleConst.FindMobilePhoneMode);
                break;
            case BleConst.RejectTelMode:
                //showDialogInfo(BleConst.RejectTelMode);
                break;
            case BleConst.TelMode:
                //showDialogInfo(BleConst.TelMode);
                break;
            case BleConst.BackHomeView:
                showToast(map.toString());
                break;
            case BleConst.Sos:
                showToast(map.toString());
                break;
        }
    }
}

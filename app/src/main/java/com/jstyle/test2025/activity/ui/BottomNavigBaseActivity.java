package com.jstyle.test2025.activity.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.jstyle.blesdk2025.Util.BleSDK;
import com.jstyle.blesdk2025.Util.ResolveUtil;
import com.jstyle.blesdk2025.constant.BleConst;
import com.jstyle.blesdk2025.constant.DeviceConst;
import com.jstyle.blesdk2025.constant.DeviceKey;
import com.jstyle.test2025.R;

import com.jstyle.test2025.Util.BleData;
import com.jstyle.test2025.Util.RxBus;
import com.jstyle.test2025.activity.BaseActivity;
import com.jstyle.test2025.activity.ui.ui.ECG.EcgFragment;
import com.jstyle.test2025.activity.ui.ui.Me.MeFragment;
import com.jstyle.test2025.activity.ui.ui.home.HomeFragment;
import com.jstyle.test2025.activity.ui.ui.health.HealthFragment;
import com.jstyle.test2025.ble.BleManager;
import com.jstyle.test2025.ble.BleService;
import com.jstyle.test2025.databinding.ActivityBottomNavigBaseBinding;
import com.neurosky.AlgoSdk.NskAlgoDataType;
import com.neurosky.AlgoSdk.NskAlgoSdk;

import java.io.Serializable;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class BottomNavigBaseActivity extends BaseActivity {

    private ActivityBottomNavigBaseBinding binding;
    private String address;
    private ProgressDialog progressDialog;
    private static final String TAG = "BottomNavigBaseActivity";
    private Disposable subscription;
    Map<String, String> maps;
    BottomNavigationView navView;
    int raw_data_index = 0;
    private NskAlgoSdk nskAlgoSdk;
    boolean isStartReal;
    Fragment selectedFragment = null;
    private boolean ishomeloaded = false;

    @Override
    protected void onStart() {
        super.onStart();
        try {
            init();
            connectDevice();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBottomNavigBaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        nskAlgoSdk = new NskAlgoSdk();
        nskAlgoSdk.NskAlgoUninit();
        navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_bottom_navig_base);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        // NavigationUI.setupWithNavController(binding.navView, navController);
        navView.setOnItemSelectedListener(navListener);


    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // By using switch we can easily get
            // the selected fragment
            // by using there id.
            // Fragment selectedFragment = null;
            // sendValue(BleSDK.RealTimeStep(true,true));
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    showConnectDialog();
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.navigation_dashboard:
                    showConnectDialog();
                    selectedFragment = new EcgFragment();
                    break;
                case R.id.navigation_notifications:
                    showConnectDialog();
                    sendValue(BleSDK.StartDeviceMeasurementWithType(3, true));
                    selectedFragment = new HealthFragment();
                    break;
              /*  case R.id.navigation_me:
                    showConnectDialog();
                    selectedFragment = new MeFragment();
                    break;*/
            }
            // It will help to replace the
            // one fragment to other.
            Bundle b = new Bundle();
            b.putSerializable("map", (Serializable) maps);
            selectedFragment.setArguments(b);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_bottom_navig_base, selectedFragment)
                    .commit();
            return true;
        }
    };

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
        subscription = RxBus.getInstance().toObservable(BleData.class).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<BleData>() {
            @Override
            public void accept(BleData bleData) throws Exception {
                String action = bleData.getAction();
                if (action.equals(BleService.ACTION_GATT_onDescriptorWrite)) {
                    dissMissDialog();
                } else if (action.equals(BleService.ACTION_GATT_DISCONNECTED)) {
                    dissMissDialog();
                }
                sendValue(BleSDK.RealTimeStep(true, true));
            }
        });
    }


    @Override
    public void dataCallback(Map<String, Object> map) {
        super.dataCallback(map);
        String dataType = getDataType(map);
        Log.e("info", map.toString());
        switch (dataType) {
            case BleConst.ReadSerialNumber:
                showDialogInfo(map.toString());
                break;
            case BleConst.RealTimeStep:
                maps = getData(map);
                if (!ishomeloaded) {
                    selectedFragment = new HomeFragment();
                    Bundle b = new Bundle();
                    b.putSerializable("map", (Serializable) maps);
                    selectedFragment.setArguments(b);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.nav_host_fragment_activity_bottom_navig_base, selectedFragment)
                            .commit();
                    ishomeloaded = true;
                }

                dissMissDialog();
                break;
            case BleConst.MeasurementHrvCallback:
            case BleConst.MeasurementHeartCallback:
            case BleConst.MeasurementOxygenCallback:
                maps = getData(map);
                selectedFragment = new HealthFragment();
                Bundle b = new Bundle();
                b.putSerializable("map", (Serializable) maps);
                selectedFragment.setArguments(b);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_bottom_navig_base, selectedFragment)
                        .commit();
                sendValue(BleSDK.StartDeviceMeasurementWithType(3, false));
                dissMissDialog();
                break;
            default:
                break;
        }
    }
}
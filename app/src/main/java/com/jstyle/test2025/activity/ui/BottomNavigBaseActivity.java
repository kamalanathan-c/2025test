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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        connectDevice();
        binding = ActivityBottomNavigBaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

       navView  = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications,R.id.navigation_me)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_bottom_navig_base);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        navView.setOnItemSelectedListener(navListener);
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // By using switch we can easily get
            // the selected fragment
            // by using there id.
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.navigation_dashboard:
                    selectedFragment = new EcgFragment();
                    break;
                case R.id.navigation_notifications:
                    selectedFragment = new HealthFragment();
                    break;
                case R.id.navigation_me:
                    selectedFragment = new MeFragment();
                    break;
            }
            // It will help to replace the
            // one fragment to other.
            Bundle b= new Bundle();
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
            }
        });
    }
    @Override
    public void dataCallback(Map<String, Object> map) {
        super.dataCallback(map);
         maps = getData(map);
        String heart = maps.get(DeviceKey.HeartRate);
        Log.e("info",map.toString());

    }
}
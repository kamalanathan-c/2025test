package com.jstyle.test2025.activity;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.jstyle.blesdk2025.Util.BleSDK;
import com.jstyle.blesdk2025.Util.ResolveUtil;
import com.jstyle.blesdk2025.constant.BleConst;
import com.jstyle.blesdk2025.constant.DeviceConst;
import com.jstyle.blesdk2025.constant.DeviceKey;
import com.jstyle.test2025.R;
import com.jstyle.test2025.Util.ChartDataUtil;
import com.jstyle.test2025.Util.PermissionsUtil;
import com.neurosky.AlgoSdk.NskAlgoDataType;
import com.neurosky.AlgoSdk.NskAlgoECGValueType;
import com.neurosky.AlgoSdk.NskAlgoProfile;
import com.neurosky.AlgoSdk.NskAlgoSampleRate;
import com.neurosky.AlgoSdk.NskAlgoSdk;
import com.neurosky.AlgoSdk.NskAlgoType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import lecho.lib.hellocharts.view.LineChartView;

public class EcgActivity extends BaseActivity {
    private static final String TAG = "EcgActivity";
    @BindView(R.id.heartValue)
    TextView heartValue;
    @BindView(R.id.hrvValue)
    TextView hrvValue;
    @BindView(R.id.Quality)
    TextView Quality;
    @BindView(R.id.info)
    TextView info;
    @BindView(R.id.radioGroup_mian)
    RadioGroup radioGroup_mian;


    @BindView(R.id.lineChartView_ecg)
    LineChartView lineChartView_ecg;

    @BindView(R.id.lineChartView_ppg)
    LineChartView lineChartView_ppg;
    List<Double> queueEcg = new ArrayList<>();
    List<Float> queuePpg = new ArrayList<>();
    private int index = 0;
    int raw_data_index = 0;
    private int handStatus;
    Disposable ppgDisposable;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecg);
        ButterKnife.bind(this);
        PermissionsUtil.requestPermissions( EcgActivity.this, new PermissionsUtil.PermissionListener() {
            @Override
            public void granted(String name) {
                sendValue(BleSDK.GetDeviceInfo());
            }
            @Override
            public void NeverAskAgain() { }
            @Override
            public void disallow(String name) { }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        radioGroup_mian.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radio_1:
                        MeasureTimes = 90;
                        break;
                    case R.id.radio_2:
                        MeasureTimes = 300;
                        break;
                    case R.id.radio_3:
                        MeasureTimes = 400;
                        break;
                }
            }
        });


    }
    private NskAlgoSdk nskAlgoSdk;
    private void init() {
        queueEcg.clear();
        queuePpg.clear();
        nskAlgoSdk = new NskAlgoSdk();
        nskAlgoSdk.NskAlgoUninit();
        nskAlgoSdk.setOnECGAlgoIndexListener(new NskAlgoSdk.OnECGAlgoIndexListener() {
            @Override
            public void onECGAlgoIndex(int type, final int value) {

                switch (type) {
                    case NskAlgoECGValueType.NSK_ALGO_ECG_VALUE_TYPE_SMOOTH:
                        queueEcg.add((double) value);
                        int size = queueEcg.size();
                        if (size >= 1536) {
                            index++;
                        }
                        Log.e("sdadasd","onECGAlgoIndex");
                        if (size % 79 == 0) {
                            lineChartView_ecg.setLineChartData(ChartDataUtil.getEcgLineChartData(EcgActivity.this, queueEcg, Color.RED, 4, index));
                        }
                        break;
                    case NskAlgoECGValueType.NSK_ALGO_ECG_VALUE_TYPE_ROBUST_HR:
                        break;
                    case NskAlgoECGValueType.NSK_ALGO_ECG_VALUE_TYPE_HRV:
                        break;
                }
            }
        });
        loadsetup();
        setAlgos();
        nskAlgoSdk.NskAlgoStart(false);
       ChartDataUtil.initDataChartView(lineChartView_ecg, 0, 8000, 1536, -8000);
        lineChartView_ecg.setLineChartData(ChartDataUtil.getEcgLineChartData(EcgActivity.this, queueEcg, Color.RED, 4, index));
        sendValue(BleSDK.enableEcgPPg(4,MeasureTimes));

        Observable.interval(100, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                ppgDisposable = d;
            }

            @Override
            public void onNext(Long aLong) {
                lineChartView_ppg.setLineChartData(ChartDataUtil.getPpgChartData(lineChartView_ppg,EcgActivity.this, queuePpg, Color.RED));
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });


    }

    private int MeasureTimes = 90;

    @OnClick({R.id.open,R.id.close})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.open:
                sendValue(BleSDK.enableEcgPPg(4,MeasureTimes));
                break;
            case R.id.close:
                sendValue(BleSDK.stopEcgPPg());
                break;
        }

    }


    @Override
    public void dataCallback(byte[] value) {
        switch (value[0]) {
            case DeviceConst.CMD_ECGDATA:
                if (raw_data_index == 0 || raw_data_index % 200 == 0) {
                    // send the good signal for every half second
                    short pqValue[] = {(short) 200};
                    nskAlgoSdk.NskAlgoDataStream(NskAlgoDataType.NSK_ALGO_DATA_TYPE_ECG_PQ, pqValue, 1);
                }
                for (int i = 0; i < value.length / 2 - 1; i++) {
                    int ecgValueAction = ResolveUtil.getValue(value[i * 2 + 1], 1) + ResolveUtil.getValue(value[i * 2 + 2], 0);
                    if (ecgValueAction >= 32768) ecgValueAction = ecgValueAction - 65536;
                    raw_data_index++;
                    short[] ecgData = new short[]{(short) -ecgValueAction};
                    nskAlgoSdk.NskAlgoDataStream(NskAlgoDataType.NSK_ALGO_DATA_TYPE_ECG, ecgData, 1);//this
                }
                break;
            case  DeviceConst.CMD_PPGGDATA:
                double maxPPG = 0;
                double minPPg = 33000;
                for (int i = 0; i < value.length / 2 - 1; i++) {
                    float ppgValue = ResolveUtil.getValue(value[i * 2 + 1], 1) + ResolveUtil.getValue(value[i * 2 + 2], 0);
                    if (ppgValue >= 32768) ppgValue = ppgValue - 65536;
                    if (queuePpg.size() > 600) queuePpg.remove(0);
                    ppgValue=ppgValue*(handStatus*2-1);
                    queuePpg.add(ppgValue);
                    maxPPG = Math.max(maxPPG, ppgValue);
                    minPPg = Math.min(minPPg, ppgValue);
                }


                break;
            case DeviceConst.CMD_Get_DeviceInfo:
              handStatus = ResolveUtil.getValue(value[4], 0);//1左手，0右手
                init();
                break;

        }
    }
    @Override
    public void dataCallback(Map<String, Object> maps) {
        super.dataCallback(maps);
        String dataType = getDataType(maps);
     Log.e("ddddddd",maps.toString());
        switch (dataType) {
            case BleConst.EcgppG:
                Map<String, Object> mapsa=(Map<String, Object>)maps.get(DeviceKey.Data);
                heartValue.setText("heartValue: "+mapsa.get("heartValue").toString());
                hrvValue.setText("hrvValue: "+mapsa.get("hrvValue").toString());
                Quality.setText("Quality: "+mapsa.get("Quality").toString());
                break;
           /* case  BleConst.EcgppandEcg:
                info.setText(maps.toString());
                break;*/
        }

    }




    private String license = "";
    private void loadsetup() {
        AssetManager assetManager = this.getAssets();
        InputStream inputStream = null;

        try {
            String prefix = "license key=\"";
            String suffix = "\"";
            String pattern = prefix + "(.+?)" + suffix;
            Pattern p = Pattern.compile(pattern);

            inputStream = assetManager.open("license.txt");
            ArrayList<String> data = new ArrayList<String>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                while (true) {
                    String line = reader.readLine();
                    if (line == null || line.isEmpty())
                        break;
                    Matcher m = p.matcher(line);
                    if (m.find()) {
                        license = line.substring(m.regionStart() + prefix.length(), m.regionEnd() - suffix.length());
                        break;
                    }
                }
            } catch (IOException e) {

            }
            inputStream.close();
        } catch (IOException e) { }
        try {
            inputStream = assetManager.open("setupinfo.txt");
            ArrayList<String> data = new ArrayList<String>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                while (true) {
                    String line = reader.readLine();
                    if (line == null || line.isEmpty()) {
                        break;
                    }
                    data.add(line);
                }
            } catch (IOException e) { }
            inputStream.close();
        } catch (IOException e) { }
    }


    private int activeProfile = -1;
    private int currentSelectedAlgo;
    private void setAlgos() {
        String path = this.getFilesDir().getAbsolutePath();
        currentSelectedAlgo |= NskAlgoType.NSK_ALGO_TYPE_ECG_HRVFD;
        currentSelectedAlgo |= NskAlgoType.NSK_ALGO_TYPE_ECG_HRVTD;
        currentSelectedAlgo |= NskAlgoType.NSK_ALGO_TYPE_ECG_HRV;
        currentSelectedAlgo |= NskAlgoType.NSK_ALGO_TYPE_ECG_SMOOTH;
        currentSelectedAlgo |= NskAlgoType.NSK_ALGO_TYPE_ECG_HEARTAGE;
        currentSelectedAlgo |= NskAlgoType.NSK_ALGO_TYPE_ECG_HEARTRATE;
        currentSelectedAlgo |= NskAlgoType.NSK_ALGO_TYPE_ECG_MOOD;
        currentSelectedAlgo |= NskAlgoType.NSK_ALGO_TYPE_ECG_RESPIRATORY;
        currentSelectedAlgo |= NskAlgoType.NSK_ALGO_TYPE_ECG_STRESS;
        int ret = nskAlgoSdk.NskAlgoInit(currentSelectedAlgo, path, license);
        if (ret == 0) {
            Log.i(TAG, "setAlgos: Algo SDK has been initialized successfully");
        } else {
            Log.i(TAG, "Failed to initialize the SDK, code = " + String.valueOf(ret));
            return;
        }
        boolean b = nskAlgoSdk.setBaudRate(NskAlgoDataType.NSK_ALGO_DATA_TYPE_ECG, NskAlgoSampleRate.NSK_ALGO_SAMPLE_RATE_512);
        if (b != true) {
            Log.i(TAG, "setAlgos: Failed to set the sampling rate");
            return;
        }
        NskAlgoProfile[] profiles = nskAlgoSdk.NskAlgoProfiles();
        if (profiles.length == 0) {
            // create a default profile
            try {
                String dobStr = "1995-1-1";
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Date dob = df.parse(dobStr);

                NskAlgoProfile profile = new NskAlgoProfile();
                profile.name = "bob";
                profile.height = 170;
                profile.weight = 80;
                profile.gender = false;
                profile.dob = dob;
                if (nskAlgoSdk.NskAlgoProfileUpdate(profile) == false) { }

                profiles = nskAlgoSdk.NskAlgoProfiles();
                // setup the ECG config
                // assert(nskAlgoSdk.NskAlgoSetECGConfigAfib((float)3.5) == true);
              /*  assert (nskAlgoSdk.NskAlgoSetECGConfigStress(30, 30) == true);
                assert (nskAlgoSdk.NskAlgoSetECGConfigHeartage(30) == true);
                assert (nskAlgoSdk.NskAlgoSetECGConfigHRV(30) == true);
                assert (nskAlgoSdk.NskAlgoSetECGConfigHRVTD(30, 30) == true);
                assert (nskAlgoSdk.NskAlgoSetECGConfigHRVFD(30, 30) == true);*/

                // nskAlgoSdk.setSignalQualityWatchDog((short)20, (short)5);
                // retrieve the baseline data
                if (profiles.length > 0) {
                    activeProfile = profiles[0].userId;
                    nskAlgoSdk.NskAlgoProfileActive(activeProfile);
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
                    String stringArray = settings.getString("ecgbaseline", null);
                    if (stringArray != null) {
                        String[] split = stringArray.substring(1, stringArray.length() - 1).split(", ");
                        byte[] array = new byte[split.length];
                        for (int i = 0; i < split.length; ++i) {
                            array[i] = Byte.parseByte(split[i]);
                        }
                        if (nskAlgoSdk.NskAlgoProfileSetBaseline(activeProfile, NskAlgoType.NSK_ALGO_TYPE_ECG_HEARTRATE, array) != true) {
                        }
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ppgDisposable != null && !ppgDisposable.isDisposed()) {
            ppgDisposable.dispose();
        }
    }
}

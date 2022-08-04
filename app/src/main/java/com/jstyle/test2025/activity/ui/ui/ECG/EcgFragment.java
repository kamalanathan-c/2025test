package com.jstyle.test2025.activity.ui.ui.ECG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jstyle.blesdk2025.constant.DeviceKey;
import com.jstyle.test2025.R;
import com.jstyle.test2025.Util.ChartDataUtil;
import com.jstyle.test2025.activity.EcgActivity;
import com.jstyle.test2025.databinding.FragmentEcgBinding;
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
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import lecho.lib.hellocharts.view.LineChartView;


public class EcgFragment extends Fragment {

    private FragmentEcgBinding binding;
    Map<String, String> maps;
    @BindView(R.id.lineChartView_ecg)
    LineChartView lineChartView_ecg;
    private NskAlgoSdk nskAlgoSdk;
    List<Double> queueEcg = new ArrayList<>();
    private int index = 0;
    private static final String TAG = "EcgActivity";
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EcgViewModel ecgViewModel =
                new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(EcgViewModel.class);

        binding = FragmentEcgBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        try {
            maps = (Map<String, String>) getArguments().getSerializable("map");
        final TextView textView_ecg_hr = binding.textViewEcgHr;
        final TextView textView_ecg_hrv = binding.textViewEcgHrv;
            textView_ecg_hr.setText(maps.get(DeviceKey.HeartRate));
            textView_ecg_hrv.setText(maps.get(DeviceKey.hrvValue));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Button bt_startEcg = binding.btStartEcg;
        bt_startEcg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //initializeEcg();
                Intent i = new Intent(getContext(),EcgActivity.class);
                startActivity(i);
            }
        });
        //ecgViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    public void initializeEcg()

         {
        queueEcg.clear();
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
                            lineChartView_ecg.setLineChartData(ChartDataUtil.getEcgLineChartData(getActivity(), queueEcg, Color.RED, 4, index));
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
        lineChartView_ecg.setLineChartData(ChartDataUtil.getEcgLineChartData(getActivity(), queueEcg, Color.RED, 4, index));
    }
    private String license = "";
    private void loadsetup() {
        AssetManager assetManager = getActivity().getAssets();
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
        String path = getActivity().getFilesDir().getAbsolutePath();
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
                assert (nskAlgoSdk.NskAlgoSetECGConfigStress(30, 30) == true);
                assert (nskAlgoSdk.NskAlgoSetECGConfigHeartage(30) == true);
                assert (nskAlgoSdk.NskAlgoSetECGConfigHRV(30) == true);
                assert (nskAlgoSdk.NskAlgoSetECGConfigHRVTD(30, 30) == true);
                assert (nskAlgoSdk.NskAlgoSetECGConfigHRVFD(30, 30) == true);

                // nskAlgoSdk.setSignalQualityWatchDog((short)20, (short)5);
                // retrieve the baseline data
                if (profiles.length > 0) {
                    activeProfile = profiles[0].userId;
                    nskAlgoSdk.NskAlgoProfileActive(activeProfile);
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
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
}
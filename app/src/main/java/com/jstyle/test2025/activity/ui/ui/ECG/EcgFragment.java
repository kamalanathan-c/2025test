package com.jstyle.test2025.activity.ui.ui.ECG;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jstyle.blesdk2025.constant.DeviceKey;
import com.jstyle.test2025.databinding.FragmentEcgBinding;

import java.util.Map;


public class EcgFragment extends Fragment {

    private FragmentEcgBinding binding;
    Map<String, String> maps;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EcgViewModel ecgViewModel =
                new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(EcgViewModel.class);

        binding = FragmentEcgBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        maps = (Map<String, String>) getArguments().getSerializable("map");
        final TextView textView_ecg_hr = binding.textViewEcgHr;
        final TextView textView_ecg_hrv = binding.textViewEcgHrv;
        textView_ecg_hr.setText("1"+maps.get(DeviceKey.HeartRate));
        textView_ecg_hrv.setText("2"+maps.get(DeviceKey.hrvValue));
        //ecgViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
package com.jstyle.test2025.activity.ui.ui.health;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jstyle.blesdk2025.constant.DeviceKey;
import com.jstyle.test2025.databinding.FragmentHealthBinding;

import java.util.Map;


public class HealthFragment extends Fragment {

    private FragmentHealthBinding binding;
    Map<String, String> maps;
    DatePicker datePicker;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HealthViewModel healthViewModel =
                new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(HealthViewModel.class);

        binding = FragmentHealthBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        try {
            maps = (Map<String, String>) getArguments().getSerializable("map");
            final TextView bb_minvalue = binding.bbMinvalue;
            final TextView bb_maxvalue = binding.bbMaxvalue;
            final TextView hrvvalue = binding.hrvvalue;
            final TextView stress_value = binding.stressValue;
            bb_minvalue.setText(maps.get(DeviceKey.LowPressure));
            bb_maxvalue.setText(maps.get(DeviceKey.HighPressure));
            hrvvalue.setText(maps.get(DeviceKey.HRV));
            stress_value.setText(maps.get(DeviceKey.Stress));
        } catch (Exception e) {
            e.printStackTrace();
        }
         datePicker = binding.datePicker;
        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Toast.makeText(getContext()," You are changed date is : "+dayOfMonth +" - "+monthOfYear+ " - "+year,Toast.LENGTH_LONG).show();
            }
        });
      //  healthViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
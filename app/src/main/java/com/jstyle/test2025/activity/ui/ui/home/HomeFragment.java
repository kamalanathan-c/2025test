package com.jstyle.test2025.activity.ui.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jstyle.blesdk2025.constant.DeviceKey;
import com.jstyle.test2025.activity.MainActivity;
import com.jstyle.test2025.activity.ui.BottomNavigBaseActivity;
import com.jstyle.test2025.databinding.FragmentHomeBinding;

import java.util.Map;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    Map<String, String> map;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        View root = binding.getRoot();
        map = (Map<String, String>) getArguments().getSerializable("map");
        String time=map.get(DeviceKey.ExerciseMinutes);
        String totalStep=map.get(DeviceKey.Step);
        String distance=map.get(DeviceKey.Distance);
        String cal=map.get(DeviceKey.Calories);
        String goal=map.get(DeviceKey.Goal);
        String date=map.get(DeviceKey.Date);
        TextView txt_stepcount = binding.txtStepcount;
        txt_stepcount.setText(totalStep);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
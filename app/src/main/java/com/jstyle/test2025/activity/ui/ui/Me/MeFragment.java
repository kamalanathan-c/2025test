package com.jstyle.test2025.activity.ui.ui.Me;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jstyle.test2025.R;
import com.jstyle.test2025.activity.ui.ui.home.HomeViewModel;
import com.jstyle.test2025.databinding.FragmentHomeBinding;
import com.jstyle.test2025.databinding.FragmentMeBinding;

public class MeFragment extends Fragment {
    private FragmentMeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MeViewModel meViewModel =
                new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(MeViewModel.class);
        // Inflate the layout for this fragment
        binding = FragmentMeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
         TextView textMe = binding.textMe;
        textMe.setText("This is Me fragment");
        //meViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
       // return inflater.inflate(R.layout.fragment_me, container, false);
    }
}
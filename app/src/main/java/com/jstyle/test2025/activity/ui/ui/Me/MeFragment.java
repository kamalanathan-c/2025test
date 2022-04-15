package com.jstyle.test2025.activity.ui.ui.Me;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jstyle.test2025.R;
import com.jstyle.test2025.databinding.FragmentHomeBinding;
import com.jstyle.test2025.databinding.FragmentMeBinding;

public class MeFragment extends Fragment {
    private FragmentMeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

       /* final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);*/
        return root;
       // return inflater.inflate(R.layout.fragment_me, container, false);
    }
}
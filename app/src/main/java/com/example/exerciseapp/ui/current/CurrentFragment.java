package com.example.exerciseapp.ui.current;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.exerciseapp.R;
import com.example.exerciseapp.databinding.FragmentCurrentBinding;

public class CurrentFragment extends Fragment {
    private CurrentViewModel currentViewModel;

    public CurrentFragment() {super(R.layout.fragment_current);}

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        currentViewModel = new ViewModelProvider(getActivity()).get(CurrentViewModel.class);

        FragmentCurrentBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_current, container, false);

        currentViewModel.getActivity().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.activityText.setText(s);
            }
        });

        currentViewModel.getClock().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.clockText.setText(s);
            }
        });

        currentViewModel.getCalories().observe(getViewLifecycleOwner(), new Observer<Float>() {
            @Override
            public void onChanged(Float f) {
                binding.caloriesText.setText(String.valueOf(f));
            }
        });

        currentViewModel.getSpeed().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                binding.speedText.setText(String.format("%d m/s", integer));
            }
        });

        currentViewModel.getInactivity().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == 0) binding.inactivityText.setVisibility(View.INVISIBLE);

                else {
                    binding.inactivityText.setVisibility(View.VISIBLE);
                    binding.inactivityText.setText(String.format("%d seconds before exercise is aborted", integer));
                }
            }
        });
        return binding.getRoot();
    }
}
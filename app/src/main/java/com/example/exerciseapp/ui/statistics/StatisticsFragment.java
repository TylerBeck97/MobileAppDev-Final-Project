package com.example.exerciseapp.ui.statistics;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.exerciseapp.LoginActivity;
import com.example.exerciseapp.databinding.FragmentStatisticsBinding;
import com.google.firebase.auth.FirebaseAuth;

public class StatisticsFragment extends Fragment {

    private StatisticsViewModel statisticsViewModel;
    private FragmentStatisticsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statisticsViewModel =
                new ViewModelProvider(this).get(StatisticsViewModel.class);

        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        statisticsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String[]>() {
            @Override
            public void onChanged(@Nullable String[] s) {
                binding.weekCaloriesText.setText(s[0]);
                binding.weekTimeText.setText(s[1]);
                binding.weekActivityText.setText(s[2]);

                binding.monthCaloriesText.setText(s[3]);
                binding.monthTimeText.setText(s[4]);
                binding.monthActivityText.setText(s[5]);

                binding.yearCaloriesText.setText(s[6]);
                binding.yearTimeText.setText(s[7]);
                binding.yearActivityText.setText(s[8]);
            }
        });
        FirebaseAuth auth = FirebaseAuth.getInstance();

        binding.signoutButton.setOnClickListener(view -> {
            auth.signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
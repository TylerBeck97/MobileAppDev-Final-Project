package com.example.exerciseapp.ui.customEntry;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.exerciseapp.R;
import com.example.exerciseapp.databinding.FragmentCustomEntryBinding;
import com.example.exerciseapp.models.Exercise;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomEntryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomEntryFragment extends Fragment {
    public CustomEntryFragment() {super(R.layout.fragment_custom_entry);}

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FragmentCustomEntryBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_custom_entry, container, false);

        View view = binding.getRoot();

        binding.button.setOnClickListener(view2 -> {
            if(TextUtils.isEmpty(binding.activityEditText.getText())){
                binding.customErrorText.setText("Please enter an activity!");
                return;
            }
            if(TextUtils.isEmpty(binding.timeEditText.getText())){
                binding.customErrorText.setText("Please enter the time elapsed in minutes");
                return;
            }
            if(TextUtils.isEmpty(binding.caloriesEditText.getText())){
                binding.customErrorText.setText("Please enter how many calories you burned");
                return;
            }

            Exercise exercise = new Exercise(
                    binding.activityEditText.getText().toString(),
                    auth.getUid(),
                    Long.parseLong(binding.timeEditText.getText().toString()),
                    new Date().getTime(),
                    Integer.parseInt(binding.caloriesEditText.getText().toString()));

            db.collection("Exercise").add(exercise).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    binding.activityEditText.setText("");
                    binding.timeEditText.setText("");
                    binding.caloriesEditText.setText("");
                    binding.customErrorText.setText("");
                    Snackbar display = Snackbar.make(view, R.string.custom_confirm, BaseTransientBottomBar.LENGTH_SHORT);
                    display.show();
                }
                else binding.customErrorText.setText(task.getException().toString());
            });
        });

        return view;
    }
}
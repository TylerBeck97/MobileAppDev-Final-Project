package com.example.exerciseapp.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.exerciseapp.R;
import com.example.exerciseapp.databinding.FragmentHistoryBinding;
import com.example.exerciseapp.models.Exercise;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;

public class HistoryFragment extends Fragment {
    public HistoryFragment() {super(R.layout.fragment_history);}

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FragmentHistoryBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false);

        ArrayList<Exercise> exercises = new ArrayList<>();
        db.collection("Exercise")
                .whereEqualTo("id", auth.getUid()).get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        QuerySnapshot collection = task.getResult();
                        for (QueryDocumentSnapshot document : collection) {
                            exercises.add(new Exercise(document.getString("activity"),
                                    document.getString("id"),
                                    document.getLong("time"),
                                    document.getLong("date"),
                                    document.getLong("calories").intValue()));
                            }
                            exercises.sort(new ExerciseComparator());
                            binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            binding.recyclerView.setAdapter(new ExerciseAdaptor(exercises));
                    }
        });
        View view = binding.getRoot();
        return view;
    }
}

class ExerciseComparator implements Comparator{
    public int compare(Object o1, Object o2){
        Exercise e1 = (Exercise) o1;
        Exercise e2 = (Exercise) o2;

        if (e1.getDate() == e2.getDate())
            return 0;
        else if(e1.getDate() > e2.getDate())
            return -1;
        else
            return 1;
    }
}
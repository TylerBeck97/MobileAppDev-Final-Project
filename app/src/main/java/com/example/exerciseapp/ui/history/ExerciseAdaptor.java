package com.example.exerciseapp.ui.history;

import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.exerciseapp.models.Exercise;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ExerciseAdaptor extends RecyclerView.Adapter<ExerciseAdaptor.ViewHolder> {
    private ArrayList<Exercise> exercises;

    public ExerciseAdaptor(ArrayList<Exercise> exercises){
        this.exercises = exercises;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        GridLayout exerciseLayout = new GridLayout(parent.getContext());
        exerciseLayout.setPadding(16, 16, 16, 16);
        exerciseLayout.setOrientation(GridLayout.VERTICAL);
        exerciseLayout.setColumnCount(3);
        exerciseLayout.setRowCount(2);
        
        TextView activityView = new TextView(parent.getContext());
        activityView.setTag("activityView");
        
        TextView timeView = new TextView(parent.getContext());
        timeView.setTag("timeView");
        
        TextView caloriesView = new TextView(parent.getContext());
        caloriesView.setTag("caloriesView");
        
        TextView dateView = new TextView(parent.getContext());
        dateView.setTag("dateView");
        
        exerciseLayout.addView(dateView);
        exerciseLayout.addView(activityView, 250, 100);
        exerciseLayout.addView(new TextView(parent.getContext()));
        exerciseLayout.addView(timeView, 250, 100);
        exerciseLayout.addView(new TextView(parent.getContext()));
        exerciseLayout.addView(caloriesView);

        return new ViewHolder(exerciseLayout);
    }
    
    public void onBindViewHolder(ViewHolder holder, int position){
        Exercise exercise = exercises.get(position);
        TextView activityView = holder.itemView.findViewWithTag("activityView");
        TextView timeView = holder.itemView.findViewWithTag("timeView");
        TextView caloriesView = holder.itemView.findViewWithTag("caloriesView");
        TextView dateView = holder.itemView.findViewWithTag("dateView");
        
        activityView.setText(exercise.getActivity());
        timeView.setText(String.format("%d mins", exercise.getTime()));
        caloriesView.setText(String.format("%.2f cals", exercise.getCalories()));

        Date date = new Date(exercise.getDate());
        DateFormat df = new SimpleDateFormat("MM/dd/yy");
        dateView.setText(df.format(date));
    }
    
    public int getItemCount(){return exercises.size();}

    class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(View itemView) { super(itemView);}
    }
}

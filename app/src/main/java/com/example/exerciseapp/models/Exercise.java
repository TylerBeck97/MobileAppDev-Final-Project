package com.example.exerciseapp.models;

public class Exercise {
    private String activity;
    private String id;
    private long time;
    private long date;
    private float calories;

    public Exercise(String activity, String id, long time, long date, int calories){
        this.activity = activity;
        this.id = id;
        this.time = time;
        this.date = date;
        this.calories = calories;
    }

    public Exercise(){
        this.activity = "";
        this.id = "";
        this.time = 0;
        this.date = 0;
        this.calories = 0;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public float getCalories() {
        return calories;
    }

    public void setCalories(float calories) {
        this.calories = calories;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}

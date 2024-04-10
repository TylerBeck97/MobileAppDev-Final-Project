package com.example.exerciseapp.ui.current;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.example.exerciseapp.R;
import com.example.exerciseapp.models.Exercise;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.Locale;

public class CurrentViewModel extends ViewModel {

    private MutableLiveData<String> mActivity;
    private MutableLiveData<String> mClock;
    private MutableLiveData<Float> mCalories;
    private MutableLiveData<Integer> mSpeed;
    private MutableLiveData<Integer> mInactivity;

    private int seconds;
    private int inactivityTicks = 0;
    private int secondsBeforeRecording = 0;
    private long userWeight;
    private float MET;
    private float lowerCutOffSpeed = 1;
    private float upperCutOffSpeed = 15;

    private Location userLastLocation;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private boolean isExercising = false;

    private Exercise currentExercise = new Exercise();

    private View view;

    final long interval = 3000;
    final int allowedInactivityTicks = 30000/(int) interval;
    final int delayRecordingTicks = 30000/(int) interval;

    public CurrentViewModel() {
        mActivity = new MutableLiveData<>();
        mActivity.setValue("Nothing");

        mClock = new MutableLiveData<>();
        mClock.setValue("0:00");

        mCalories = new MutableLiveData<>();
        mCalories.setValue(0.0f);

        mSpeed = new MutableLiveData<>();
        mSpeed.setValue(0);

        mInactivity = new MutableLiveData<>();
        mInactivity.setValue(0);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        db.collection("UserInfo").whereEqualTo("id", auth.getUid()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                QuerySnapshot collection = task.getResult();
                for(QueryDocumentSnapshot document : collection){
                    userWeight = document.getLong("weight");
                }
            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location: locationResult.getLocations()){
                    if(location != null) {

                        Log.d("__Last Location__", String.format("Lat:%f Long:%f", location.getLatitude(), location.getLongitude()));
                        float speed = userLastLocation.distanceTo(location) / (interval / 1000.0f);
                        userLastLocation = location;
                        mSpeed.setValue(Math.round(speed));

                        if (speed >= 0.5 && speed < 12 && !isExercising) {
                            if (secondsBeforeRecording >= delayRecordingTicks) {
                                secondsBeforeRecording = 0;
                                if (speed >= 0.5 && speed < 2) {
                                    mActivity.setValue("Walking");
                                    currentExercise.setActivity("Walking");
                                    lowerCutOffSpeed = 0.5f;
                                    upperCutOffSpeed = 3;
                                    MET = 2.5f;
                                } else if (speed >= 2 && speed < 5) {
                                    mActivity.setValue("Running");
                                    currentExercise.setActivity("Running");
                                    lowerCutOffSpeed = 3;
                                    upperCutOffSpeed = 5;
                                    MET = 8.8f;
                                } else if (speed >= 7 && speed < 12) {
                                    mActivity.setValue("Biking");
                                    currentExercise.setActivity("Biking");
                                    lowerCutOffSpeed = 7;
                                    upperCutOffSpeed = 12;
                                    MET = 6.0f;
                                }
                                startTimer();
                            } else secondsBeforeRecording++;
                        } else if ((speed < lowerCutOffSpeed || speed > upperCutOffSpeed) && isExercising) {
                            if (inactivityTicks >= allowedInactivityTicks) {
                                isExercising = false;
                                mActivity.setValue("Nothing");
                                int time = (seconds / 60);
                                if (time > 4) {
                                    currentExercise.setTime(time);
                                    currentExercise.setDate(new Date().getTime());
                                    currentExercise.setId(auth.getUid());

                                    db.collection("Exercise").add(currentExercise).addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Snackbar display = Snackbar.make(view, R.string.current_exercise, BaseTransientBottomBar.LENGTH_SHORT);
                                            display.show();
                                        }
                                    });
                                }
                            } else {
                                inactivityTicks++;
                                mInactivity.setValue((allowedInactivityTicks - inactivityTicks) * ((int) interval / 1000));
                            }
                        } else {
                            inactivityTicks = 0;
                            mInactivity.setValue(0);
                        }
                    }
                }
            }
        };
    }

    public LiveData<String> getActivity(){return mActivity;}
    public LiveData<String> getClock(){return mClock;}
    public LiveData<Float> getCalories(){return mCalories;}
    public LiveData<Integer> getSpeed(){return mSpeed;}
    public LiveData<Integer> getInactivity(){return  mInactivity;}

    public void setFusedLocationProviderClient(FusedLocationProviderClient fusedLocationProviderClient) {
        this.fusedLocationProviderClient = fusedLocationProviderClient;
    }

    public void setView(View view) {
        this.view = view;
    }

    public void startMonitoring(){
        try {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                if(task.isSuccessful()) userLastLocation = task.getResult();
            });
            setUpLocationRequest();
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
        catch (SecurityException E) {

        }
    }

    private void startTimer() {
        isExercising = true;
        seconds = 0;

        Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                mClock.setValue(String.format(Locale.getDefault(),"%d:%02d:%02d", hours, minutes, secs));

                float caloriesBurned = (minutes * userWeight * MET)/200.0f;
                mCalories.setValue(caloriesBurned);
                currentExercise.setCalories(caloriesBurned);

                seconds++;

                if(isExercising) handler.postDelayed(this, 1000);
                else{
                    mClock.setValue("00:00:00");
                    mCalories.setValue(0.0f);
                }
            }
        });
    }

    private void setUpLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(interval);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

}
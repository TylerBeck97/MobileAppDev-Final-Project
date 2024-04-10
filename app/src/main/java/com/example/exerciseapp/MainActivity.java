package com.example.exerciseapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.exerciseapp.ui.current.CurrentViewModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.exerciseapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FusedLocationProviderClient locationProviderClient;
    private CurrentViewModel currentViewModel;

    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActivityResultLauncher<String> launcher =
                registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(),
                        isGranted -> {
                            if (isGranted) {
                                setUpCurrentViewModel();
                            }
                        }
                );

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            setUpCurrentViewModel();
        }
        else {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    private void setUpCurrentViewModel(){
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        currentViewModel = new ViewModelProvider(this).get(CurrentViewModel.class);
        currentViewModel.setFusedLocationProviderClient(locationProviderClient);
        currentViewModel.startMonitoring();
        currentViewModel.setView(binding.getRoot());
    }

    @Override
    public  void onBackPressed(){

    }

}
package com.example.exerciseapp.ui.statistics;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class StatisticsViewModel extends ViewModel {

    private MutableLiveData<String[]> mText;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public StatisticsViewModel() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        mText = new MutableLiveData<>();

        String[] strings = new String[9];
        int[] rawNumbers = new int[9];

        HashMap hashMapYear = new HashMap();
        HashMap hashMapMonth = new HashMap();
        HashMap hashMapWeek = new HashMap();


        db.collection("Exercise").whereEqualTo("id", auth.getUid()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for(DocumentSnapshot document : task.getResult()){
                    Calendar currentCalendar = Calendar.getInstance();
                    Calendar documentCalendar = Calendar.getInstance();
                    documentCalendar.setTime(new Date(document.getLong("date")));
                    if (documentCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR)){
                        rawNumbers[6] += document.getLong("calories");
                        rawNumbers[7] += document.getLong("time");
                        updateHash(hashMapYear, document.getString("activity"), document.getLong("time"));

                        if (documentCalendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)){
                            rawNumbers[3] += document.getLong("calories");
                            rawNumbers[4] += document.getLong("time");
                            updateHash(hashMapMonth, document.getString("activity"), document.getLong("time"));

                            if (documentCalendar.get(Calendar.WEEK_OF_YEAR) == currentCalendar.get(Calendar.WEEK_OF_YEAR)){
                                rawNumbers[0] += document.getLong("calories");
                                rawNumbers[1] += document.getLong("time");
                                updateHash(hashMapWeek, document.getString("activity"), document.getLong("time"));
                            }
                        }
                    }
                }
                strings[8]=findFavoriteActivity(hashMapYear);
                strings[5]=findFavoriteActivity(hashMapMonth);
                strings[2]=findFavoriteActivity(hashMapWeek);
                for(int i=0; i<9; i++){
                    switch (i%3){
                        case 0:
                            strings[i] = String.format("%d cals", rawNumbers[i]);
                            break;
                        case 1:
                            strings[i] = String.format("%d mins", rawNumbers[i]);
                            break;
                    }
                }
                mText.setValue(strings);
            }
        });
    }

    private void updateHash(HashMap hashMap, String key, long value){
        if (hashMap.containsKey(key)){
            Object temp = hashMap.get(key);
            hashMap.remove(key);
            hashMap.put(key, (long) temp + value);
        }
        else hashMap.put(key, value);
    }

    private String findFavoriteActivity(HashMap hashMap){
        long max = 0;
        for (Object value: hashMap.values().toArray()
             ) {
            if ((long)value > max){
                max = (long)value;
            }
        }
        for (Object key: hashMap.keySet()){
            if (hashMap.get(key).equals(max)) return key.toString();
        }
        return "";
    }

    public LiveData<String[]> getText() {
        return mText;
    }
}
package com.example.exerciseapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.exerciseapp.models.UserInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpFragment extends Fragment {
    public SignUpFragment() {
        super(R.layout.fragment_sign_up);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        TextView emailText = (TextView) getView().findViewById(R.id.signUpEmailText);
        TextView emailConfirmText = (TextView) getView().findViewById(R.id.signUpEmailConfirmText);
        TextView passwordText = (TextView) getView().findViewById(R.id.signUpPasswordText);
        TextView passwordConfirmText = (TextView) getView().findViewById(R.id.signUpPasswordConfirmText);
        TextView errorText = (TextView) getView().findViewById(R.id.signUpErrorText);
        TextView weightText = (TextView) getView().findViewById(R.id.signUpWeightText);

        Button signUpButton = (Button) getView().findViewById(R.id.button3);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        signUpButton.setOnClickListener(view1 -> {
            if (TextUtils.isEmpty(emailText.getText()) || TextUtils.isEmpty(emailConfirmText.getText())){
                errorText.setText("Empty email field!");
                return;
            }

            if (!isValidEmail(emailText.getText())){
                errorText.setText("Invalid Email!");
                return;
            }
            if (!emailText.getText().toString().equals(emailConfirmText.getText().toString())) {
                errorText.setText("Emails don't match!");
                return;
            }

            if (TextUtils.isEmpty(passwordText.getText()) || TextUtils.isEmpty(passwordConfirmText.getText())){
                errorText.setText("Empty Password Field");
                return;
            }

            if (passwordText.getText().toString().length() < 8){
                errorText.setText("Passwords must be at least 8 characters long!");
                return;
            }

            if (!passwordText.getText().toString().equals(passwordConfirmText.getText().toString())){
                errorText.setText("Passwords don't match!");
                return;
            }

            if (TextUtils.isEmpty(weightText.getText())){
                errorText.setText("Empty Weight Field");
            }

            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.createUserWithEmailAndPassword(
                    emailText.getText().toString(),
                    passwordText.getText().toString()
            ).addOnCompleteListener((task) -> {
                if (task.isSuccessful()) {
                    UserInfo userInfo = new UserInfo(auth.getUid(), Integer.parseInt(weightText.getText().toString()));
                    db.collection("UserInfo").add(userInfo);

                    startActivity(new Intent(getContext(), MainActivity.class));
                }
                else {
                    errorText.setText(task.getException().toString());
                }
            });
        });
    }

    //got this method from stack overflow https://stackoverflow.com/questions/12947620/email-address-validation-in-android-on-edittext
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
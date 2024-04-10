package com.example.exerciseapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {
    public LoginFragment() {
        super(R.layout.fragment_login);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        TextView emailText = (TextView) getView().findViewById(R.id.logInEmailText);
        TextView passwordText = (TextView) getView().findViewById(R.id.logInPasswordText);
        TextView errorText = (TextView) getView().findViewById(R.id.logInErrorText);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        NavController controller = NavHostFragment.findNavController(this);

        Button logInButton = (Button) getView().findViewById(R.id.loginButton);
        logInButton.setOnClickListener(view2 -> {
            if (TextUtils.isEmpty(emailText.getText()) || TextUtils.isEmpty(passwordText.getText())){
                errorText.setText("Both password and email fields must be filled in!");
                return;
            }

            if (!isValidEmail(emailText.getText())){
                errorText.setText("Invalid email format!");
                return;
            }

            auth.signInWithEmailAndPassword(
                    emailText.getText().toString(),
                    passwordText.getText().toString()
            ).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    startActivity(new Intent(getContext(), MainActivity.class));
                }
                else {
                    errorText.setText(task.getException().toString());
                }
            });
        });

        Button signupButton = (Button) getView().findViewById(R.id.signUpButton);
        signupButton.setOnClickListener(view2 -> {
            controller.navigate(R.id.action_loginFragment_to_signUpFragment);
        });
    }

    //got this method from stack overflow https://stackoverflow.com/questions/12947620/email-address-validation-in-android-on-edittext
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
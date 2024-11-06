package com.example.sportscompiler;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class NewLoginPage extends Fragment {

    private EditText loginMail;
    private EditText loginPassword;
    private Button loginButton;
    private Button forgotPasswordButton;

    private Button registerButton;


    public NewLoginPage() {
        // Required empty public constructor
    }


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_new_login_page, container, false);

        loginMail = view.findViewById(R.id.YourBilkentMail);
        loginPassword = view.findViewById(R.id.signInPassword);
        loginButton = view.findViewById(R.id.signInButton);
        forgotPasswordButton = view.findViewById(R.id.forgetPasswordButton);
        registerButton = view.findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> {

            ((MainActivity) requireActivity()).loadFragment(new RegisterPage());
        });

        forgotPasswordButton.setOnClickListener(view1 -> {

            String loginMail1 = loginMail.getText().toString();
            ForgetPasswordPage forgetPasswordPage = new ForgetPasswordPage();

            Bundle bundle = new Bundle();
            bundle.putString("mail", loginMail1);
            forgetPasswordPage.setArguments(bundle);

            ((MainActivity) requireActivity()).loadFragment(forgetPasswordPage);
        });
        return  view;
    }
}
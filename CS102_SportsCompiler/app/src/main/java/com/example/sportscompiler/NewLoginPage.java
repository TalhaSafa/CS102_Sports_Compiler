package com.example.sportscompiler;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class NewLoginPage extends Fragment {

    private EditText loginMail;
    private EditText loginPassword;
    private Button loginButton;
    private Button forgotPasswordButton;

    private Button registerButton;

    private String eMail;
    private String password;
    private FirebaseAuth firebaseAuth;


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

        firebaseAuth = FirebaseAuth.getInstance();

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


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryLogin();
            }
        });
        return  view;
    }

    private void tryLogin()
    {
        eMail = loginMail.getText().toString();
        password = loginPassword.getText().toString();

        if(isBilkentMail(eMail))
        {
            firebaseAuth.signInWithEmailAndPassword(eMail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(getActivity(), "Logged in as " + eMail, Toast.LENGTH_SHORT).show();
                        FragmentLoad.changeActivity(getActivity(), homeActivity.class);
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "Error occured: " + task.getException() , Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }

    private boolean isBilkentMail(String mail)
    {
        boolean isValid = true;
        String mailDomain;
        if(!mail.contains("@"))
        {
            Toast.makeText(getActivity(), "Enter a valid Bilkent mail", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        else
        {
            mailDomain = mail.substring(mail.indexOf("@") + 1);
            if(mailDomain.equals("bilkent.edu.tr") || mailDomain.equals("ug.bilkent.edu.tr") )
            {
                isValid = true;
            }
            else
            {
                Toast.makeText(getActivity(), "Enter a valid Bilkent mail", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
        }

        return isValid;
    }
}
package com.example.sportscompiler;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseUser;

public class RegisterPage extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private EditText registerName;
    private EditText registerMail;
    private EditText registerPassword;
    private EditText registerRePassword;
    private EditText birthDate;
    private EditText department;
    private Button registerButton;
    private FirebaseAuth firebaseAuth;

    public RegisterPage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register_page, container, false);

        registerName = view.findViewById(R.id.registName);
        registerMail = view.findViewById(R.id.registerMail);
        registerPassword = view.findViewById(R.id.registerPassword);
        registerRePassword = view.findViewById(R.id.registerRePassword);
        birthDate = view.findViewById(R.id.birthDate);
        department = view.findViewById(R.id.department);
        registerButton = view.findViewById(R.id.registerButton);
        firebaseAuth = FirebaseAuth.getInstance(); //initializing firebase authentication

        registerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String name = registerName.getText().toString();
                String mail = registerMail.getText().toString();
                String password = registerPassword.getText().toString();
                String rePassword = registerRePassword.getText().toString();
                String birthDate1 = birthDate.getText().toString();
                String department1 = department.getText().toString();

                if(!name.isEmpty() && !mail.isEmpty() && !password.isEmpty() && !rePassword.isEmpty()
                    && !birthDate1.isEmpty() && !department1.isEmpty() && password.equals(rePassword))
                {
                    if(isPasswordValid(password) && isBilkentMail(mail))
                    {
                        createAccount(mail, password);
                    }
                }
                else if(!password.equals(rePassword))
                {
                    Toast.makeText(getActivity(), "Passwords doesn't match!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getActivity(), "Please fill all the blanks!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return  view;
    }

    private void createAccount(String email, String password)
    {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    FirebaseUser newUser = firebaseAuth.getCurrentUser(); //returns the current signed in user
                    if(newUser != null){
                        newUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getActivity(), "Registration successful! Verification Email sent!", Toast.LENGTH_SHORT).show();
                                    //TODO pass to NewLoginPage
                                }
                                else{
                                    Toast.makeText(getActivity(), "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else{
                        Toast.makeText(getActivity(), "Authentication failed!", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getActivity(), "An error has occured!" + task.getException(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private boolean isPasswordValid(String password)
    {
        boolean isValid = true;
        if(password.length() < 7)
        {
            isValid = false;
            Toast.makeText(getActivity(), "Password must be longer than 6 characters", Toast.LENGTH_SHORT).show();
        }
        else if (!doesIncludeUppercase(password))
        {
            isValid = false;
            Toast.makeText(getActivity(), "Password must include at least 1 uppercase", Toast.LENGTH_SHORT).show();
        }
        else if(!doesIncludeLowercase(password))
        {
            isValid = false;
            Toast.makeText(getActivity(), "Password must include at least 1 lowercase", Toast.LENGTH_SHORT).show();
        }
        else if(!doesIncludeNumeric(password))
        {
            isValid = true;
            Toast.makeText(getActivity(), "Password must include at least 1 digit", Toast.LENGTH_SHORT).show();
        }

        return isValid;
    }

    private boolean doesIncludeUppercase(String pass)
    {
        for(int i = 0 ; i < pass.length() ; i++)
        {
            if(Character.isUpperCase(pass.charAt(i)))
            {
                return  true;
            }
        }
        return false;
    }

    private boolean doesIncludeLowercase(String pass)
    {
        for(int i = 0 ; i < pass.length() ; i++)
        {
            if(Character.isLowerCase(pass.charAt(i)))
            {
                return  true;
            }
        }
        return false;
    }

    private boolean doesIncludeNumeric(String pass)
    {
        for(int i = 0 ; i < pass.length() ; i++)
        {
            if(Character.isDigit(pass.charAt(i)))
            {
                return  true;
            }
        }
        return false;
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
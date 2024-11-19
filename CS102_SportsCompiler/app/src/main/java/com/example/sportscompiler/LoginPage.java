package com.example.sportscompiler;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPage extends AppCompatActivity {

    private EditText loginMail;
    private EditText loginPassword;

    private Button loginButton;
    private Button forgotPasswordButton;
    private Button registerButton;

    private String eMail;
    private String password;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);

        loginMail = findViewById(R.id.YourBilkentMail);
        loginPassword = findViewById(R.id.signInPassword);
        loginButton = findViewById(R.id.signInButton);
        forgotPasswordButton = findViewById(R.id.forgetPasswordButton);
        registerButton = findViewById(R.id.registerButton);

        firebaseAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(v -> {

            FragmentLoad.changeActivity(LoginPage.this, RegisterPageAc.class);
        });

        forgotPasswordButton.setOnClickListener(view1 -> {

            FragmentLoad.changeActivity(LoginPage.this, ForgetPasswordPage.class);
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryLogin();
            }
        });
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
                        Toast.makeText(LoginPage.this, "Logged in as " + eMail, Toast.LENGTH_SHORT).show();
                        FragmentLoad.changeActivity(LoginPage.this, homeActivity.class);
                    }
                    else
                    {
                        Toast.makeText(LoginPage.this, "Error occured: " + task.getException() , Toast.LENGTH_SHORT).show();

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
            Toast.makeText(LoginPage.this, "Enter a valid Bilkent mail", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(LoginPage.this, "Enter a valid Bilkent mail", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
        }

        return isValid;
    }

}
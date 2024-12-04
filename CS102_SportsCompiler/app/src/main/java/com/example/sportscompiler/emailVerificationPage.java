package com.example.sportscompiler;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sportscompiler.AdditionalClasses.FragmentLoad;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class emailVerificationPage extends AppCompatActivity {
    Handler handler = new Handler();
    Runnable runnable;
    final int DELAY_TO_CHECK_VERIFICATION = 1000;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private Button resendButton;
    private TextView verificationTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_email_verification_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        resendButton = findViewById(R.id.resendButton);
        verificationTxt = findViewById(R.id.verificationTxt);

        firebaseAuth = FirebaseAuth.getInstance();

        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(emailVerificationPage.this, "Verification Email sent again!", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(emailVerificationPage.this, "Verification Email could not be sent again!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null)
        {
            verificationTxt.setText("A verification has been sent to " + currentUser.getEmail() +"\nPlease verify your account!");
        }
        else
        {
            Toast.makeText(emailVerificationPage.this, "Verified", Toast.LENGTH_SHORT).show();
            FragmentLoad.changeActivity(emailVerificationPage.this, homeActivity.class);

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        currentUser = firebaseAuth.getCurrentUser();

        if(currentUser != null)
        {
            handler.postDelayed(runnable = new Runnable() {
                @Override
                public void run() {
                    currentUser.reload();
                    if(currentUser.isEmailVerified())
                    {
                        handler.removeCallbacks(this);
                        Toast.makeText(emailVerificationPage.this, "Verification is successful!", Toast.LENGTH_SHORT).show();
                        FragmentLoad.changeActivity(emailVerificationPage.this, homeActivity.class);
                        //To stop working of current activtiy:
                        finish();

                    }
                    handler.postDelayed(this, DELAY_TO_CHECK_VERIFICATION);
                }
            }, DELAY_TO_CHECK_VERIFICATION);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        firebaseAuth.signOut();

        //TODO change this after converting login page to activity
        FragmentLoad.changeActivity(emailVerificationPage.this, LoginPage.class);
        finish();
    }
}
package com.example.sportscompiler;

//import static android.app.PendingIntent.getActivity;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class RegisterPageAc extends AppCompatActivity {

    private EditText registerName;
    private EditText registerMail;
    private EditText registerPassword;
    private EditText registerRePassword;
    private EditText birthDate;
    private EditText department;
    private Button registerButton;
    private FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_page);

        registerName = findViewById(R.id.registName);
        registerMail = findViewById(R.id.registerMail);
        registerPassword = findViewById(R.id.registerPassword);
        registerRePassword = findViewById(R.id.registerRePassword);
        department = findViewById(R.id.department);
        registerButton = findViewById(R.id.registerButton);
        birthDate = findViewById(R.id.birthDate);

        firebaseAuth = FirebaseAuth.getInstance();

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
                    Toast.makeText(RegisterPageAc.this, "Passwords doesn't match!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(RegisterPageAc.this, "Please fill all the blanks!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Spinner daySpinner = findViewById(R.id.day_spinner);
        Spinner monthSpinner = findViewById(R.id.month_spinner);
        Spinner yearSpinner = findViewById(R.id.year_spinner);

        List<String> days = new ArrayList<>();

        for(int i = 1; i <= 31; i++)
        {
            days.add(String.valueOf(i));
        }

        List<String> months = new ArrayList<>();
        months.add("January");
        months.add("February");
        months.add("March");
        months.add("April");
        months.add("May");
        months.add("June");
        months.add("July");
        months.add("August");
        months.add("September");
        months.add("October");
        months.add("November");
        months.add("December");

        List<String> years = new ArrayList<>();

        for(int i = 1970; i <= 2024; i++)
        {
            years.add(String.valueOf(i));
        }

        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(RegisterPageAc.this, android.R.layout.simple_spinner_item, days);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(dayAdapter);
        daySpinner.setDropDownVerticalOffset(500);

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(RegisterPageAc.this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);
        monthSpinner.setDropDownVerticalOffset(500);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(RegisterPageAc.this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);
        yearSpinner.setDropDownVerticalOffset(500);

        birthDate.setOnClickListener(v ->
        {
            String selecetedDay = daySpinner.getSelectedItem().toString();
            String selectedMonth = monthSpinner.getSelectedItem().toString();
            String selectedYear = yearSpinner.getSelectedItem().toString();

            String date = selecetedDay + "/" + selectedMonth + "/" + selectedYear;
            birthDate.setText(date);
        });
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
                                            Toast.makeText(RegisterPageAc.this, "Registration successful! Verification Email sent!", Toast.LENGTH_SHORT).show();
                                            //TODO when changed to actvity will be changed to Intent(this, emailVerificationPage.class
                                            FragmentLoad.changeActivity(RegisterPageAc.this, emailVerificationPage.class);
                                        }
                                        else{
                                            Toast.makeText(RegisterPageAc.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            else{
                                Toast.makeText(RegisterPageAc.this, "Authentication failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(RegisterPageAc.this, "An error has occured!" + task.getException(), Toast.LENGTH_SHORT).show();

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
            Toast.makeText(RegisterPageAc.this, "Password must be longer than 6 characters", Toast.LENGTH_SHORT).show();
        }
        else if (!doesIncludeUppercase(password))
        {
            isValid = false;
            Toast.makeText(RegisterPageAc.this, "Password must include at least 1 uppercase", Toast.LENGTH_SHORT).show();
        }
        else if(!doesIncludeLowercase(password))
        {
            isValid = false;
            Toast.makeText(RegisterPageAc.this, "Password must include at least 1 lowercase", Toast.LENGTH_SHORT).show();
        }
        else if(!doesIncludeNumeric(password))
        {
            isValid = true;
            Toast.makeText(RegisterPageAc.this, "Password must include at least 1 digit", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(RegisterPageAc.this, "Enter a valid Bilkent mail", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(RegisterPageAc.this, "Enter a valid Bilkent mail", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
        }

        return isValid;
    }
}
package com.example.sportscompiler;

//import static android.app.PendingIntent.getActivity;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sportscompiler.AdditionalClasses.PasswordValidity;
import com.example.sportscompiler.AdditionalClasses.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.model.DatabaseId;

import java.util.ArrayList;
import java.util.List;

public class RegisterPageAc extends AppCompatActivity {

    private EditText registerName;
    private EditText registerMail;
    private EditText registerPassword;
    private EditText registerRePassword;
    private TextView birthDate;
    private EditText department;
    private Button registerButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private Spinner daySpinner;
    private Spinner monthSpinner;
    private Spinner yearSpinner;

    private ImageView passwordToggle, passwordToggle2;


    @SuppressLint("MissingInflatedId")
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
        daySpinner = findViewById(R.id.day_spinner);
        monthSpinner = findViewById(R.id.month_spinner);
        yearSpinner = findViewById(R.id.year_spinner);

        passwordToggle = findViewById(R.id.passwordToggle);

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
                    if(PasswordValidity.isPasswordValid(password, RegisterPageAc.this) && isBilkentMail(mail))
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

        List<String> days = new ArrayList<>();

        for(int i = 1; i <= 31; i++)
        {
            days.add(String.valueOf(i));
        }

        List<String> months = new ArrayList<>();
        months.add("01");
        months.add("02");
        months.add("03");
        months.add("04");
        months.add("05");
        months.add("06");
        months.add("07");
        months.add("08");
        months.add("09");
        months.add("10");
        months.add("11");
        months.add("12");

        List<String> years = new ArrayList<>();

        for(int i = 1960; i <= 2010; i++)
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

        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                updateBirthDateEdittext();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateBirthDateEdittext();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateBirthDateEdittext();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        

        passwordToggle.setOnClickListener(new View.OnClickListener() {
            private boolean isPasswordVisible = false;

            @Override
            public void onClick(View view)
            {
                if(isPasswordVisible)
                {
                    registerPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    registerRePassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordToggle.setImageResource(R.drawable.ic_eye_closed);
                }
                else
                {
                    registerPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    registerRePassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passwordToggle.setImageResource(R.drawable.ic_eye_open);
                }
                isPasswordVisible = !isPasswordVisible;
            }
        });

    }

    private  void updateBirthDateEdittext()
    {
        String selectedDay = daySpinner.getSelectedItem().toString();
        String selectedMonth = monthSpinner.getSelectedItem().toString();
        String selectedYear = yearSpinner.getSelectedItem().toString();

        String date = selectedDay + "/" + selectedMonth + "/" + selectedYear;
        birthDate.setText(date);
    }

    //A method to update database based on user informations.
    //To update need a user first. Initialize it with id, name, birthdate, department
    private void updateDatabase(User user)
    {
        firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").document(firebaseAuth.getCurrentUser().getUid())
                .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(RegisterPageAc.this, "Updated Data", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(RegisterPageAc.this, task.getException().toString(), Toast.LENGTH_SHORT).show();

                        }
                    }
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

                                            //CREATING NEW USER:
                                            User user = new User(newUser.getUid(), registerName.getText().toString()
                                                    , birthDate.getText().toString(), department.getText().toString(),"" , 0 , 0, email);

                                            updateDatabase(user);

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
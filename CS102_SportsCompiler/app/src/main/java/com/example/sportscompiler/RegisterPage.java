package com.example.sportscompiler;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
                    Toast.makeText(getActivity(), "Registration successful!", Toast.LENGTH_SHORT).show();
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
}
package com.example.sportscompiler;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SigningPage extends Fragment {

    private EditText signingMailEditText;
    private EditText signingPasswordEditText;
    private EditText signingPasswordCheckEditText;
    private Button signInButton;

    public SigningPage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signing_page, container, false);

        // Link UI components with their XML IDs
        signingMailEditText = view.findViewById(R.id.signingmail);
        signingPasswordEditText = view.findViewById(R.id.signingPassword);
        signingPasswordCheckEditText = view.findViewById(R.id.signingPasswordcheck);
        signInButton = view.findViewById(R.id.signInButton);

        // Set OnClickListener for the button
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = signingMailEditText.getText().toString();
                String password = signingPasswordEditText.getText().toString();
                String passwordCheck = signingPasswordCheckEditText.getText().toString();

                if (password.equals(passwordCheck)) {
                    Toast.makeText(getActivity(), "Sign-in successful!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}

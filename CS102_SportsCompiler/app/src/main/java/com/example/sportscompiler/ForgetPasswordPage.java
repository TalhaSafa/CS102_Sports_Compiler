package com.example.sportscompiler;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


public class ForgetPasswordPage extends Fragment {

    private TextView mailTextView;


    public ForgetPasswordPage() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forget_password_page, container, false);

        mailTextView = view.findViewById(R.id.mailTextView);

        Bundle args = getArguments();

        if(args != null)
        {
            String mail = args.getString("mail");

            mailTextView.setText("Mail: " + mail);
        }
        return  view;
    }
}
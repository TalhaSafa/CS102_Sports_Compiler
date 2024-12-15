package com.example.sportscompiler;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sportscompiler.AdditionalClasses.FragmentLoad;
import com.example.sportscompiler.AdditionalClasses.PasswordValidity;
import com.example.sportscompiler.AdditionalClasses.User;
import com.example.sportscompiler.AdditionalClasses.adminHelpMessage;
import com.example.sportscompiler.AdditionalClasses.firestoreUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link profile_settings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class profile_settings extends Fragment {

    private Button confirmPassBut, sendNotesBut, logOutButton;
    private EditText newPasswordFirst, newPasswordAgain, notes;
    private FirebaseAuth fAuth;
    private FirebaseUser fUser;
    private firestoreUser fsUser;
    private User user;
    FirebaseFirestore firestore;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public profile_settings() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment profile_settings.
     */
    // TODO: Rename and change types and number of parameters
    public static profile_settings newInstance(String param1, String param2) {
        profile_settings fragment = new profile_settings();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_settings, container, false);
        confirmPassBut = view.findViewById(R.id.button4);
        logOutButton = view.findViewById(R.id.logOutButton);
        newPasswordFirst = view.findViewById(R.id.editTextTextPassword);
        newPasswordAgain = view.findViewById(R.id.editTextTextPassword2);
        notes = view.findViewById(R.id.notes_edit_text);
        sendNotesBut = view.findViewById(R.id.button3);
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        fsUser = new firestoreUser();
        user = new User();
        initializeUser();
        firestore = FirebaseFirestore.getInstance();

        confirmPassBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String password = newPasswordFirst.getText().toString();
                String rePassword = newPasswordAgain.getText().toString();

                if(!password.isEmpty() && !rePassword.isEmpty() && password.equals(rePassword))
                {
                    if(PasswordValidity.isPasswordValid(password, getContext()) )
                    {
                        fUser.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(getContext(), "Password has been changed succesfully!", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(getContext(), "Failed to change password!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
                else if(!password.equals(rePassword))
                {
                    Toast.makeText(getContext(), "Passwords doesn't match!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getContext(), "Please password parts!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        sendNotesBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = notes.getText().toString();
                if(!message.isEmpty())
                {
                    String userID = user.getUserID();
                    String userMail = fUser.getEmail();
                    String userName = user.getName();
                    Timestamp sendTime = Timestamp.now();
                    String messageID = userID + sendTime.toDate().toString() + "help";

                    adminHelpMessage newHelpMessage = new adminHelpMessage(message, userID, userMail, userName, sendTime);


                    firestore.collection("helpMessages").document(messageID).set(newHelpMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(getContext(), "Sent message successfully!", Toast.LENGTH_SHORT).show();
                                notes.setText("");
                            }
                            else
                            {
                                Toast.makeText(getContext(), "Could not send message!", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });


                }

                else
                {
                    Toast.makeText(getContext(), "Please fill notes part!", Toast.LENGTH_SHORT).show();

                }
            }
        });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Yes", (dialog, which) -> logOut())
                        .setNegativeButton("No", null)
                        .show();
                }
        });



        return view;
    }
    private void logOut()
    {
        fAuth.signOut();
        FragmentLoad.changeActivity(requireActivity(), LoginPage.class);

    }

    private void initializeUser()
    {
        fsUser.updateInfo(user, new firestoreUser.FirestoreCallback<User>() {
            @Override
            public void onSuccess(User result) {
                user = result;
            }

            @Override
            public void onError(Exception e) {
                Log.e("HomePage", "Error fetching user info", e);
            }
        });
    }
}
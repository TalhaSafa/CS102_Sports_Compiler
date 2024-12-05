package com.example.sportscompiler;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.sportscompiler.AdditionalClasses.User;
import com.example.sportscompiler.AdditionalClasses.firestoreUser;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfilePage extends Fragment {

    private TextView nameTextView, departmentTextView, ageTextView;
    private static User user;
    private firestoreUser fireuser;
    private Button settingsButton;

    public ProfilePage() {
        user = new User();
        fireuser = new firestoreUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_page, container, false);

        // Initialize UI elements
        nameTextView = rootView.findViewById(R.id.nameTextView);
        departmentTextView = rootView.findViewById(R.id.departmentTextView);
        ageTextView = rootView.findViewById(R.id.ageTextView);
        settingsButton = rootView.findViewById(R.id.settingsButton);

        // Fetch user info and update UI
        fireuser.updateInfo(user, new firestoreUser.FirestoreCallback<User>() {
            @Override
            public void onSuccess(User updatedUser) {
                user = updatedUser;
                updateUI(); // Update the UI with user data
            }

            @Override
            public void onError(Exception e) {
                Log.e("HomePage", "Error fetching user info", e);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                FragmentLoad.loadFragment((AppCompatActivity) ProfilePage.this.getActivity(), R.id.fragmentContainerView, new profile_settings() );

            }
        });

        return rootView;
    }

    public static User getUser() {
        return user;
    }

    /**
     * Updates the UI elements with the user's data.
     */
    private void updateUI() {
        if (user != null) {
            nameTextView.setText(user.getName());
            departmentTextView.setText(user.getDepartment());
            ageTextView.setText(user.getBirthDate());
        }
    }
    //TODO: implement a method calculates age according to birth date.
    public int ageCalc(String birthDate){
        return 0;
    }
}


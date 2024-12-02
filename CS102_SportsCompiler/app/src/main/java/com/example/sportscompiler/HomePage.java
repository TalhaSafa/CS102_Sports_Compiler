package com.example.sportscompiler;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.sportscompiler.AdditionalClasses.FragmentLoad;
import com.example.sportscompiler.AdditionalClasses.Match;
import com.example.sportscompiler.AdditionalClasses.MatchAdapter;
import com.example.sportscompiler.AdditionalClasses.User;
import com.example.sportscompiler.AdditionalClasses.firestoreUser;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends Fragment {

    private TextView nameTextView, departmentTextView, ageTextView;
    private User user;
    private firestoreUser fireuser;
    private Button settingsButton;
    private RecyclerView recyclerView;
    private List<Match> matches;
    private MatchAdapter matchAdapter;

    public HomePage() {
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
        recyclerView = rootView.findViewById(R.id.matchListRecyclerforProfilePage);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        matches = getMatches();
        matchAdapter = new MatchAdapter(matches);
        recyclerView.setAdapter(matchAdapter);

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
                FragmentLoad.loadFragment((AppCompatActivity) HomePage.this.getActivity(), R.id.fragmentContainerView, new profile_settings() );

            }
        });

        return rootView;
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

    // TO DO: we need to pull match datas from database. It's not done yet.
    private List<Match> getMatches()
    {
        List<Match> matches = new ArrayList<>();

        return matches;
    }
}


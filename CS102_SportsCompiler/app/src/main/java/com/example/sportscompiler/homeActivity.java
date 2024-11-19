package com.example.sportscompiler;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class homeActivity extends AppCompatActivity {
    private Fragment matchAttendence;
    private Fragment mainPageFragment;
    private Fragment profileFragment;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    public void onStart()
    {
        super.onStart();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null)
        {
            if(!firebaseUser.isEmailVerified())
            {
                FragmentLoad.changeActivity(this, emailVerificationPage.class);
            }
            else
            {
                Toast.makeText(this, firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            //TODO will be changed when converted to activity
            FragmentLoad.changeActivity(this, RegisterPageAc.class);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        profileFragment = new HomePage();
        mainPageFragment = new mainPageFragment();
        matchAttendence = new MatchAttendencePage();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.mainPage);
        // Set default fragment
        loadFragment(new mainPageFragment());

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment selectedFragment = null;

                if(item.getItemId() == R.id.matches)
                {
                    selectedFragment = matchAttendence;
                }
                else if(item.getItemId() == R.id.mainPage)
                {
                    selectedFragment = mainPageFragment;
                }
                else if(item.getItemId() == R.id.profile)
                {
                    selectedFragment = profileFragment;
                }
                return loadFragment(selectedFragment);
            }
        });
    }

    public boolean loadFragment(Fragment fragment)
    {
        if(fragment != null)
        {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainerView, fragment);
            transaction.commit();
            return  true;
        }
        return  false;
    }
}
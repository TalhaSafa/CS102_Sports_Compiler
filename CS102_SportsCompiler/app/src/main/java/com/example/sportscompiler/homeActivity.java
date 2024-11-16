package com.example.sportscompiler;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;



public class homeActivity extends AppCompatActivity {
    private Fragment matchAttendence;
    private Fragment mainPageFragment;
    private Fragment profileFragment;

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
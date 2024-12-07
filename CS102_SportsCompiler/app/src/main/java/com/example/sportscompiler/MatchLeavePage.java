package com.example.sportscompiler;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.sportscompiler.AdditionalClasses.PlayerAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MatchLeavePage extends Fragment {

    private RecyclerView recyclerViewTeamA;
    private RecyclerView recyclerViewTeamB;

    private PlayerAdapter teamAAdapter;
    private PlayerAdapter teamBAdapter;

    private List<String> teamAPlayers;
    private List<String> teamBPlayers;

    public MatchLeavePage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_match_leave_page, container, false);

        // Initialize player lists
        teamAPlayers = new ArrayList<>(Arrays.asList("User_1234", "User_1122", "User_8891"));
        teamBPlayers = new ArrayList<>(Arrays.asList("User_7521", "User_9991", "User_6434"));

        // Set up RecyclerViews
        recyclerViewTeamA = view.findViewById(R.id.recyclerViewTeamA);
        recyclerViewTeamB = view.findViewById(R.id.recyclerViewTeamB);

        // Create adapters
        teamAAdapter = new PlayerAdapter(teamAPlayers);
        teamBAdapter = new PlayerAdapter(teamBPlayers);

        // Set up RecyclerView for Team A
        recyclerViewTeamA.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewTeamA.setAdapter(teamAAdapter);

        // Set up RecyclerView for Team B
        recyclerViewTeamB.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewTeamB.setAdapter(teamBAdapter);

        return view;
    }
}

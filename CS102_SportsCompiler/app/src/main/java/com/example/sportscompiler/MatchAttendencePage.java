package com.example.sportscompiler;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.sportscompiler.AdditionalClasses.Match;
import com.example.sportscompiler.AdditionalClasses.MatchFields;
import com.example.sportscompiler.AdditionalClasses.Player;
import com.example.sportscompiler.AdditionalClasses.Positions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MatchAttendencePage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MatchAttendencePage extends Fragment {

    private Button createMatchButton;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MatchAttendencePage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MatchAttendencePage.
     */
    // TODO: Rename and change types and number of parameters
    public static MatchAttendencePage newInstance(String param1, String param2) {
        MatchAttendencePage fragment = new MatchAttendencePage();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_match_attendence_page, container, false);
        createMatchButton = view.findViewById(R.id.createMatchButton);
        firebaseAuth = FirebaseAuth.getInstance();


        createMatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewMatch(firebaseAuth.getCurrentUser().getUid().toString(), 5, view);
            }
        });

        return view;
    }

    //To initialize team maps
    private void initializeMaps(Map<String, Player> team, int numberOfPlayersInATeam)
    {
        if(numberOfPlayersInATeam == 5)
        {
            team.put(Positions.GK1.getAction(), new Player());
            team.put(Positions.CB1.getAction(), new Player());
            team.put(Positions.CB2.getAction(), new Player());
            team.put(Positions.MO3.getAction(), new Player());
            team.put(Positions.FW3.getAction(), new Player());
        }

        if(numberOfPlayersInATeam == 6)
        {
            team.put(Positions.GK1.getAction(), new Player());
            team.put(Positions.CB1.getAction(), new Player());
            team.put(Positions.CB2.getAction(), new Player());
            team.put(Positions.MO1.getAction(), new Player());
            team.put(Positions.MO2.getAction(), new Player());
            team.put(Positions.FW3.getAction(), new Player());
        }

    }

    //This method is to try whether database is working or not:
    private void createNewMatch(String adminID, int numberOfPlayersInATeam, View view){
        Timestamp date = Timestamp.now();
        Map<String, Player> teamA = new HashMap<>();
        Map<String, Player> teamB = new HashMap<>();
        initializeMaps(teamA, numberOfPlayersInATeam);
        initializeMaps(teamB, numberOfPlayersInATeam);
        Match newMatch = new Match(adminID, date, MatchFields.MAIN1, teamA, teamB);

        //To create distinct id for each match:
        String matchID = newMatch.getAdminID() + newMatch.getDate().toDate().toString();


        firestore = FirebaseFirestore.getInstance();

        firestore.collection(newMatch.getMatchType()).document(matchID).set(newMatch)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(view.getContext(), "Created new match", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(view.getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

    }
}
package com.example.sportscompiler;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.sportscompiler.AdditionalClasses.FragmentLoad;
import com.example.sportscompiler.AdditionalClasses.Match;
import com.example.sportscompiler.AdditionalClasses.MatchAdapter;
import com.example.sportscompiler.AdditionalClasses.MatchFields;
import com.example.sportscompiler.AdditionalClasses.Player;
import com.example.sportscompiler.AdditionalClasses.Positions;
import com.example.sportscompiler.AdditionalClasses.firestoreUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MatchAttendencePage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MatchAttendencePage extends Fragment {

    private FloatingActionButton createMatchButton;
    private String matchName;
    private RecyclerView recyclerView;
    private List<Match> matches = new ArrayList<>();
    private MatchAdapter matchAdapter;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    public firestoreUser user= new firestoreUser();

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

        recyclerView = view.findViewById(R.id.matchListRecyclerforMatchAttendencePage);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

         user.getMatches(new firestoreUser.FirestoreCallback<List<Match>>() {
            @Override
            public void onSuccess(List<Match> result) {
                matches = result;
                System.out.println(matches);
                //matchAdapter = new MatchAdapter(matches,);
                recyclerView.setAdapter(matchAdapter);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Failed to fetch matches: " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        //matchAdapter = new MatchAdapter(matches);
        recyclerView.setAdapter(matchAdapter);


        createMatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Context context = getActivity();
                if(context != null)
                {
                    FragmentLoad.changeActivity(context, CreateMatch.class);
                }
            }
        });

        return view;
    }

    // TODO: we need to pull match datas from database. It's not done yet.

}
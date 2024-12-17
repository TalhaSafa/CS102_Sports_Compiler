package com.example.sportscompiler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.example.sportscompiler.AdditionalClasses.FragmentLoad;
import com.example.sportscompiler.AdditionalClasses.Match;
import com.example.sportscompiler.AdditionalClasses.MatchAdapter;
import com.example.sportscompiler.AdditionalClasses.MatchFields;
import com.example.sportscompiler.AdditionalClasses.Player;
import com.example.sportscompiler.AdditionalClasses.Positions;
import com.example.sportscompiler.AdditionalClasses.SearchForPlayer;
import com.example.sportscompiler.AdditionalClasses.firestoreUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MatchAttendencePage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MatchAttendencePage extends Fragment implements MatchAdapter.OnItemClickListener {

    private FloatingActionButton createMatchButton;
    private Button sortButton, filterButton;
    private String matchName;
    private RecyclerView recyclerView;
    private List<Match> matches = new ArrayList<>();
    private MatchAdapter matchAdapter;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    public firestoreUser user= new firestoreUser();

    private Calendar calender1;
    private Calendar calendar2;

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
        sortButton = view.findViewById(R.id.sortingButton);
        filterButton = view.findViewById(R.id.filteringButton);

        recyclerView = view.findViewById(R.id.matchListRecyclerforMatchAttendencePage);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getMatches(new firestoreUser.FirestoreCallback<List<Match>>() {
            @Override
            public void onSuccess(List<Match> result) {
                matches = result;
                System.out.println(matches);
                filterNonExpiredMatches();
                matchAdapter.updateData(matches);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Failed to fetch matches: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



        matchAdapter = new MatchAdapter(requireContext(),matches,this);
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

        sortButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                SortDialogFragment sortDialogFragment = new SortDialogFragment();
                sortDialogFragment.show(requireActivity().getSupportFragmentManager().beginTransaction(), "SortDialog");
            }
        });

        setupSortResultListener();

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                FilterDialog filterDialogFragment = new FilterDialog();
                filterDialogFragment.show(getChildFragmentManager(), "FilterDialog");
            }
        });

        setupFilterResultListener();

        return view;
    }

    @Override
    //HANGI FRAGMENT GIDIYOR ? USER VE ADMIN ICIN....
    public void onItemClick(Match match)
    {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String currentUserID = firebaseAuth.getCurrentUser().getUid();

        if(match.getPlayersA() == null)
        {
            match.setPlayersA(new HashMap<>());
        }

        if(match.getPlayersB() == null)
        {
            match.setPlayersB(new HashMap<>());
        }

        if(currentUserID.equals(match.getAdminID()))
        {
            Context context = getActivity();

            if(context != null)
            {
                Intent intent = new Intent(context, AdminAcceptApplicationPage.class);
                intent.putExtra("matchID", match.getMatchID());
                intent.putExtra("matchType", match.getMatchType());
                startActivity(intent);
            }
        }

        else if(SearchForPlayer.doesMatchContainUser(match, currentUserID))
        {
            Context context = getActivity();

            if(context != null)
            {
                Intent intent = new Intent(context, LeaveMatchPage.class);
                intent.putExtra("matchID", match.getMatchID());
                intent.putExtra("matchType", match.getMatchType());
                startActivity(intent);
            }
        }

        else
        {
            Context context = getActivity();

            if(context != null)
            {
                if(match.getMatchType().equals("matches5"))
                {
                    Intent intent = new Intent(context, MatchApplication5x5.class);
                    intent.putExtra("matchID", match.getMatchID());
                    intent.putExtra("matchType", match.getMatchType());
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(context, MatchApplication6x6.class);
                    intent.putExtra("matchID", match.getMatchID());
                    intent.putExtra("matchType", match.getMatchType());
                    startActivity(intent);
                }
            }
        }

    }
    public void filterNonExpiredMatches() {
        List<Match> nonExpiredMatches = new ArrayList<>();
        Date currentDate = new Date(); // Current date and time

        for (Match match : matches) {
            // Compare the match's timestamp with the current date
            if (match.getDate().toDate().after(currentDate)) {
                nonExpiredMatches.add(match); // Add if it's not expired
            }
        }

        // Update the list and refresh RecyclerView
        this.matches = nonExpiredMatches;
        matchAdapter.notifyDataSetChanged();
    }

    private void setupSortResultListener()
    {
        getParentFragmentManager().setFragmentResultListener("sortRequest", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String sortingOption = result.getString("sortingOption");

                if (sortingOption != null) {
                    Toast.makeText(requireContext(), "Sorting by: " + sortingOption, Toast.LENGTH_SHORT).show();
                    sortMatches(sortingOption);
                }
            }
        });
    }

    private void setupFilterResultListener()
    {
        getChildFragmentManager().setFragmentResultListener("filterDialogData", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result)
            {
                Log.d("FilterDialog", "FragmentResult received");
                String matchField = result.getString("matchField");
                int quota = result.getInt("quota");

                int year = result.getInt("year", -1);
                int month = result.getInt("month", -1);
                int day = result.getInt("day", -1);
                int hour = result.getInt("hour", -1);
                int minute = result.getInt("minute", -1);

                Log.d("FilterDialog", "Field: " + matchField + ", Quota: " + quota);
                Log.d("FilterDialog", "Date: " + year + "/" + month + "/" + day);
                Log.d("FilterDialog", "Time: " + hour + ":" + minute);

                if(year != -1)
                {
                    calender1 = Calendar.getInstance();
                    calender1.set(Calendar.YEAR, year);
                    calender1.set(Calendar.MONTH, month);
                    calender1.set(Calendar.DAY_OF_MONTH, day);
                }

                if(hour != -1)
                {
                    calendar2 = Calendar.getInstance();
                    calendar2.set(Calendar.HOUR_OF_DAY, hour);
                    calendar2.set(Calendar.MINUTE, minute);
                }
                Log.d("FilterDialog", "Matches Before Filter: " + matches.size());
                filterMatches(matchField, quota, calender1, calendar2);
                Log.d("FilterDialog", "Matches After Filter: " + matches.size());
            }
        });
    }

    private void sortMatches(String sortingOption)
    {
        if(matches == null ||matches.isEmpty())
        {
            return;
        }

        if(sortingOption.equalsIgnoreCase("date"))
        {
            Collections.sort(matches, new Comparator<Match>() {
                @Override
                public int compare(Match m1, Match m2)
                {
                    return m1.getDate().compareTo(m2.getDate());
                }
            });
        }
        else if(sortingOption.equalsIgnoreCase("place"))
        {
            Collections.sort(matches, new Comparator<Match>() {
                @Override
                public int compare(Match m1, Match m2)
                {
                    return m1.getField().getAction().compareToIgnoreCase(m2.getField().getAction());
                }
            });
        }
        else if(sortingOption.equalsIgnoreCase("quota"))
        {
            Collections.sort(matches, new Comparator<Match>() {
                @Override
                public int compare(Match m1, Match m2)
                {
                    int size1 = m1.getPlayersA().size() + m2.getPlayersB().size();
                    int size2 = m2.getPlayersA().size() + m2.getPlayersB().size();

                    return Integer.compare(size1, size2);
                }
            });
        }
        else
        {
            Toast.makeText(getContext(), "Unknown sorting option", Toast.LENGTH_SHORT).show();
            return;
        }

        matchAdapter.notifyDataSetChanged();
    }

    private void filterMatches(String matchField, Integer quota,
                               Calendar selectedDate, Calendar selectedTime )
    {
        List<Match> filteredMathces = new ArrayList<>(matches);

        if(!matchField.equalsIgnoreCase("Select"))
        {
            filteredMathces = filterMatchesByField(matchField);
        }

        filteredMathces = filterMatchesByQuota(quota);

        if (selectedDate != null)
        {
            filteredMathces = filterMatchesByDate(selectedDate);
        }

        if(selectedTime != null)
        {
            filteredMathces = filterMatchesByTime(selectedTime);
        }

        matches.clear();
        matches.addAll(filteredMathces);

        matchAdapter.notifyDataSetChanged();
    }

    private List<Match> filterMatchesByField(String matchField)
    {
        List<Match> filteredMatches = new ArrayList<>();

        for(Match match : matches)
        {
            if(match.getField().toString().equalsIgnoreCase(matchField))
            {
                filteredMatches.add(match);
            }
        }
        return filteredMatches;
    }

    private List<Match> filterMatchesByQuota(int quota)
    {
        List<Match> filteredMatches = new ArrayList<>();

        for(Match match : matches)
        {
            int totalPlayers = match.getPlayersA().size() + match.getPlayersB().size();

            if(totalPlayers <= quota)
            {
                filteredMatches.add(match);
            }
        }
        return filteredMatches;
    }

    private List<Match> filterMatchesByDate(Calendar selectedDate)
    {
        List<Match> filteredMatches = new ArrayList<>();

        for(Match match : matches)
        {
            Timestamp matchDateAndTimeOfTimeStamp = match.getDate();
            Calendar matchDateAndTime = Calendar.getInstance();
            matchDateAndTime.setTimeInMillis(matchDateAndTimeOfTimeStamp.getSeconds() * 1000);

            if(selectedDate.get(Calendar.YEAR) == matchDateAndTime.get(Calendar.YEAR)
                    && selectedDate.get(Calendar.MONTH) == matchDateAndTime.get(Calendar.MONTH)
                    && selectedDate.get(Calendar.DAY_OF_MONTH) == matchDateAndTime.get(Calendar.DAY_OF_MONTH))
            {
                filteredMatches.add(match);
            }
        }
        return filteredMatches;
    }

    private List<Match> filterMatchesByTime(Calendar selectedTime)
    {
        List<Match> filteredMatches = new ArrayList<>();

        for(Match match : matches)
        {
            Timestamp matchDateAndTimeOfTimeStamp = match.getDate();
            Calendar matchDateAndTime = Calendar.getInstance();
            matchDateAndTime.setTimeInMillis(matchDateAndTimeOfTimeStamp.getSeconds() * 1000);

            if (selectedTime.get(Calendar.HOUR_OF_DAY) == matchDateAndTime.get(Calendar.HOUR_OF_DAY)
                && selectedTime.get(Calendar.MINUTE) >= matchDateAndTime.get(Calendar.MINUTE))
            {
                filteredMatches.add(match);
            }
        }
        return filteredMatches;
    }
    public void getMatches(firestoreUser.FirestoreCallback<List<Match>> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Task<List<Match>> taskMatches5 = db.collection("matches5").get()
                .continueWith(task -> {
                    List<Match> matches = new ArrayList<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Match match = doc.toObject(Match.class);
                            matches.add(match);
                        }
                    }
                    return matches;
                });

        Task<List<Match>> taskMatches6 = db.collection("matches6").get()
                .continueWith(task -> {
                    List<Match> matches = new ArrayList<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Match match = doc.toObject(Match.class);
                            matches.add(match);
                        }
                    }
                    return matches;
                });

        Task<List<List<Match>>> allTasks = Tasks.whenAllSuccess(taskMatches5, taskMatches6);
        allTasks.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Match> combinedMatches = new ArrayList<>();
                for (List<Match> matchList : task.getResult()) {
                    combinedMatches.addAll(matchList);
                }
                callback.onSuccess(combinedMatches);
            } else {
                callback.onError(task.getException());
            }
        });
    }

}
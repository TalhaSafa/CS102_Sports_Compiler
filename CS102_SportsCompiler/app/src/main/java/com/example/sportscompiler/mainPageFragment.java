package com.example.sportscompiler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sportscompiler.AdditionalClasses.Match;
import com.example.sportscompiler.AdditionalClasses.MatchAdapter;
import com.example.sportscompiler.AdditionalClasses.MatchSearch;
import com.example.sportscompiler.AdditionalClasses.SearchForPlayer;
import com.example.sportscompiler.AdditionalClasses.firestoreUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link mainPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class mainPageFragment extends Fragment implements MatchAdapter.OnItemClickListener {

    private TextView day1Txt, day2Txt, day3Txt;
    private ImageView day1Img, day2Img, day3Img;
    private int day;
    private String cityName = "Ankara";
    private String APIKey = "9f5051ab914d08850742c634382c54ee";
    private RecyclerView recyclerView;
    private MatchAdapter matchAdapter;
    private List<Match> matches = new ArrayList<>();
    public firestoreUser user= new firestoreUser();
    private FirebaseFirestore firestore;
    private ListenerRegistration registration;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public mainPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment mainPageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static mainPageFragment newInstance(String param1, String param2) {
        mainPageFragment fragment = new mainPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInsanceState)
    {
        day1Txt = view.findViewById(R.id.day1Txt);
        day2Txt = view.findViewById(R.id.day2Txt);
        day3Txt = view.findViewById(R.id.day3Txt);

        day1Img = view.findViewById(R.id.day1Img);
        day2Img = view.findViewById(R.id.day2Img);
        day3Img = view.findViewById(R.id.day3Img);

        updateWeatherConditions(1, day1Txt, day1Img); //for tomorrow
        updateWeatherConditions(2, day2Txt, day2Img); //tomorrow + 1
        updateWeatherConditions(3, day3Txt, day3Img); //tomorrow + 2

        recyclerView = view.findViewById(R.id.matchListRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        matchAdapter = new MatchAdapter(requireContext(),matches,this);
        recyclerView.setAdapter(matchAdapter);


        firestore = FirebaseFirestore.getInstance();
        registration = firestore.collection("users").document(user.getUserID()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error != null)
                {
                    if(getContext() != null)
                        Toast.makeText(getContext(), "Failed to fetch matches: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
                else if (error == null && value.exists())
                {
                    List<String> registeredMatches = (List<String>) value.get("matches");
                    if(registeredMatches != null)
                    {
                        fetchUserMatches("matches5", registeredMatches);
                        fetchUserMatches("matches6", registeredMatches);
                    }

                }
            }
        });



//        user.getMatches(new firestoreUser.FirestoreCallback<List<Match>>() {
//            @Override
//            public void onSuccess(List<Match> result) {
//                matches = result;
//                filterNonExpiredMatches();
//                matchAdapter.updateData(matches);
//            }
//
//            @Override
//            public void onError(Exception e) {
//                Toast.makeText(getContext(), "Failed to fetch matches: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//        matchAdapter = new MatchAdapter(requireContext(),matches,this);
//        recyclerView.setAdapter(matchAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove Firestore listener to avoid memory leaks
        if (registration != null) {
            registration.remove();
            registration = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_page, container, false);


        return view;
    }

    private void fetchUserMatches(String collectionName, List<String > registeredMatches)
    {
        firestore.collection(collectionName).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!= null)
                {
                    Log.e("Firebase", "Error fetching matches", error);
                }
                else
                {
                    if(!value.isEmpty())
                    {
                        for(DocumentSnapshot snapshot: value.getDocuments())
                        {
                            Match match = snapshot.toObject(Match.class);
                            if(registeredMatches.contains(match.getMatchID()) && !MatchSearch.doesContainMatch(matches, match))
                            {
                                matches.add(match);
                            }
                        }
                        filterNonExpiredMatches();
                        matchAdapter.updateData(matches);
                    }
                    else
                    {
                        System.out.println("Empty matches");
                    }
                }
            }
        });
    }
    private void fetchUsersMatches(String collectionName, List<String> registeredMatches)
    {
        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();

        for (String matchID : registeredMatches) {
            Task<DocumentSnapshot> task = firestore.collection(collectionName).document(matchID).get();
            tasks.add(task);
        }

        Tasks.whenAllSuccess(tasks).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
            @Override
            public void onSuccess(List<Object> objects) {
                for (Object result : objects) {
                    DocumentSnapshot documentSnapshot = (DocumentSnapshot) result;
                    if (documentSnapshot.exists()) {
                        Match match = documentSnapshot.toObject(Match.class);
                        if (match != null) {
                            if(!MatchSearch.doesContainMatch(matches, match))
                                matches.add(match);
                        }
                    }
                }
                filterNonExpiredMatches();
                matchAdapter.updateData(matches);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Firebase", "Error fetching matches", e);
            }
        });
    }

    private void updateWeatherConditions(int day, TextView txtView, ImageView imgView)
    {
        // Run the task on a background thread
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                String result = getWeatherData();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateWeatherUI(result, handler, day, txtView, imgView);
                    }
                });
            }
        });
    }

    private String getWeatherData() {
        StringBuilder result = new StringBuilder();
        try {
            String urlStr = "https://api.openweathermap.org/data/2.5/forecast?q=" + cityName + "&appid=" + APIKey;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;
            //To get the return of API
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }


    private void updateWeatherUI(String result, Handler handler, int day, TextView txtView, ImageView imgView) {
        try {
            // Parse JSON data
            JSONObject jsonObject = new JSONObject(result);
            JSONArray list = jsonObject.getJSONArray("list");

            for(int i = 0 ; i < list.length() ; i++)
            {
                JSONObject weatherData = list.getJSONObject(i);
                String dateStr = weatherData.getString("dt_txt");

                if(isWantedDate(day, dateStr, txtView) && dateStr.contains("12:00:00")) //to get on wanted day at 12
                {
                    JSONObject wantedDay = weatherData.getJSONObject("main");
                    double temperature = wantedDay.getDouble("temp") - 273.15;

                    String weatherDescription = weatherData
                            .getJSONArray("weather")
                            .getJSONObject(0)
                            .getString("description");

                    String iconCode = weatherData
                            .getJSONArray("weather")
                            .getJSONObject(0)
                            .getString("icon");
                    weatherDescription = capitalizeWords(weatherDescription);
                    String formattedText = String.format("%.1f°C\n%s\n%s\n%s", temperature, weatherDescription, dateStr.substring(0, 10), dateStr.substring(11, 16));

// Create a SpannableString from the formatted string
                    SpannableString spannableString = new SpannableString(formattedText);

// Find the index of the temperature part in the string (e.g., the first part before "\n")
                    int temperatureEndIndex = formattedText.indexOf("°") + 1;  // Index where the temperature ends

// Apply bold and increase size to the temperature part
                    spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, temperatureEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(new RelativeSizeSpan(2f), 0, temperatureEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  // Increase size by 1.5 times

// Set the styled text to the TextView
                    txtView.setText(spannableString);

                    //To download icon and set it to image view:
//                    String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@4x.png";
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                URL url = new URL(iconUrl);
//                                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, false);
//                                handler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        imgView.setImageBitmap(scaledBitmap);
//                                    }
//                                });
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }).start();

                    String iconWay ="i" + iconCode;
                    try {
                        int iconID = getContext().getResources().getIdentifier(iconWay, "drawable", getContext().getPackageName());
                        imgView.setImageResource(iconID);
                    }
                    catch (Exception e)
                    {
                        //ERROR TO LOAD
                    }


                    break;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String capitalizeWords(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String[] words = input.split(" "); // Split the string into words
        StringBuilder capitalizedString = new StringBuilder();

        for (String word : words) {
            // Capitalize the first letter of each word and add the rest unchanged
            capitalizedString.append(word.substring(0, 1).toUpperCase())
                    .append(word.substring(1).toLowerCase())
                    .append(" ");
        }

        // Remove the last extra space
        return capitalizedString.toString().trim();
    }
    private boolean isWantedDate(int day, String dayStr, TextView txt)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, day);
        String tomorrowDate = sdf.format(tomorrow.getTime());
        return dayStr.startsWith(tomorrowDate);
    }
    public void onItemClick(Match match)
    {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String currentUserID = firebaseAuth.getCurrentUser().getUid();

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
    }
    public List<Match> filterNonExpiredMatches() {
        List<Match> nonExpiredMatches = new ArrayList<>();
        List<Match> expiredMatches = new ArrayList<>();
        Date currentDate = new Date(); // Current date and time
        for (Match match : matches) {
            // Compare the match's timestamp with the current date
            if (match.getDate().toDate().after(currentDate)) {
                nonExpiredMatches.add(match); // Add if it's not expired
            }
            else
            {
                expiredMatches.add(match);
            }
        }
        Collections.sort(nonExpiredMatches, new Comparator<Match>() {
            @Override
            public int compare(Match m1, Match m2)
            {
                return m1.getDate().compareTo(m2.getDate());
            }
        });
        // Update the list and refresh RecyclerView
        this.matches = nonExpiredMatches;
        matchAdapter.notifyDataSetChanged();

        return expiredMatches;
    }
}
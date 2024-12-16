package com.example.sportscompiler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.sportscompiler.AdditionalClasses.Match;
import com.example.sportscompiler.AdditionalClasses.MatchAdapter;
import com.example.sportscompiler.AdditionalClasses.User;
import com.example.sportscompiler.AdditionalClasses.firestoreUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ProfilePage extends Fragment implements MatchAdapter.OnItemClickListener{

    private TextView nameTextView, departmentTextView, ageTextView;
    private static User user;
    private firestoreUser fireuser;
    private Button settingsButton, changeProfile;
    public Uri ImageUri;
    private ImageView Image;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private RecyclerView currentMatchRecyclerView, pastMatchRecyclerView;
    private MatchAdapter matchAdapterForCurrentMatches, matchAdapterForPastMatches;
    private List<Match> allMatches = new ArrayList<>();
    private List<Match> pastMatches = new ArrayList<>();
    private FirebaseFirestore firestore;
    private ListenerRegistration listenerRegistration;

    public ProfilePage() {
        user = new User();
        fireuser = new firestoreUser();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_page, container, false);

        // Initialize UI elements
        Image = rootView.findViewById(R.id.profile_image);
        nameTextView = rootView.findViewById(R.id.nameTextView);
        departmentTextView = rootView.findViewById(R.id.departmentTextView);
        ageTextView = rootView.findViewById(R.id.ageTextView);
        settingsButton = rootView.findViewById(R.id.settingsButton);
        changeProfile = rootView.findViewById(R.id.addPP);
        pastMatchRecyclerView = rootView.findViewById(R.id.matchListRecyclerforPastMatches);

        pastMatchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        matchAdapterForPastMatches = new MatchAdapter(requireContext(), pastMatches, (MatchAdapter.OnItemClickListener) this);
        pastMatchRecyclerView.setAdapter(matchAdapterForPastMatches);
        firestore = FirebaseFirestore.getInstance();



        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    ImageUri = result.getData().getData();
                    Image.setImageURI(ImageUri);
                    Log.d("report", "uri: " + ImageUri);

                    if (ImageUri != null) {
                        Log.d("ProfilePage", "Saving image with URI: " + ImageUri);
                        fireuser.saveImage(ImageUri, requireContext()); // Saving image
                    }
                }
            }
        });

        changeProfile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                pickImageLauncher.launch(intent);
            }
        });

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

        listenerRegistration = firestore.collection("users").document(fireuser.getUserID()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
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
                        fetchUsersMatches("matches5", registeredMatches);
                        fetchUsersMatches("matches6", registeredMatches);
                    }

                }
            }
        });


        return rootView;
    }



    @Override
    public void onStop() {
        super.onStop();

        // Remove Firestore listener to avoid memory leaks
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
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
                            if(!allMatches.contains(match))
                                allMatches.add(match);
                        }
                    }
                }
                filterNonExpiredMatches();
                matchAdapterForPastMatches.updateData(pastMatches);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Firebase", "Error fetching matches", e);
            }
        });



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
            ageTextView.setText(Integer.toString(calculateAge(user.getBirthDate())));
            loadProfilePic();
        }
    }

    private int calculateAge(String birthday) {
        // Define the date format for "dd/mm/yyyy"
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        int age;
        try {
            // Parse the birthday string into a Date object
            Date birthDate = dateFormat.parse(birthday);

            // Get the current date
            Calendar today = Calendar.getInstance();

            // Set the calendar to the birth date
            Calendar birthDay = Calendar.getInstance();
            birthDay.setTime(birthDate);

            // Calculate the age
            age = today.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);

            // If the birthdate hasn't occurred yet this year, subtract one
            if (today.get(Calendar.DAY_OF_YEAR) < birthDay.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

        }
        catch (ParseException e)
        {
            age = 0;
        }
            return age;
        }

    private void loadProfilePic() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(user.getUserID()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.contains("profilePicture")) {
                String uri = documentSnapshot.getString("profilePicture");
                Bitmap map = fireuser.decodeBase64ToImage(uri);
                Uri imageUri = fireuser.saveBitmapToFile(requireContext(),map);
                Image.setImageURI(imageUri);
                //Glide.with(this).load(imageUri).placeholder(R.drawable.blank_profile_picture).error(android.R.drawable.stat_notify_error).into(Image);


            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Error fetching URI: " + e.getMessage(), Toast.LENGTH_SHORT).show());

    }

    public void filterNonExpiredMatches() {
        List<Match> expiredMatches = new ArrayList<>();
        Date currentDate = new Date(); // Current date and time
        for (Match match : allMatches) {
            // Compare the match's timestamp with the current date
            if (match.getDate().toDate().before(currentDate)) {
                expiredMatches.add(match);
            }
        }
        // Update the list and refresh RecyclerView
        this.pastMatches = expiredMatches;
        matchAdapterForPastMatches.notifyDataSetChanged();

    }

    @Override
    public void onItemClick(Match match) {
        //TODO
    }
}


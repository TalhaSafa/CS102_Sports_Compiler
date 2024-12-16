package com.example.sportscompiler;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import com.bumptech.glide.Glide;
import com.example.sportscompiler.AdditionalClasses.Match;
import com.example.sportscompiler.AdditionalClasses.MatchAdapter;
import com.example.sportscompiler.AdditionalClasses.User;
import com.example.sportscompiler.AdditionalClasses.firestoreUser;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ProfilePage extends Fragment {

    private TextView nameTextView, departmentTextView, ageTextView;
    private static User user;
    private firestoreUser fireuser;
    private Button settingsButton, changeProfile;
    public Uri ImageUri;
    private ImageView Image;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private RecyclerView currentMatchRecyclerView, pastMatchRecyclerView;
    private MatchAdapter matchAdapterForCurrentMatches, matchAdapterForPastMatches;
    private List<Match> currentMatches = new ArrayList<>();
    private List<Match> pastMatches = new ArrayList<>();

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
        currentMatchRecyclerView = rootView.findViewById(R.id.matchListRecyclerforCurrentMatches);
        pastMatchRecyclerView = rootView.findViewById(R.id.matchListRecyclerforPastMatches);

        currentMatchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pastMatchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        matchAdapterForCurrentMatches = new MatchAdapter(requireContext(),currentMatches, (MatchAdapter.OnItemClickListener) this);
        matchAdapterForPastMatches = new MatchAdapter(requireContext(), pastMatches, (MatchAdapter.OnItemClickListener) this);

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

    //TODO: implement a method calculates age according to birth date.
    public int ageCalc(String birthDate){
        return 0;
    }

}


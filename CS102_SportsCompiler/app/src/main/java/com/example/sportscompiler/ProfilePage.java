package com.example.sportscompiler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.sportscompiler.AdditionalClasses.Match;
import com.example.sportscompiler.AdditionalClasses.MatchAdapter;
import com.example.sportscompiler.AdditionalClasses.MatchSearch;
import com.example.sportscompiler.AdditionalClasses.User;
import com.example.sportscompiler.AdditionalClasses.firestoreUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;    import android.media.ExifInterface;

public class ProfilePage extends Fragment implements MatchAdapter.OnItemClickListener{

    private TextView nameTextView, departmentTextView, ageTextView;
    private User user;
    private firestoreUser fireuser;
    private Button settingsButton;
    private ImageButton changeProfile;
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
                    Bitmap map = getDownscaledBitmap(ImageUri);
                    Uri imageuri2 = fireuser.saveBitmapToFile(getContext(),map);
                    Log.d("report", "uri: " + ImageUri);

                    if (imageuri2 != null) {
                        Log.d("ProfilePage", "Saving image with URI: " + ImageUri);
                        fireuser.saveImage(imageuri2, requireContext());



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
                            if(!MatchSearch.doesContainMatch(allMatches, match))
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

    public User getUser() {
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
                Uri imageUri = fireuser.saveBitmapToFile(getContext(),map);
                Image.setImageURI(imageUri);
                //Glide.with(this).load(imageUri).placeholder(R.drawable.blank_profile_picture).error(android.R.drawable.stat_notify_error).into(Image);


            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Error fetching URI: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onItemClick(Match match)
    {
        String matchType = match.getMatchType();
        if(matchType.equals("matches5"))
        {
            Intent intent = new Intent(getContext(), PlayerScreenOfMatch5x5.class);
            intent.putExtra("matchID", match.getMatchID());
            intent.putExtra("matchType", matchType);
            startActivity(intent);
        }
        else if(matchType.equals("matches6"))
        {
            Intent intent = new Intent(getContext(), PlayerScreenOfMatch6x6.class);
            intent.putExtra("matchID", match.getMatchID());
            intent.putExtra("matchType", matchType);
            startActivity(intent);
        }
    }
    public void filterNonExpiredMatches() {
        List<Match> expiredMatches = new ArrayList<>();
        Date currentDate = new Date(); // Current date and time
        for (Match match : allMatches) {
            // Compare the match's timestamp with the current date
            if (match.getDate().toDate().before(currentDate)) {
                if(!MatchSearch.doesContainMatch(expiredMatches, match))
                {
                    expiredMatches.add(match);

                }
            }
        }
        // Update the list and refresh RecyclerView
        this.pastMatches = expiredMatches;
        matchAdapterForPastMatches.notifyDataSetChanged();

    }
    private Bitmap getDownscaledBitmap(Uri uri) {
        InputStream inputStream = null;
        try {
            // Open the input stream for the given URI
            inputStream = getContext().getContentResolver().openInputStream(uri);

            // Decode only the bounds to determine image dimensions
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);

            // Close and reopen the input stream
            inputStream.close();

            // Calculate a sample size to reduce memory usage
            options.inSampleSize = calculateInSampleSize(options, 512, 512); // Target size
            options.inJustDecodeBounds = false;

            inputStream = getContext().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);

            // Fix the image orientation
            return fixImageOrientation(uri, bitmap);

        } catch (IOException e) {
            // Log the error and show a message to the user
            Log.e("ProfilePage", "Error downscaling bitmap: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Error loading image.", Toast.LENGTH_SHORT).show();
            return null; // Return null if there's an error
        } finally {
            // Ensure the InputStream is closed to prevent resource leaks
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e("ProfilePage", "Error closing input stream: " + e.getMessage(), e);
                }
            }
        }
    }
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    private Bitmap fixImageOrientation(Uri uri, Bitmap bitmap) {
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            ExifInterface exif = new ExifInterface(inputStream);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            int rotationDegrees = 0;
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotationDegrees = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotationDegrees = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotationDegrees = 270;
                    break;
            }

            if (rotationDegrees != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(rotationDegrees);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }

            inputStream.close();
        } catch (IOException e) {
            Log.e("ProfilePage", "Error fixing image orientation: " + e.getMessage(), e);
        }

        return bitmap;
    }


}







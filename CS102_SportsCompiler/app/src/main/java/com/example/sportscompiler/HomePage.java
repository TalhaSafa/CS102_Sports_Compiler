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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sportscompiler.AdditionalClasses.User;
import com.example.sportscompiler.AdditionalClasses.firestoreUser;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;

public class HomePage extends Fragment {

    private TextView nameTextView, departmentTextView, ageTextView;
    private User user;
    private firestoreUser fireuser;
    private Button settingsButton, changeProfile;
    public Uri ImageUri;
    private ImageView Image;


    public HomePage() {
        user = new User();
        fireuser = new firestoreUser();
    }

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>(){
        @Override
        public void onActivityResult(ActivityResult o) {
            if(o.getResultCode() == Activity.RESULT_OK && o.getData() != null){
                ImageUri = o.getData().getData();
                Image.setImageURI(ImageUri);

            }
            if(ImageUri != null){
                fireuser.saveImage(ImageUri, requireContext());
            }
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_page, container, false);

        // Initialize UI elements
        Image = rootView.findViewById(R.id.profile_image);
        nameTextView = rootView.findViewById(R.id.nameTextView);
        departmentTextView = rootView.findViewById(R.id.departmentTextView);
        ageTextView = rootView.findViewById(R.id.ageTextView);
        settingsButton = rootView.findViewById(R.id.settingsButton);
        changeProfile = rootView.findViewById(R.id.addPP);

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
                FragmentLoad.loadFragment((AppCompatActivity) HomePage.this.getActivity(), R.id.fragmentContainerView, new profile_settings() );

            }
        });


        return rootView;
    }


    /**
     * Updates the UI elements with the user's data.
     */
    private void updateUI() {
        if (user != null) {
            nameTextView.setText(user.getName());
            departmentTextView.setText(user.getDepartment());
            ageTextView.setText(user.getBirthDate());
            loadProfilePic();

        }
    }
    private void loadProfilePic() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(user.getUserID()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.contains("profilePicture")) {
                String uri = documentSnapshot.getString("profilePicture");
                Bitmap map = fireuser.decodeBase64ToImage(uri);
                Uri imageUri = fireuser.saveBitmapToFile(requireContext(),map);

                Glide.with(this).load(imageUri).placeholder(R.drawable.blank_profile_picture).error(android.R.drawable.stat_notify_error).into(Image);


            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Error fetching URI: " + e.getMessage(), Toast.LENGTH_SHORT).show());

    }

            //TODO: implement a method calculates age according to birth date.
    public int ageCalc(String birthDate){
        return 0;
    }

}


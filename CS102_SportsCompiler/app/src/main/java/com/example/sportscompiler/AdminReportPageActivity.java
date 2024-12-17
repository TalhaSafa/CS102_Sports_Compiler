package com.example.sportscompiler;

import android.os.Bundle;
import android.util.Log;
import android.os.Message;
import android.widget.Toast;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sportscompiler.AdditionalClasses.adminHelpMessage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
public class AdminReportPageActivity extends AppCompatActivity {

    private FirebaseFirestore data;
    private FirebaseAuth firebaseAuth;
    private Button sendButton;
    private String matchID, adminID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_report_page);

        matchID = getIntent().getStringExtra("matchID");
        adminID = getIntent().getStringExtra("adminID");

        


        data = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

    }
    private void uptadeMessage(String reason, String message, String mail, String userName, String matchID, String adminID){

        String repMessage = reason + " - "  + message + "\n" + "Admin ID: " + adminID + "\nMatch ID: " + matchID;
        Timestamp nowTime = Timestamp.now();
        adminHelpMessage reportMessage = new adminHelpMessage(repMessage, firebaseAuth.getUid(), mail, userName, nowTime );
        String mssgID = adminID + " " + userName + " " + nowTime.toDate().toString();
        data.collection("helpMessages").document(mssgID).set(reportMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(AdminReportPageActivity.this, "Sent Report!" , Toast.LENGTH_SHORT).show();
                FragmentLoad.changeActivity(AdminReportPageActivity.this, homeActivity.class);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminReportPageActivity.this, "Couldn't Send Report!" , Toast.LENGTH_SHORT).show();

            }
        });

    }
}
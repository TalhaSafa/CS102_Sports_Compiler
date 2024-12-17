package com.example.sportscompiler;

import android.os.Bundle;
import android.util.Log;
import android.os.Message;
import android.widget.Toast;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class AdminReportPageActivity extends AppCompatActivity {

    private FirebaseFirestore data;
    private FirebaseAuth firebaseAuth;
    private Button sendButton;
    private String matchID, adminID;
    private TextView reasonTextView, reportTheAdminTextView, specificationTextView;
    private EditText specificationEditText;
    private Spinner reasonSpinner;
    private EditText enterUserMail;
    private String userMail, userReason, specification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_report_page);

        matchID = getIntent().getStringExtra("matchID");
        adminID = getIntent().getStringExtra("adminID");

        data = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        sendButton = findViewById(R.id.reportButton);
        reasonSpinner = findViewById(R.id.spinnerReport);
        specificationEditText = findViewById(R.id.textInputEditText);
        enterUserMail = findViewById(R.id.enterUserMail);

        List<String> reasons = new ArrayList<>();

        reasons.add("Admin entered the scored wrong");
        reasons.add("Admin didn't enter the score");
        reasons.add("Admin entered the match informations wrong");
        reasons.add("Else");

        ArrayAdapter<String> reasonsAdapter = new ArrayAdapter<>(AdminReportPageActivity.this, android.R.layout.simple_spinner_item, reasons);
        reasonsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reasonSpinner.setAdapter(reasonsAdapter);
        reasonSpinner.setDropDownVerticalOffset(reasonSpinner.getHeight());

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                userMail = enterUserMail.getText().toString();
                specification = specificationEditText.getText().toString();
                if(userReason == null || userReason.equals(""))
                {
                    Toast.makeText(AdminReportPageActivity.this, "Choose Reason" , Toast.LENGTH_SHORT).show();
                }
                else if(specification == null || specification.equals(""))
                {
                    Toast.makeText(AdminReportPageActivity.this, "Specify Your Report" , Toast.LENGTH_SHORT).show();
                }
                else if (userMail == null || userMail.equals(""))
                {
                    Toast.makeText(AdminReportPageActivity.this, "Enter Mail For Further Communication" , Toast.LENGTH_SHORT).show();
                }
                else
                {
                    uptadeMessage(userReason, specification, userMail, matchID, adminID);
                }
            }
        });



        reasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                userReason = reasonSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                userReason = "";
            }
        });

        
    }
    private void uptadeMessage(String reason, String message, String mail, String matchID, String adminID){

        String repMessage = reason + " - "  + message + "\n" + "Admin ID: " + adminID + "\nMatch ID: " + matchID;
        Timestamp nowTime = Timestamp.now();
        adminHelpMessage reportMessage = new adminHelpMessage(repMessage, firebaseAuth.getUid(), mail, mail, nowTime );
        String mssgID = adminID + " " + mail + " " + nowTime.toDate().toString();
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
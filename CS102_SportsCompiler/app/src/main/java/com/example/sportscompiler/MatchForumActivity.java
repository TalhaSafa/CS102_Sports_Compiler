package com.example.sportscompiler;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportscompiler.AdditionalClasses.Match;
import com.example.sportscompiler.AdditionalClasses.Message;
import com.example.sportscompiler.AdditionalClasses.User;
import com.example.sportscompiler.AdditionalClasses.firestoreUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MatchForumActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editTextMessage;
    private Button buttonSend;

    private ArrayList<Message> messageList;
    private com.example.sportscompiler.Adapters.MessageAdapter messageAdapter;
    private firestoreUser fireUser;
    private User loggedInUser;
    private Match currMatch;
    private String matchID;
    private String matchType;

    private Timestamp messageTime;
    private FirebaseFirestore firestore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_match_forum);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        firestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerViewMessagesAc);
        editTextMessage = findViewById(R.id.editTextTextMultiLineAc);
        buttonSend = findViewById(R.id.sendButtonAc);

        loggedInUser = new User();
        fireUser = new firestoreUser();
        initializeUser();

        messageList = new ArrayList<>();
        messageAdapter = new com.example.sportscompiler.Adapters.MessageAdapter(this, messageList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        matchID = getIntent().getStringExtra("matchID");
        matchType = getIntent().getStringExtra("matchType");



        firestore.collection(matchType).document(matchID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(MatchForumActivity.this, "Could not access database", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(value != null && value.exists())
                {
                    currMatch = value.toObject(Match.class);
                }
                else
                {
                    Toast.makeText(MatchForumActivity.this, "Null Match", Toast.LENGTH_SHORT).show();
                }
            }
        });

        firestore.collection(matchType).document(matchID).collection("forum").orderBy("time", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    Toast.makeText(MatchForumActivity.this, "Could not fetch messages", Toast.LENGTH_SHORT).show();

                }
                else if(error == null && value != null)
                {

                    for(DocumentChange change: value.getDocumentChanges())
                    {
                        switch (change.getType()) {
                            case ADDED:
                                Message message = change.getDocument().toObject(Message.class);
                                messageList.add(message);
                                break;
                            case MODIFIED:
                                // Optionally handle message updates
                                break;
                            case REMOVED:
                                // Optionally handle message deletions
                                break;
                        }
                    }


                    //HAS SOME ERROR
                    /*
                    for(DocumentSnapshot snapshot: value.getDocuments())
                    {
                        Message currMessages = snapshot.toObject(Message.class);
                        if(currMessages != null && !messageList.contains(currMessages))
                        {
                            messageList.add(currMessages);
                            messageAdapter.notifyItemInserted(messageList.size() - 1);
                            recyclerView.scrollToPosition(messageList.size() - 1);
                        }
                    }
                    */

                    messageAdapter.updateData(messageList);


                }
            }
        });

        buttonSend.setOnClickListener(v -> {
            String content = editTextMessage.getText().toString().trim();
            if (!content.isEmpty()) {
                // Add message with logged-in user
                messageTime = Timestamp.now();

                //SETTING MESSAGE ID:
                String messageID = currMatch.getMatchID() + loggedInUser.getName() + messageTime.toDate().toString();

                Message newMessage = new Message(loggedInUser, messageTime, content, messageID);

                //Adding message to match forum

                firestore.collection(currMatch.getMatchType()).document(currMatch.getMatchID()).
                        collection("forum").document(messageID).set(newMessage).
                        addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
//                                    messageList.add(newMessage);
//                                    messageAdapter.notifyItemInserted(messageList.size() - 1);
//                                    recyclerView.scrollToPosition(messageList.size() - 1);
                                }
                                else
                                {
                                    Toast.makeText(MatchForumActivity.this, "Could not send message: " + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                editTextMessage.setText("");
            }
        });
    }

    private void initializeUser()
    {
        fireUser.updateInfo(loggedInUser, new firestoreUser.FirestoreCallback<User>() {
            @Override
            public void onSuccess(User result) {
                loggedInUser = result;
            }

            @Override
            public void onError(Exception e) {
                Log.e("HomePage", "Error fetching user info", e);
            }
        });
    }
}
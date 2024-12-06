package com.example.sportscompiler;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportscompiler.Adapters.MessageAdapter;
import com.example.sportscompiler.AdditionalClasses.Match;
import com.example.sportscompiler.AdditionalClasses.Message;
import com.example.sportscompiler.AdditionalClasses.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class matchForum extends Fragment {

    private RecyclerView recyclerView;
    private EditText editTextMessage;
    private Button buttonSend;

    private List<Message> messageList;
    private MessageAdapter messageAdapter;
    private User loggedInUser;
    private Match currMatch;

    private Timestamp messageTime;
    private FirebaseFirestore firestore;

    public matchForum() {
        this.loggedInUser = ProfilePage.getUser();
    }

    public matchForum(Match match) {
        this.loggedInUser = ProfilePage.getUser();
        currMatch = match;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match_forum, container, false);

        firestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recyclerViewMessages);
        editTextMessage = view.findViewById(R.id.editTextTextMultiLine2);
        buttonSend = view.findViewById(R.id.button4);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(getContext(), messageList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(messageAdapter);

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
                                    messageList.add(newMessage);
                                    messageAdapter.notifyItemInserted(messageList.size() - 1);
                                    recyclerView.scrollToPosition(messageList.size() - 1);
                                }
                                else
                                {
                                    Toast.makeText(view.getContext(), "Could not send message: " + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                editTextMessage.setText("");
            }
        });

        return view;
    }
}

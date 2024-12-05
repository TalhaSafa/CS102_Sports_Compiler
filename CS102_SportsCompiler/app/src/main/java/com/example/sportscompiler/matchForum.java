package com.example.sportscompiler;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportscompiler.Adapters.MessageAdapter;
import com.example.sportscompiler.AdditionalClasses.Message;
import com.example.sportscompiler.AdditionalClasses.User;

import java.util.ArrayList;
import java.util.List;

public class matchForum extends Fragment {

    private RecyclerView recyclerView;
    private EditText editTextMessage;
    private Button buttonSend;

    private List<Message> messageList;
    private MessageAdapter messageAdapter;
    private User loggedInUser;

    public matchForum() {
        this.loggedInUser = ProfilePage.getUser();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match_forum, container, false);

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
                messageList.add(new Message(loggedInUser, content));
                messageAdapter.notifyItemInserted(messageList.size() - 1);
                recyclerView.scrollToPosition(messageList.size() - 1);
                editTextMessage.setText("");
            }
        });

        return view;
    }
}

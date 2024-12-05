package com.example.sportscompiler.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sportscompiler.AdditionalClasses.Message;
import com.example.sportscompiler.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private Context context;
    private List<Message> messageList;

    public MessageAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);

        holder.username.setText(message.getUser().getName());
        holder.content.setText(message.getContent());

        // Load profile picture using Glide
        Glide.with(context)
                .load(message.getUser().getProfilePicture())
                .placeholder(R.drawable.baseline_person_24)
                .into(holder.profilePicture);
    }

    @Override
    public int getItemCount() { return messageList.size(); }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView username, content;
        ImageView profilePicture;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.textUsername);
            content = itemView.findViewById(R.id.textContent);
            profilePicture = itemView.findViewById(R.id.imageProfilePicture);
        }
    }
}

package com.example.sportscompiler.AdditionalClasses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportscompiler.R;

import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    private List<String> players;

    // Constructor
    public PlayerAdapter(List<String> players) {
        this.players = players;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_list_item, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        String playerName = players.get(position);
        holder.playerName.setText(playerName);

        // Set a click listener to remove the player when clicked
        holder.itemView.setOnClickListener(v -> {
            removePlayer(position); // Remove the player when clicked
            Toast.makeText(v.getContext(), playerName + " removed", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    // Method to remove a player
    public void removePlayer(int position) {
        if (position >= 0 && position < players.size()) {
            players.remove(position); // Remove player from the list
            notifyItemRemoved(position); // Notify adapter to remove the item from the list
            notifyItemRangeChanged(position, players.size()); // Update remaining items
        }
    }

    // ViewHolder class to hold the player name TextView
    public static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView playerName;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            playerName = itemView.findViewById(R.id.playerName);
        }
    }
}

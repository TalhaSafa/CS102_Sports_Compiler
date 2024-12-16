package com.example.sportscompiler.AdditionalClasses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportscompiler.R;

import java.util.List;

public class DismissAdapter extends RecyclerView.Adapter<DismissAdapter.PlayerViewHolder> {

    private List<Player> players;
    private DismissActionListener listener;

    // Constructor
    public DismissAdapter(List<Player> players, DismissActionListener listener) {
        this.players = players;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player_dismiss, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        // Get the current player
        Player player = players.get(position);

        // Bind data to views
        holder.playerName.setText(player.getName());
        holder.playerPosition.setText("Position: " + player.getPosition().toString());
        holder.playerRating.setText("Rating: " + player.getRating());

        // Set dismiss button click listener
        holder.btnDismiss.setOnClickListener(v -> {
            // Remove player from the list
            removePlayer(position);

            // Show a Toast message
            Toast.makeText(v.getContext(), player.getName() + " dismissed", Toast.LENGTH_SHORT).show();
        });
        holder.btnDismiss.setOnClickListener(v -> listener.onDismissClick(player));
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    // Method to remove a player
    public void removePlayer(int position) {
        if (position >= 0 && position < players.size()) {
            players.remove(position); // Remove the player from the list
            notifyItemRemoved(position); // Notify the adapter to refresh the UI
            notifyItemRangeChanged(position, players.size()); // Adjust indices of the remaining items
        }
    }

    // ViewHolder class
    public static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView playerName, playerPosition, playerRating;
        Button btnDismiss;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            playerName = itemView.findViewById(R.id.playerName);
            playerPosition = itemView.findViewById(R.id.playerPosition);
            playerRating = itemView.findViewById(R.id.playerRating);
            btnDismiss = itemView.findViewById(R.id.btnDismiss);
        }
    }
}

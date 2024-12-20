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
    private double rating;

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
        holder.teamName.setText("Team: " + player.getTeam());
        player.getRating(new RatingCallback() {
            @Override
            public void onRatingFetched(double rating1) {
                String formattedRating = String.format("%.2f", rating1);
                holder.playerRating.setText("Rating: " + formattedRating);
            }
        });

        // Set dismiss button click listener
        holder.btnDismiss.setOnClickListener(v -> {
            if(!player.isAdmin()){
                removePlayer(position); // Remove player from the list
                listener.onDismissClick(player); // Trigger the listener callback
                Toast.makeText(v.getContext(), player.getName() + " dismissed", Toast.LENGTH_SHORT).show(); // Optiona
            }
            else {
                Toast.makeText(v.getContext(), player.getName() + " is admin.", Toast.LENGTH_SHORT).show(); // Optiona
            }
        });
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
        TextView playerName, playerPosition, playerRating, teamName;
        Button btnDismiss;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            playerName = itemView.findViewById(R.id.playerName);
            teamName = itemView.findViewById(R.id.teamName);
            playerPosition = itemView.findViewById(R.id.playerPosition);
            playerRating = itemView.findViewById(R.id.playerRating);
            btnDismiss = itemView.findViewById(R.id.btnDismiss);
        }
    }
}

package com.example.sportscompiler.AdditionalClasses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportscompiler.R;

import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    private List<Player> players;

    // Constructor
    public PlayerAdapter(List<Player> players) {
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
        // Get the player object
        Player player = players.get(position);

        // Bind data to the views
        holder.playerName.setText(player.getName());
        holder.position.setText(player.getPosition() != null ? player.getPosition().getAction() : "Unknown Position");
        player.getRating(new RatingCallback() {
            @Override
            public void onRatingFetched(double rating1) {
                String formattedRating = String.format("%.2f", rating1);
                holder.averageRating.setText(formattedRating);
            }
        });

        // Optional: Add a click listener
        holder.itemView.setOnClickListener(v ->
                Toast.makeText(v.getContext(), "Selected: " + player.getName(), Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    // Method to remove a player
    public void removePlayer(int position) {
        if (position >= 0 && position < players.size()) {
            players.remove(position); // Remove player from the lis
            notifyItemRemoved(position); // Notify adapter to remove the item from the list
            notifyItemRangeChanged(position, players.size()); // Update remaining items
        }
    }

    // ViewHolder class to hold the player name TextView
    public static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView playerName,position, averageRating;
        ImageView profilePicture;


        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            playerName = itemView.findViewById(R.id.playerName);
            position = itemView.findViewById(R.id.playerPosition);
            averageRating = itemView.findViewById(R.id.playerRating);
        }
    }
}

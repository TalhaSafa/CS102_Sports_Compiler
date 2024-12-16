package com.example.sportscompiler.AdditionalClasses;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportscompiler.R;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {
    private Context context;
    private List<Match> matchList;
    private OnItemClickListener onItemClickListener;
    public interface OnItemClickListener {
        void onItemClick(Match match);
    }

    public MatchAdapter(Context context ,List<Match> matchList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.matchList = matchList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        Match match = matchList.get(position);

        // Bind data
        holder.matchName.setText(match.getMatchName());
        holder.matchDate.setText("Date: " + match.getDate().toDate().toString());
        holder.place.setText("Place: " + match.getField().getAction());
        holder.adminName.setText("Admin: " + match.getAdminName());
        holder.notes.setText("Notes: " + (match.getNotes() != null ? match.getNotes() : "None"));


        // Set background color based on match status
        if (match.isFull()) {
            holder.cardBackground.setBackgroundColor(Color.RED);
        } else {
            holder.cardBackground.setBackgroundColor(Color.GREEN);
        }
        Date currentDate = new Date(); // Current date and time
        if (match.getDate().toDate().before(currentDate)) {
            holder.cardBackground.setBackgroundColor(Color.YELLOW);
        }


        // Handle item click
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(match));
    }

    @Override
    public int getItemCount()
    {
        if(matchList != null){
            return matchList.size();
        }
        return 0;
    }

    public static class MatchViewHolder extends RecyclerView.ViewHolder {
        TextView matchName, matchDate, place, adminName, notes;
        RelativeLayout cardBackground;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            matchName = itemView.findViewById(R.id.matchName);
            matchDate = itemView.findViewById(R.id.matchDate);
            place = itemView.findViewById(R.id.place);
            adminName = itemView.findViewById(R.id.adminName);
            notes = itemView.findViewById(R.id.notes);
            cardBackground = itemView.findViewById(R.id.cardBackground);
        }
    }
    public void updateData(List<Match> newMatchList) {
        this.matchList = newMatchList;
        notifyDataSetChanged();
    }
}


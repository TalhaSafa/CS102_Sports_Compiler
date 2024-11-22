package com.example.sportscompiler.AdditionalClasses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportscompiler.R;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder>
{
    private List<Match> matchList;

    public MatchAdapter(List<Match> matchList)
    {
        this.matchList = matchList;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, parent, false);

        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position)
    {
        Match match = matchList.get(position);
        holder.matchName.setText(match.getMa);
        holder.matchScore.setText(match.getMa);
        holder.matchDate.setText(match.getMat);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class MatchViewHolder extends RecyclerView.ViewHolder
    {
        TextView matchName, matchScore, matchPosition, matchDate;

        public MatchViewHolder(@NonNull View itemView)
        {
            super(itemView);
            matchName = itemView.findViewById(R.id.matchName);
            matchScore = itemView.findViewById(R.id.matchScore);
            matchPosition = itemView.findViewById(R.id.matchPosition);
            matchDate = itemView.findViewById(R.id.matchDate);
        }
    }

    public String formatTimeStamp(Timestamp timestamp)
    {
        Date date = timestamp.
    }
}

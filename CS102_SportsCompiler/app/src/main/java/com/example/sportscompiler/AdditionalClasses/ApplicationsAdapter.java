package com.example.sportscompiler.AdditionalClasses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportscompiler.R;

import java.util.List;

public class ApplicationsAdapter extends RecyclerView.Adapter<ApplicationsAdapter.ApplicationViewHolder> {

    private List<Application> applications;
    private ApplicationActionListener actionListener;

    // Constructor
    public ApplicationsAdapter(List<Application> applications, ApplicationActionListener actionListener) {
        this.applications = applications;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public ApplicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_application, parent, false);
        return new ApplicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicationViewHolder holder, int position) {
        Application app = applications.get(position);

        holder.nameSurname.setText("Name: " +app.getName());
        holder.position.setText("Position: " + app.getPosition());
        holder.note.setText("Note: " + app.getNote());

        holder.acceptButton.setOnClickListener(v -> actionListener.onAccept(app));
        holder.declineButton.setOnClickListener(v -> actionListener.onDecline(app));
    }

    @Override
    public int getItemCount() {
        return applications.size();
    }

    // ViewHolder Class
    public static class ApplicationViewHolder extends RecyclerView.ViewHolder {
        TextView nameSurname, position, note;
        Button acceptButton, declineButton;

        public ApplicationViewHolder(@NonNull View itemView) {
            super(itemView);
            nameSurname = itemView.findViewById(R.id.applicantNameSurname);
            position = itemView.findViewById(R.id.applicantPosition);
            note = itemView.findViewById(R.id.applicationNote);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            declineButton = itemView.findViewById(R.id.declineButton);
        }
    }
}

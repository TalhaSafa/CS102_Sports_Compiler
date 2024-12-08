

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.sportscompiler.AdditionalClasses.User;
import com.example.sportscompiler.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RatingUser extends Fragment {

    private User[] players;  // Assuming you have an array of users (players)

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize players array or retrieve from your data source
        players = new User[] {
                new User("1", "John Doe", "01/01/1995", "CS", "profile1", 4.2),
                new User("2", "Jane Smith", "02/02/1996", "Engineering", "profile2", 3.8),
                new User("3", "Bob Johnson", "03/03/1997", "Biology", "profile3", 4.5),
                new User("4", "Alice Brown", "04/04/1998", "Math", "profile4", 3.9),
                new User("5", "Tom White", "05/05/1999", "Physics", "profile5", 4.0)
        };

        // Initialize FABs
        FloatingActionButton fabPlayer1 = view.findViewById(R.id.fab_player1);
        FloatingActionButton fabPlayer2 = view.findViewById(R.id.fab_player2);
        FloatingActionButton fabPlayer3 = view.findViewById(R.id.fab_player3);
        FloatingActionButton fabPlayer4 = view.findViewById(R.id.fab_player4);
        FloatingActionButton fabPlayer5 = view.findViewById(R.id.fab_player5);

        // Set click listeners for FABs
        fabPlayer1.setOnClickListener(v -> showPlayerDetails(players[0]));
        fabPlayer2.setOnClickListener(v -> showPlayerDetails(players[1]));
        fabPlayer3.setOnClickListener(v -> showPlayerDetails(players[2]));
        fabPlayer4.setOnClickListener(v -> showPlayerDetails(players[3]));
        fabPlayer5.setOnClickListener(v -> showPlayerDetails(players[4]));
    }

    private void showPlayerDetails(User player) {
        // Create an AlertDialog to show the user's details
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_user_details, null);

        ImageView profileImage = dialogView.findViewById(R.id.profileImage);
        TextView nameText = dialogView.findViewById(R.id.nameText);
        TextView ratingText = dialogView.findViewById(R.id.ratingText);

        // Set data for the player
        nameText.setText(player.getName());
        ratingText.setText("Rating: " + player.getAverageRating());
        // You can set the profile image from the drawable resource, depending on how you store the profile picture
        profileImage.setImageResource(getResources().getIdentifier(player.getProfilePicture(), "drawable", getActivity().getPackageName()));

        builder.setView(dialogView)
                .setPositiveButton("Close", null)
                .create()
                .show();
    }
}

package win.spirithunt.android.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import win.spirithunt.android.R;
import win.spirithunt.android.model.Player;

/**
 * @author Sven Boekelder
 * @author Remco Schipper
 */

public class LeaderboardAdapter extends ArrayAdapter<Player> {
    private Context context;

    public LeaderboardAdapter(Context context, ArrayList<Player> values) {
        super(context, R.layout.leaderboard_textview, values);

        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        Player player = this.getItem(position);

        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.leaderboard_textview, parent, false);

        if(player != null) {
            TextView rankText = (TextView)rowView.findViewById(R.id.leaderboard_rank);
            rankText.setText(String.valueOf(position + 1));

            TextView nameText = (TextView)rowView.findViewById(R.id.leaderboard_player);
            nameText.setText(player.getName());

            TextView scoreText = (TextView)rowView.findViewById(R.id.leaderboard_score);
            scoreText.setText(String.valueOf(player.getScore()));

            AppCompatImageView teamImg = (AppCompatImageView)rowView.findViewById(R.id.leaderboard_team);
            if (player.getTeam() == 0) {
                teamImg.setColorFilter(Color.argb(255, 255, 0, 0));
            } else {
                teamImg.setColorFilter(Color.argb(255, 0, 0, 255));
            }

//            TextView teamText = (TextView)rowView.findViewById(R.id.leaderboard_team);
//            teamText.setText((player.getTeam() == 0) ? "R" : "B");
        }

        return rowView;
    }
}

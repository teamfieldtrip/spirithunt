package win.spirithunt.android.gui;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import win.spirithunt.android.R;

import static android.content.ContentValues.TAG;

/**
 * Created by sven on 23-5-17.
 */

public class ListViewAdapter extends BaseAdapter {

    public static final String FIRST_COLUMN = "First";
    public static final String SECOND_COLUMN = "Second";
    public static final String THIRD_COLUMN = "Third";
    public static final String FOURTH_COLUMN = "Fourth";
    public static final String FIFTH_COLUMN = "Fifth";
    private final int layout;

    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    TextView txtPlace, txtPlayer, txtTags, txtDeaths, txtTeam;

    public ListViewAdapter(Activity activity, int layout, ArrayList<HashMap<String, String>> list) {
        super();
        this.activity = activity;
        this.layout = layout;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = activity.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(this.layout, null);

            txtPlace = (TextView) convertView.findViewById(R.id.leaderboard_rank);
            txtPlayer = (TextView) convertView.findViewById(R.id.leaderboard_player);
            txtTags = (TextView) convertView.findViewById(R.id.leaderboard_tags);
            txtDeaths = (TextView) convertView.findViewById(R.id.leaderboard_deaths);
            txtTeam = (TextView) convertView.findViewById(R.id.leaderboard_team);
        }

        HashMap<String, String> map = list.get(position);
        txtPlace.setText(map.get(FIRST_COLUMN));
        txtPlayer.setText(map.get(SECOND_COLUMN));
        txtTags.setText(map.get(THIRD_COLUMN));
        txtDeaths.setText(map.get(FOURTH_COLUMN));
        txtTeam.setText(map.get(FIFTH_COLUMN));

        return convertView;
    }


}

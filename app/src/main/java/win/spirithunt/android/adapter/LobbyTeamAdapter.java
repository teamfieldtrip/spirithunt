package win.spirithunt.android.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import win.spirithunt.android.R;
import win.spirithunt.android.gui.CustomTextView;
import win.spirithunt.android.model.Player;

/**
 * @author Remco Schipper
 */

public class LobbyTeamAdapter extends ArrayAdapter<Player> {
    private Context context;

    public LobbyTeamAdapter(Context context, ArrayList<Player> values) {
        super(context, R.layout.lobby_view_team_row, values);

        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        Player player = this.getItem(position);

        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.lobby_view_team_row, parent, false);

        if(player != null) {
            CustomTextView numberText = (CustomTextView)rowView.findViewById(R.id.team_row_number);
            numberText.setText(String.valueOf(position + 1));

            CustomTextView nameText = (CustomTextView)rowView.findViewById(R.id.team_row_name);
            nameText.setText(player.getName());
        }

        return rowView;
    }
}

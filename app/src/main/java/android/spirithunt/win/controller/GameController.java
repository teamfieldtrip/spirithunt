package android.spirithunt.win.controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.spirithunt.win.R;
import android.spirithunt.win.gui.RadarDisplay;
import android.spirithunt.win.model.Location;
import android.spirithunt.win.model.Player;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import static java.util.UUID.randomUUID;


/**
 * Renders the main game interface, which consists of a radar, list of power ups, consume buttons
 * and a bottom navigation bar
 *
 * @author Roelof Roos <github@roelof.io>
 */
public class GameController extends AppCompatActivity implements View.OnClickListener {

    private RadarDisplay radar;
    private Player ownPlayer;
    private ArrayList<Player> players = new ArrayList<>();

    protected Player buildPlayer(double lat, double lng, int team) {
        Player out = new Player(randomUUID().toString());
        out.latitude = lat;
        out.longitude = lng;
        out.team = team;
        return out;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_view);

        radar = (RadarDisplay) findViewById(R.id.game_status_radar);

        // Determine who we are
        ownPlayer = buildPlayer(6.093206, 52.512121, 2);

        // Flood the player list
        players.add(buildPlayer(6.094408, 52.512813, 1));
        players.add(buildPlayer(6.093850, 52.512813, 2));
        players.add(buildPlayer(6.091683, 52.512199, 1));
        players.add(buildPlayer(6.093056, 52.511455, 1));

        radar.setActivePlayer(ownPlayer);
        radar.setPlayerList(players);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        };

        new AlertDialog.Builder(view.getContext(), R.style.AppDialog)
            .setTitle(getString(R.string.game_powerup_consume_title))
            .setMessage(getString(R.string.game_powerup_consume_desc))
            .setPositiveButton(R.string.game_powerup_consume_yes, listener)
            .setNegativeButton(R.string.game_powerup_consume_no, listener)
            .show();
    }
}

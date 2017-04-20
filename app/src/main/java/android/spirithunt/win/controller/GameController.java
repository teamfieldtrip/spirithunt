package android.spirithunt.win.controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.spirithunt.win.R;
import android.spirithunt.win.gui.RadarDisplay;
import android.spirithunt.win.model.Player;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;

import static java.util.UUID.randomUUID;


/**
 * Renders the main game interface, which consists of a radar, list of power ups, consume buttons
 * and a bottom navigation bar
 *
 * @author Roelof Roos <github@roelof.io>
 */
public class GameController extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "GameController";
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
        ownPlayer = buildPlayer(52.512740, 6.093505, 2);

        // Flood the player list
        players.add(buildPlayer(
            ownPlayer.latitude + 0.001000,
            ownPlayer.longitude,
            1
        ));
        players.add(buildPlayer(
            ownPlayer.latitude - 0.002000,
            ownPlayer.longitude,
            2
        ));
        players.add(buildPlayer(
            ownPlayer.latitude,
            ownPlayer.longitude + 0.003000,
            3
        ));
        players.add(buildPlayer(
            ownPlayer.latitude,
            ownPlayer.longitude - 0.004000,
            4
        ));
//        players.add(buildPlayer(52.512813, 6.093850, 2));
//        players.add(buildPlayer(52.512199, 6.091683, 1));
//        players.add(buildPlayer(52.511455, 6.093056, 1));

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

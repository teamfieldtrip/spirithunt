package win.spirithunt.android.controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import win.spirithunt.android.R;
import win.spirithunt.android.gui.RadarDisplay;
import win.spirithunt.android.model.Player;

import static java.lang.Math.sqrt;
import static java.util.UUID.randomUUID;


/**
 * Renders the main game interface, which consists of a radar, list of power ups, consume buttons
 * and a bottom navigation bar
 *
 * @author Roelof Roos [github@roelof.io]
 * @author sven
 */
public class GameController extends AppCompatActivity implements View.OnClickListener {

    // Determine who we are
    Player ownPlayer = buildPlayer(52.512740, 6.093505, 2);
    private ArrayList<Player> players = new ArrayList<>();
    private double range = 2;

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

        // Flood the player list
        players.add(buildPlayer(
            ownPlayer.latitude + 0.001000,
            ownPlayer.longitude,
            1
        ));
        players.add(buildPlayer(
            ownPlayer.latitude - 0.000500,
            ownPlayer.longitude - 0.00100,
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

        RadarDisplay radar = (RadarDisplay) findViewById(R.id.game_status_radar);
        radar.setActivePlayer(ownPlayer);
        radar.setPlayerList(players);

        onUpdateLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Handle the OnClick events of Views in the layout
     * @param view the View that triggered the event
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.game_consume:
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
                break;
            case R.id.game_tag:
                Log.d("TAGGED", "TAGGED");
                // TODO emit person tagged to server
                break;
            default:
                break;
        }
    }

    public void onUpdateLocation() {
        for (Player p :
            players) {
            if (checkTagable(p)) {
                // TODO show R.id.game_tag
            }
        }
    }

    /**
     * Check if a Player object is close enough to tag
     *
     * @param p Player object to compare distance to
     * @return boolean if Player can be tagged
     */
    public boolean checkTagable(Player p) {
        double deltaX = ownPlayer.longitude - p.longitude;
        double deltaY = ownPlayer.latitude - p.latitude;
        double distance = sqrt((deltaX * deltaX) + (deltaY * deltaY)) * 1000;

        return (distance > range * -1 && distance < range);
    }
}

package win.spirithunt.android.controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.UUID;

import win.spirithunt.android.R;
import win.spirithunt.android.gui.RadarDisplay;
import win.spirithunt.android.model.Player;
import win.spirithunt.android.model.PowerUp;

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

    public static final int TEAM_RED = 0;

    public static final int TEAM_BLUE = 1;

    private static final String TAG = "GameController";

    private static final double MAX_RANGE = 2d;

    // Determine who we are
    private Player ownPlayer = buildPlayer(52.512740, 6.093505, 2);

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
//            case R.id.game_consume:
//                usePowerup(view);
//                break;
            case R.id.game_tag:
                Log.d(TAG, "onClick: Person has been tagged");
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

        return (distance > MAX_RANGE * -1 && distance < MAX_RANGE);
    }

    private void usePowerup(View view) {
        PowerUp powerUp = new PowerUp(UUID.randomUUID().toString(), "Baked cookies", false);

        PowerUpUseHandler listener = new PowerUpUseHandler(powerUp);

        String message = getString(R.string.game_powerup_consume_desc, powerUp.getName());

        new AlertDialog.Builder(view.getContext(), R.style.AppDialog)
            .setTitle(getString(R.string.game_powerup_consume_title))
            .setMessage(message)
            .setPositiveButton(R.string.game_powerup_consume_yes, listener)
            .setNegativeButton(R.string.game_powerup_consume_no, listener)
            .show();
    }

    private void tagPlayer() {

    }
}

class PowerUpUseHandler implements
    DialogInterface.OnClickListener {

    private static final String TAG = "PowerUpUseHandler";

    private final PowerUp powerUp;

    public PowerUpUseHandler(PowerUp powerUp) {
        this.powerUp = powerUp;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            Log.d(TAG, "onClick: Consuming powerup " + powerUp.getName());
        } else if(which == DialogInterface.BUTTON_NEGATIVE) {
            Log.d(TAG, "onClick: Not consuming " + powerUp.getName());
        }
    }
}

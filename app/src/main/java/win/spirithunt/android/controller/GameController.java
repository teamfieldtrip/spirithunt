package win.spirithunt.android.controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.UUID;

import io.socket.client.Ack;
import io.socket.client.Socket;
import win.spirithunt.android.R;
import win.spirithunt.android.gui.RadarDisplay;
import win.spirithunt.android.model.Player;
import win.spirithunt.android.model.PowerUp;
import win.spirithunt.android.protocol.GameTag;
import win.spirithunt.android.provider.SocketProvider;

import static java.lang.Math.sqrt;

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
    private Player ownPlayer = buildPlayer("b95c67ec-38ed-463d-866d-763f6369a439", 52.512740, 6.093505, 0);

    private Player target = buildPlayer("db1cd8e0-abc4-4072-b46b-f63df0b80654", 52.512240, 6.093405, 0);

    private ArrayList<Player> players = new ArrayList<>();

    protected Player buildPlayer(String Uuid, double lat, double lng, int team) {
        Player out = new Player(Uuid);
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
        players.add(target);

        players.add(buildPlayer(
            "a5393197-a932-4f8d-911d-5bb21fd840e6",
            ownPlayer.latitude + 0.001000,
            ownPlayer.longitude,
            1
        ));
        players.add(buildPlayer(
            "52768d1b-20ee-4330-aa4f-96d8f0e29ea8",
            ownPlayer.latitude,
            ownPlayer.longitude + 0.003000,
            0
        ));
        players.add(buildPlayer(
            "857be781-3629-4976-8e22-d177a656ba3b",
            ownPlayer.latitude,
            ownPlayer.longitude - 0.004000,
            1
        ));

        RadarDisplay radar = (RadarDisplay) findViewById(R.id.game_status_radar);
        radar.setActivePlayer(ownPlayer);
        radar.setPlayerList(players);

        // TODO Register subscriber

        onUpdateLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Handle the OnClick events of Views in the layout
     *
     * @param view the View that triggered the event
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
//            case R.id.game_consume:
//                usePowerup(view);
//                break;
            case R.id.game_tag:
                // TODO hold button for 3 seconds to tag person (probably use onLongClickListener)
                Log.d(TAG, "onClick: Person has been tagged");
                Button btnTag = (Button) findViewById(R.id.game_tag);
                btnTag.setVisibility(View.INVISIBLE);

                Socket socket = SocketProvider.getInstance().getConnection();
                final GameController self = this;

                socket.emit("gameplay:tag", new GameTag(ownPlayer.getId(), target.getId()));
                // TODO get confirmation
                break;
            default:
                break;
        }
    }

    /**
     * Called after a location update
     */
    public void onUpdateLocation() {
        Button btnTag = (Button) findViewById(R.id.game_tag);

        ownPlayer.target = "db1cd8e0-abc4-4072-b46b-f63df0b80654";

        // Debug if-statement because of incomplete Player model
        if (!ownPlayer.target.equals("")) {
            for (Player p : players) {
                // If p is the ownPlayer's target
                if (ownPlayer.target.equals(p.getId())) {
                    Log.d(TAG, "Aquired target: " + p.getId());
                    if (checkTagable(p)) {
                        Log.d(TAG, "onUpdateLocation: User can be tagged");
                        btnTag.setVisibility(View.VISIBLE);
                    } else {
                        Log.d(TAG, "onUpdateLocation: User cannot be tagged");
                        btnTag.setVisibility(View.INVISIBLE);
                    }
                }
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

    /**
     * Uses a powerup and informs the server about it.
     *
     * @param view
     */
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

    /**
     * Tags the target player, sends this information to the server and blocks the UI in the
     * meantime.
     */
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

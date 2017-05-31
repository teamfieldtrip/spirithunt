package win.spirithunt.android.controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InvalidObjectException;
import java.util.ArrayList;

import io.socket.client.Ack;
import io.socket.client.Socket;
import win.spirithunt.android.R;
import win.spirithunt.android.gui.RadarDisplay;
import win.spirithunt.android.lib.GpsReader;
import win.spirithunt.android.model.Player;
import win.spirithunt.android.model.PowerUp;
import win.spirithunt.android.protocol.GameTag;
import win.spirithunt.android.provider.PlayerProvider;
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

    /**
     * Sent from the server when the client action (tag or consume) is approved.
     */
    private static final String CLIENT_ACT_OK = "act-ok";

    private String gameId;
    private Player ownPlayer = PlayerProvider.getInstance().getPlayer();
    private Player target;
    private ArrayList<Player> players = new ArrayList<>();

    private GpsReader gpsReader = new GpsReader(this);;

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
        setTheme(R.style.GameView);
        setContentView(R.layout.game_view);

        // Set app bar
        Toolbar appToolbar = (Toolbar) findViewById(R.id.game_toolbar);

        //Title and subtitle
        // TODO This is not yet working properly, maybe actually update it to match the correct information
        appToolbar.setTitle("In-game");
        appToolbar.setSubtitle("With " + players.size() + " players");

        // TODO find a way to actually show the menu
        appToolbar.inflateMenu(R.menu.ingame);

        setSupportActionBar(appToolbar);
        appToolbar.setNavigationIcon(R.drawable.ic_hooded_white_big);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayUseLogoEnabled(true);
        }

        // Parsing JSON to actual Players
        try {
            JSONObject gameData = new JSONObject(this.getIntent().getStringExtra("gameData"));

            gameId = gameData.getString("id");
            target = new Player(gameData.getString("target"));

            JSONArray jsonPlayers = gameData.getJSONArray("players");

            for (int i = 0; i < jsonPlayers.length(); i++) {
                // Create new Player Model per player
                Log.d("Player", jsonPlayers.getString(i));

                players.add(Player.FromJson(new JSONObject(jsonPlayers.getString(i))));
            }

            players.add(buildPlayer("aoeu", 52.523390, 6.118051 , 1));
            Log.d("Location", "Lat: " + ownPlayer.latitude + "Lon:" + ownPlayer.longitude);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InvalidObjectException e) {
            e.printStackTrace();
        }

        gpsReader.start();
        RadarDisplay radar = (RadarDisplay) findViewById(R.id.game_status_radar);
        radar.setActivePlayer(ownPlayer);
        radar.setPlayerList(players);

        // TODO Register subscriber
        onUpdateLocation();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        gpsReader.stop();
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
            case R.id.game_tag:
                tagPlayer();
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
            default:
                break;
        }
    }

    /**
     * Called after a location update
     */
    public void onUpdateLocation() {
        Button btnTag = (Button) findViewById(R.id.game_tag);

        if (ownPlayer.target != null) {
            for (Player p : players) {
                // If p is the ownPlayer's target
                if (ownPlayer.target.equals(p.getId())) {
                    Log.d(TAG, "Aquired target: " + p.getId());
                    if (checkTagable(p)) {
                        Log.d(TAG, "onUpdateLocation: User can be tagged");
                        // TODO animate the object in
                        btnTag.setVisibility(View.VISIBLE);
                        btnTag.setEnabled(true);
                    } else {
                        Log.d(TAG, "onUpdateLocation: User cannot be tagged");
                        // TODO animate the object out
                        btnTag.setVisibility(View.INVISIBLE);
                        btnTag.setEnabled(false);
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
     * Tags the target player, sends this information to the server and blocks the UI in the
     * meantime.
     */
    private void tagPlayer() {
        Log.d(TAG, "onClick: Person has been tagged");
        Button btnTag = (Button) findViewById(R.id.game_tag);
        btnTag.setVisibility(View.INVISIBLE);
        btnTag.setEnabled(false);

        Socket socket = SocketProvider.getInstance().getConnection();
        final GameController self = this;

        // TODO Indicate progress using Dialog

        socket.emit("gameplay:tag", new GameTag(ownPlayer.getId(), target.getId()), new Ack() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "\"gameplay:tag\" responsed with: args = [" + args + "]");
                if (args != null && args.length > 1 && args[0] == null) {
                    self.tagComplete((String) args[1]);
                }
            }
        });
        // TODO get confirmation
        // TODO hold button for 3 seconds to tag person
    }

    public void tagComplete(String message) {
        if (message.equals(CLIENT_ACT_OK)) {
            Log.d(TAG, "tagComplete: Tag acknowledged");
            // TODO inform player
        } else {
            Log.d(TAG, "tagComplete: Tag denied");
            // TODO inform player of failure
        }
    }

    class PowerUpUseHandler implements DialogInterface.OnClickListener {

        private static final String TAG = "PowerUpUseHandler";

        private PowerUp powerUp;

        public void tagComplete(String message) {
            if (message.equals(CLIENT_ACT_OK)) {
                Log.d(TAG, "tagComplete: Tag acknowledged");
                // TODO inform player
            } else {
                Log.d(TAG, "tagComplete: Tag denied");
                // TODO inform player of failure
            }
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            //TODO stuff
        }
    }
}

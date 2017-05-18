package win.spirithunt.android.controller;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import io.socket.client.Socket;
import win.spirithunt.android.R;
import win.spirithunt.android.gui.RadarDisplay;
import win.spirithunt.android.model.Player;
import win.spirithunt.android.protocol.GameTag;
import win.spirithunt.android.provider.SocketProvider;
import win.spirithunt.android.model.PowerUp;
import win.spirithunt.android.protocol.GameTag;
import win.spirithunt.android.provider.SocketProvider;
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

    /**
     * Sent from the server when the client action (tag or consume) is approved.
     */
    private static final String CLIENT_ACT_OK = "act-ok";

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
        setTheme(R.style.GameView);
        setContentView(R.layout.game_view);

        // Set app bar
        Toolbar appToolbar = (Toolbar) findViewById(R.id.game_toolbar);

        //Title and subtitle
        // TODO This is not yet working properly, maybe actually update it to match the correct information
        appToolbar.setTitle("In-game");
        appToolbar.setSubtitle("With 8 players");

        // TODO find a way to actually show the menu
        appToolbar.inflateMenu(R.menu.ingame);

        appToolbar.setTitle("In-game");
        appToolbar.setSubtitle("With 8 players");

        // TODO find a way to actually show the menu
        appToolbar.inflateMenu(R.menu.ingame);
        setSupportActionBar(appToolbar);
        appToolbar.setNavigationIcon(R.drawable.ic_hooded_white_big);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayUseLogoEnabled(true);
        }

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
            ownPlayer.latitude - 0.000500,
            ownPlayer.longitude - 0.00100,
            0
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
            case R.id.game_tag:
                tagPlayer();
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
}

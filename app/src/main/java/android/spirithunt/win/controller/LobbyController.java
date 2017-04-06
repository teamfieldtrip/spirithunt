package android.spirithunt.win.controller;

import android.os.Bundle;
import android.spirithunt.win.R;
import android.spirithunt.win.model.Player;
import android.spirithunt.win.provider.SocketProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import io.socket.client.Socket;

/**
 * Created by sven on 30-3-17.
 */

public class LobbyController extends AppCompatActivity {

    private int lobbyId;
    private SocketProvider socketProvider = SocketProvider.getInstance();

    private ArrayList<Player> players = new ArrayList<Player>();    // General list of players
    private ArrayList<Player> teamRed = new ArrayList<Player>();    // Team 0
    private ArrayList<Player> teamBlue = new ArrayList<Player>();   // Team 1


    /**
     * Generate a lobby
     *
     * @param p Player model
     */
//    public LobbyController(Player p) {
//
//        // First add player to the general list of players, then assign to a team
//        players.add(p);
//        assignPlayer(p);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby_view);

        players.add(new Player("aoeu"));
        players.add(new Player("asdf"));

        updatePlayerList();
    }

    /**
     * Assign player to team
     *
     * @param p Player model
     */
    public void assignPlayer(Player p) {
        switch (p.team) {
            case 0:
                teamRed.add(p);
                break;
            case 1:
                teamBlue.add(p);
                break;
            default:
                // Handle noTeamException
                break;
        }
    }

    public void getLobbyFromServer() {

        Socket socket = socketProvider.getConnection();

//        TODO Socket connection
//        lobbyId = idfromsocket;
//        players.addAll(playersfromsocket);

    }

    /**
     * Occupy the ListView with the list of players
     */
    public void updatePlayerList() {

        ListView listView = (ListView) findViewById(R.id.listview_lobby);

        ArrayList<String> playerNames = new ArrayList<String>();

        for (Player p : players) {
            playerNames.add(p.getId());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            playerNames);

        listView.setAdapter(arrayAdapter);
    }
}

package android.spirithunt.win.controller;

import android.app.FragmentManager;
import android.os.Bundle;
import android.spirithunt.win.R;
import android.spirithunt.win.model.Player;
import android.spirithunt.win.provider.SocketProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.ArrayList;

import io.socket.client.Socket;

/**
 * Created by sven on 30-3-17.
 */

public class LobbyController extends AppCompatActivity {

    private int lobbyId;
    private SocketProvider socketProvider = SocketProvider.getInstance();

    private GoogleMap map;

    private ArrayList<Player> players = new ArrayList<>();    // General list of players
    private ArrayList<Player> teamRed = new ArrayList<>();    // Team 0
    private ArrayList<Player> teamBlue = new ArrayList<>();   // Team 1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby_view);

        FragmentManager manager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) manager.findFragmentById(R.id.lobby_map);



        final LobbyController self = this;
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                try {
                    googleMap.setMyLocationEnabled(true);
                } catch (SecurityException e) {
                    System.out.println(e.getMessage());
                }

                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                googleMap.getUiSettings().setAllGesturesEnabled(false);
                self.map = googleMap;
            }
        });

        for(int i = 0; i<100; i++){

            Player p1 = new Player("Sven");
            p1.setTeam(i%2);
            assignPlayer(p1);
        }

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
                players.add(p);
                break;
            case 1:
                teamBlue.add(p);
                players.add(p);
                break;
            default:
                Log.e("No team", "No team in Player model");
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

        ListView listViewTeamRed = (ListView) findViewById(R.id.listview_teamred_lobby);
        ListView listViewTeamBlue = (ListView) findViewById(R.id.listview_teamblue_lobby);

        ArrayAdapter<Player> arrayAdapterRed = new ArrayAdapter<>(
            this,
            android.R.layout.simple_list_item_1,
            teamRed);

        ArrayAdapter<Player> arrayAdapterBlue = new ArrayAdapter<>(
            this,
            android.R.layout.simple_list_item_1,
            teamBlue);

        listViewTeamRed.setAdapter(arrayAdapterRed);
        listViewTeamBlue.setAdapter(arrayAdapterBlue);
    }

    public void getLobby() {

        Socket socket = SocketProvider.getInstance().getConnection();

        // TODO get lobby an set variable
    }
}

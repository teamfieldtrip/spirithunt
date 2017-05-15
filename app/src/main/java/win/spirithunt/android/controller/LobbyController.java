package win.spirithunt.android.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import win.spirithunt.android.R;
import win.spirithunt.android.gui.LobbyInfoFragment;
import win.spirithunt.android.gui.LobbyMapFragment;
import win.spirithunt.android.model.Player;
import win.spirithunt.android.protocol.LobbyList;
import win.spirithunt.android.provider.DialogProvider;
import win.spirithunt.android.provider.SocketProvider;

/**
 * Created by sven on 30-3-17.
 *
 * @author Remco Schipper
 */

public class LobbyController extends AppCompatActivity {
    private String lobbyId;

    private ArrayList<Player> players = new ArrayList<>();    // General list of players

    private ArrayList<Player> teamRed = new ArrayList<>();    // Team 0

    private ArrayList<Player> teamBlue = new ArrayList<>();   // Team 1


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby_view);

        boolean isLobbyHost;

        this.lobbyId = this.getIntent().getStringExtra("lobbyId");
        isLobbyHost = this.getIntent().getBooleanExtra("lobbyHost", false);

        if (isLobbyHost) {
            View view = findViewById(R.id.btn_start);
            view.setVisibility(View.VISIBLE);
        }

        ViewPager mPager;
        PagerAdapter mPagerAdapter;

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(this.lobbyId, getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }

    protected void onResume() {
        super.onResume();
        final LobbyController self = this;

        Socket socket = SocketProvider.getInstance().getConnection();
        socket.on("lobby:joined", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                self.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        self.assignPlayer(self.createPlayer((JSONObject) args[0]));
                        self.updatePlayerList();
                    }
                });
            }
        });

        // TODO player abandonment

        socket.on("lobby:started", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                self.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String gameId = (String) args[0];
                        self.startGame(gameId);
                    }
                });
            }
        });

        this.getLobbyFromServer();
    }

    protected void onPause() {
        super.onPause();

        Socket socket = SocketProvider.getInstance().getConnection();
        socket.off("lobby:joined");
        socket.off("lobby:started");
    }

    /**
     * Fired on game start, shows progress, fetches players and jumps to the player list.
     */
    protected void startGame(final String gameId) {
        // Show progress
        final DialogProvider provider = new DialogProvider(this);
        provider.showProgressDialog(R.string.game_start_dialog_title, R.string.game_start_dialog_message);

        // Create intent
        final Intent gameIntent = new Intent(this, GameController.class);
        gameIntent.putExtra("id", this.getLobbyId());

        final LobbyController self = this;

        Socket socket = SocketProvider.getInstance().getConnection();
        socket.emit("game:info", gameId, new Ack() {
            @Override
            public void call(Object... args) {
                if (provider.isProgressDialogOpen()) {
                    provider.hideProgressDialog();
                }

                /*
                    TODO process response, it's a single object containing id, game, players and target
                     but how we're sending this and what the game looks like... It's TBD
                 */
                // TODO Add data
                self.startActivity(gameIntent);
            }
        });
    }

    public void start(View view) {
        Socket socket = SocketProvider.getInstance().getConnection();
        socket.emit("lobby:start", null, new Ack() {
            @Override
            public void call(Object... args) {
                Log.d("Lobby", "Lobby started");
            }
        });
    }

    /**
     * Assign player to team
     *
     * @param player Player model
     */
    private void assignPlayer(Player player) {
        switch (player.team) {
            case 0:
                teamRed.add(player);
                players.add(player);
                break;
            case 1:
                teamBlue.add(player);
                players.add(player);
                break;
            default:
                Log.e("No team", "No team in Player model");
                break;
        }
    }

    /**
     * Occupy the ListView with the list of players
     */
    private void updatePlayerList() {
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

    /**
     * Returns a list of players
     * @return
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    public String getLobbyId() {
        return lobbyId;
    }

    private void getLobbyFromServer() {
        final LobbyController self = this;
        Socket socket = SocketProvider.getInstance().getConnection();

        socket.emit("lobby:list", new LobbyList(this.lobbyId), new Ack() {
            @Override
            public void call(Object... args) {
                if (args[0] != null || args.length < 2) {
                    Log.e("Lobby", "Error retrieving lobby");
                    //handle error
                } else {
                    self.fillLobbyFromServer((JSONArray) args[1]);
                }
            }
        });
    }

    private void fillLobbyFromServer(JSONArray players) {
        if (players != null) {
            final LobbyController self = this;
            int len = players.length();

            try {
                for (int i = 0; i < len; i++) {
                    this.assignPlayer(this.createPlayer(players.getJSONObject(i)));
                }
            } catch (JSONException e) {
                System.out.println(e.getMessage());
            }

            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    self.updatePlayerList();
                }
            });
        }
    }

    private Player createPlayer(JSONObject jsonObject) {
        try {
            Player player = new Player(jsonObject.getString("player_id"));
            player.setTeam(jsonObject.getInt("team"));
            player.setName(jsonObject.getString("name"));

            return player;
        } catch (JSONException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private String lobbyId;

        public ScreenSlidePagerAdapter(String lobbyId, FragmentManager fm) {
            super(fm);

            this.lobbyId = lobbyId;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new LobbyMapFragment();
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("lobbyId", this.lobbyId);

                LobbyInfoFragment lobbyInfoFragment = new LobbyInfoFragment();
                lobbyInfoFragment.setArguments(bundle);
                return lobbyInfoFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}

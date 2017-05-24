package win.spirithunt.android.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Ack;
import io.socket.client.Socket;
import win.spirithunt.android.R;
import win.spirithunt.android.adapter.LeaderboardAdapter;
import win.spirithunt.android.model.Player;
import win.spirithunt.android.provider.SocketProvider;

/**
 * Created by sven on 16-5-17.
 *
 * @author Remco Schipper
 */

public class LeaderboardController extends AppCompatActivity {
    private LeaderboardAdapter players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard_view);

        ListView listViewLeaderboard = (ListView) findViewById(R.id.listview_leaderboard);
        this.players = new LeaderboardAdapter(this, new ArrayList<Player>());
        listViewLeaderboard.setAdapter(players);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final LeaderboardController self = this;

        Socket socket = SocketProvider.getInstance().getConnection();
        socket.emit("leaderboard:results", new Ack() {
            @Override
            public void call(Object... args) {
                if (args[0] == null) {
                    self.parseList((JSONArray) args[1]);
                }
            }
        });
    }

    private Player createPlayer(JSONObject jsonObject) {
        try {
            Player player = new Player(jsonObject.getString("player_id"));
            player.setScore(jsonObject.getInt("score"));
            player.setName(jsonObject.getString("name"));

            return player;
        } catch (JSONException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private void addPlayer(final Player player) {
        final LeaderboardController self = this;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                self.players.add(player);
            }
        });
    }

    private void parseList(JSONArray players) {
        if (players != null) {
            try {
                int len = players.length();

                for (int i = 0; i < len; i++) {
                    this.addPlayer(this.createPlayer(players.getJSONObject(i)));
                }
            } catch (JSONException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}

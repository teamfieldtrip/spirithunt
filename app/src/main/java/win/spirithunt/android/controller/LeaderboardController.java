package win.spirithunt.android.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;

import io.socket.client.Ack;
import io.socket.client.Socket;
import win.spirithunt.android.R;
import win.spirithunt.android.model.Player;
import win.spirithunt.android.provider.SocketProvider;

/**
 * Created by sven on 16-5-17.
 */

public class LeaderboardController extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard_view);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final LeaderboardController self = this;

        Socket socket = SocketProvider.getInstance().getConnection();
        socket.emit("leaderboard:results", new Ack() {
            @Override
            public void call(Object... args) {
                if (args[0] != null || args.length < 2) {
                    Log.e("Leaderboard", "Error retrieving leaderboard");
                    //handle error
                } else {
                    Log.d("Leaderboard",args[1].toString());
//                    self.fillLeaderboardFromServer((JSONArray) args[1]);
                }
            }
        });
    }

    protected void fillLeaderboardFromServer(JSONArray players) {
        ListView listViewLeaderboard = (ListView) findViewById(R.id.listview_leaderboard);

//        ArrayAdapter<Player> arrayAdapter = new ArrayAdapter<Player>(
//            this,
//            android.R.layout.simple_list_item_1,
//            players);
    }
}

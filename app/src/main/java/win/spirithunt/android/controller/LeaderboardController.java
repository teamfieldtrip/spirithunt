package win.spirithunt.android.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

import io.socket.client.Ack;
import io.socket.client.Socket;
import win.spirithunt.android.R;
import win.spirithunt.android.gui.ListViewAdapter;
import win.spirithunt.android.provider.SocketProvider;

import static win.spirithunt.android.gui.ListViewAdapter.FIFTH_COLUMN;
import static win.spirithunt.android.gui.ListViewAdapter.FIRST_COLUMN;
import static win.spirithunt.android.gui.ListViewAdapter.FOURTH_COLUMN;
import static win.spirithunt.android.gui.ListViewAdapter.SECOND_COLUMN;
import static win.spirithunt.android.gui.ListViewAdapter.THIRD_COLUMN;

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

//        final LeaderboardController self = this;
//
//        Socket socket = SocketProvider.getInstance().getConnection();
//        socket.emit("leaderboard:results", new Ack() {
//            @Override
//            public void call(Object... args) {
//                if (args[0] != null || args.length < 2) {
//                    Log.e("Leaderboard", "Error retrieving leaderboard");
//                    //handle error
//                } else {
//                    Log.d("Leaderboard", args[1].toString());
////                    self.fillLeaderboard((JSONArray) args[1]);
//                }
//            }
//        });

        ArrayList<HashMap<String, String>> players = new ArrayList<>();
        HashMap<String, String> temp = new HashMap<>();
        temp.put(FIRST_COLUMN, "1");
        temp.put(SECOND_COLUMN, "Sven");
        temp.put(THIRD_COLUMN, "18");
        temp.put(FOURTH_COLUMN, "9");
        temp.put(FIFTH_COLUMN, "R");
        players.add(temp);

        HashMap<String, String> temp2 = new HashMap<>();
        temp2.put(FIRST_COLUMN, "2");
        temp2.put(SECOND_COLUMN, "Sven");
        temp2.put(THIRD_COLUMN, "15");
        temp2.put(FOURTH_COLUMN, "11");
        temp2.put(FIFTH_COLUMN, "B");
        players.add(temp2);

        fillLeaderboard(players);
    }

    protected void fillLeaderboard(ArrayList<HashMap<String, String>> players) {

        ListView listViewLeaderboard = (ListView) findViewById(R.id.listview_leaderboard);
        ListViewAdapter adapter = new ListViewAdapter(this, R.layout.leaderboard_textview, players);

        listViewLeaderboard.setAdapter(adapter);
    }
}

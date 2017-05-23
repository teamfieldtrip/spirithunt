package win.spirithunt.android.gui;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import win.spirithunt.android.R;
import win.spirithunt.android.adapter.LobbyTeamAdapter;
import win.spirithunt.android.model.Player;
import win.spirithunt.android.protocol.LobbyList;
import win.spirithunt.android.provider.SocketProvider;

/**
 * @author Remco Schipper
 */

public class LobbyTeamFragment extends ListFragment {
    private int team;

    private View view;

    private Socket socket;

    private LobbyTeamAdapter players;

    private PlayerJoinedListener playerJoinedListener;

    private PlayerLeftListener playerLeftListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.socket = SocketProvider.getInstance().getConnection();

        this.view = inflater.inflate(R.layout.lobby_view_team, container, false);
        return this.view;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.players.clear();
        this.getList();

        this.playerJoinedListener = new PlayerJoinedListener(this);
        this.playerLeftListener = new PlayerLeftListener(this);

        socket.on("lobby:joined", this.playerJoinedListener);
        socket.on("lobby:left", this.playerLeftListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        socket.off("lobby:joined", this.playerJoinedListener);
        socket.off("lobby:left", this.playerLeftListener);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = this.getArguments();
        this.team = bundle.getInt("team");

        this.players = new LobbyTeamAdapter(this.getActivity(), new ArrayList<Player>());
        this.setListAdapter(this.players);

        LinearLayoutCompat layout = (LinearLayoutCompat)this.view.findViewById(R.id.lobby_team_background);
        if (this.team == 0) {
            layout.setBackgroundResource(R.drawable.gui_el_lobby_team_red_border);
        } else {
            layout.setBackgroundResource(R.drawable.gui_el_lobby_team_blue_border);
        }
    }

    private void getList() {
        final LobbyTeamFragment self = this;
        Socket socket = SocketProvider.getInstance().getConnection();

        socket.emit("lobby:list", new LobbyList(""), new Ack() {
            @Override
            public void call(Object... args) {
                if (args[0] != null || args.length < 2) {
                    Log.e("Lobby", "Error retrieving lobby");
                    //handle error
                } else {
                    self.parseList((JSONArray) args[1]);
                }
            }
        });
    }

    private Player createJoinPlayer(JSONObject jsonObject) {
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

    private Player createLeavePlayer(JSONObject jsonObject) {
        try {
            return new Player(jsonObject.getString("player_id"));
        } catch (JSONException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private void parseList(JSONArray players) {
        if (players != null) {
            try {
                int len = players.length();

                for (int i = 0; i < len; i++) {
                    Player player = this.createJoinPlayer(players.getJSONObject(i));

                    if (player != null && player.getTeam() == this.team) {
                        this.addPlayer(player);
                    }
                }
            } catch (JSONException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void addPlayer(final Player player) {
        boolean saved = false;

        for(int i = 0; i < this.players.getCount(); i++) {
            Player savedPlayer = this.players.getItem(i);

            if (savedPlayer != null && player.getId().equals(savedPlayer.getId())) {
                saved = true;
                break;
            }
        }

        if (!saved) {
            final LobbyTeamFragment self = this;
            this.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    self.players.add(player);
                }
            });
        }
    }

    private void removePlayer(final Player player) {
        Player removePlayer = null;

        for(int i = 0; i < this.players.getCount(); i++) {
            Player savedPlayer = this.players.getItem(i);

            if (savedPlayer != null && savedPlayer.getId().equals(player.getId())) {
                removePlayer = savedPlayer;
                break;
            }
        }

        if (removePlayer != null) {
            final LobbyTeamFragment self = this;
            final Player finalRemovePlayer = removePlayer;

            this.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    self.players.remove(finalRemovePlayer);
                }
            });
        }
    }

    private class PlayerJoinedListener implements Emitter.Listener {
        private LobbyTeamFragment parent;

        PlayerJoinedListener(LobbyTeamFragment parent) {
            this.parent = parent;
        }

        @Override
        public void call(final Object... args) {
            final PlayerJoinedListener self = this;

            this.parent.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Player player = self.parent.createJoinPlayer((JSONObject) args[0]);

                    if (player != null && player.getTeam() == self.parent.team) {
                        self.parent.addPlayer(player);
                    }
                }
            });
        }
    }

    private class PlayerLeftListener implements Emitter.Listener {
        private LobbyTeamFragment parent;

        PlayerLeftListener(LobbyTeamFragment parent) {
            this.parent = parent;
        }

        @Override
        public void call(final Object... args) {
            final PlayerLeftListener self = this;

            this.parent.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Player player = self.parent.createLeavePlayer((JSONObject) args[0]);

                    if (player != null) {
                        self.parent.removePlayer(player);
                    }
                }
            });
        }
    }
}

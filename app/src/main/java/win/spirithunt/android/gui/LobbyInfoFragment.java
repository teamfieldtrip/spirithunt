package win.spirithunt.android.gui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import win.spirithunt.android.R;
import win.spirithunt.android.protocol.LobbyInfo;
import win.spirithunt.android.provider.SocketProvider;

/**
 * @author Remco Schipper
 */

public class LobbyInfoFragment extends Fragment {
    /**
     * Stores the current amount of players
     */
    private int amountOfPlayers = 0;

    /**
     * Stores the max amount of players in the lobby
     */
    private int maxAmountOfPlayers = 0;

    private String lobbyId;

    private PlayerJoinedListener playerJoinedListener;

    private PlayerLeftListener playerLeftListener;

    private Socket socket;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.lobby_view_info, container, false);
        return this.view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = this.getArguments();
        this.lobbyId = bundle.getString("lobbyId");
    }

    @Override
    public void onResume() {
        super.onResume();

        this.socket = SocketProvider.getInstance().getConnection();
        this.socket.emit("lobby:info", new LobbyInfo(this.lobbyId), new LobbyInfoAck(this));

        this.playerJoinedListener = new PlayerJoinedListener(this);
        this.playerLeftListener = new PlayerLeftListener(this);

        this.socket.on("lobby:joined", this.playerJoinedListener);
        this.socket.on("lobby:left", this.playerLeftListener);
    }

    @Override
    public void onPause() {
        super.onPause();

        this.socket.off("lobby:joined", this.playerJoinedListener);
        this.socket.off("lobby:left", this.playerLeftListener);
    }

    private void setTeamSize(int totalSize) {
        this.maxAmountOfPlayers = totalSize;

        int size = totalSize / 2;
        CustomTextView text = (CustomTextView)this.view.findViewById(R.id.team_size_text);
        text.setText(this.getActivity().getString(R.string.lobby_text_team_size, size, size));
    }

    private void setAmountOfPlayers(int players) {
        this.amountOfPlayers = players;

        CustomTextView text = (CustomTextView)this.view.findViewById(R.id.player_amount_text);
        text.setText(this.getActivity().getString(R.string.lobby_text_amount_of_players, players, this.maxAmountOfPlayers));
    }

    private void setDuration(int duration) {
        CustomTextView text = (CustomTextView)this.view.findViewById(R.id.time_limit_text);
        text.setText(this.getActivity().getString(R.string.lobby_text_time_limit, duration / 60));
    }

    private void incrementAmountOfPlayers() {
        this.setAmountOfPlayers(this.amountOfPlayers + 1);
    }

    private void decrementAmountOfPlayers() {
        this.setAmountOfPlayers(this.amountOfPlayers - 1);
    }

    private class LobbyInfoAck implements Ack {
        private LobbyInfoFragment parent;

        LobbyInfoAck(LobbyInfoFragment parent) {
            this.parent = parent;
        }

        @Override
        public void call(final Object... args) {
            if (args[0] == null) {
                final LobbyInfoFragment parent = this.parent;

                this.parent.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        parent.setTeamSize(Integer.parseInt(args[2].toString()));
                        parent.setDuration(Integer.parseInt(args[4].toString()));
                        parent.setAmountOfPlayers(Integer.parseInt(args[1].toString()));
                    }
                });
            }
        }
    }

    private class PlayerJoinedListener implements Emitter.Listener {
        private LobbyInfoFragment parent;

        PlayerJoinedListener(LobbyInfoFragment parent) {
            this.parent = parent;
        }

        @Override
        public void call(final Object... args) {
            final PlayerJoinedListener self = this;

            this.parent.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    self.parent.incrementAmountOfPlayers();
                }
            });
        }
    }

    private class PlayerLeftListener implements Emitter.Listener {
        private LobbyInfoFragment parent;

        PlayerLeftListener(LobbyInfoFragment parent) {
            this.parent = parent;
        }

        @Override
        public void call(final Object... args) {
            final PlayerLeftListener self = this;

            this.parent.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    self.parent.decrementAmountOfPlayers();
                }
            });
        }
    }
}

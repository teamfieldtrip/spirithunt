package win.spirithunt.android.controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import win.spirithunt.android.R;
import win.spirithunt.android.gui.LobbyInfoFragment;
import win.spirithunt.android.gui.LobbyMapFragment;
import win.spirithunt.android.gui.LobbyQrFragment;
import win.spirithunt.android.gui.LobbyTeamFragment;
import win.spirithunt.android.model.Player;
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

        boolean isLobbyHost = this.getIntent().getBooleanExtra("lobbyHost", false);

        if (isLobbyHost) {
            View view = findViewById(R.id.btn_start);
            view.setVisibility(View.VISIBLE);
        }

        ViewPager infoPager = (ViewPager) findViewById(R.id.pager_info);
        infoPager.setAdapter(new InfoPagerAdapter(this.getIntent().getStringExtra("lobbyId"), getSupportFragmentManager()));
        infoPager.setCurrentItem(1);

        ViewPager teamPager = (ViewPager) findViewById(R.id.pager_team);
        teamPager.setAdapter(new TeamPagerAdapter(getSupportFragmentManager()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        final LobbyController self = this;

        Socket socket = SocketProvider.getInstance().getConnection();

        socket.on("lobby:started", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                self.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(self, GameController.class);
                        startActivity(intent);
                        self.finish();
                    }
                });
            }
        });

        socket.on("lobby:destroy", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                self.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Socket socket = SocketProvider.getInstance().getConnection();
                        socket.emit("lobby:leave");

                        self.showHostLeftDialog();
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
    }

    @Override
    protected void onPause() {
        super.onPause();

        Socket socket = SocketProvider.getInstance().getConnection();
        socket.off("lobby:started");
        socket.off("lobby:destroy");
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

    @Override
    public void onBackPressed() {
        Socket socket = SocketProvider.getInstance().getConnection();
        socket.emit("lobby:leave");

        super.onBackPressed();
    }

    /**
     * Returns a list of players
     *
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

    private void showHostLeftDialog() {
        final LobbyController self = this;

        new AlertDialog.Builder(this, R.style.AppDialog)
            .setTitle(R.string.lobby_text_dialog_title)
            .setMessage(R.string.lobby_text_dialog_content)
            .setCancelable(false)
            .setPositiveButton(R.string.lobby_text_dialog_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    self.finish();
                }
            })
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
    }

    private class InfoPagerAdapter extends FragmentStatePagerAdapter {
        private String lobbyId;

        InfoPagerAdapter(String lobbyId, FragmentManager fm) {
            super(fm);

            this.lobbyId = lobbyId;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new LobbyMapFragment();
            } else if (position == 1) {
                Bundle bundle = new Bundle();
                bundle.putString("lobbyId", this.lobbyId);

                LobbyInfoFragment lobbyInfoFragment = new LobbyInfoFragment();
                lobbyInfoFragment.setArguments(bundle);

                return lobbyInfoFragment;
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("lobbyId", this.lobbyId);

                LobbyQrFragment lobbyQrFragment = new LobbyQrFragment();
                lobbyQrFragment.setArguments(bundle);
                return lobbyQrFragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    private class TeamPagerAdapter extends FragmentStatePagerAdapter {
        TeamPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putInt("team", position);

            LobbyTeamFragment lobbyTeamFragment = new LobbyTeamFragment();
            lobbyTeamFragment.setArguments(bundle);

            return lobbyTeamFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}

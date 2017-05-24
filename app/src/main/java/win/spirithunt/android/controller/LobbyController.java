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

import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import win.spirithunt.android.R;
import win.spirithunt.android.gui.LobbyInfoFragment;
import win.spirithunt.android.gui.LobbyQrFragment;
import win.spirithunt.android.gui.LobbyMapFragment;
import win.spirithunt.android.gui.LobbyTeamFragment;
import win.spirithunt.android.provider.SocketProvider;

/**
 * Created by sven on 30-3-17.
 *
 * @author Remco Schipper
 */

public class LobbyController extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby_view);

        boolean isLobbyHost = this.getIntent().getBooleanExtra("lobbyHost", false);

        if (isLobbyHost) {
            View view = findViewById(R.id.btn_start);
            view.setVisibility(View.VISIBLE);
        }

        ViewPager infoPager = (ViewPager)findViewById(R.id.pager_info);
        infoPager.setAdapter(new InfoPagerAdapter(this.getIntent().getStringExtra("lobbyId"), getSupportFragmentManager()));
        infoPager.setCurrentItem(1);

        ViewPager teamPager = (ViewPager)findViewById(R.id.pager_team);
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
    }

    @Override
    protected void onPause() {
        super.onPause();

        Socket socket = SocketProvider.getInstance().getConnection();
        socket.off("lobby:started");
        socket.off("lobby:destroy");
    }

    @Override
    public void onBackPressed() {
        Socket socket = SocketProvider.getInstance().getConnection();
        socket.emit("lobby:leave");

        super.onBackPressed();
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

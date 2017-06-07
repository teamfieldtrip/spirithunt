package win.spirithunt.android.controller;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import win.spirithunt.android.R;
import win.spirithunt.android.callback.PlayerCreateCallback;
import win.spirithunt.android.model.Player;
import win.spirithunt.android.protocol.LobbyJoin;
import win.spirithunt.android.provider.CustomFontSpan;
import win.spirithunt.android.provider.DialogProvider;
import win.spirithunt.android.provider.PermissionProvider;
import win.spirithunt.android.provider.PlayerProvider;
import win.spirithunt.android.provider.SocketProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import io.socket.client.Ack;
import io.socket.client.Socket;

/**
 * Created by Remco Schipper
 */

public class GameJoinInfoController extends PermissionRequestingActivity {
    private DialogProvider dialogProvider;

    private String lobbyId;

    public void close(View view) {
        this.finish();
    }

    public void doJoinGame(View view) {
        if (hasPermission(PermissionProvider.Permissions.LOCATION)) {
            joinLobby();
        } else {
            requestPermission(PermissionProvider.Permissions.LOCATION, false);
        }
    }

    public void joinLobby() {
        this.showProgressDialog();

        final GameJoinInfoController self = this;

        PlayerProvider.getInstance().getNewPlayer(new PlayerCreateCallback() {
            @Override
            public void call(String error, Player player) {
                Socket socket = SocketProvider.getInstance().getConnection();
                socket.emit("lobby:join", new LobbyJoin(self.lobbyId), new Ack() {
                    @Override
                    public void call(final Object... args) {
                    self.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        if (args[0] == null) {
                            self.onSuccess();
                        } else {
                            self.onError(args[0].toString());
                        }
                        }
                    });
                    }
                });
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_join_info_view);

        dialogProvider = new DialogProvider(this);

        Bundle extras = getIntent().getExtras();
        this.lobbyId = extras.getString("lobbyId");

        String hostname = extras.getString("hostname");
        this.setText(getString(R.string.join_game_info_text_host), 25, hostname, 20, R.id.hostText);
        this.setText(getString(R.string.join_game_info_text_mode), 25, getString(R.string.join_game_info_text_mode_gotcha), 20, R.id.modeText);

        int maxPlayers = extras.getInt("maxPlayers");
        int currentPlayers = extras.getInt("currentPlayers");
        this.setText(getString(R.string.join_game_info_text_players), 25, currentPlayers + "/" + maxPlayers, 20, R.id.playersText);
    }

    private void setText(String prefix, int size1, String content, int size2, int id) {
        String total = prefix + ": " + content;
        TextView txt = (TextView) findViewById(id);
        txt.setTextSize(total.length());
        SpannableStringBuilder SS = new SpannableStringBuilder(total);
        SS.setSpan(new CustomFontSpan("lobster.ttf", size1), 0, prefix.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        SS.setSpan(new CustomFontSpan("exo-regular.ttf", size2), prefix.length(), total.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        txt.setText(SS);
    }

    private void onSuccess() {
        this.hideProgressDialog();

        Intent intent = new Intent(this, LobbyController.class);
        intent.putExtra("lobbyId", this.lobbyId);
        startActivity(intent);
        finish();
    }

    private void onError(String error) {
        int textId;

        switch(error) {
            case "error_lobby_not_found":
                textId = R.string.join_game_text_error_lobby_not_found;
                break;
            case "error_lobby_full":
                textId = R.string.join_game_text_error_lobby_full;
                break;
            case "error_player_joined":
                textId = R.string.join_game_text_error_player_joined;
                break;
            default:
                textId = R.string.join_game_text_error_data;
                break;
        }

        this.hideProgressDialog();
        this.showErrorDialog(getString(textId));
    }

    private void showErrorDialog(String text) {
        dialogProvider.provideAlertBuilder()
            .setTitle(getString(R.string.join_game_text_error_title))
            .setMessage(text)
            .setNeutralButton(R.string.join_game_text_error_button, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // do nothing
                }
            })
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
    }

    private void hideProgressDialog() {
        dialogProvider.hideProgressDialog();
    }

    private void showProgressDialog() {
        dialogProvider.showProgressDialog(R.string.lobby_text_progress_title, R.string.lobby_text_progress_content);
    }


    @Override
    public void showPermissionRationale(PermissionProvider.Permissions permission) {
        final GameJoinInfoController self = this;

        dialogProvider.provideAlertBuilder()
            .setTitle(getString(R.string.join_game_location_explain_title))
            .setMessage(getString(R.string.join_game_location_explain_message))
            .setPositiveButton(R.string.join_game_location_explain_positive, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    self.requestPermission(PermissionProvider.Permissions.LOCATION, true);
                }
            })
            .setNegativeButton(R.string.join_game_location_explain_negative, null)
            .setIcon(android.R.drawable.ic_dialog_info)
            .show();
    }

    @Override
    public void onPermissionGranted(PermissionProvider.Permissions permission) {
        joinLobby();
    }
}

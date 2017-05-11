package win.spirithunt.android.controller;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.LinearLayout;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import io.socket.client.Ack;
import io.socket.client.Socket;
import win.spirithunt.android.R;
import win.spirithunt.android.protocol.LobbyInfo;
import win.spirithunt.android.provider.PermissionProvider;
import win.spirithunt.android.provider.SocketProvider;

/**
 * @author Remco Schipper
 */

public class GameJoinScanController extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener {

    private static final PermissionProvider permissionProvider = PermissionProvider.getInstance();

    private ProgressDialog progressDialog;

    private QRCodeReaderView qrCodeReaderView;

    private LinearLayoutCompat cameraContainer;

    private boolean hasPermission = false;

    private boolean isSending = false;

    public void close(View view) {
        this.finish();
    }

    @Override
    public void onQRCodeRead(final String text, PointF[] points) {
        if (this.hasPermission && !this.isSending) {
            final GameJoinScanController self = this;
            this.isSending = true;
            this.qrCodeReaderView.stopCamera();
            this.showProgressDialog();

            Socket socket = SocketProvider.getInstance().getConnection();
            socket.emit("lobby:info", new LobbyInfo(text), new Ack() {
                @Override
                public void call(final Object... args) {
                    self.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (args[0] == null) {
                                int currentPlayers = Integer.parseInt(args[1].toString());
                                int maxPlayers = Integer.parseInt(args[2].toString());

                                self.onSuccess(currentPlayers, maxPlayers, args[3].toString(), text);
                            } else {
                                self.onError(args[0].toString());
                            }
                        }
                    });
                }
            });
        }
    }

    protected void startCamera() {
        qrCodeReaderView = new QRCodeReaderView(this);
        qrCodeReaderView.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT));
        qrCodeReaderView.setOnQRCodeReadListener(this);
        qrCodeReaderView.setQRDecodingEnabled(true);
        qrCodeReaderView.setAutofocusInterval(2000L);
        qrCodeReaderView.setBackCamera();
        this.cameraContainer.addView(qrCodeReaderView);
    }

    protected void describeCameraAccess() {
        final GameJoinScanController self = this;

        new AlertDialog.Builder(this, R.style.AppDialog)
            .setTitle(getString(R.string.join_game_camera_explain_title))
            .setMessage(getString(R.string.join_game_camera_explain_text))
            .setCancelable(true)
            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    self.finish();
                }
            })
            .setPositiveButton(R.string.join_game_camera_explain_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    self.askForCameraAccess();
                }
            })
            .setNegativeButton(R.string.join_game_camera_explain_no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    self.finish();
                }
            })
            .show();
    }

    /**
     * Asks the OS for camera access
     */
    protected void askForCameraAccess() {
        permissionProvider.requestPermission(this, PermissionProvider.PERMISSION_CAMERA);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_join_scan_view);

        this.cameraContainer = (LinearLayoutCompat)findViewById(R.id.camera_preview);

        // If we've already got permission,
        if (permissionProvider.hasPermission(this, PermissionProvider.PERMISSION_CAMERA)) {
            startCamera();
            return;
        }

        // Should we explain why we need the permission?
        if (permissionProvider.shouldShowRationale(this, PermissionProvider.PERMISSION_CAMERA)) {
            describeCameraAccess();
        } else {
            askForCameraAccess();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.isSending = false;

        if (this.hasPermission) {
            qrCodeReaderView.startCamera();
        } else {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (this.hasPermission) {
            qrCodeReaderView.stopCamera();
        }
    }

    protected void onDestroy() {
        super.onDestroy();

        this.cameraContainer = null;
    }

    private void onSuccess(int currentPlayers, int maxPlayers, String hostname, String lobbyId) {
        if (this.isSending) {
            this.isSending = false;
            this.hideProgressDialog();

            Bundle bundle = new Bundle();
            bundle.putInt("maxPlayers", maxPlayers);
            bundle.putInt("currentPlayers", currentPlayers);
            bundle.putString("hostname", hostname);
            bundle.putString("lobbyId", lobbyId);

            Intent gameInfoIntent = new Intent(this, GameJoinInfoController.class);
            gameInfoIntent.putExtras(bundle);
            startActivity(gameInfoIntent);
        }
    }

    private void onError(String error) {
        if (this.isSending) {
            this.isSending = false;
            this.hideProgressDialog();
            int textId;

            switch (error) {
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

            this.showErrorDialog(getString(textId));

            if (this.hasPermission) {
                this.qrCodeReaderView.startCamera();
            }
        }
    }

    private void hideProgressDialog() {
        if(this.progressDialog != null) {
            this.progressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if(this.progressDialog == null) {
            this.progressDialog = new ProgressDialog(this);
            this.progressDialog.setTitle(getString(R.string.join_game_scan_text_progress_title));
            this.progressDialog.setMessage(getString(R.string.join_game_scan_text_progress_message));
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }
    }

    /**
     * Shows an error
     *
     * @param text
     */
    private void showErrorDialog(String text) {
        new android.app.AlertDialog.Builder(this)
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

    /**
     * Returns when permissions were requested.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode != PermissionProvider.PERMISSION_CAMERA) return;

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            this.hasPermission = true;
            startCamera();
        } else {
            finish();
        }
    }
}

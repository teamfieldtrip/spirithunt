package win.spirithunt.android.controller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import io.socket.client.Ack;
import io.socket.client.Socket;
import win.spirithunt.android.R;
import win.spirithunt.android.protocol.LobbyInfo;
import win.spirithunt.android.provider.DialogProvider;
import win.spirithunt.android.provider.PermissionProvider;
import win.spirithunt.android.provider.SocketProvider;

/**
 * @author Remco Schipper
 */

public class GameJoinScanController extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener {

    private static final PermissionProvider permissionProvider = PermissionProvider.getInstance();
    private static final String TAG = "JoinScanner";

    private QRCodeReaderView qrCodeReaderView;

    private LinearLayoutCompat cameraContainer;

    SharedPreferences prefs;

    private boolean hasPermission = false;

    private boolean isSending = false;

    private boolean isShowingError = false;
    private DialogProvider dialogProvider;

    public void close(View view) {
        finish();
    }

    @Override
    public void onQRCodeRead(final String text, PointF[] points) {
        if (hasPermission && !isSending) {
            final GameJoinScanController self = this;
            isSending = true;
            qrCodeReaderView.stopCamera();
            showProgressDialog();

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

    /**
     * Starts the QR code reader
     */
    protected void startCamera() {
        qrCodeReaderView = new QRCodeReaderView(this);
        qrCodeReaderView.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT));
        qrCodeReaderView.setOnQRCodeReadListener(this);
        qrCodeReaderView.setQRDecodingEnabled(true);
        qrCodeReaderView.setAutofocusInterval(2000L);
        qrCodeReaderView.setBackCamera();
        cameraContainer.addView(qrCodeReaderView);
    }

    /**
     * Explains why we need the camera access
     */
    protected void describeCameraAccess() {
        if (isFinishing()) {
            Log.w(TAG, "describeCameraAccess: Activity is already finishing");
            return;
        }

        Log.d(TAG, "describeCameraAccess: Describing why we need the camera");

        // Alert the user why we need the camera.
        final GameJoinScanController self = this;

        dialogProvider.provideAlertBuilder()
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
            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    self.finish();
                }
            })
            .show();
    }

    /**
     * Asks the OS for camera access
     */
    protected void askForCameraAccess() {
        Log.d(TAG, "askForCameraAccess: Asking for camera access");

        permissionProvider.requestPermission(this, PermissionProvider.Permissions.CAMERA);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_join_scan_view);

        Log.d(TAG, "onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");

        cameraContainer = (LinearLayoutCompat) findViewById(R.id.camera_preview);
        hasPermission = permissionProvider.hasPermission(this, PermissionProvider.Permissions.CAMERA);

        dialogProvider = new DialogProvider(this);

        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        // If we've already got permission,
        if (hasPermission) {
            startCamera();
            return;
        }

        // Should we explain why we need the permission?
        if (permissionProvider.shouldShowRationale(this, PermissionProvider.Permissions.CAMERA)) {
            Log.d(TAG, "onCreate: Describing why we need the camera");
            describeCameraAccess();
        } else {
            Log.d(TAG, "onCreate: Asking for camera access");
            askForCameraAccess();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        isSending = false;

        if (hasPermission) {
            qrCodeReaderView.startCamera();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (hasPermission) {
            qrCodeReaderView.stopCamera();
        }
    }

    protected void onDestroy() {
        super.onDestroy();

        cameraContainer = null;
    }

    private void onSuccess(int currentPlayers, int maxPlayers, String hostname, String lobbyId) {
        if (isSending) {

            if(prefs.getBoolean("settings_vibration", true)){
                Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(150);
            }

            isSending = false;
            hideProgressDialog();

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
        if (isSending) {

            if(prefs.getBoolean("settings_vibration", true)){
                Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);
            }

            isSending = false;
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

            hideProgressDialog();
            showErrorDialog(getString(textId));

            if (hasPermission) {
                qrCodeReaderView.startCamera();
            }
        }
    }

    private void hideProgressDialog() {
        dialogProvider.hideProgressDialog();
    }

    private void showProgressDialog() {
        dialogProvider.showProgressDialog(R.string.join_game_scan_text_progress_title, R.string.join_game_scan_text_progress_message);
    }

    /**
     * Shows an error
     *
     * @param text
     */
    private void showErrorDialog(String text) {
        if (!isShowingError) {

            dialogProvider.provideAlertBuilder()
                .setTitle(getString(R.string.join_game_text_error_title))
                .setMessage(text)
                .setNeutralButton(R.string.join_game_text_error_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        isShowingError = false;
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

            isShowingError = true;
        }
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
        if (requestCode != PermissionProvider.getPermissionId(PermissionProvider.Permissions.CAMERA))
            return;

        Log.d(TAG, "onRequestPermissionsResult: Recieved a result, which is " + grantResults[0]);
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            hasPermission = true;
            startCamera();
        } else {
            finish();
        }
    }
}

package win.spirithunt.android.controller;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

import io.socket.client.Ack;
import io.socket.client.Socket;
import win.spirithunt.android.R;
import win.spirithunt.android.protocol.AuthLogout;
import win.spirithunt.android.provider.SocketProvider;

import static android.content.ContentValues.TAG;

/**
 * Created by sven on 30-3-17.
 */

public class SettingsController extends PreferenceActivity {

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_view);
    }

    /**
     * Logs a user out, ends the activity and returns to the login screen.
     */
    protected void logoutUser() {
        Log.d(TAG, "logoutUser() called");

        // Get SharedPreferences
        final SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE);
        String jwt = sharedPref.getString(getString(R.string.saved_jwt), "DEFAULT");

        // Emit to the server
        final Socket socket = SocketProvider.getInstance().getConnection();

        final SettingsController self = this;

        self.showProgressDialog(
            getString(R.string.logout_title),
            getString(R.string.logout_content)
        );

        socket.emit("auth:logout", new AuthLogout(jwt), new Ack() {
            @Override
            public void call(final Object... args) {
                self.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        self.hideProgressDialog();
                        if (args[0] == null) {
                            SharedPreferences.Editor editor = sharedPref.edit();

                            // Remove JSON Web Token from SharedPreferences
                            editor.remove(getString(R.string.saved_jwt));

                            // Safe to do async, as the in-memory entry is instantly updated.
                            editor.apply();

                            // Close the connection
                            socket.close();

                            // Open Loginscreen
                            self.openLogin();
                        } else {
                            self.showErrorDialog(
                                getString(R.string.logout_error_title),
                                getString(R.string.logout_error_content),
                                getString(R.string.logout_error_btn));
                        }
                    }
                });
            }
        });
    }

    protected void openLogin(){

        // Get the LoginIntent
        Intent intent = new Intent(this, LoginController.class);

        // Don't save history
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Don't animate after logout
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        // Start activity and end this one.
        startActivity(intent);
        this.finish();
    }


    /**
     * Closes an open progress dialog, if any.
     */
    protected void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    /**
     * Opens a progress dialog, specify the title and body. The progress is non-cancellable.
     *
     * @param title Title of the dialog
     * @param body  Body of the dialog
     */
    protected void showProgressDialog(String title, String body) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this, R.style.AppDialog);
            progressDialog.setTitle(title);
            progressDialog.setMessage(body);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    /**
     * Shows an alert for failure to login or w/e
     *
     * @param title   Title of alert
     * @param content Message of alert
     * @param button  Content of dismiss button
     */
    protected void showErrorDialog(String title, String content, String button) {
        new AlertDialog.Builder(this, R.style.AppDialog)
            .setTitle(title)
            .setMessage(content)
            .setCancelable(true)
            .setPositiveButton(button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // do nothing
                }
            })
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
    }
}

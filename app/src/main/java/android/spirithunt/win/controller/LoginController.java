package android.spirithunt.win.controller;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.spirithunt.win.R;
import android.spirithunt.win.protocol.AuthLogin;
import android.spirithunt.win.provider.SocketProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import io.socket.client.Ack;
import io.socket.client.Socket;

/**
 * Created by sven on 30-3-17.
 * @author Remco Schipper
 */

public class LoginController extends AppCompatActivity {
    private ProgressDialog progressDialog;

    private void hideProgressDialog() {
        if(this.progressDialog != null) {
            this.progressDialog.dismiss();
        }
    }

    private void showMenu(Context context) {
        Intent intent = new Intent(context, MenuController.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    private void showProgressDialog() {
        if(this.progressDialog == null) {
            this.progressDialog = new ProgressDialog(this);
            this.progressDialog.setTitle(getString(R.string.authentication_progress_title));
            this.progressDialog.setMessage(getString(R.string.authentication_progress_content));
            this.progressDialog.setCancelable(true);
            this.progressDialog.show();
        }
    }

    private void showErrorDialog(Context context) {
        new AlertDialog.Builder(context)
            .setTitle(getString(R.string.authentication_alert_title))
            .setMessage(getString(R.string.authentication_alert_content))
            .setNeutralButton(R.string.authentication_alert_button, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // do nothing
                }
            })
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
    }

    private void saveJwt(String token) {
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.saved_jwt), token);
        editor.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);
    }

    public void submit(View view) {
        final LoginController self = this;
        this.showProgressDialog();

        String email = ((TextView)findViewById(R.id.username)).getText().toString();
        String password = ((TextView)findViewById(R.id.password)).getText().toString();

        Socket socket = SocketProvider.getInstance().getConnection();

        socket.emit("auth:login", new AuthLogin(email, password), new Ack() {
            @Override
            public void call(final Object... args) {
                self.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        self.hideProgressDialog();

                        if(args[0] == null) {
                            self.saveJwt(args[1].toString());
                            self.showMenu(self);
                        } else {
                            self.showErrorDialog(self);
                        }
                    }
                });
            }
        });
    }
}

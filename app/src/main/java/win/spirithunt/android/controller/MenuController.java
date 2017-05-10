package android.spirithunt.win.controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.spirithunt.win.R;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by sven on 30-3-17.
 *
 * @author Sven Boekelder
 */

public class MenuController extends AppCompatActivity implements DialogInterface.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_view);
    }

    public void createGame(View view){
        Intent intent = new Intent(view.getContext(), CreateGameController.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void joinGame(View view){
        Intent intent = new Intent(view.getContext(), GameJoinScanController.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void store(View view) {
        new AlertDialog.Builder(this, R.style.AppDialog)
            .setTitle(getString(R.string.store_alert_title))
            .setMessage(getString(R.string.store_alert_message))
            .setPositiveButton(getString(R.string.store_alert_button), null)
            .setCancelable(true)
            .setIcon(android.R.drawable.ic_dialog_info)
            .show();
    }

    public void settings(View view){
        Intent intent = new Intent(view.getContext(), SettingsController.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Onclick for the store, does nothing.
     */
    public void onClick(DialogInterface dialog, int which) {
        // do nothing
    }
}

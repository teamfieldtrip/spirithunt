package android.spirithunt.win.controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.spirithunt.win.R;
import android.spirithunt.win.gui.RadarDisplay;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Renders the main game interface, which consists of a radar, list of power ups, consume buttons
 * and a bottom navigation bar
 *
 * @author Roelof Roos <github@roelof.io>
 */
public class GameController extends AppCompatActivity implements View.OnClickListener {

    private RadarDisplay radar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_view);

        radar = (RadarDisplay) findViewById(R.id.game_status_radar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        };

        new AlertDialog.Builder(view.getContext(), R.style.AppDialog)
            .setTitle(getString(R.string.game_powerup_consume_title))
            .setMessage(getString(R.string.game_powerup_consume_desc))
            .setPositiveButton(R.string.game_powerup_consume_yes, listener)
            .setNegativeButton(R.string.game_powerup_consume_no, listener)
            .show();
    }
}

package android.spirithunt.win.controller;

import android.content.Intent;
import android.os.Bundle;
import android.spirithunt.win.R;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by sven on 30-3-17.
 * @author Remco Schipper
 */

public class MenuController extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_view);
    }

    public void createGame(View view) {
        Intent intent = new Intent(this, CreateGameController.class);
        startActivity(intent);
    }
}

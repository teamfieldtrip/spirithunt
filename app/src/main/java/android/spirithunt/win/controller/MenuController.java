package android.spirithunt.win.controller;

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

public class MenuController extends AppCompatActivity {
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

    public void store(View view){
        Intent intent = new Intent(view.getContext(), StoreController.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void settings(View view){
        Intent intent = new Intent(view.getContext(), SettingsController.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

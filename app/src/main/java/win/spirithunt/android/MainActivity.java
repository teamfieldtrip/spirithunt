package win.spirithunt.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import win.spirithunt.android.controller.GameController;
import win.spirithunt.android.controller.LoginController;
import win.spirithunt.android.controller.MenuController;
import win.spirithunt.android.provider.ContextProvider;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ContextProvider.getInstance().setContext(this.getApplicationContext());
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE);

        Intent intent;
        if(sharedPref.contains(getString(R.string.saved_jwt))) {
            intent = new Intent(this, GameController.class);
        } else {
            intent = new Intent(this, LoginController.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        overridePendingTransition(android.R.anim.fade_in, 0);
    }
}

package android.spirithunt.win;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.spirithunt.win.controller.GameJoinScanController;
import android.spirithunt.win.controller.LoginController;
import android.spirithunt.win.controller.MenuController;
import android.spirithunt.win.provider.ContextProvider;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private void showLogin() {
        Intent intent = new Intent(this, LoginController.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showMenu() {
        Intent intent = new Intent(this, GameJoinScanController.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ContextProvider.getInstance().setContext(this.getApplicationContext());
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE);

        if(sharedPref.contains(getString(R.string.saved_jwt))) {
            this.showMenu();
        } else {
            this.showLogin();
        }
    }
}

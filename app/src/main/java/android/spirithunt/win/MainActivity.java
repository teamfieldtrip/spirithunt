package android.spirithunt.win;

import android.spirithunt.win.callback.PlayerCreateCallback;
import android.spirithunt.win.lib.GpsReader;
import android.spirithunt.win.lib.PlayerManager;
import android.spirithunt.win.model.Player;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private Player player;
    private GpsReader gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ContextProvider.getInstance().setContext(this.getApplicationContext());
        final MainActivity self = this;

        PlayerManager.getInstance().getNewPlayer(new PlayerCreateCallback() {
            @Override
            public void call(String e, Player p) {
                player = p;
                gps = new GpsReader(self);
            }
        });

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == GpsReader.permissionRequestCode) {
            if(grantResults.length > 0) {
                gps.start();
            }
        }
    }
}

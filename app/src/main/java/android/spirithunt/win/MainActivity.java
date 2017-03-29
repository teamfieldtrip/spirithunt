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

    private void createGps() {
        if(this.gps == null) {
            this.gps = new GpsReader(this);
        }
    }

    private void getOrCreatePlayer() {
        final MainActivity self = this;

        if(this.player == null) {
            Player p = PlayerManager.getInstance().getPlayer();

            if(p == null) {
                PlayerManager.getInstance().getNewPlayer(new PlayerCreateCallback() {
                    @Override
                    public void call(String e, Player p) {
                        self.player = p;
                        self.createGps();
                    }
                });
            }
            else {
                this.player = p;
                this.createGps();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ContextProvider.getInstance().setContext(this.getApplicationContext());
        this.getOrCreatePlayer();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == GpsReader.permissionRequestCode) {
            if(grantResults.length > 0) {
                gps.start();
            }
        }
    }
}

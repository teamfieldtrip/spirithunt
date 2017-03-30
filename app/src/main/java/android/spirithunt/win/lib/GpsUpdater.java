package android.spirithunt.win.lib;

import android.spirithunt.win.callback.GpsUpdateCallback;
import android.spirithunt.win.protocol.GpsUpdate;
import android.spirithunt.win.model.Player;
import android.spirithunt.win.provider.SocketProvider;

import io.socket.client.*;

/**
 * @author Remco Schipper
 */

public class GpsUpdater {
    private static GpsUpdater instance;
    private io.socket.client.Socket socket;

    /**
     * Get the socket to use when needed
     */
    private GpsUpdater() {
        this.socket = SocketProvider.getInstance().getConnection();
    }

    /**
     * Set the coordinates without callback
     * @param latitude The new latitude
     * @param longitude The new longitude
     */
    public void setCoordinates(double latitude, double longitude) {
        this.setCoordinates(latitude, longitude, new GpsUpdateCallback() {
            @Override
            public void call(String error) {

            }
        });
    }

    /**
     * Set the coordinates with callback
     * @param latitude The new latitude
     * @param longitude The new longitude
     * @param callback Callback called in case of an error or on success
     */
    public void setCoordinates(double latitude, double longitude, final GpsUpdateCallback callback) {
        if(PlayerManager.getInstance().getPlayer() != null) {
            GpsUpdate update = new GpsUpdate(latitude, longitude, System.currentTimeMillis() / 1000);

            socket.emit("gps:update", update, new Ack() {
                @Override
                public void call(Object... args) {
                    if (args[0] == null) {
                        Player player = PlayerManager.getInstance().getPlayer();

                        if (player != null) {
                            player.latitude = Double.parseDouble(args[1].toString());
                            player.longitude = Double.parseDouble(args[2].toString());
                        }

                        callback.call(null);
                    } else {
                        callback.call(args[0].toString());
                    }
                }
            });
        }
        else {
            callback.call(null);
        }
    }

    /**
     * Get the GPS updater instance
     * @return {GpsUpdater}
     */
    public static GpsUpdater getInstance() {
        if(instance == null) {
            instance = new GpsUpdater();
        }

        return instance;
    }
}

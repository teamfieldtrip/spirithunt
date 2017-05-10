package android.spirithunt.win.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.spirithunt.win.R;
import android.spirithunt.win.callback.PlayerCreateCallback;
import android.spirithunt.win.model.Player;
import android.spirithunt.win.protocol.PlayerCreate;
import android.spirithunt.win.protocol.PlayerResume;

import io.socket.client.*;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * @author Remco Schipper
 */

public class PlayerProvider {
    private static PlayerProvider instance;
    private Player player;
    private Socket socket;

    /**
     * Attach the events to the socket object
     */
    private PlayerProvider() {
        final PlayerProvider self = this;

        this.socket = SocketProvider.getInstance().getConnection();
        this.socket.on(io.socket.client.Socket.EVENT_RECONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if(self.player != null) {
                    self.socket.emit("player:resume", new PlayerResume(self.player));
                }
            }
        });
    }

    /**
     * Return the current player or null
     * @return {Player}
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Return the current player or create a new one
     * @param callback Called with the (new) player instance
     */
    public void getNewPlayer(final PlayerCreateCallback callback) {
        Context context = ContextProvider.getInstance().getContext();
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preferences_file), Context.MODE_PRIVATE);
        final PlayerProvider self = this;
        PlayerCreate createPlayer = new PlayerCreate(sharedPref.getString(context.getString(R.string.saved_jwt), null));

        socket.emit("player:create", createPlayer, new Ack() {
            @Override
            public void call(Object... args) {
                if(args[0] == null) {
                    self.player = new Player(args[1].toString());
                    callback.call(null, self.player);
                }
                else {
                    callback.call(args[0].toString(), null);
                }
            }
        });
    }

    /**
     * Get the player manager instance
     * @return {PlayerManager}
     */
    public static PlayerProvider getInstance() {
        if(instance == null) {
            instance = new PlayerProvider();
        }

        return instance;
    }
}

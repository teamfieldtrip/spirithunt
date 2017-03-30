package android.spirithunt.win.lib;

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

public class PlayerManager {
    private static PlayerManager instance;
    private Player player;
    private Socket socket;

    /**
     * Attach the events to the socket object
     */
    private PlayerManager() {
        final PlayerManager self = this;

        this.socket = android.spirithunt.win.lib.Socket.getInstance().getConnection();
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
        final PlayerManager self = this;
        PlayerCreate createPlayer = new PlayerCreate();

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
    public static PlayerManager getInstance() {
        if(instance == null) {
            instance = new PlayerManager();
        }

        return instance;
    }
}

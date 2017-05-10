package win.spirithunt.android.provider;

import win.spirithunt.android.callback.PlayerCreateCallback;
import win.spirithunt.android.model.Player;
import win.spirithunt.android.protocol.PlayerCreate;
import win.spirithunt.android.protocol.PlayerResume;

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
        final PlayerProvider self = this;
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
    public static PlayerProvider getInstance() {
        if(instance == null) {
            instance = new PlayerProvider();
        }

        return instance;
    }
}

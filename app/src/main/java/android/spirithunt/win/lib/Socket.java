package android.spirithunt.win.lib;

import android.spirithunt.win.Constants;
import android.util.Log;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.emitter.Emitter;

/**
 * @author Remco Schipper
 */

public class Socket {
    private static final String TAG = Socket.class.getSimpleName();
    private io.socket.client.Socket socket;

    public Socket() {
        try {
            this.socket = IO.socket(Constants.SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        this.socket.connect();
        this.attachEvents();
    }

    private void attachEvents() {
        this.socket.once(io.socket.client.Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(Socket.TAG, "Connected to the server");
            }
        });
        this.socket.once(io.socket.client.Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(Socket.TAG, "Could not connect to the server");
            }
        });
        this.socket.on(io.socket.client.Socket.EVENT_RECONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(Socket.TAG, "Reconnected to the server");
            }
        });
        this.socket.on(io.socket.client.Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(Socket.TAG, "Lost the connection to the server");
            }
        });
    }

    public io.socket.client.Socket getConnection() {
        return this.socket;
    }
}

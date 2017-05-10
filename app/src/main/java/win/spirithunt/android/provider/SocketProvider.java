package win.spirithunt.android.provider;

import win.spirithunt.android.R;
import android.util.Log;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.emitter.Emitter;

/**
 * @author Remco Schipper
 */

public class SocketProvider {
    private static final String TAG = SocketProvider.class.getSimpleName();
    private static final SocketProvider instance = new SocketProvider();
    private io.socket.client.Socket socket;

    private SocketProvider() {
        try {
            String hostname = ContextProvider.getInstance().getContext().getString(R.string.server_address);
            Log.d(TAG, "SocketProvider: Connecting to " + hostname);
            this.socket = IO.socket(hostname);
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
                Log.d(SocketProvider.TAG, "Connected to the server");
            }
        });
        this.socket.once(io.socket.client.Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(SocketProvider.TAG, "Could not connect to the server");
            }
        });
        this.socket.on(io.socket.client.Socket.EVENT_RECONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(SocketProvider.TAG, "Reconnected to the server");
            }
        });
        this.socket.on(io.socket.client.Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(SocketProvider.TAG, "Lost the connection to the server");
            }
        });
    }

    public io.socket.client.Socket getConnection() {
        return this.socket;
    }
    public static SocketProvider getInstance() {
        return SocketProvider.instance;
    }
}

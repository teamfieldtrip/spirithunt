package android.spirithunt.win.callback;

import android.spirithunt.win.model.Player;

/**
 * @author Remco Schipper
 */

public interface PlayerCreateCallback {
    public void call(String error, Player player);
}

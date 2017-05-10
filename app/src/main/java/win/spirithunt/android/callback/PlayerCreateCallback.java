package win.spirithunt.android.callback;

import win.spirithunt.android.model.Player;

/**
 * @author Remco Schipper
 */

public interface PlayerCreateCallback {
    public void call(String error, Player player);
}

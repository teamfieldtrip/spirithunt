package android.spirithunt.win.Controllers;

import android.content.Context;

/**
 * @author Remco Schipper
 */

public class ContextController {
    private static final ContextController ourInstance = new ContextController();
    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return this.context;
    }

    public static ContextController getInstance() {
        return ourInstance;
    }
}

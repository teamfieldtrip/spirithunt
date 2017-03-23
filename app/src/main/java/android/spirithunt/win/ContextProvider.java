package android.spirithunt.win;

import android.content.Context;

/**
 * @author Remco Schipper
 */

public class ContextProvider {
    private static final ContextProvider ourInstance = new ContextProvider();
    private Context context;

    void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return this.context;
    }

    public static ContextProvider getInstance() {
        return ourInstance;
    }
}

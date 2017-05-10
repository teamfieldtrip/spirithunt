package win.spirithunt.android.provider;

import android.content.Context;

/**
 * @author Remco Schipper
 */

public class ContextProvider {
    private static final ContextProvider ourInstance = new ContextProvider();
    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return this.context;
    }

    public static ContextProvider getInstance() {
        return ourInstance;
    }
}

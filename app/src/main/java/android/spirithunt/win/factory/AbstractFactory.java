package android.spirithunt.win.factory;

import android.content.Context;
import android.spirithunt.win.ContextProvider;

/**
 * @author Remco Schipper
 */

abstract class AbstractFactory {
    private ContextProvider contextProvider;

    AbstractFactory() {
        this.contextProvider = ContextProvider.getInstance();
    }

    Context getContext() {
        return contextProvider.getContext();
    }
}

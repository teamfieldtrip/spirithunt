package win.spirithunt.android.cache;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;

/**
 * @author Remco Schipper
 * @see <a href="http://stackoverflow.com/a/16648457">How to set a particular font for a button text in android?</a>
 */

public class FontCache {
    private static Hashtable<String, Typeface> fontCache = new Hashtable<String, Typeface>();

    public static Typeface get(String name, Context context) {
        Typeface tf = fontCache.get(name);
        if(tf == null) {
            try {
                tf = Typeface.createFromAsset(context.getAssets(), name);
            } catch (Exception e) {
                return null;
            }
            fontCache.put(name, tf);
        }
        return tf;
    }
}

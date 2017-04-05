package android.spirithunt.win.provider;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.spirithunt.win.R;
import android.spirithunt.win.cache.FontCache;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author Remco Schipper <github@remcoschipper.com>
 * @see <a href="http://stackoverflow.com/a/16648457">How to set a particular font for a button text in android?</a>
 */

public class FontProvider {
    /**
     * Sets a font on a textview based on the custom com.my.package:font attribute
     * If the custom font attribute isn't found in the attributes nothing happens
     * @param textview
     * @param context
     * @param attrs
     */
    public static void setCustomFont(TextView textview, Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomFont);
        String font = a.getString(R.styleable.CustomFont_font);
        setCustomFont(textview, font, context);
        a.recycle();
    }

    /**
     * Sets a font on a textview
     * @param textview
     * @param font
     * @param context
     */
    public static void setCustomFont(TextView textview, String font, Context context) {
        if(font == null) {
            return;
        }
        Typeface tf = FontCache.get(font, context);
        if(tf != null) {
            textview.setTypeface(tf);
        }
    }
}

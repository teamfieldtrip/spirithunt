package android.spirithunt.win.gui;

import android.content.Context;
import android.spirithunt.win.provider.FontProvider;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

/**
 * Provides the lay-out with a custom textview (accepts a custom font)
 * @author Remco Schipper
 * @see <a href="http://stackoverflow.com/a/16648457">How to set a particular font for a button text in android?</a>
 */
public class CustomTextView extends AppCompatEditText {
    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        FontProvider.setCustomFont(this, context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        FontProvider.setCustomFont(this, context, attrs);
    }
}

package android.spirithunt.win.gui;

import android.content.Context;
import android.spirithunt.win.provider.FontProvider;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

/**
 * @author Remco Schipper
 * @see <a href="http://stackoverflow.com/a/16648457">How to set a particular font for a button text in android?</a>
 */

public class CustomEditText extends AppCompatEditText {
    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        FontProvider.setCustomFont(this, context, attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        FontProvider.setCustomFont(this, context, attrs);
    }
}

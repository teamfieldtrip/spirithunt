package android.spirithunt.win.gui.button;

import android.content.Context;
import android.spirithunt.win.provider.FontProvider;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

/**
 * @author Remco Schipper
 * @see <a href="http://stackoverflow.com/a/16648457">How to set a particular font for a button text in android?</a>
 */

public class LobsterButton extends AppCompatButton {
    public LobsterButton(Context context) {
        super(context);
    }

    public LobsterButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        FontProvider.setCustomFont(this, context, attrs);
    }

    public LobsterButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        FontProvider.setCustomFont(this, context, attrs);
    }
}

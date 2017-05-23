package win.spirithunt.android.gui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.ActionBarOverlayLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.FrameLayout.LayoutParams;

import win.spirithunt.android.R;

/**
 * Provides a text view with an icon view next to it.
 *
 * @author Remco Schipper
 */
public class ScoreBlock extends LinearLayout {
    public static final String TAG = "ScoreBlockGUI";

    protected CustomTextView textView;
    protected AppCompatImageView iconView;

    /**
     * Adds an icon and text view to the layout
     */
    protected void initContents() {
        // Set configuration
        setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
        setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_NONE);
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER);

        // Build layout params
        LayoutParams iconLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        LayoutParams textLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        iconLayoutParams.setMarginEnd(getResources().getDimensionPixelSize(R.dimen.scoreblock_icon_margin));

        // Create views
        iconView = new AppCompatImageView(getContext());
        textView = new CustomTextView(getContext(), null, R.style.textView_normal_main);

        iconView.setVisibility(INVISIBLE);
        textView.setText(getResources().getString(R.string.app_name));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        // Set layout params
        iconView.setLayoutParams(iconLayoutParams);
        textView.setLayoutParams(textLayoutParams);

        // Add views
        addView(iconView);
        addView(textView);
    }

    /**
     * Gets content from the attributes of this layout and adds it to the required elements
     * @param attrs
     */
    protected void fillContent(AttributeSet attrs) {
        Context context = getContext();

        String appName = null;
        Drawable appIcon = null;
        boolean showIcon = false;
        int textColour;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textColour = getResources().getColor(R.color.colorText, null);
        } else {
            textColour = getResources().getColor(R.color.colorText);
        }

        if (attrs == null) {
            showIcon = false;
        } else {
            TypedArray attributes = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.ScoreBlock, 0, 0);

            try {
                appName = attributes.getText(R.styleable.ScoreBlock_text).toString();
                appIcon = attributes.getDrawable(R.styleable.ScoreBlock_text);
                showIcon = attributes.getBoolean(R.styleable.ScoreBlock_showIcon, false);
                textColour = attributes.getColor(R.styleable.ScoreBlock_colour, textColour);
            } catch (RuntimeException exception) {
                Log.e(TAG, "assignContent: Failed to assign variables", exception);
            } finally {
                attributes.recycle();
            }
        }

        if (showIcon && appIcon != null) {
            iconView.setVisibility(VISIBLE);
            iconView.setMinimumWidth(appIcon.getIntrinsicWidth());
            iconView.setImageDrawable(appIcon);
        }

        if (appName != null && !appName.isEmpty()) {
            textView.setText(appName);
            textView.setTextColor(textColour);
        }

    }

    /**
     * Inflates layout and assigns content as given in the attributes.
     *
     * @param attributeSet
     */
    protected void init(AttributeSet attributeSet) {
        initContents();
        fillContent(attributeSet);
    }

    public ScoreBlock(Context context) {
        super(context);
        init(null);
    }

    public ScoreBlock(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ScoreBlock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }
}

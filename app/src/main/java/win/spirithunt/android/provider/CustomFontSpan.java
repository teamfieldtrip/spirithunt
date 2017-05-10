package android.spirithunt.win.provider;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;
import android.util.TypedValue;

/**
 * @author Remco Schipper
 */

public class CustomFontSpan extends TypefaceSpan {
    private final String family;

    private Typeface typeface;

    private int dp;

    private int size;

    public CustomFontSpan(String family, int dp) {
        super(family);
        this.family = family;
        this.dp = dp;
        this.size = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            this.dp, ContextProvider.getInstance().getContext().getResources().getDisplayMetrics());
    }

    private CustomFontSpan(Parcel in) {
        super(in);
        this.family = in.readString();
        this.dp = in.readInt();
        this.size = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            this.dp, ContextProvider.getInstance().getContext().getResources().getDisplayMetrics());
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        applyCustomTypeFace(ds, this.getTypeFace());
    }

    @Override
    public void updateMeasureState(TextPaint paint) {
        applyCustomTypeFace(paint, this.getTypeFace());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.family);
        dest.writeInt(this.dp);
    }

    private Typeface getTypeFace() {
        if (this.typeface == null) {
            this.typeface = Typeface.createFromAsset(
                ContextProvider.getInstance().getContext().getAssets(),
                "fonts/" + this.family
            );
        }

        return this.typeface;
    }

    public static final Creator<CustomFontSpan> CREATOR = new Creator<CustomFontSpan>() {
        @Override
        public CustomFontSpan createFromParcel(Parcel in) {
            return new CustomFontSpan(in);
        }

        @Override
        public CustomFontSpan[] newArray(int size) {
            return new CustomFontSpan[size];
        }
    };

    private void applyCustomTypeFace(Paint paint, Typeface tf) {
        int oldStyle;
        Typeface old = paint.getTypeface();
        if (old == null) {
            oldStyle = 0;
        } else {
            oldStyle = old.getStyle();
        }

        int fake = oldStyle & ~tf.getStyle();
        if ((fake & Typeface.BOLD) != 0) {
            paint.setFakeBoldText(true);
        }

        if ((fake & Typeface.ITALIC) != 0) {
            paint.setTextSkewX(-0.25f);
        }

        paint.setTextSize(this.size);

        paint.setTypeface(tf);
    }
}

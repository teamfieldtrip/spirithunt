package android.spirithunt.win.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Location;
import android.spirithunt.win.controller.RadarRenderController;
import android.spirithunt.win.model.Player;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

import java.util.List;

/**
 * Handles updating the radar
 *
 * @author Roelof Roos <github@roelof.io>
 */

public class RadarDisplay extends SurfaceView {

    static String TAG = "Tag";

    protected Location playerLocation;
    protected List<Player> players;
    protected Rect size;
    protected RadarRenderController renderController;

    public RadarDisplay(Context context) {
        super(context);
        renderController = new RadarRenderController(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        renderController.start();
        getHolder().addCallback(renderController);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getHolder().removeCallback(renderController);
        renderController.interrupt();
    }

    public RadarDisplay(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        renderController = new RadarRenderController(context);
    }
}

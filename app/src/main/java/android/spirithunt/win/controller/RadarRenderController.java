package android.spirithunt.win.controller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Handles updating the radar in a separate thread
 *
 * @author Roelof Roos <github@roelof.io>
 */

public class RadarRenderController extends Thread implements SurfaceHolder.Callback {

    private static final String TAG = "RRC";
    boolean active;
    SurfaceHolder holder;
    Context context;
    Rect canvasSize;

    /**
     * Start with a context
     *
     * @param context Application context
     */
    public RadarRenderController(Context context) {
       this.context = context;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.holder = holder;

        if (holder != null) {
            active = true;
            canvasSize = holder.getSurfaceFrame();
        } else {
            active = false;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged: Got a new surface!" + holder.toString());
        this.holder = holder;

        if (holder != null) {
            active = true;
            canvasSize = holder.getSurfaceFrame();
        } else {
            active = false;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed: Surface gone");
        this.holder = null;
        this.active = false;

    }

    protected Canvas drawRadar(Canvas canvas) {
        if (canvasSize == null) {
            canvasSize = canvas.getClipBounds();
        }

        int[] colors = new int[]{
            (int) (Math.random() * 255),
            (int) (Math.random() * 255),
            (int) (Math.random() * 255)
        };

        int color = Color.rgb(colors[0], colors[1], colors[2]);

        Paint paint = new Paint(0);
        paint.setColor(color);
        canvas.drawRect(canvasSize, paint);

        double time = System.currentTimeMillis() / 1000D;

        Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.parseColor("#ff0000"));
        linePaint.setStrokeWidth(30f);
        canvas.drawLine(
            canvasSize.centerX(),
            canvasSize.centerY(),
            (float) (canvasSize.centerX() + Math.cos(time) * canvasSize.width()),
            (float) (canvasSize.centerY() + Math.sin(time) * canvasSize.height()),
            linePaint
        );

        return canvas;
    }

    @Override
    public void run() {
        super.run();

        Canvas canvas;

        while(!isInterrupted()) {
            if (!active || holder == null) continue;

            canvas = holder.lockCanvas();
            if (canvas != null) {
                canvas = drawRadar(canvas);
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }
}

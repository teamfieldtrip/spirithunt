package android.spirithunt.win.controller;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.spirithunt.win.R;
import android.spirithunt.win.gui.RadarDisplay;
import android.spirithunt.win.model.Location;
import android.spirithunt.win.model.Player;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.ArrayList;

/**
 * Handles updating the radar in a separate thread
 *
 * @author Roelof Roos <github@roelof.io>
 */

public class RadarRenderController extends Thread implements SurfaceHolder.Callback {

    /**
     * Logger tag
     */
    private static final String TAG = "RadarRenderController";

    /**
     * Duration of one full spin of the radar dial
     */
    private static final double ROTATION_DURATION = 3.45;

    /**
     * Number of frames per second when unable to draw. Basically the responsiveness when a canvas
     * becomes available
     */
    private static final int RATE_IDLE = 4;

    /**
     * Number of frames per second to draw the radar. Determines the update-rate of the radar, but
     * not the rate at which it spins (time-based)
     */
    private static final int RATE_ACTIVE = 40;

    /**
     * Split
     */
    private static final long ROTATION_SPLIT = (long) Math.floor(1000 * ROTATION_DURATION);

    /**
     * If the loop is active and drawing
     */
    private boolean active;

    /**
     * Surface holder to draw on
     */
    private SurfaceHolder holder;

    /**
     * Radar Display which is visisble to the user.
     */
    private RadarDisplay radarDisplay;

    /**
     * Center of the canvas, for performance reasons
     */
    private Point canvasCenter;

    /**
     * Background image for the radar
     */
    private Drawable radarBackgroundPicture;

    /**
     * Foreground picture for the radar, which is the scan line
     */
    private Drawable radarScannerPicture;

    /**
     * Start with a context
     *
     * @param context Application context
     */
    public RadarRenderController(RadarDisplay radarDisplay) {
       this.radarDisplay = radarDisplay;

        Resources res = radarDisplay.getContext().getResources();

        // HACK Backwards compatibility, Resources.getDrawable(int) is deprecated, but the replacing
        // API exists in API 21, which is too new for our min API level (19)
        radarBackgroundPicture = res.getDrawable(R.drawable.gui_el_blue_radar);
        radarScannerPicture = res.getDrawable(R.drawable.gui_el_sweeper_red);
    }

    /**
     * Safely updates SurfaceHolder and activates while loop, or disables it in case the holder is
     * null.
     * @param surfaceHolder
     */
    protected void updateHolder(@Nullable SurfaceHolder surfaceHolder) {
        holder = surfaceHolder;

        if (holder == null) {
            active = false;
            return;
        }

        // Activate for loop
        active = true;

        // Resize graphics
        resizeGraphics(holder.getSurfaceFrame());
    }

    /**
     * Registers the created SurfaceHolder to be used in the drawing process, enables the drawing
     * loop.
     *
     * @param surfaceHolder
     */
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        // Log
        Log.d(TAG, "surfaceCreated: Received initial surface");

        // Activate transparency
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);

        // Update holder and activate loop
        updateHolder(surfaceHolder);
    }

    /**
     * Updates the SurfaceHolder for the loop, (de)activating it if required.
     *
     * @param surfaceHolder
     * @param format
     * @param width
     * @param height
     */
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged: Received surface update.");
        updateHolder(surfaceHolder);
    }

    /**
     * Stops the drawing loop and removes the reference to the SurfaceHolder.
     *
     * @param surfaceHolder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "surfaceDestroyed: Received surface destruction.");
        updateHolder(null);
    }

    /**
     * Recalculates
     * @param size
     */
    protected void resizeGraphics(Rect size) {
        // Calculate smallest rectangle
        int canvasMinSize = Math.min(size.height(), size.width());
        int canvasHalfSize = Math.round(canvasMinSize / 2f);
        int canvasCircleSize = (int) Math.floor(canvasHalfSize * 0.85d);

        int cx = size.centerX();
        int cy = size.centerY();

        // Determine square area
        Rect radarBackgroundArea = new Rect(
            cx - canvasHalfSize,
            cy - canvasHalfSize,
            cx + canvasHalfSize,
            cy + canvasHalfSize
        );

        // Determine area for tracker
        Rect radarSweepRect = new Rect(
            cx - canvasCircleSize,
            cy - canvasCircleSize,
            cx,
            cy
        );
        // Log info
        Log.d(TAG, String.format(
            "resizeGraphics: %s",
            radarSweepRect.toString()
        ));

        // Apply bounds to picture
        radarBackgroundPicture.setBounds(radarBackgroundArea);
        radarScannerPicture.setBounds(radarSweepRect);

        // Add point of center
        canvasCenter = new Point(size.centerX(), size.centerY());
    }

    /**
     * Draws canvas, by clearing it first, then adding the radar, the players and the sweeper
     * animation.
     *
     * @param canvas
     * @return
     */
    protected Canvas drawRadar(Canvas canvas) {
        // Get a time
        long time = System.currentTimeMillis() % ROTATION_SPLIT;
        float rotation = (float) ((time / 1000f) * (360 / ROTATION_DURATION)) % 360f;

        // Clear canvas
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);

        // Draw radar background
        radarBackgroundPicture.draw(canvas);

        // Draw players
        // TODO

        // Draw sweeper
        // NOTE Rotating the whole flippin' canvas is quicker than rotating a Drawable for some
        // weird reason.
        canvas.save();
        canvas.rotate(rotation, canvasCenter.x, canvasCenter.y);
        radarScannerPicture.draw(canvas);
        canvas.restore();

        // Done
        return canvas;
    }

    public ArrayList<PlayerDrawLocation> buildPlayerLocations(ArrayList<Player> players, Player activePlayer) {
        if (players == null) {
            return new ArrayList<>();
        }

        ArrayList<PlayerDrawLocation> res = new ArrayList<>();

        PlayerDrawLocation playerLocation;

        for (Player player: players) {
            playerLocation = new PlayerDrawLocation(player);
            playerLocation.calibrate(activePlayer);
            res.add(playerLocation);
        }

        return res;
    }

    @Override
    public void run() {
        super.run();

        Canvas canvas;
        int rate;

        // Atomic list, updated at the end of a loop if required
        ArrayList<Player> players = radarDisplay.getPlayerList();
        ArrayList<PlayerDrawLocation> playerLocations = null;

        while(!isInterrupted()) {
            if (!active || holder == null) {
                rate = RATE_IDLE;
            } else {
                rate = RATE_ACTIVE;

                canvas = holder.lockCanvas();
                if (canvas != null) {
                    canvas = drawRadar(canvas);
                    holder.unlockCanvasAndPost(canvas);
                }
            }

            // Update the arrayList of players, if required
            if (players != radarDisplay.getPlayerList()) {
                players = radarDisplay.getPlayerList();
                playerLocations = buildPlayerLocations(
                    players,
                    radarDisplay.getActivePlayer()
                );
            }

            // Wait the normal frame duration, if we drop below it's no problem
            try {
                sleep(1000 / rate);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    class PlayerDrawLocation {
        Player player;
        Point location;

        PlayerDrawLocation(Player player) {
            this.player = player;
        }

        void calibrate(Player alignmentPlayer) {
            float[] gpsData = new float[3];

            Location.distanceBetween(
                player.latitude,
                player.longitude,
                alignmentPlayer.latitude,
                alignmentPlayer.longitude,
                gpsData
            );

            Location actPlyLoc = Location.build(player.latitude, player.longitude);


        }


    }
}

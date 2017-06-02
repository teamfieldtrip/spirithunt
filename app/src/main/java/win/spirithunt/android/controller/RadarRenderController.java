package win.spirithunt.android.controller;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.ArrayList;

import win.spirithunt.android.R;
import win.spirithunt.android.gui.RadarDisplay;
import win.spirithunt.android.model.Location;
import win.spirithunt.android.model.Player;

/**
 * Handles updating the radar in a separate thread
 *
 * @author Roelof Roos [github@roelof.io]
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
     * Number of frames per second to draw the radar. Determines the update-rate of the radar when
     * in power-save mode.
     */
    private static final int RATE_ACTIVE_PS = 10;

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
     * Current team.
     */
    private int team;

    public RadarRenderController(RadarDisplay display, int theme) {
        radarDisplay = display;

        setTeam(theme);
    }

    /**
     * Returns true if power save mode is supported and currently active.
     *
     * @return
     */
    protected boolean isInPowerSaveMode() {
        PowerManager powerManager = (PowerManager) radarDisplay.getContext().getSystemService(Context.POWER_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return powerManager.isPowerSaveMode();
        } else {
            return false;
        }
    }

    /**
     * Changes the team we're on.
     *
     * @param team
     */
    public void setTeam(int team) {
        Log.d(TAG, "setTeam() called with: team = [" + team + "]");
        Resources res = radarDisplay.getContext().getResources();
        this.team = team;

        int backgroundResource;
        int sweeperResource;

        if (team == GameController.TEAM_RED) {
            backgroundResource = R.drawable.gui_el_red_radar;
            sweeperResource = R.drawable.gui_el_sweeper_red;
        } else {
            backgroundResource = R.drawable.gui_el_blue_radar;
            sweeperResource = R.drawable.gui_el_sweeper_blue;
        }

        // Support API > 21 with skins, we should be able to link resources to it, but whatever.
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            radarBackgroundPicture = res.getDrawable(backgroundResource, null);
            radarScannerPicture = res.getDrawable(sweeperResource, null);
        } else {
            radarBackgroundPicture = res.getDrawable(backgroundResource);
            radarScannerPicture = res.getDrawable(sweeperResource);
        }
        if (holder != null) {
            resizeGraphics(holder.getSurfaceFrame());
        }
    }

    /**
     * Safely updates SurfaceHolder and activates while loop, or disables it in case the holder is
     * null.
     *
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
     * Recalculates the size of all graphics when the canvas size changes.
     *
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
    protected Canvas drawRadar(Canvas canvas, ArrayList<DrawablePlayer> players, Player currentPlayer, boolean inPowerSaveMode) {
        // Get a time
        long time = System.currentTimeMillis() % ROTATION_SPLIT;
        float rotation = (float) ((time / 1000f) * (360f / ROTATION_DURATION)) % 360f;

        // Clear canvas
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);

        // Draw radar background
        radarBackgroundPicture.draw(canvas);

        if (!inPowerSaveMode) {
            // Draw sweeper
            // NOTE Rotating the whole flippin' canvas is quicker than rotating a Drawable for some
            // weird reason.
            canvas.save();

            // Rotate the canvas 29°, to compensate the sweeper not being top-aligned.
            canvas.rotate(29, canvasCenter.x, canvasCenter.y);
            canvas.rotate(rotation, canvasCenter.x, canvasCenter.y);
            radarScannerPicture.draw(canvas);
            canvas.restore();
        }

        // Determine paint for friendly target
        Paint paintFriendly = new Paint();
        paintFriendly.setStyle(Paint.Style.FILL);
        paintFriendly.setColor(Color.WHITE);
        paintFriendly.setAlpha(90);

        // Determine paint for hostile target
        Paint paintHostile = new Paint(paintFriendly);
        paintHostile.setAlpha(200);

        // Draw targets, both friendly and hostile.
        for (DrawablePlayer player : players) {
            player.draw(canvas, currentPlayer, paintFriendly, paintHostile, rotation, inPowerSaveMode);
        }

        // Done
        return canvas;
    }

    /**
     * Converts a list of Player objects to a list of PlayerDrawLocation objects, which contain
     * information on where to draw the player.
     *
     * @param players
     * @param perspectivePlayer
     * @return List of players, with their respective draw locations
     */
    public ArrayList<DrawablePlayer> buildDrawablePlayerList(ArrayList<Player> players, Player perspectivePlayer) {
        if (players == null) {
            return new ArrayList<>();
        }

        ArrayList<DrawablePlayer> res = new ArrayList<>();

        DrawablePlayer playerLocation;
        float angle = radarDisplay.getAngle();

        for (Player player : players) {
            playerLocation = new DrawablePlayer(player);
            playerLocation.preload(perspectivePlayer, angle);

            if (player != perspectivePlayer) {
                res.add(playerLocation);
            }
        }

        return res;
    }

    @Override
    public void run() {
        super.run();

        Canvas canvas;
        int rate;

        // Atomic list, updated at the end of a loop if required
        ArrayList<Player> players = null;
        ArrayList<DrawablePlayer> playerList = null;
        Player activePlayer = null;
        boolean changes;
        boolean inPowerSaveMode = isInPowerSaveMode();

        while (!isInterrupted()) {
            if (!active || holder == null) {
                rate = RATE_IDLE;
            } else {
                rate = inPowerSaveMode ? RATE_ACTIVE_PS : RATE_ACTIVE;

                changes = false;

                // Update or initialize the activePlayer variable
                if (activePlayer == null || activePlayer != radarDisplay.getActivePlayer()) {
                    activePlayer = radarDisplay.getActivePlayer();
                    setTeam(activePlayer.team);
                    changes = true;
                }

                // Update or initialize the arrayList of players, if required
                if (radarDisplay.getUpdateState()) {
                    radarDisplay.setUpdateState(false);
                    players = radarDisplay.getPlayerList();
                    changes = true;
                }

                if (changes || playerList == null) {
                    playerList = buildDrawablePlayerList(players, activePlayer);
                }

                // Get a lock on the canvas.
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    // Draw the radar
                    canvas = drawRadar(canvas, playerList, activePlayer, inPowerSaveMode);

                    // Release lock and update canvas contents
                    holder.unlockCanvasAndPost(canvas);
                }
            }

            // Wait the normal frame duration, if we drop below it's no problem
            try {
                sleep(1000 / rate);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    class DrawablePlayer {
        private static final double RADIAL_MULTIPLIER = ((2.0 * Math.PI) / 360.0);

        private static final double MAX_DISTANCE = 50f; //250f;

        private static final int CIRCLE_SIZE = 10;

        private static final double HALF_PI = Math.PI * .5;

        private Player player;

        private float distance = -1;

        private float bearing = -1;

        DrawablePlayer(Player player) {
            this.player = player;
        }

        /**
         * Preload the distance and bearing calculation for the player, does not include the
         * current bearing of the player, just the north-faced bearing.
         *
         * @param perspectivePlayer Player to use as alignment, basically the Player for this device.
         */
        public void preload(Player perspectivePlayer, float angle) {
            float[] results = new float[3];

            Location.distanceBetween(
                player.latitude,
                player.longitude,
                perspectivePlayer.latitude,
                perspectivePlayer.longitude,
                results
            );

            // 360 + 360 + 180, 180 would work, but would require extra statements below
            bearing = (900f - angle) - results[2];
            distance = results[0];

            Log.d("Compass Angle", String.valueOf(angle));
            Log.d("Player Angle", String.valueOf(results[2]));

            while (bearing > 360f) {
                bearing -= 360f;
            }

            double latDist = perspectivePlayer.latitude - player.latitude;
            double longDist = perspectivePlayer.longitude - player.longitude;

            Log.d(TAG, String.format(
                "preload: %d, (%.0f, %.0f), b: %.1f, d: %.1f",
                player.team,
                latDist * 10000f,
                longDist * 10000f,
                bearing,
                distance
            ));
        }

        /**
         * Draws the player on the radar, using the given radar dimensions and center location.
         *
         * @param canvas
         */
        public void draw(Canvas canvas, Player perspectivePlayer, Paint paintFriendly, Paint paintHostile, double sweeperRotation, boolean inPowerSaveMode) {
            if (canvasCenter == null || bearing == -1 || distance == -1) {
                return;
            }

            // Get canvas information
            Rect canvasSize = canvas.getClipBounds();
            Point canvasCenter = new Point(canvasSize.centerX(), canvasSize.centerY());

            // Converts bearing in meters to bearing between 0 - 2π
            double direction = bearing * RADIAL_MULTIPLIER;

            // Converts distance in meters to distance in pixels from the center.
            double distance = Math.min(MAX_DISTANCE, this.distance) * (canvasSize.height() * .45f / MAX_DISTANCE);

            Point location = new Point(
                canvasCenter.x + (int) Math.round(Math.sin(direction) * distance),
                canvasCenter.y - (int) Math.round(Math.cos(direction) * distance)
            );

            Paint paint = new Paint((perspectivePlayer.team == player.team) ? paintFriendly : paintHostile);

            if (!inPowerSaveMode) {
                double rotationAlpha = sweeperRotation - bearing;

                if (rotationAlpha < 0) {
                    rotationAlpha += 360;
                }

                double alphaMultiplier = Math.cos(HALF_PI * Math.min(1, (rotationAlpha / 200f)) + HALF_PI) + 1;
                double alphaBase = 0d;//paint.getAlpha() / 255f * 150f;
                int alpha = (int) Math.round(alphaBase + (255 - alphaBase) * alphaMultiplier);
                paint.setAlpha(alpha);
            }

            canvas.drawCircle(location.x, location.y, CIRCLE_SIZE, paint);
        }
    }
}

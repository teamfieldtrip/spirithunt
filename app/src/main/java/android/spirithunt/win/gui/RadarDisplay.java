package android.spirithunt.win.gui;

import android.content.Context;
import android.graphics.PixelFormat;
import android.spirithunt.win.controller.RadarRenderController;
import android.spirithunt.win.model.Player;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * Handles updating the radar
 *
 * @author Roelof Roos [github@roelof.io]
 */

public class RadarDisplay extends SurfaceView {

    private static String TAG = "RadarDisplay";

    protected RadarRenderController renderController;

    /**
     * Current player
     */
    protected Player activePlayer;

    /**
     * List of players
     */
    private ArrayList<Player> playerList;

    public RadarDisplay(Context context) {
        super(context);
        setup();
    }

    public RadarDisplay(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setup();
    }

    private void setup() {
        // Required for transparency
        setZOrderOnTop(true);

        // Try harder to get transparent
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);

        // Add handler for the renderController
        renderController = new RadarRenderController(this);
    }

    public void setActivePlayer(Player activePlayer) {
        this.activePlayer = activePlayer;
    }

    public void setPlayerList(ArrayList<Player> playerList) {
        this.playerList = playerList;
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    public ArrayList<Player> getPlayerList() {
        return playerList;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        Log.d(TAG, "onAttachedToWindow: Starting and attaching RenderController...");
        renderController.start();
        getHolder().addCallback(renderController);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        Log.d(TAG, "onDetachedFromWindow: Detaching and stopping RenderController");
        getHolder().removeCallback(renderController);
        renderController.interrupt();
    }
}

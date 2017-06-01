package win.spirithunt.android.gui;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

import win.spirithunt.android.controller.GameController;
import win.spirithunt.android.controller.RadarRenderController;
import win.spirithunt.android.model.Player;

/**
 * Handles updating the radar
 *
 * @author Roelof Roos [github@roelof.io]
 */

public class RadarDisplay extends SurfaceView {

    private static String TAG = "RadarDisplay";

    protected boolean isUpdated;

    protected RadarRenderController renderController;

    /**
     * Current player
     */
    protected Player activePlayer = new Player("");

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
        renderController = new RadarRenderController(this, GameController.TEAM_BLUE);
    }

    public void setUpdateState(boolean bool) {
        this.isUpdated = bool;
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(Player activePlayer) {
        this.activePlayer = activePlayer;
        renderController.setTeam(activePlayer.team);
    }

    public ArrayList<Player> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(ArrayList<Player> playerList) {
        this.playerList = playerList;
    }

    public boolean getUpdateState() {
        return isUpdated;
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

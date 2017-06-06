package win.spirithunt.android.controller;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import io.socket.client.Ack;
import io.socket.client.Socket;
import win.spirithunt.android.R;
import win.spirithunt.android.callback.PlayerCreateCallback;
import win.spirithunt.android.gui.CustomTextView;
import win.spirithunt.android.model.AmountOfLives;
import win.spirithunt.android.model.AmountOfPlayers;
import win.spirithunt.android.model.AmountOfRounds;
import win.spirithunt.android.model.Duration;
import win.spirithunt.android.model.Player;
import win.spirithunt.android.protocol.GameCreate;
import win.spirithunt.android.provider.PermissionProvider;
import win.spirithunt.android.provider.PlayerProvider;
import win.spirithunt.android.provider.SocketProvider;

/**
 * @author Remco Schipper
 */

public class CreateGameController extends AppCompatActivity implements
    GoogleMap.OnMapClickListener,
    GoogleMap.OnMapLongClickListener,
    GoogleMap.OnMarkerClickListener,
    GoogleMap.OnCameraMoveStartedListener,
    OnMapReadyCallback {
    private ProgressDialog progressDialog;

    /**
     * Stores a list with the possible amount of players
     */
    private ArrayList<AmountOfPlayers> amountOfPlayers;

    /**
     * Stores a list with the possible amount of rounds
     */
    private ArrayList<AmountOfRounds> amountOfRounds;

    /**
     * Stores a list with the possible amount of lives
     */
    private ArrayList<AmountOfLives> amountOfLives;

    private boolean powerUpsEnabled = true;

    private boolean mapWasMoved = false;

    private int amountOfPlayersIndex = 0;

    private int amountOfRoundsIndex = 0;

    private int amountOfLivesIndex = 0;

    private View mainView;

    private GoogleMap map;

    private Circle radiusCircle;

    private Marker centerMarker;

    private Marker borderMarker;

    private LatLng centerLatLng;

    private LatLng borderLatLng;

    private ArrayList<Duration> durations;

    private int timeIndicatorIndex;

    private PermissionProvider permissionProvider;

    private MapFragment mapView;

    public CreateGameController() {
        permissionProvider = PermissionProvider.getInstance();

        this.amountOfPlayers = new ArrayList<>();
        for (int i = 4; i <= 16; i+= 2) {
            this.amountOfPlayers.add(new AmountOfPlayers(i, Integer.toString(i)));
        }

        this.amountOfRounds = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            this.amountOfRounds.add(new AmountOfRounds(i, Integer.toString(i)));
        }

        this.amountOfLives = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            this.amountOfLives.add(new AmountOfLives(i, Integer.toString(i)));
        }

        this.durations = new ArrayList<>();
        for (int i = 10; i <= 60; i+=10) {
            this.durations.add(new Duration(i * 60, Integer.toString(i)));
        }
    }

    private void createCircle() {
        int strokeColor = 0xffff0000;
        int shadeColor = 0x44ff0000;

        float[] dist = new float[1];
        Location.distanceBetween(this.centerLatLng.latitude,
            this.centerLatLng.longitude,
            this.borderLatLng.latitude,
            this.borderLatLng.longitude,
            dist);

        CircleOptions circleOptions = new CircleOptions()
            .center(this.centerLatLng)
            .radius(dist[0])
            .fillColor(shadeColor)
            .strokeColor(strokeColor)
            .strokeWidth(8);
        this.radiusCircle = map.addCircle(circleOptions);
    }

    private void removeCircle() {
        if (this.radiusCircle != null) {
            this.radiusCircle.remove();
        }

        this.radiusCircle = null;
    }

    private void removeBorderMarker() {
        this.removeCircle();

        if (this.borderMarker != null) {
            this.borderMarker.remove();
        }

        this.borderMarker = null;
    }

    private void setTimeIndicator(int index) {
        if (index > -1 && this.durations.size() > index) {
            this.timeIndicatorIndex = index;

            Duration duration = this.durations.get(this.timeIndicatorIndex);
            CustomTextView view = (CustomTextView) findViewById(R.id.timeIndicator);
            view.setText(duration.getDescription() + " " + this.getString(R.string.create_game_text_time));
        }
    }

    public void close(View view) {
        this.finish();
    }

    public void addTime(View view) {
        this.setTimeIndicator(this.timeIndicatorIndex + 1);
    }

    public void subtractTime(View view) {
        this.setTimeIndicator(this.timeIndicatorIndex - 1);
    }

    public void openAdvanced(View view) {
        Bundle bundle = new Bundle();
        bundle.putInt("playersIndex", this.amountOfPlayersIndex);
        bundle.putInt("roundsIndex", this.amountOfRoundsIndex);
        bundle.putInt("livesIndex", this.amountOfLivesIndex);
        bundle.putBoolean("powerUpsEnabled", this.powerUpsEnabled);
        bundle.putParcelableArrayList("players", this.amountOfPlayers);
        bundle.putParcelableArrayList("rounds", this.amountOfRounds);
        bundle.putParcelableArrayList("lives", this.amountOfLives);

        Intent advancedSettingsIntent = new Intent(this, CreateGameAdvancedController.class);
        advancedSettingsIntent.putExtras(bundle);
        startActivityForResult(advancedSettingsIntent, 1);
    }

    public void submit(View view) {
        if (this.centerLatLng != null && this.borderLatLng != null) {
            this.showProgressDialog();
            final GameCreate gameCreate = new GameCreate(
                this.durations.get(this.timeIndicatorIndex).getTime(),
                this.amountOfPlayers.get(this.amountOfLivesIndex).getAmount(),
                this.amountOfRounds.get(this.amountOfRoundsIndex).getAmount(),
                this.amountOfLives.get(this.amountOfLivesIndex).getAmount(),
                this.powerUpsEnabled,
                this.centerLatLng,
                this.borderLatLng
            );

            final CreateGameController self = this;


            // Create new Player

            PlayerProvider playerProvider = PlayerProvider.getInstance();
            playerProvider.getNewPlayer(new PlayerCreateCallback() {
                @Override
                public void call(String error, Player player) {
                    Socket socket = SocketProvider.getInstance().getConnection();
                    socket.emit("lobby:create", gameCreate, new Ack() {
                        @Override
                        public void call(final Object... args) {
                            self.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    self.hideProgressDialog();

                                    if (args[0] != null || args.length < 2) {
                                        self.showErrorDialog(self);
                                    }else{
                                        Intent intent = new Intent(self, LobbyController.class);
                                        intent.putExtra("lobbyId", args[1].toString());
                                        intent.putExtra("lobbyHost", true);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                    });
                }
            });
        } else {
            new AlertDialog.Builder(this, R.style.AppDialog)
                .setTitle(getString(R.string.create_game_no_area_title))
                .setMessage(getString(R.string.create_game_no_area_content))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            this.amountOfPlayersIndex = data.getIntExtra("playersIndex", 0);
            this.amountOfRoundsIndex = data.getIntExtra("roundsIndex", 0);
            this.amountOfLivesIndex = data.getIntExtra("livesIndex", 0);
            this.powerUpsEnabled = data.getBooleanExtra("powerUpsEnabled", true);
        }
    }

    /**
     * Asks the OS for camera access
     */
    protected void askForLocationAccess() {
        // Check if we already have permission to use it
        if(permissionProvider.hasPermission(this, PermissionProvider.Permissions.LOCATION)) return;

        // Check if we should explain why we're asking, cancel if we do.
        if(permissionProvider.shouldShowRationale(this, PermissionProvider.Permissions.LOCATION)) return;

        // Request location access
        permissionProvider.requestPermission(this, PermissionProvider.Permissions.LOCATION);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_game_view);

        // Get the container

        FragmentManager manager = getFragmentManager();
        mapView = (MapFragment) manager.findFragmentById(R.id.map);
        mapView.getMapAsync(this);

        // Set defaults
        setTimeIndicator(0);

        // TODO Add question support!
        askForLocationAccess();
    }

    /**
     * Moves the map to the player position or The Netherlands if no location is available. Handles
     * displaying of the "Your location" button.
     */
    @SuppressWarnings("MissingPermission")
    private void updateMapPosition() {
        if (map == null) return;

        boolean hasPerm = permissionProvider.hasPermission(this, PermissionProvider.Permissions.LOCATION);
        map.setMyLocationEnabled(hasPerm);

        if (mapWasMoved) return;

        if (!hasPerm) {
            LatLng goalPos = new LatLng(52.132633, 5.2912659999999505);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(goalPos, 8f));
        }

    }

    /**
     * Assign some properties on the map.
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Disable some display types
        googleMap.setIndoorEnabled(false);
        googleMap.setTrafficEnabled(false);

        // Sets the map type
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Add listeners
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMapLongClickListener(this);
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnCameraMoveStartedListener(this);

        // Assign to self
        map = googleMap;

        // Update map location
        updateMapPosition();
    }

    /**
     * Handles long presses on the map.
     *
     * TODO lower complexity of this method.
     * @param point
     */
    @Override
    public void onMapLongClick(LatLng point) {
        if (centerMarker == null) {
            MarkerOptions markerOptions = new MarkerOptions()
                .position(point)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            centerMarker = map.addMarker(markerOptions);
            centerLatLng = point;
        } else {
            float[] dist = new float[1];
            Location.distanceBetween(centerLatLng.latitude,
                this.centerLatLng.longitude,
                point.latitude,
                point.longitude,
                dist);

            if (dist[0] < 10000) {
                this.removeBorderMarker();

                MarkerOptions markerOptions = new MarkerOptions().position(point).alpha(0.0f);
                this.borderMarker = map.addMarker(markerOptions);
                this.borderLatLng = point;

                this.createCircle();
            } else {
                Toast.makeText(getApplicationContext(),
                    getString(R.string.create_game_area_too_large), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Handles clicking on markers, which removes the border by default, unless the centerMarker is
     * clicked.
     *
     * @return true, always
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        this.removeBorderMarker();

        if (marker.equals(centerMarker)) {
            centerMarker.remove();
            centerMarker = null;
        }

        return true;
    }

    /**
     * Show an instruction when clicking on the map, to inform the user what to do.
     *
     * @param point
     */
    @Override
    public void onMapClick(LatLng point) {
        Toast.makeText(getApplicationContext(),
            getString(R.string.create_game_select_location), Toast.LENGTH_LONG).show();
    }

    /**
     * Mark the map as manually moved when the user does so. Prevents the map from moving when a
     * location update is received.
     *
     * @param reason Reason the camera started moving, OnCameraMoveStartedListener constant.
     */
    @Override
    public void onCameraMoveStarted(int reason) {
        if (reason != GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION) {
            mapWasMoved = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode != PermissionProvider.getPermissionId(PermissionProvider.Permissions.LOCATION)) return;

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            updateMapPosition();
        }
    }

    /**
     * Hides the progress bar.
     */
    private void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * Shows a progress dialog
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this, R.style.AppDialog);
            progressDialog.setTitle(getString(R.string.create_game_progress_title));
            progressDialog.setMessage(getString(R.string.create_game_progress_content));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    /**
     * Shows error messages when stuff is done incorrectly.
     *
     * @param context
     */
    private void showErrorDialog(Context context) {
        new android.app.AlertDialog.Builder(context, R.style.AppDialog)
            .setTitle(getString(R.string.create_game_alert_title))
            .setMessage(getString(R.string.create_game_alert_content))
            .setNeutralButton(R.string.create_game_alert_button, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // do nothing
                }
            })
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
    }
}

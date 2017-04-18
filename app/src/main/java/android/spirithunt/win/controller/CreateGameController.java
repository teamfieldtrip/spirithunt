package android.spirithunt.win.controller;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.spirithunt.win.R;
import android.spirithunt.win.gui.CustomTextView;
import android.spirithunt.win.model.AmountOfLifes;
import android.spirithunt.win.model.AmountOfPlayers;
import android.spirithunt.win.model.AmountOfRounds;
import android.spirithunt.win.model.Duration;
import android.spirithunt.win.protocol.GameCreate;
import android.spirithunt.win.provider.SocketProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

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

/**
 * @author Remco Schipper
 */

public class CreateGameController extends AppCompatActivity implements
    GoogleMap.OnMapClickListener,
    GoogleMap.OnMapLongClickListener,
    GoogleMap.OnMarkerClickListener {
    private ArrayList<AmountOfPlayers> amountOfPlayers;
    private ArrayList<AmountOfRounds> amountOfRounds;
    private ArrayList<AmountOfLifes> amountOfLifes;
    private boolean powerUpsEnabled = true;
    private int amountOfPlayersIndex = 0;
    private int amountOfRoundsIndex = 0;
    private int amountOfLifesIndex = 0;

    private GoogleMap map;
    private Circle radiusCircle;
    private Marker centerMarker;
    private Marker borderMarker;
    private LatLng centerLatLng;
    private LatLng borderLatLng;
    private ArrayList<Duration> durations;
    private int timeIndicatorIndex;

    public CreateGameController() {
        this.amountOfPlayers = new ArrayList<>();
        this.amountOfPlayers.add(new AmountOfPlayers(4, "4"));
        this.amountOfPlayers.add(new AmountOfPlayers(6, "6"));
        this.amountOfPlayers.add(new AmountOfPlayers(8, "8"));
        this.amountOfPlayers.add(new AmountOfPlayers(10, "10"));
        this.amountOfPlayers.add(new AmountOfPlayers(12, "12"));
        this.amountOfPlayers.add(new AmountOfPlayers(14, "16"));
        this.amountOfPlayers.add(new AmountOfPlayers(16, "16"));

        this.amountOfRounds = new ArrayList<>();
        this.amountOfRounds.add(new AmountOfRounds(1, "1"));
        this.amountOfRounds.add(new AmountOfRounds(2, "2"));
        this.amountOfRounds.add(new AmountOfRounds(3, "3"));
        this.amountOfRounds.add(new AmountOfRounds(4, "4"));
        this.amountOfRounds.add(new AmountOfRounds(5, "5"));
        this.amountOfRounds.add(new AmountOfRounds(6, "6"));
        this.amountOfRounds.add(new AmountOfRounds(7, "7"));
        this.amountOfRounds.add(new AmountOfRounds(8, "8"));
        this.amountOfRounds.add(new AmountOfRounds(9, "9"));
        this.amountOfRounds.add(new AmountOfRounds(10, "10"));

        this.amountOfLifes = new ArrayList<>();
        this.amountOfLifes.add(new AmountOfLifes(1, "1"));
        this.amountOfLifes.add(new AmountOfLifes(2, "2"));
        this.amountOfLifes.add(new AmountOfLifes(3, "3"));
        this.amountOfLifes.add(new AmountOfLifes(4, "4"));
        this.amountOfLifes.add(new AmountOfLifes(5, "5"));
        this.amountOfLifes.add(new AmountOfLifes(6, "6"));
        this.amountOfLifes.add(new AmountOfLifes(7, "7"));
        this.amountOfLifes.add(new AmountOfLifes(8, "8"));
        this.amountOfLifes.add(new AmountOfLifes(9, "9"));

        this.durations = new ArrayList<>();
        this.durations.add(new Duration(600, "10"));
        this.durations.add(new Duration(1200, "20"));
        this.durations.add(new Duration(1800, "30"));
        this.durations.add(new Duration(2400, "40"));
        this.durations.add(new Duration(3000, "50"));
        this.durations.add(new Duration(3600, "60"));
    }

    private void createCircle() {
        int strokeColor = 0xffff0000;
        int shadeColor = 0x44ff0000;

        float [] dist = new float[1];
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
        if(this.radiusCircle != null) {
            this.radiusCircle.remove();
        }

        this.radiusCircle = null;
    }

    private void removeBorderMarker() {
        this.removeCircle();

        if(this.borderMarker != null) {
            this.borderMarker.remove();
        }

        this.borderMarker = null;
    }

    private void setTimeIndicator(int index) {
        if(index > -1 && this.durations.size() > index) {
            this.timeIndicatorIndex = index;

            Duration duration = this.durations.get(this.timeIndicatorIndex);
            CustomTextView view = (CustomTextView)findViewById(R.id.timeIndicator);
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
        Intent advancedSettingsIntent = new Intent(this, CreateGameAdvancedController.class);

        Bundle bundle = new Bundle();
        bundle.putInt("playersIndex", this.amountOfPlayersIndex);
        bundle.putInt("roundsIndex", this.amountOfRoundsIndex);
        bundle.putInt("lifesIndex", this.amountOfLifesIndex);
        bundle.putBoolean("powerUpsEnabled", this.powerUpsEnabled);
        bundle.putParcelableArrayList("players", this.amountOfPlayers);
        bundle.putParcelableArrayList("rounds", this.amountOfRounds);
        bundle.putParcelableArrayList("lifes", this.amountOfLifes);
        advancedSettingsIntent.putExtras(bundle);

        startActivityForResult(advancedSettingsIntent, 1);
    }

    public void submit(View view) {
        if (this.centerLatLng != null && this.borderLatLng != null) {
            GameCreate gameCreate = new GameCreate(
                this.durations.get(this.timeIndicatorIndex).getTime(),
                this.amountOfPlayers.get(this.amountOfLifesIndex).getAmount(),
                this.amountOfRounds.get(this.amountOfRoundsIndex).getAmount(),
                this.amountOfLifes.get(this.amountOfLifesIndex).getAmount(),
                this.powerUpsEnabled,
                this.centerLatLng.latitude,
                this.centerLatLng.longitude,
                this.borderLatLng.latitude,
                this.borderLatLng.longitude
            );

            Socket socket = SocketProvider.getInstance().getConnection();
            socket.emit("lobby:create", gameCreate, new Ack() {
                @Override
                public void call(Object... args) {

                }
            });
        }
        else {
            new AlertDialog.Builder(this)
                .setTitle("Hello Infidel")
                .setMessage("Please select the area for your game")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                this.amountOfPlayersIndex = data.getIntExtra("playersIndex", 0);
                this.amountOfRoundsIndex = data.getIntExtra("roundsIndex", 0);
                this.amountOfLifesIndex = data.getIntExtra("lifesIndex", 0);
                this.powerUpsEnabled = data.getBooleanExtra("powerUpsEnabled", true);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_game_view);

        FragmentManager manager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) manager.findFragmentById(R.id.map);

        final CreateGameController self = this;
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                try {
                    googleMap.setMyLocationEnabled(true);
                }
                catch (SecurityException e) {
                    System.out.println(e.getMessage());
                }
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                googleMap.setOnMapClickListener(self);
                googleMap.setOnMapLongClickListener(self);
                googleMap.setOnMarkerClickListener(self);
                self.map = googleMap;
            }
        });

        this.setTimeIndicator(0);
    }

    @Override
    public void onMapLongClick(LatLng point)
    {
        if(this.centerMarker == null) {
            MarkerOptions markerOptions = new MarkerOptions()
                .position(point)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            this.centerMarker = map.addMarker(markerOptions);
            this.centerLatLng = point;
        }
        else {
            float [] dist = new float[1];
            Location.distanceBetween(this.centerLatLng.latitude,
                this.centerLatLng.longitude,
                point.latitude,
                point.longitude,
                dist);

            if(dist[0] < 10000) {
                this.removeBorderMarker();

                MarkerOptions markerOptions = new MarkerOptions().position(point).alpha(0.0f);
                this.borderMarker = map.addMarker(markerOptions);
                this.borderLatLng = point;

                this.createCircle();
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker)
    {
        if(marker.equals(this.centerMarker)) {
            this.removeBorderMarker();

            this.centerMarker.remove();
            this.centerMarker = null;
        }
        else {
            this.removeBorderMarker();
        }

        return true;
    }

    @Override
    public void onMapClick(LatLng point)
    {
        Toast.makeText(getApplicationContext(),
            "Long Press to select locations", Toast.LENGTH_LONG).show();
    }
}
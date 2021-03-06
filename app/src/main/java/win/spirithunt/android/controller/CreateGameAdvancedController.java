package win.spirithunt.android.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import win.spirithunt.android.R;
import win.spirithunt.android.gui.CustomTextView;
import win.spirithunt.android.model.AmountOfLives;
import win.spirithunt.android.model.AmountOfPlayers;
import win.spirithunt.android.model.AmountOfRounds;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;

/**
 * @author Remco Schipper
 */

public class CreateGameAdvancedController extends AppCompatActivity {
    private ArrayList<AmountOfPlayers> amountOfPlayers;

    private ArrayList<AmountOfRounds> amountOfRounds;

    private ArrayList<AmountOfLives> amountOfLives;

    private int amountOfPlayersIndex;

    private int amountOfRoundsIndex;

    private int amountOfLivesIndex;

    private boolean powerUpsEnabled;

    public CreateGameAdvancedController() {
        this.amountOfPlayers = new ArrayList<>();
        this.amountOfRounds = new ArrayList<>();
        this.amountOfLives = new ArrayList<>();
    }

    private void setAmountOfPlayers(int index) {
        if(index > -1 && this.amountOfPlayers.size() > index) {
            this.amountOfPlayersIndex = index;

            AmountOfPlayers playerAmount = this.amountOfPlayers.get(this.amountOfPlayersIndex);
            CustomTextView view = (CustomTextView)findViewById(R.id.amount_of_players);
            view.setText(playerAmount.getDescription());
        }
    }

    private void setAmountOfRounds(int index) {
        if(index > -1 && this.amountOfRounds.size() > index) {
            this.amountOfRoundsIndex = index;

            AmountOfRounds amountOfRounds = this.amountOfRounds.get(this.amountOfRoundsIndex);
            CustomTextView view = (CustomTextView)findViewById(R.id.amount_of_rounds);
            view.setText(amountOfRounds.getDescription());
        }
    }

    private void setAmountOfLives(int index) {
        if(index > -1 && this.amountOfLives.size() > index) {
            this.amountOfLivesIndex = index;

            AmountOfLives amountOfLives = this.amountOfLives.get(this.amountOfLivesIndex);
            CustomTextView view = (CustomTextView)findViewById(R.id.amount_of_lives);
            view.setText(amountOfLives.getDescription());
        }
    }

    private void setPowerUpsEnabled(boolean enabled) {
        this.powerUpsEnabled = enabled;

        CustomTextView view = (CustomTextView)findViewById(R.id.power_ups_enabled);
        view.setText(this.powerUpsEnabled ? "On" : "Off");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_game_advanced_view);
        Bundle extras = getIntent().getExtras();

        this.amountOfPlayers  = extras.getParcelableArrayList("players");
        this.amountOfRounds  = extras.getParcelableArrayList("rounds");
        this.amountOfLives  = extras.getParcelableArrayList("lives");

        this.setAmountOfPlayers(extras.getInt("playersIndex"));
        this.setAmountOfRounds(extras.getInt("roundsIndex"));
        this.setAmountOfLives(extras.getInt("livesIndex"));
        this.setPowerUpsEnabled(extras.getBoolean("powerUpsEnabled"));
    }

    public void addAmountOfPlayers(View view) {
        this.setAmountOfPlayers(this.amountOfPlayersIndex + 1);
    }

    public void subtractAmountOfPlayers(View view) {
        this.setAmountOfPlayers(this.amountOfPlayersIndex - 1);
    }

    public void addAmountOfRounds(View view) {
        this.setAmountOfRounds(this.amountOfRoundsIndex + 1);
    }

    public void subtractAmountOfRounds(View view) {
        this.setAmountOfRounds(this.amountOfRoundsIndex - 1);
    }

    public void addAmountOfLives(View view) {
        this.setAmountOfLives(this.amountOfLivesIndex + 1);
    }

    public void subtractAmountOfLives(View view) {
        this.setAmountOfLives(this.amountOfLivesIndex - 1);
    }

    public void enablePowerUps(View view) {
        this.setPowerUpsEnabled(true);
    }

    public void disablePowerUps(View view) {
        this.setPowerUpsEnabled(false);
    }

    public void close(View view) {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        this.finish();
    }

    public void closeWithResult(View view) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("playersIndex", this.amountOfPlayersIndex);
        returnIntent.putExtra("roundsIndex", this.amountOfRoundsIndex);
        returnIntent.putExtra("livesIndex", this.amountOfLivesIndex);
        returnIntent.putExtra("powerUpsEnabled", this.powerUpsEnabled);

        this.setResult(Activity.RESULT_OK, returnIntent);
        this.finish();
    }
}

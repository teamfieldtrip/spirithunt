package android.spirithunt.win.protocol;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Remco Schipper
 */

public class GameCreate extends JSONObject {
    public GameCreate(int duration,
                      int amountOfPlayers,
                      int amountOfRounds,
                      int amountOfLifes,
                      boolean powerUpsEnabled,
                      double centerLatitude,
                      double centerLongitude,
                      double borderLatitude,
                      double borderLongitude) {
        try {
            this.put("duration", duration);
            this.put("amountOfPlayers", amountOfPlayers);
            this.put("amountOfRounds", amountOfRounds);
            this.put("amountOfLifes", amountOfLifes);
            this.put("powerUpsEnabled", powerUpsEnabled);
            this.put("centerLatitude", centerLatitude);
            this.put("centerLongitude", centerLongitude);
            this.put("borderLongitude", borderLatitude);
            this.put("borderLatitude", borderLongitude);
        } catch(JSONException e) {
            System.out.println(e.getMessage());
        }
    }
}

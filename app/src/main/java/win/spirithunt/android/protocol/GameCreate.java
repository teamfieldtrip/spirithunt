package win.spirithunt.android.protocol;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Remco Schipper
 */

public class GameCreate extends JSONObject {
    public GameCreate(int duration,
                      int amountOfPlayers,
                      int amountOfRounds,
                      int amountOfLives,
                      boolean powerUpsEnabled,
                      LatLng center,
                      LatLng border) {
        try {
            this.put("duration", duration);
            this.put("amountOfPlayers", amountOfPlayers);
            this.put("amountOfRounds", amountOfRounds);
            this.put("amountOfLives", amountOfLives);
            this.put("powerUpsEnabled", powerUpsEnabled);
            this.put("centerLatitude", center.latitude);
            this.put("centerLongitude", center.longitude);
            this.put("borderLongitude", border.latitude);
            this.put("borderLatitude", border.longitude);
        } catch(JSONException e) {
            System.out.println(e.getMessage());
        }
    }
}

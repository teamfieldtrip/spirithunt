package android.spirithunt.win.protocol;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Remco Schipper
 */

public class GpsUpdate extends JSONObject {
    public GpsUpdate(double latitude, double longitude, Long time) {
        try {
            this.put("latitude", latitude);
            this.put("longitude", longitude);
            this.put("time", time);
        } catch(JSONException e) {
            System.out.println(e.getMessage());
        }
    }
}

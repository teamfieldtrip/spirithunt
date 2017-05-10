package win.spirithunt.android.protocol;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Remco Schipper
 */

public class LobbyInfo extends JSONObject {
    public LobbyInfo(String id) {
        try {
            this.put("id", id);
        } catch(JSONException e) {
            System.out.println(e.getMessage());
        }
    }
}

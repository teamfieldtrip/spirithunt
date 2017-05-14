package win.spirithunt.android.protocol;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sven on 14-5-17.
 */

public class GameTag extends JSONObject {
    public GameTag(String playerId, String targetId) {
        try {
            this.put("playerId", playerId);
            this.put("targetId", targetId);
        } catch(JSONException e) {
            System.out.println(e.getMessage());
        }
    }
}

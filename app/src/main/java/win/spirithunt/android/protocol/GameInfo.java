package win.spirithunt.android.protocol;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sven on 16-5-17.
 */

public class GameInfo extends JSONObject{
    public GameInfo(String gameId) {
        try {
            this.put("gameId", gameId);
        } catch(JSONException e) {
            System.out.println(e.getMessage());
        }
    }
}

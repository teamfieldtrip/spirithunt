package android.spirithunt.win.protocol;

import android.spirithunt.win.model.Player;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Remco Schipper
 */

public class PlayerResume extends JSONObject {
    public PlayerResume(String id) {
        try {
            this.put("id", id);
        }
        catch(JSONException e) {
            System.out.println(e.getMessage());
        }
    }
    public PlayerResume(Player player) {
        this(player.getId());
    }
}

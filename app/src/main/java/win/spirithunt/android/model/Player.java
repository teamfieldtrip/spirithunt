package win.spirithunt.android.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InvalidObjectException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Remco Schipper
 */

public class Player implements Serializable {
    private static final String FIELD_ID = "id";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_TEAM = "team";
    private static final String FIELD_LAT = "latitude";
    private static final String FIELD_LNG = "longitude";
    private static final String FIELD_SCORE = "score";
    private static final String FIELD_TARGET = "target";
    private static final String FIELD_RESULT = "result";

    public String name;
    public double latitude;
    public double longitude;
    public int score;
    public String target;
    public int team;
    public int result;

    private String id;

    /**
     * Builds a player object from JSON.
     * @param object JSON player
     * @return Player
     * @throws JSONException
     */
    public static Player FromJson(JSONObject object) throws InvalidObjectException {
        if (!object.has(FIELD_ID)) {
            throw new InvalidObjectException("Player doesn't have an ID");
        }
        Player out = new Player(object.optString(FIELD_ID));
        out.name = object.optString(FIELD_NAME, "Unnamed Player");
        out.team = object.optInt(FIELD_TEAM);
        out.latitude = object.optDouble(FIELD_LAT);
        out.longitude = object.optDouble(FIELD_LNG);
        out.score = object.optInt(FIELD_SCORE);
        out.target = object.optString(FIELD_TARGET);
        out.result = object.optInt(FIELD_RESULT, 0);
        return out;
    }

    /**
     * Convers a list of players to a list of Player objects.
     * @param array
     * @return
     */
    public static Player[] FromJsonArray(JSONArray array) {
        // Return empty array if feed is empty.
        if (array.length() == 0) {
            return new Player[0];
        }

        // Create an array list
        ArrayList<Player> players = new ArrayList<>();
        players.ensureCapacity(array.length());

        JSONObject subnode;
        for (int i = 0; i < array.length(); i++) {
            subnode = array.optJSONObject(i);
            if (subnode == null) continue;
            try {
                Player player = FromJson(subnode);
                players.add(player);
            } catch(InvalidObjectException e) {
                // Intentionally kept empty
            }
        }

        return players.toArray(new Player[players.size()]);
    }

    public Player(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public int getTeam() {
        return this.team;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return this.score;
    }

    public String toString() {
        return this.name;
    }
}

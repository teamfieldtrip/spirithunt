package android.spirithunt.win.protocol;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Remco Schipper
 */

public class AuthToken extends JSONObject {
    public AuthToken(String token) {
        try {
            this.put("token", token);
        } catch(JSONException e) {
            System.out.println(e.getMessage());
        }
    }
}

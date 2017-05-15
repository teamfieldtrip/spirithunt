package win.spirithunt.android.protocol;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Remco Schipper
 */

public class AuthLogout extends JSONObject {
    public AuthLogout(String token) {
        try {
            this.put("token", token);
        } catch(JSONException e) {
            System.out.println(e.getMessage());
        }
    }
}

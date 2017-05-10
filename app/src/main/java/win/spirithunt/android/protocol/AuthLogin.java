package win.spirithunt.android.protocol;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Remco Schipper
 */

public class AuthLogin extends JSONObject {
    public AuthLogin(String email, String password) {
        try {
            this.put("email", email);
            this.put("password", password);
        } catch(JSONException e) {
            System.out.println(e.getMessage());
        }
    }
}

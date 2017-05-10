package win.spirithunt.android.protocol;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Contains information to save a user in the database
 *
 * @author Roelof Roos
 */

public class AuthRegister extends JSONObject {
    public AuthRegister(String name, String email, String password) {
        try {
            put("name", name);
            put("email", email);
            put("password", password);
        } catch(JSONException e) {
            System.out.println(e.getMessage());
        }
    }
}

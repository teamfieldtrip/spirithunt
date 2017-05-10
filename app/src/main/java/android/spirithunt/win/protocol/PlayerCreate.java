package android.spirithunt.win.protocol;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Remco Schipper
 */

public class PlayerCreate extends JSONObject {
    public PlayerCreate(String account){
        try{
            this.put("account",account);
        } catch(JSONException e) {
            System.out.println(e.getMessage());
        }
    }
}

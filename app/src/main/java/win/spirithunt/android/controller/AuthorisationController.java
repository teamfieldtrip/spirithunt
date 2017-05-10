package win.spirithunt.android.controller;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import win.spirithunt.android.R;
import android.support.v7.app.AppCompatActivity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains shared methods for Login and Sign up forms.
 *
 * @author Roelof Roos
 */

abstract public class AuthorisationController extends AppCompatActivity {
    private ProgressDialog progressDialog;

    /**
     * Contains error messages for registration and sign up
     */
    protected static final Map<String, Integer> errorDictionary;

    /**
     * Initialise the errorDictionary with the right fields
     */
    static {
        Map<String, Integer> errorMap = new HashMap<>();
        errorMap.put("wrong-combination", R.string.auth_error_wrong_combination);
        errorMap.put("empty-name", R.string.auth_error_name_empty);
        errorMap.put("short-name", R.string.auth_error_name_short);
        errorMap.put("empty-pass", R.string.auth_error_password_empty);
        errorMap.put("short-pass", R.string.auth_error_password_short);
        errorMap.put("empty-mail", R.string.auth_error_email_empty);
        errorMap.put("invalid-email", R.string.auth_error_email_invalid);
        errorMap.put("taken-email", R.string.auth_error_email_taken);

        errorDictionary = Collections.unmodifiableMap(errorMap);
    }

    /**
     * Closes an open progress dialog, if any.
     */
    protected void hideProgressDialog() {
        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    /**
     * Opens a progress dialog, specify the title and body. The progress is non-cancellable.
     *
     * @param title Title of the dialog
     * @param body Body of the dialog
     */
    protected void showProgressDialog(String title, String body) {
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(this, R.style.AppDialog);
            progressDialog.setTitle(title);
            progressDialog.setMessage(body);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    /**
     * Shows an alert for failure to login or w/e
     *
     * @param title Title of alert
     * @param content Message of alert
     * @param button Content of dismiss button
     */
    protected void showErrorDialog(String title, String content, String button) {
        new AlertDialog.Builder(this, R.style.AppDialog)
            .setTitle(title)
            .setMessage(content)
            .setCancelable(true)
            .setPositiveButton(button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // do nothing
                }
            })
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
    }

    /**
     * Goes to the main menu, make sure you've stored a JWT at this point!
     *
     * @param context
     */
    protected void showMainMenu(Context context) {
        Intent intent = new Intent(context, MenuController.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Saves a JSON Web Token in the SharedPreferences, which is used to sign in the next time the
     * app is opened.
     *
     * @param token
     */
    protected void saveJwt(String token) {
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.saved_jwt), token);
        editor.commit();
    }

    /**
     * Gets a message from the dictionary, or returns the original message if not found.
     *
     * @param message
     * @return
     */
    protected String getErrorFromDictionary(String message) {
        if (errorDictionary.containsKey(message)) {
            return getString(errorDictionary.get(message));
        } else {
            return message;
        }
    }
}

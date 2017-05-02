package android.spirithunt.win.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.spirithunt.win.R;
import android.support.v7.app.AppCompatActivity;

/**
 * Contains shared methods for Login and Sign up forms.
 *
 * @author Roelof Roos
 */

abstract public class AuthorisationController extends AppCompatActivity {
    private ProgressDialog progressDialog;

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
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(title);
            progressDialog.setMessage(body);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    /**
     * Goes to the main menu, make sure you've stored a JWT at this point!
     *
     * @param context
     */
    protected void showMainMenu(Context context) {
        Intent intent = new Intent(context, MenuController.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
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
}

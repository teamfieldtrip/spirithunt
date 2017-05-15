package win.spirithunt.android.provider;

import android.app.Activity;
import android.app.ProgressDialog;

import win.spirithunt.android.R;

/**
 * Creates dialogs, such as progress dialgs.
 *
 * @author Roelof Roos
 */

public class DialogProvider {

    private ProgressDialog progressDialog;

    private Activity activity;

    public DialogProvider(Activity activity) {
        this.activity = activity;
    }

    /**
     * Returns if a dialog is visible.
     * @return
     */
    public boolean isProgressDialogOpen() {
        return progressDialog != null;
    }

    /**
     * Closes an open progress dialog, if any.
     */
    public void hideProgressDialog() {
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
    public void showProgressDialog(String title, String body) {
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(activity, R.style.AppDialog);
            progressDialog.setTitle(title);
            progressDialog.setMessage(body);
            progressDialog.setIndeterminate(true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    /**
     *
     * @param title
     * @param body
     */
    public void showProgressDialog(int title, int body) {
        showProgressDialog(activity.getString(title), activity.getString(body));
    }
}


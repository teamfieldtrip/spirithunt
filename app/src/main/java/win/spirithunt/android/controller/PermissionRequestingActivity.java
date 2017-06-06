package win.spirithunt.android.controller;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import win.spirithunt.android.provider.PermissionProvider;

/**
 * @author Roelof Roos
 */

abstract class PermissionRequestingActivity extends AppCompatActivity {
    private static final String TAG = PermissionRequestingActivity.class.getSimpleName();
    protected static final PermissionProvider permissionProvider = PermissionProvider.getInstance();

    /**
     * Quick permission check
     *
     * @param permission
     * @return
     */
    protected boolean hasPermission(PermissionProvider.Permissions permission) {
        return permissionProvider.hasPermission(this, permission);
    }

    /**
     * Requests a permission, and might invoke {@link #showPermissionRationale(PermissionProvider.Permissions)}
     * in case the OS recommends to show a rationale why the permission is required.
     *
     * After the permission is granted, the {@link #onPermissionGranted(PermissionProvider.Permissions)}
     * method is invoked and the app may proceed at will. If the permission was already granted, the
     * {@link #onPermissionGranted(PermissionProvider.Permissions)} method is invoked instantly.
     *
     * @param permission    The permission to request, as a value from the enum.
     * @param gaveRationale Set to true when you've explained why you need the permission, otherwise
     *                      you'd end up in a loop.
     */
    protected void requestPermission(PermissionProvider.Permissions permission, boolean gaveRationale) {
        if (hasPermission(permission)) {
            Log.d(TAG, String.format("requestPermission: Permission %s has already been granted, continuing", permission.name()));
            onPermissionGranted(permission);
            return;
        }
        if(!gaveRationale && permissionProvider.shouldShowRationale(this, permission)) {
            Log.d(TAG, String.format("requestPermission: Requesting rationale explanation for %s...", permission.name()));
            showPermissionRationale(permission);
            return;
        }

        Log.d(TAG, String.format("requestPermission: Requesting permission for %s...", permission.name()));
        permissionProvider.requestPermission(this, permission);
    }

    /**
     * Returns after the user has granted or declined the requested permission. If the permission
     * was granted, the {@link #onPermissionGranted(PermissionProvider.Permissions)} method is
     * invoked and the app may continue at will
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        PermissionProvider.Permissions permission = null;

        // Loop through all available permissions until we found a match.
        for (PermissionProvider.Permissions perm: PermissionProvider.Permissions.values()) {
            if (requestCode == PermissionProvider.getPermissionId(perm)) {
                permission = perm;
                break;
            }
        }

        // Cancel if we don't recognize the permission.
        if (permission == null) {
            Log.w(TAG, "onRequestPermissionsResult: Received a permission response for an unknown permission...");
            return;
        }

        Log.d(TAG, String.format("onRequestPermissionsResult: Received response for permission %s.", permission.name()));

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onPermissionGranted(permission);
        }
    }

    /**
     * Should open a dialog explaining why the permission is required.
     * Afterwards, call {@link #requestPermission(PermissionProvider.Permissions, boolean)} with
     * {@code gaveRationale} set to {@code true}.
     *
     * @param permission
     */
    public abstract void showPermissionRationale(PermissionProvider.Permissions permission);

    /**
     * Called after the requested permission has been granted.
     *
     * @param permission
     */
    public abstract void onPermissionGranted(PermissionProvider.Permissions permission);
}

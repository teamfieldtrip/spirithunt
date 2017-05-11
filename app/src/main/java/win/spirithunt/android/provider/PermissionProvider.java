package win.spirithunt.android.provider;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Handles prompting for permissions and checking if they're granted or should be explained.
 *
 * @author Sven Boekelder
 * @author Roelof Roos
 */
public class PermissionProvider {
    private static PermissionProvider instance;

    /**
     * Request access to the camera
     */
    public static final int PERMISSION_CAMERA = 1;

    /**
     * Request access to the user's GPS location
     */
    public static final int PERMISSION_LOCATION = 2;

    /**
     * Get the permission provider instance
     *
     * @return Singleton instance of the PermissionProvider
     */
    public static PermissionProvider getInstance() {
        if (instance == null) {
            instance = new PermissionProvider();
        }

        return instance;
    }

    /**
     * Returns the permission name for the given permission constant.
     *
     * @param permission One of the PERMISSION_ constants.
     * @return permission name, or null if invalid.
     */
    private String getPermissionName(int permission) {
        switch (permission) {
            case PERMISSION_LOCATION:
                return Manifest.permission.ACCESS_FINE_LOCATION;
            case PERMISSION_CAMERA:
                return Manifest.permission.CAMERA;
            default:
                return null;
        }
    }

    /**
     * Check if the permission has been granted yet.
     *
     * @param permission PERMISSION_* constant of the permission you want to check
     * @return           Boolean if the permission has been granted.
     */
    public boolean hasPermission(Context context, int permission) {
        String permissionName = getPermissionName(permission);
        return permissionName != null &&
            (ContextCompat.checkSelfPermission(context, permissionName) == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Checks if you should explain why you need the requested permission. Returns TRUE if you
     * should explain WHY you need access to the given system, or false if you can prompt for the
     * permission instantly.
     *
     * <strong>Make sure your permission request is async!</strong>
     *
     * @param activity Used to check if the given activity has asked for access earlier on
     * @param permission PERMISSION_* constant of the permission you'd like to use.
     * @return true if you should explain why you need the permission, false if you can prompt.
     */
    public boolean shouldShowRationale(Activity activity, int permission) {
        String permissionName = getPermissionName(permission);
        if (permissionName == null) {
            return false;
        }

        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionName);
    }

    /**
     * Request a certain permission. Make sure to check if you need to explain why you need this
     * permission if {@link #shouldShowRationale} returns true.
     *
     * This method returns false if the permission can't be requested. <em>It does
     * <strong>not</strong> return true if the permission is granted, just when the request has
     * been sent!</em>
     *
     * @param activity   Current activity to show the request dialog on
     * @param permission Permission that needs to be requested
     * @return false if the permission couldn't be requested, true if the request is made.
     */
    public Boolean requestPermission(Activity activity, int permission) {
        String permissionName = getPermissionName(permission);
        if (permissionName == null) {
            return false;
        }

        ActivityCompat.requestPermissions(
            activity,
            new String[]{permissionName},
            permission
        );

        return true;
    }
}

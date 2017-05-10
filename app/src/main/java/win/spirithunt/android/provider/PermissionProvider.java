package win.spirithunt.android.provider;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by sven on 10-5-17.
 */

// TODO callbacks

public class PermissionProvider {
    private static PermissionProvider instance;

    /**
     * Get the permission provider instance
     *
     * @return {PermissionProvider}
     */
    public static PermissionProvider getInstance() {
        if (instance == null) {
            instance = new PermissionProvider();
        }

        return instance;
    }

    /**
     * Check if the permission has been granted yet
     *
     * @param permission    Permission that needs to be checked
     * @return              Boolean if the permission has been granted yet
     */
    public boolean hasPermission(String permission) {
        Context context = ContextProvider.getInstance().getContext();
        int PERMISSION = 0;
        switch (permission) {
            case "location":
                PERMISSION = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
                break;
            case "camera":
                PERMISSION = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
                break;
            default:
                break;
        }
        return (PERMISSION == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Request a certain permission
     *
     * @param activity      Current activity to show the request dialog on
     * @param permission    Permission that needs to be requested
     */
    public int requestPermission(Activity activity, String permission) {
        int PERMISSION = 0;
        switch (permission) {
            case "location":
                ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION);
                break;
            case "camera":
                ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION);
                break;
            default:
                break;
        }

        return PERMISSION;
    }
}

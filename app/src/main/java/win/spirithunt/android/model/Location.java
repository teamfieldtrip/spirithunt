package win.spirithunt.android.model;

/**
 * Created by roelof on 13/04/17.
 */

public class Location extends android.location.Location {
    /**
     * Builds a Location object from a given latitude and longitude provided by the named provider
     * @param latitude
     * @param longitude
     * @param provider
     * @return
     */
    public static Location build(double latitude, double longitude, String provider) {
        Location res = new Location(provider);
        res.setLatitude(latitude);
        res.setLongitude(longitude);
        return res;
    }

    /**
     * Builds a Location object from a given latitude and longitude. The provider is "user".
     * @param latitude
     * @param longitude
     * @return
     */
    public static Location build(double latitude, double longitude) {
        return build(latitude, longitude, "user");
    }

    public Location(String provider) {
        super(provider);
    }
}

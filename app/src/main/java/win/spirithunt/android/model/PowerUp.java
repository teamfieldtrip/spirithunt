package win.spirithunt.android.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Describes a usable powerup.
 *
 * @author Roelof Roos [github@roelof.io]
 */
public class PowerUp implements Parcelable {
    private String id;
    private String name;
    private boolean persistent;

    private PowerUp(Parcel parcel) {
        this(parcel.readString(), parcel.readString(), parcel.readByte() != 0);
    }

    public PowerUp(String id, String name, boolean persistent) {
        super();
        this.id = id;
        this.name = name;
        this.persistent = persistent;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean getPersistent() {
        return persistent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getId());
        dest.writeString(this.getName());
        dest.writeByte((byte) (this.getPersistent() ? 1 : 0));
    }

    public static final Parcelable.Creator<PowerUp> CREATOR = new Parcelable.Creator<PowerUp>() {
        public PowerUp createFromParcel(Parcel in) {
            return new PowerUp(in);
        }

        public PowerUp[] newArray(int size) {
            return new PowerUp[size];
        }

    };
}

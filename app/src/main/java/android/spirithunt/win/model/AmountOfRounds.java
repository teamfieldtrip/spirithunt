package android.spirithunt.win.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Remco Schipper
 */

public class AmountOfRounds implements Parcelable {
    private int amount;

    private String description;

    private AmountOfRounds(Parcel parcel) {
        this(parcel.readInt(), parcel.readString());
    }

    public AmountOfRounds(int amount, String description) {
        this.amount = amount;
        this.description = description;
    }

    public int getAmount() {
        return this.amount;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.getAmount());
        dest.writeString(this.getDescription());
    }

    public static final Parcelable.Creator<AmountOfRounds> CREATOR = new Parcelable.Creator<AmountOfRounds>() {
        public AmountOfRounds createFromParcel(Parcel in) {
            return new AmountOfRounds(in);
        }

        public AmountOfRounds[] newArray(int size) {
            return new AmountOfRounds[size];
        }

    };
}

package android.spirithunt.win.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Remco Schipper
 */

public class AmountOfPlayers implements Parcelable {
    private int amount;

    private String description;

    private AmountOfPlayers(Parcel parcel) {
        this(parcel.readInt(), parcel.readString());
    }

    public AmountOfPlayers(int amount, String description) {
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

    public static final Parcelable.Creator<AmountOfPlayers> CREATOR = new Parcelable.Creator<AmountOfPlayers>() {
        public AmountOfPlayers createFromParcel(Parcel in) {
            return new AmountOfPlayers(in);
        }

        public AmountOfPlayers[] newArray(int size) {
            return new AmountOfPlayers[size];
        }

    };
}

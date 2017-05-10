package android.spirithunt.win.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Remco Schipper
 */

public class AmountOfLives implements Parcelable {
    private int amount;

    private String description;

    private AmountOfLives(Parcel parcel) {
        this(parcel.readInt(), parcel.readString());
    }

    public AmountOfLives(int amount, String description) {
        super();
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

    public static final Parcelable.Creator<AmountOfLives> CREATOR = new Parcelable.Creator<AmountOfLives>() {
        public AmountOfLives createFromParcel(Parcel in) {
            return new AmountOfLives(in);
        }

        public AmountOfLives[] newArray(int size) {
            return new AmountOfLives[size];
        }

    };
}

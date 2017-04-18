package android.spirithunt.win.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Remco Schipper
 */

public class AmountOfLifes implements Parcelable {
    private int amount;
    private String description;

    private AmountOfLifes(Parcel parcel) {
        this(parcel.readInt(), parcel.readString());
    }

    public AmountOfLifes(int amount, String description) {
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

    public static final Parcelable.Creator<AmountOfLifes> CREATOR = new Parcelable.Creator<AmountOfLifes>() {
        public AmountOfLifes createFromParcel(Parcel in) {
            return new AmountOfLifes(in);
        }

        public AmountOfLifes[] newArray(int size) {
            return new AmountOfLifes[size];
        }

    };
}

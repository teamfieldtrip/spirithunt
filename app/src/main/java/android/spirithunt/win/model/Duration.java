package android.spirithunt.win.model;

/**
 * @author Remco Schipper
 */
public class Duration {
    private int time;

    private String description;

    public Duration(int time, String description) {
        this.time = time;
        this.description = description;
    }

    public int getTime() {
        return this.time;
    }

    public String getDescription() {
        return this.description;
    }
}

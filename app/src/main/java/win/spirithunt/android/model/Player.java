package win.spirithunt.android.model;

/**
 * @author Remco Schipper
 */

public class Player {
    private String id;

    public double latitude;

    public double longitude;

    public int score;

    public String target;

    public int team;

    public int result;

    public Player(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setTeam(int team){
        this.team = team;
    }

    public String toString() {
        return this.id;
    }
}

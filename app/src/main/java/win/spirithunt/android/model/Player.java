package win.spirithunt.android.model;

/**
 * @author Remco Schipper
 */

public class Player {
    public String name;
    public double latitude;
    public double longitude;
    public int score;
    public String target;
    public int team;
    public int result;

    private String id;

    public Player(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public int getTeam() {
        return this.team;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return this.name;
    }

    public int getTeam() {
        return this.team;
    }

    public int getScore() {
        return this.score;
    }

    public String toString() {
        return this.name;
    }
}

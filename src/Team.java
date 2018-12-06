public class Team {

    public int[] memberIds;
    public int connectionStrength;
    public ResultScorer.TeamScore score;

    public Team(int size) {
        this.memberIds = new int[size];
    }

    @Override
    public String toString() {
        return "Members: " + memberIds.toString() + ", connection strength: " + connectionStrength;
    }

}

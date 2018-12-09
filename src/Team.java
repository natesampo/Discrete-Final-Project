public class Team {

    public int[] memberIds;
    public double connectionStrength;
    public ResultScorer.TeamScore score;

    public Team(int size) {
        this.memberIds = new int[size];
    }

    @Override
    public String toString() {
        return "Members: " + memberIds.toString() + ", connection strength: " + connectionStrength;
    }
    
    public void increaseSize(int amt) {
    	int[] newIds = new int[amt + this.memberIds.length];
    	for(int i = 0; i < this.memberIds.length; i++) {
    		newIds[i] = this.memberIds[i];
    	}
    	this.memberIds = newIds;
    }

}

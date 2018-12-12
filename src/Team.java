public class Team {

    public int[] memberIds;
    public double connectionStrength;
    public TeamScore score;

    public Team(int size) {
        this.memberIds = new int[size];
    }

    @Override
    public String toString() {
        return "Members: " + memberIds.toString() + ", connection strength: " + connectionStrength;
    }
    
    //Increases the size of this team by an amount "amt"
    public void increaseSize(int amt) {
    	int[] newIds = new int[amt + this.memberIds.length];
    	for(int i = 0; i < this.memberIds.length; i++) {
    		newIds[i] = this.memberIds[i];
    	}
    	this.memberIds = newIds;
    }

    public int getSize() {
        return memberIds.length;
    }

}

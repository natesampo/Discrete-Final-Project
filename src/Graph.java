public class Graph {
	private Helper helper;
	private int cliqueSize;
	private int num_teams;
	private double[][] adjacency;
	
	private int numNodes;

	public Graph() {
		final int numNodes = 100;
		final int minAttribute = 0;
		final int maxAttribute = 1;
		final int maxSilverBullets = 5;
		final double skillsWeight = 0.5; // Average dot product looks to be ~1.3
		final double preferenceWeight = 0.5;
		
		cliqueSize = 4;

		helper = new Helper();
		num_teams = (int) java.lang.Math.ceil((numNodes / cliqueSize));

		final Helper.Profile[] profiles = helper.generateProfiles(numNodes, maxSilverBullets);
		
		adjacency = generateAdjacency(profiles, skillsWeight, preferenceWeight);
		adjacency = helper.normalize(adjacency);
		
		helper.arrayPrint(adjacency);
	}

	//Returns a matrix with rows showing different teams with the first column being a score out of 100
	public int[][] greedycliques(){
		double[][] editable_adjacency = adjacency;

		int[][] final_teams = new int[num_teams][cliqueSize+1];

		//Go through and find the highest edge.
		int[] loc = highestEdge(editable_adjacency);

		//Then repeat for highest sum or edges for all nodes connected to first edge


		return final_teams;

	}
	
	

	public int[] highestEdge(double[][] adjacency) {
		int[] locs = new int[2];
		
		
		return locs;
	}


	public double[][] generateAdjacency(Helper.Profile[] profiles, double skillsWeight, double preferenceWeight) {
		double[][] adjacencyArray = new double[profiles.length][profiles.length];
		int silverBullet;
		double preferredPartner;
		
		for(int i=0; i<profiles.length; i++) {
			for(int j=0; j<profiles.length; j++) {
				adjacencyArray[i][j] = calculateEdgeWeight(profiles[i], profiles[j], skillsWeight, preferenceWeight);
			}
		}
		
		return adjacencyArray;
	}
	
	/**
	 * Calculates the edge weight from p1 to p2.
	 * @param p1 the profile of the starting node
	 * @param p2 the profile of the ending node
	 * @param skillsWeight how to weight the lack of skills overlap
	 * @param preferenceWeight how to weight the preference overlap
	 * @return the weight of the edge from p1 to p2, where a lower score is more desirable
	 */
	 public double calculateEdgeWeight(Helper.Profile p1, Helper.Profile p2, double skillsWeight, double preferenceWeight) {
		return ((p1.silverBullets.contains(p2.id)) ? 0 : 1) * (skillsWeight * helper.dotProduct(p1.skills, p2.skills) + preferenceWeight * (p1.preferredPartners.contains(p2.id) ? 0 : 1));
	}
	
	public static void main(String args[]) {
		new Graph();
		
	}
}

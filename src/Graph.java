import java.util.Arrays;

public class Graph {
	private Helper helper;
	private int cliqueSize;
	private int numTeams;
	private double[][] adjacency;
	
	private int numNodes;

	public Graph() {
		final int numNodes = 100;
		final int minAttribute = 0;
		final int maxAttribute = 1;
		final int maxSilverBullets = 3;
		final double skillsWeight = 0.5; // Average dot product looks to be ~1.3
		final double preferenceWeight = 0.5;
		
		cliqueSize = 4;

		helper = new Helper();
		numTeams = (int) java.lang.Math.ceil((numNodes / cliqueSize));

		final Helper.Profile[] profiles = helper.generateProfiles(numNodes, maxSilverBullets);
		
		adjacency = generateAdjacency(profiles, skillsWeight, preferenceWeight);
		adjacency = helper.normalize(adjacency);
		
//		helper.arrayPrintDouble2D(adjacency);
		helper.arrayPrintInt2D(greedyCliques());
	}

	//Returns a matrix with rows showing different teams with the first column being a score out of 100
	public int[][] greedyCliques(){		
		double[][] editableAdjacency = new double[adjacency.length][adjacency.length];
		System.arraycopy(adjacency, 0, editableAdjacency, 0, adjacency.length);
		int[] newAdditions = new int[2];

		int[][] finalTeams = new int[numTeams][cliqueSize+1]; //+1 to indicate how strong the connections are.

		//Go through and find the highest edge.
		for (int teamNum = 0; teamNum < numTeams; teamNum++) {
			for (int memNum = 0; memNum < cliqueSize; memNum++) {
				if(memNum == 0) {
					newAdditions = highestEdge(editableAdjacency, new int[0]);
					finalTeams[teamNum][0] = newAdditions[0];
					finalTeams[teamNum][1] = newAdditions[1];
					memNum++;
				}
				else {
					newAdditions = highestEdge(editableAdjacency, Arrays.copyOfRange(finalTeams[teamNum], 0, memNum));
					finalTeams[teamNum][memNum] = newAdditions[0];
				}
			}
			//TODO: Also calculate total team score here
			for (int x = 0; x < adjacency.length; x++) {
				for (int y = 0; y < cliqueSize; y++) {
					editableAdjacency[finalTeams[teamNum][y]][x] = -1;
					editableAdjacency[x][finalTeams[teamNum][y]] = -1;
				}
				
			}
		}



		return finalTeams;

	}
	
	

	public int[] highestEdge(double[][] adjacency, int[] neighbors) {
		int[] locs = new int[2];
		double currHigh = -1;
		
		if(neighbors.length == 0) {
			for(int i = 0; i < adjacency.length; i ++) {
				for (int j = 0; j < adjacency[0].length; j++) {
					if (adjacency[i][j] > currHigh && i != j) {
						currHigh = adjacency[i][j];
						locs[0] = i;
						locs[1] = j;
					}
				}
			}
		}
		else {
			double tempVal;
			locs[1] = -1;
			for (int x = 0; x < adjacency.length; x++) { //loop through other members to see how good they fit
				tempVal = 1.0;
				for (int j = 0; j < neighbors.length; j++) { //check how they fit with each other groupmember
					if (!searchArray(neighbors, x) && adjacency[x][j] >= 0 ) {//j isn't in neighbors{
						tempVal = tempVal * adjacency[x][j];
					}
					else {
						tempVal = 0.0;
					}
					
				}
				if (tempVal > currHigh) {
					currHigh = tempVal;
					locs[0] = x;
				}
			}
		}
		
		return locs;
	}
	
	public boolean searchArray(int[] list, int val) {
		
		for (int x = 0; x < list.length; x++) {
			if (list[x] == val) {
				return true;
			}
		}
		return false;
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
		return ((p1.silverBullets.contains(p2.id)) ? 0 : (skillsWeight * (1-helper.dotProduct(p1.skills, p2.skills)) + preferenceWeight * (p1.preferredPartners.contains(p2.id) ? 1 : 0)));
	}
	
	public static void main(String args[]) {
		new Graph();
		
	}
}

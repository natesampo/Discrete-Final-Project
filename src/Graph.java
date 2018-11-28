import java.util.Arrays;

public class Graph {
	private Helper helper;
	private int cliqueSize;
	private int numTeams;
	private double[][] adjacency;
	
	private int numNodes;

	public Graph() {
		numNodes = 100;
		final int numProperties = 5;
		final int minAttribute = 0;
		final int maxAttribute = 1;
		
		cliqueSize = 4;

		helper = new Helper();
		numTeams = (int) java.lang.Math.ceil( (numNodes / cliqueSize));


		final double[][] properties = helper.generateProperties(numNodes, numProperties, minAttribute, maxAttribute);
		
		adjacency = generateAdjacency(properties);
		adjacency = helper.normalize(adjacency);
		
		helper.arrayPrint(adjacency);
	}

	//Returns a matrix with rows showing different teams with the first column being a score out of 100
	public int[][] greedyCliques(){
		double[][] editableAdjacency = adjacency;
		int[] newAdditions = new int[2];

		int[][] finalTeams = new int[numTeams][cliqueSize+1]; //+1 to indicate how strong the connections are. 

		//Go through and find the highest edge.
		for (int teamNum = 0; teamNum < numTeams; teamNum++) {
			for (int memNum = 0; memNum < numTeams; memNum++) {
				if(memNum == 0) {
					newAdditions = highestEdge(editableAdjacency, new int[0]);
					finalTeams[teamNum][memNum] = newAdditions[0];
					finalTeams[teamNum][1] = newAdditions[1];
					memNum++;
				}
				else {
					newAdditions = highestEdge(editableAdjacency, Arrays.copyOfRange(finalTeams[teamNum], 0, memNum-1));
					finalTeams[teamNum][memNum] = newAdditions[0];
				}
			}
		}


		//Then repeat for highest sum or edges for all nodes connected to first edge


		return finalTeams;

	}
	
	

	public int[] highestEdge(double[][] adjacency, int[] neighbors) {
		int[] locs = new int[2];
		double currHigh = -1;
		

		if(neighbors.length == 0) {
			for(int i = 0; i < adjacency.length; i ++) {
				for (int j = 0; j < adjacency[0].length; j++) {
					if (adjacency[i][j] > currHigh) {
						currHigh = adjacency[i][j];
						locs[0] = i;
						locs[1] = j;
					}
				}
			}
		}
		else {
			double tempVal;
			for (int x = 0; x < neighbors.length; x++) { //loop through current members
				tempVal = 1.0;
				for (int j = 0; j < adjacency.length; j++) { //Loop through all other people
					if (searchArray(neighbors, j)) {//j isn't in neighbors{
						tempVal = tempVal * adjacency[x][j];
					}
					
				}
				if (tempVal > currHigh) {
					currHigh = tempVal;
					locs[0] = x;
					locs[1] = -1;
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


	public double[][] generateAdjacency(double[][] properties) {
		double[][] adjacencyArray = new double[properties.length][properties.length];
		
		for(int i=0; i<properties.length; i++) {
			for(int j=0; j<properties.length; j++) {
				for(int k=0; k<properties[0].length; k++) {
					// Currently sums difference of attributes. Add real weighting here
					adjacencyArray[i][j] += Math.abs(properties[i][k] - properties[j][k]);
				}
			}
		}
		
		return adjacencyArray;
	}
	
	public static void main(String args[]) {
		new Graph();
		
	}
}

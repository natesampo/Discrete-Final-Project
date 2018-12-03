import java.util.Arrays;
import java.util.HashSet;

public class Graph {
	private Helper helper;
	private int cliqueSize;
	private int numTeams;
	private double[][] adjacency;
	private HashSet<Integer> visitedNodes;
	private HashSet<Integer> coloredClique;

public Graph() {
		final int numNodes = 24;
		final int maxSilverBullets = 2;
		final double skillsWeight = 0.2; // Average dot product looks to be ~1.3
		final double preferenceWeight = 0.5;
		final int numSkills = 5;
		
		cliqueSize = 4;

		helper = new Helper();
		numTeams = (int) java.lang.Math.ceil((numNodes / cliqueSize));

		final Helper.Profile[] profiles = helper.generateProfiles(numNodes, numSkills, maxSilverBullets);
		
		adjacency = generateAdjacency(profiles, skillsWeight, preferenceWeight);
		adjacency = helper.normalize(adjacency);
		
		helper.arrayPrintDouble2D(greedyCliques());
		
//		visitedNodes = new HashSet<Integer>();
//		for (int i=0; i<numNodes; i++) {
//			if (!visitedNodes.contains(i)) {
//				coloredClique = getNodeColoredClique(i, new HashSet<Integer>(), 0.95);
//				helper.hashSetPrintInt(coloredClique);
//				visitedNodes.addAll(coloredClique);
//			}
//		}
	}

	// Recursive strategy of finding cliques by traveling along edges that are above minWeight
	public HashSet<Integer> getNodeColoredClique(int node, HashSet<Integer> connected, double minWeight) {
		// Connected is a hashset of nodes belonging to this clique, add this node to the clique
		connected.add(node);
		
		// Loop through all of this node's edges and check for other nodes to add to the clique
		for (int i=0; i<adjacency[node].length; i++) {
			
			// If this node is not already in the clique and the edge is above minWeight
			if (!connected.contains(i) && adjacency[node][i] > minWeight) {
				
				// Travel to this node and continue to add nodes to the clique from there
				getNodeColoredClique(i, connected, minWeight);
			}
		}
		
		return connected;
	}

	//Returns a matrix with rows showing different teams with the first column being a score out of 100
	public double[][] greedyCliques(){		
		double[][] editableAdjacency = helper.arrayCopy(adjacency);
//		System.arraycopy(adjacency, 0, editableAdjacency, 0, adjacency.length);
		int[] newAdditions = new int[2];
		int newNeighbor;

		double[][] finalTeams = new double[numTeams][cliqueSize+1]; //+1 to indicate how strong the connections are.
		
		for (int teamNum = 0; teamNum < numTeams; teamNum++) {
			for (int memNum = 0; memNum < cliqueSize; memNum++) {
				if(memNum == 0) { //If we need to find the highest edge (starting a new team)
					newAdditions = highestEdge(editableAdjacency);
					finalTeams[teamNum][0] = newAdditions[0];
					finalTeams[teamNum][1] = newAdditions[1];
					memNum++;
				}
				else { //If we want to find the node that best fits with our current nodes
					newNeighbor = highestNode(editableAdjacency, Arrays.copyOfRange(finalTeams[teamNum], 0, memNum));
					finalTeams[teamNum][memNum] = newNeighbor;
				}
			}
			//TODO: Also calculate total team score here
			for (int rowCol = 0; rowCol < adjacency.length; rowCol++) {
				for (int currMem = 0; currMem < cliqueSize; currMem++) { //Edit the editableadjacency to make sure we don't select 
					if(finalTeams[teamNum][currMem] >= 0) {
						editableAdjacency[(int) finalTeams[teamNum][currMem]][rowCol] = -1;
						editableAdjacency[rowCol][(int) finalTeams[teamNum][currMem]] = -1;
					}
				}
			}
		}
		
		double tempScore;
		int col;
		int row;
		boolean badTeamingExperience;
		for (int teamNum = 0; teamNum < numTeams; teamNum++) {
			tempScore = 1.0; 
			badTeamingExperience = false;
			for (int memNum = 0; memNum < cliqueSize; memNum++) {
				for(int otherMem = memNum + 1; otherMem < cliqueSize; otherMem++) {
					col = (int) finalTeams[teamNum][memNum];
					row = (int) finalTeams[teamNum][otherMem];
					if (row <0 || col < 0) {
						System.out.println("INVALID TEAM.");
						badTeamingExperience = true;
						break;
					}
					else {
						tempScore = tempScore*adjacency[col][row];
					}
				}
				if(badTeamingExperience) {
					break;
				}
			}
			if(badTeamingExperience) {
				finalTeams[teamNum][cliqueSize] = -1;
			}
			else {
				finalTeams[teamNum][cliqueSize] = tempScore;
			}
		}
		return finalTeams;
	}
	

	public int[] highestEdge(double[][] adjacency) {
		/**
		 * Looking at the current adjacency matrix, find the edge with the highest value and return the corresponding nodes
		 * input: adjacency (double[][]) -- The adjacency matrix
		 * output: locs (int[2]) -- locations of nodes whose edge is highest. 
		 */
		int[] locs = new int[2];
		double currHigh = -1;
		
		for(int i = 0; i < adjacency.length; i ++) {
			for (int j = 0; j < adjacency[0].length; j++) {
				if (adjacency[i][j] > currHigh && i != j) { //Loop through, finding and updating highest edge
					currHigh = adjacency[i][j];
					locs[0] = i;
					locs[1] = j;
				}
			}
		}
		
		return locs;
	}
	
	
	/*
	 * Do the thing where we have a second Greedy algorithm that Skips the top (numToIgnore) edges for the first two members of a group
	 */
	public int[] highestEdgeV2(double[][] adjacency, int numToIgnore) {
		/**
		 * Looking at the current adjacency matrix, find the edge with the highest value and return the corresponding nodes. New fuctionality: numToIgnore, which allows ignoring of top x edges in an attempt to make a series of teams which work together
		 * input: adjacency (double[][]) -- The adjacency matrix
		 * output: locs (int[2]) -- locations of nodes whose edge is highest. 
		 */
		int[] locs = new int[2];
		double currHigh = -1;
		int numIgnored = 0;
		double[] valsIgnored = new double[numToIgnore + 1];
		int[][] locsIgnored = new int[numToIgnore + 1][2];
		double minVal = 0;
		int minloc = 0;
		
		//Loop through every edge possible
		for(int i = 0; i < adjacency.length; i ++) {
			numIgnored = 0;
			for (int j = 0; j < adjacency[0].length - i; j++) { 
				if (adjacency[i][j] > currHigh && i != j) { //Loop through, finding and updating highest edge
					if(numIgnored < numToIgnore + 1) { //If we haven't ignored enough edges yet, add the current edge to the ignored list
						valsIgnored[numIgnored] = adjacency[i][j];
						locsIgnored[numIgnored][0] = i;
						locsIgnored[numIgnored][1] = j;
						numIgnored = numIgnored + 1;
						if(numIgnored == numToIgnore + 1) {
							minVal = helper.getMinVal(valsIgnored);
						}
					}
					else {
						if (adjacency[i][j] > minVal) {
							minloc = helper.getMinLoc(valsIgnored, minVal);
							
							valsIgnored[minloc] = adjacency[i][j];
							locsIgnored[minloc][0] = i;
							locsIgnored[minloc][1] = j;
							minVal = helper.getMinVal(valsIgnored);
						}
					}
				}
			}
		}
		
		return locsIgnored[minloc];
	}
	
	

	
	public int highestNode(double[][] adjacency, double[] neighbors) {
		/**
		 * Looking at the current adjacency matrix, find the edge with the highest value and return the corresponding nodes
		 * input: adjacency (double[][]) -- The adjacency matrix
		 * input: neighbors (int[]) -- A list of the current nodes in the group (or neighbors we are looking at)
		 * output: loc (int) -- location of node who fits best. 
		 */
		int loc = -1;
		double currHigh = 0.0;
		double tempVal;
		for (int x = 0; x < adjacency.length; x++) { //loop through other members to see how good they fit
			tempVal = 1.0;
			for (int j = 0; j < neighbors.length; j++) { //check how they fit with each other groupmember
				//  Make sure that x isn't a neighbor; make sure there is a neighbor in the slot being looked at; make sure there is a valid connection
				if (!searchArray(neighbors, x) && neighbors[j] != -1 && adjacency[x][(int) neighbors[j]] >= 0) {//j isn't in neighbors{
					tempVal = tempVal * adjacency[x][(int) neighbors[j]];
				}
				else {
					tempVal = 0.0;
				}
				
			}
			if (tempVal > currHigh) {
				currHigh = tempVal;
				loc = x; 
			}
		}
		
		return loc;
	
	}
	
	public boolean searchArray(double[] list, int val) {
		/*
		 * Searches an array for a value, because array.contains wasn't liking me.
		 */
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
		 // Silver bullet makes weight 0. 
		return (((p1.silverBullets.contains(p2.id) || p2.silverBullets.contains(p1.id))) ? 0 : skillsWeight * (1-(helper.dotProduct(p1.skills, p2.skills)/p1.skills.length)) + preferenceWeight * (p1.preferredPartners.contains(p2.id) ? 1 : 0));
	}
	
	public static void main(String args[]) {
		new Graph();	
	}
}

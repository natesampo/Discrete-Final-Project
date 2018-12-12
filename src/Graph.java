import java.util.ArrayList;
import java.util.HashSet;

public class Graph {
	private int cliqueSize;
	private int numTeams;
	public double[][] adjacency;
	private HashSet<Integer> visitedNodes;
	private HashSet<Integer> coloredClique;
	private ArrayList<HashSet<Integer>> coloredCliques;
	private double avgScore;
	private double colorWeight;
	private double skillsWeight;
	private double projectWeight;
	private double preferenceWeight;

	private ResultScorer scorer;

	public Graph(PersonProfile[] profiles, double skillsWeight, double preferenceWeight, double projectWeight) {
		this.skillsWeight = skillsWeight;
		this.preferenceWeight = preferenceWeight;
		this.projectWeight = projectWeight;

		adjacency = new double[profiles.length][profiles.length];

		for(int i=0; i<profiles.length; i++) {
			for(int j=0; j<profiles.length; j++) {
				adjacency[i][j] = calculateEdgeWeight(profiles[i], profiles[j]);
			}
		}
	}

	public Graph(Graph g) {
		this.skillsWeight = g.skillsWeight;
		this.projectWeight = g.projectWeight;
		this.preferenceWeight = g.preferenceWeight;

		this.adjacency = Helper.arrayCopy(g.adjacency);
	}

	public int getNodeCount() {
		return adjacency.length;
	}

	/**
	 * Calculates the edge weight from p1 to p2.
	 * @param p1 the profile of the starting node
	 * @param p2 the profile of the ending node
	 * @return the weight of the edge from p1 to p2, where a lower score is more desirable
	 */
	private double calculateEdgeWeight(PersonProfile p1, PersonProfile p2) {
		// Silver bullet makes weight 0

		// Check to see how many of the same preferred projects they have
		int sameProjects = 0;
		for (int element : p2.preferredProjects) {
			if (p1.preferredProjects.contains(element)) {
				sameProjects++;
			}
		}

		return (((p1.silverBullets.contains(p2.id) || p2.silverBullets.contains(p1.id))) ? 0 : skillsWeight * (1-(Helper.dotProduct(p1.skills, p2.skills)/p1.skills.length)) + (p1.preferredPartners.contains(p2.id) ? preferenceWeight : 0) + projectWeight * sameProjects);
	}

	public int[] highestEdge() {
		/**
		 * Looking at the current adjacency matrix, find the edge with the highest value and return the corresponding nodes
		 * input: adjacency (double[][]) -- The adjacency matrix
		 * output: locs (int[2]) -- locations of nodes whose edge is highest.
		 */
		int[] locs = new int[2];
		double currHigh = 0.0;

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
	public int[] highestEdgeV2(int numToIgnore) {
		/**
		 * Looking at the current adjacency matrix, find the edge with the highest value and return the corresponding nodes. New fuctionality: numToIgnore, which allows ignoring of top x edges in an attempt to make a series of teams which work together
		 * input: adjacency (double[][]) -- The adjacency matrix
		 * output: locs (int[2]) -- locations of nodes whose edge is highest.
		 */
		double currHigh = -1;
		int numIgnored = 0;
		double[] valsIgnored = new double[numToIgnore + 1];
		int[][] locsIgnored = new int[numToIgnore + 1][2];
		double minVal = 0;
		int minloc = 0;

		//Loop through every edge possible
		for(int i = 0; i < adjacency.length; i ++) {
			numIgnored = 0;
			for (int j = 0; j < adjacency[0].length; j++) {
				if (adjacency[i][j] > currHigh && i != j) { //Loop through, finding and updating highest edge
					if(numIgnored < numToIgnore + 1) { //If we haven't ignored enough edges yet, add the current edge to the ignored list
						valsIgnored[numIgnored] = adjacency[i][j];
						locsIgnored[numIgnored][0] = i;
						locsIgnored[numIgnored][1] = j;
						numIgnored = numIgnored + 1;
						if(numIgnored == numToIgnore + 1) {
							minVal = Helper.getMinVal(valsIgnored);
						}
					}
					else {
						if (adjacency[i][j] > minVal) {
							minloc = Helper.getMinLoc(valsIgnored, minVal);

							valsIgnored[minloc] = adjacency[i][j];
							locsIgnored[minloc][0] = i;
							locsIgnored[minloc][1] = j;
							minVal = Helper.getMinVal(valsIgnored);
						}
					}
				}
			}
		}

		return locsIgnored[minloc];
	}


	public int highestNode(int[] neighbors) {
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
			for (int j = 0; j < neighbors.length; j++) { //check how they fit with each other group member
				//  Make sure that x isn't a neighbor; make sure there is a neighbor in the slot being looked at; make sure there is a valid connection
				if (!Helper.searchArray(neighbors, x) && neighbors[j] != -1 && adjacency[x][neighbors[j]] > 0) {//j isn't in neighbors{
					tempVal = tempVal * adjacency[x][neighbors[j]];
				}
				else {
					tempVal = 0.0;
				}

			}
			//If the new calculated value is higher than the old saved one
			if (tempVal > currHigh) {
				currHigh = tempVal;
				loc = x;
			}
		}
		//Make sure it's a valid location before returning. If it isn't, there's a print statement about it.
		if(currHigh > 0.0) {
			return loc;
		}
		else {
			System.out.println("Impossible!");
			return loc;
		}
	}

}

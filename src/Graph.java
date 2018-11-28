public class Graph {
	private Helper helper;
	private int[][] adjacency;
	private int cliqueSize;
	private int num_teams;
	
	private int numNodes;

	public Graph() {
		numNodes = 100;
		final int numProperties = 5;
		final int minAttribute = 0;
		final int maxAttribute = 100;
		
		cliqueSize = 4;

		helper = new Helper();
		num_teams = (int) java.lang.Math.ceil( (numNodes / cliqueSize));


		final int[][] properties = helper.generateProperties(numNodes, numProperties, minAttribute, maxAttribute);

		adjacency = helper.generateAdjacency(properties);

		helper.arrayPrint(adjacency);
	}

	//Returns a matrix with rows showing different teams with the first column being a score out of 100
	public int[][] greedycliques(){
		int[][] editable_adjacency = adjacency;

		int[][] final_teams = new int[num_teams][cliqueSize+1];

		//Go through and find the highest edge.
		int[] loc = highestEdge(editable_adjacency);

		//Then repeat for highest sum or edges for all nodes connected to first edge


		return final_teams;

	}
	
	
	public int[] highestEdge(int[][] adjacency) {
		int[] locs = new int[2];
		
		
		return locs;
	}

	public static void main(String args[]) {
		new Graph();
		
	}
}

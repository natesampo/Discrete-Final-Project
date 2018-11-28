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
		
		cliqueSize = 4;

		helper = new Helper();
		num_teams = (int) java.lang.Math.ceil((numNodes / cliqueSize));

		final Helper.Profile[] profiles = helper.generateProfiles(numNodes, maxSilverBullets);
		
		adjacency = generateAdjacency(properties);
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

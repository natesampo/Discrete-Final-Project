public class Graph {
	private Helper helper;
	private double[][] adjacency;
	
	public Graph() {
		final int numNodes = 100;
		final int numProperties = 5;
		final int minAttribute = 0;
		final int maxAttribute = 1;
		
		helper = new Helper();
		
		final double[][] properties = helper.generateProperties(numNodes, numProperties, minAttribute, maxAttribute);
		
		adjacency = generateAdjacency(properties);
		adjacency = helper.normalize(adjacency);
		
		helper.arrayPrint(adjacency);
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
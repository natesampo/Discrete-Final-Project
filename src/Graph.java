public class Graph {
	private Helper helper;
	private int[][] adjacency;
	
	public Graph() {
		final int numNodes = 100;
		final int numProperties = 5;
		final int minAttribute = 0;
		final int maxAttribute = 100;
		
		helper = new Helper();
		
		final int[][] properties = helper.generateProperties(numNodes, numProperties, minAttribute, maxAttribute);
		
		adjacency = helper.generateAdjacency(properties);
		
		helper.arrayPrint(adjacency);
	}
	
	public static void main(String args[]) {
		new Graph();
	}
}
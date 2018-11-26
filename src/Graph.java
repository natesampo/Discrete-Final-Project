public class Graph {
	private Helper helper;
	
	public Graph() {
		final int numNodes = 100;
		final int numProperties = 5;
		final int minAttribute = 0;
		final int maxAttribute = 100;
		
		helper = new Helper();
		
		final int[][] properties = helper.generateProperties(numNodes, numProperties, minAttribute, maxAttribute);
		
		helper.arrayPrint(properties);
	}
	
	public static void main(String args[]) {
		new Graph();
	}
}

import java.util.concurrent.ThreadLocalRandom;

public class Helper {
	public int[][] generateAdjacency(int[][] properties) {
		int[][] adjacency = new int[properties.length][properties.length];
		
		for(int i=0; i<properties.length; i++) {
			for(int j=0; j<properties.length; j++) {
				for(int k=0; k<properties[0].length; k++) {
					// Currently sums difference of attributes. Add real weighting here
					adjacency[i][j] += Math.abs(properties[i][k] - properties[j][k]);
				}
			}
		}
		
		return adjacency;
	}
	
	public int[][] generateProperties(int nodes, int properties, int min, int max) {
		int[][] propertiesArray = new int[nodes][properties];
		
		for(int i=0; i<nodes; i++) {
			for(int j=0; j<properties; j++) {
				// Generate a random integer from the min to the max as one of the properties
				propertiesArray[i][j] = ThreadLocalRandom.current().nextInt(min, max + 1);
			}
		}
		
		return propertiesArray;
	}
	
	public void arrayPrint(int[][] array) {
		// Iterate through the 2D array and print every value in a way that looks nice
		for(int i=0; i<array.length; i++) {
			for(int j=0; j<array[i].length; j++) {
				System.out.print(" " + array[i][j]);
			}
			System.out.println("");
		}
	}
}
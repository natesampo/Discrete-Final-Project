import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

public class Helper {

	public double[][] normalize(double[][] array) {
		// This is O(n^2). Little yikes but whatever
		// Normalize from minimum value (not including silver bullets which remain at 0) to maximum value
		double max = 0.0;
		double min = 100.0;
		
		for(int i=0; i<array.length; i++) {
			for(int j=0; j<array[i].length; j++) {
				if(array[i][j] > max) {
					max = array[i][j];
				}
				
				if(array[i][j] != 0.0 && array[i][j] < min) {
					min = array[i][j];
				}
			}
		}
		
		max -= min;
		
		for(int i=0; i<array.length; i++) {
			for(int j=0; j<array[i].length; j++) {
				// The lowest value should be 0, along with the silver bullets. Math.max is because silver bullets would go below 0 when subtracting min.
				array[i][j] = Math.max((array[i][j]-min)/max, 0.0);
			}
		}
		
		return array;
	}
	
	public double[][] arrayCopy(double[][] arrayIn) {
		//Copies an array because Java's functions weren't working well.
		double[][] temparr = new double[arrayIn.length][arrayIn[0].length];
		for (int i = 0; i < arrayIn.length; i++) {
			for (int j = 0; j < arrayIn[0].length; j++) {
				temparr[i][j] = arrayIn[i][j];
			}
		}
		return temparr;
	}
	
	public double getMinVal(double[] vals) {
		//Get the min value in an array of doubles.
		//It should never be higher than 1
		double tempVal = 1;
		for (int x = 0; x < vals.length; x ++) {
			if (vals[x] <= tempVal) {
				tempVal = vals[x];
			}
		}
		return tempVal;
	}
	
	public int getMinLoc(double[] vals, double val) {
		//Get the location of the minimum value in a double array
		int loc = -1;
		for (int x = 0; x < vals.length; x++) {
			if (vals[x] == val) {
				loc = x;
				break;
			}
		}
		return loc;
	}
	
	
	public Profile[] generateProfiles(int nodes, int numSkills, int maxSilverBullets) {
		/*
		 * 5 Skills Total
		 * 
		 * 0. Mechanical Design
		 * 1. Fabrication
		 * 2. ECE
		 * 3. Software
		 * 4. Design
		 */
		Profile[] profiles = new Profile[nodes];
		
		for(int i=0; i<nodes; i++) {
			profiles[i] = generateRandomProfile(i, numSkills, maxSilverBullets, nodes);
		}
		
		return profiles;
	}

	private Profile generateRandomProfile(int id, int numSkills, int maxSilverBullets, int totalNumPeople) {
		Profile prof = new Profile(id, numSkills);

		// Generate some random skill levels
		int skillsMin = 0;
		int skillsMax = 1;
		for (int skillIdx = 0; skillIdx < numSkills; ++skillIdx) {
			prof.skills[skillIdx] = ((double) ThreadLocalRandom.current().nextInt(skillsMin*100, skillsMax*100 + 1))/100;
		}

		// Pick some silver bullets
		int numSilverBullets = ThreadLocalRandom.current().nextInt(0, maxSilverBullets+1);
		for (int i = 0; i < numSilverBullets; ++i) {
			int silverBulletedPersonId = ThreadLocalRandom.current().nextInt(0, totalNumPeople);
			prof.silverBullets.add(silverBulletedPersonId);
		}

		return prof;
	}

	/**
	 * Calculates the dot product of two vectors.
	 */
	public double dotProduct(double[] v1, double[] v2) {
		double res = 0.0;
		for (int i = 0; i < v1.length; ++i) {
			res += v1[i] * v2[i];
		}
		return res;
	}
	
	public void arrayPrintDouble2D(double[][] array) {
		// Iterate through the 2D array and print every value in a way that looks nice (not horrible)
		for(int i=0; i<array.length; i++) {
			for(int j=0; j<array[i].length; j++) {
				System.out.print(" " + array[i][j]);
			}
			System.out.println("");
		}
	}
	
	public void arrayPrintInt2D(int[][] array) {
		// Iterate through the 2D array and print every value in a way that looks nice (not horrible)
		for(int i=0; i<array.length; i++) {
			for(int j=0; j<array[i].length; j++) {
				System.out.print(" " + array[i][j]);
			}
			System.out.println("");
		}
	}
	
	public void hashSetPrintInt(HashSet<Integer> hashset) {
		// Iterate over every value and print it
		for (int i : hashset) {
			System.out.print(i + " ");
		}
		System.out.println("");
	}

	public class Profile {
		public int id;
		public double[] skills;
		public HashSet<Integer> preferredPartners;
		public HashSet<Integer> silverBullets;

		public Profile(int id, int numSkills) {
			this.id = id;
			skills = new double[numSkills];
			preferredPartners = new HashSet<>();
			silverBullets = new HashSet<>();
		}
	}
}
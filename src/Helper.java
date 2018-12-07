import java.util.ArrayList;
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
	
	
	public PersonProfile[] generateProfiles(int nodes, int numSkills, int maxSilverBullets) {
		/*
		 * 5 Skills Total
		 * 
		 * 0. Mechanical Design
		 * 1. Fabrication
		 * 2. ECE
		 * 3. Software
		 * 4. Design
		 */
		PersonProfile[] profiles = new PersonProfile[nodes];
		
		for(int i=0; i<nodes; i++) {
			profiles[i] = generateRandomProfile(i, numSkills, maxSilverBullets, nodes);
		}
		
		return profiles;
	}

	private PersonProfile generateRandomProfile(int id, int numSkills, int maxSilverBullets, int totalNumPeople) {
		PersonProfile prof = new PersonProfile(id, numSkills);

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
	public double dotProduct(int[] v1, int[] v2) {
		double res = 0.0;
		for (int i = 0; i < v1.length; ++i) {
			res += v1[i] * v2[i];
		}
		return res;
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

	public static double[] sumArrays(double[] a1, double[] a2) {
		if (a1.length != a2.length)
			throw new IllegalArgumentException("Length of arrays to add must be equal.");

		double[] res = new double[a1.length];
		for (int i = 0; i < a1.length; ++i) {
			res[i] = a1[i] + a2[i];
		}

		return res;
	}

	public static double[] arrayElementWiseMin(double[] a1, double[] a2) {
		if (a1.length != a2.length)
			throw new IllegalArgumentException("Lengths of arrays to perform element-wise min on must be equal.");

		double[] res = new double[a1.length];

		for (int i = 0; i < a1.length; ++i)
			res[i] = Math.min(a1[i], a2[i]);

		return res;
	}
	
	public static double[] arrayElementWiseRange(double[] a1, double[] min, double[] max) {
		if (a1.length != min.length)
			throw new IllegalArgumentException("Lengths of arrays to perform element-wise min on must be equal.");
		
		double[] res = new double[a1.length];
		
		for (int i = 0; i < a1.length; ++i)
			res[i] = min[i] - max[i];

		return res;
	}
	
	public static double[] arrayElementWiseSD(double[] a1, double[] a2) {
		double[] res = new double[a1.length];
		double sum, standardDeviation;
		int length = a1.length;
		double mean;
		
		for (int i = 0; i < a1.length; i++) {
			sum = 0.0;
			standardDeviation = 0.0;
			for(double num : a2) {
	            sum += num;
	        }
	        mean = sum/length;
	        for(double num: a2) {
	            standardDeviation += Math.pow(num - mean, 2);
	        }
	        res[i] = Math.sqrt(standardDeviation/length);
		}
		
		
		return res;
	}

	public static double[] arrayElementWiseMax(double[] a1, double[] a2) {
		if (a1.length != a2.length)
			throw new IllegalArgumentException("Lengths of arrays to perform element-wise max on must be equal.");

		double[] res = new double[a1.length];

		for (int i = 0; i < a1.length; ++i)
			res[i] = Math.max(a1[i], a2[i]);

		return res;
	}

	public Team[] generateTeamArray(int teamCount, int membersPerTeam) {
		Team[] res = new Team[teamCount];
		for (int i = 0; i < teamCount; ++i) {
			res[i] = new Team(membersPerTeam);
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
	
	public void hashSetArrayPrintInt(ArrayList<HashSet<Integer>> array) {
		for (HashSet<Integer> element : array) {
			hashSetPrintInt(element);
		}
	}
	
	
	public boolean searchArray(int[] list, int val) {
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

}
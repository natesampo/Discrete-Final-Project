import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

public class Helper {
	public double[][] generateProperties(int nodes, int properties, double min, double max) {
		double[][] propertiesArray = new double[nodes][properties];
		
		for(int i=0; i<nodes; i++) {
			for(int j=0; j<properties; j++) {
				// Generate a random integer from the min to the max as one of the properties
				propertiesArray[i][j] = ((double) ThreadLocalRandom.current().nextInt((int) min*100, (int) max*100 + 1))/100;
			}
		}
		
		return propertiesArray;
	}
	
	public double[][] normalize(double[][] array) {
		// This is O(n^2). Little yikes but whatever
		double max = 0.0;
		
		for(int i=0; i<array.length; i++) {
			for(int j=0; j<array[i].length; j++) {
				if(array[i][j] > max) {
					max = array[i][j];
				}
			}
		}
		
		for(int i=0; i<array.length; i++) {
			for(int j=0; j<array[i].length; j++) {
				array[i][j] = array[i][j]/max;
			}
		}
		
		return array;
	}

	private Profile generateRandomProfile(int id, int numSkills, int maxSilverBullets, int totalNumPeople) {
		Profile prof = new Profile(id, numSkills);

		// Generate some random skill levels
		int skillsMin = 0;
		int skillsMax = 5;
		for (int skillIdx = 0; skillIdx < numSkills; ++skillIdx) {
			prof.skills[skillIdx] = ThreadLocalRandom.current().nextInt(skillsMin, skillsMax+1);
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
	 * Calculates the edge weight from p1 to p2.
	 * @param p1 the profile of the starting node
	 * @param p2 the profile of the ending node
	 * @param skillsWeight how to weight the lack of skills overlap
	 * @param preferenceWeight how to weight the preference overlap
	 * @return the weight of the edge from p1 to p2, where a lower score is more desirable
	 */
	 public double calculateEdgeWeight(Profile p1, Profile p2, double skillsWeight, double preferenceWeight) {
		return skillsWeight * dotProduct(p1.skills, p2.skills)
				+ preferenceWeight * (p1.preferredPartners.contains(p2.id) ? 0 : 1);
	}

	/**
	 * Calculates the dot product of two vectors.
	 */
	private double dotProduct(double[] v1, double[] v2) {
		double res = 0.0;
		for (int i = 0; i < v1.length; ++i) {
			res += v1[i] * v2[i];
		}
		return res;
	}
	
	public void arrayPrint(double[][] array) {
		// Iterate through the 2D array and print every value in a way that looks nice (not horrible)
		for(int i=0; i<array.length; i++) {
			for(int j=0; j<array[i].length; j++) {
				System.out.print(" " + array[i][j]);
			}
			System.out.println("");
		}
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
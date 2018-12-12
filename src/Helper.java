import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

public class Helper {

	public static double[][] normalize(double[][] array) {
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
	
	public static double[][] arrayCopy(double[][] arrayIn) {
		//Copies an array because Java's functions weren't working well.
		double[][] temparr = new double[arrayIn.length][arrayIn[0].length];
		for (int i = 0; i < arrayIn.length; i++) {
			for (int j = 0; j < arrayIn[0].length; j++) {
				temparr[i][j] = arrayIn[i][j];
			}
		}
		return temparr;
	}
	
	public static double getMinVal(double[] vals) {
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
	
	public static int getMinLoc(double[] vals, double val) {
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
	
	
	public static PersonProfile[] generateProfiles(int nodes, int numSkills, int maxSilverBullets, int maxPreferredPartners, int numProjects, int projectPreferences) {
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
			profiles[i] = generateRandomProfile(i, numSkills, maxSilverBullets, maxPreferredPartners, numProjects, projectPreferences, nodes);
		}
		
		return profiles;
	}

	private static PersonProfile generateRandomProfile(int id, int numSkills, int maxSilverBullets, int maxPreferredPartners, int numProjects, int projectPreferences, int totalNumPeople) {
		PersonProfile prof = new PersonProfile(id, numSkills);

		// Generate some random skill levels
		int skillsMin = 0;
		int skillsMax = 1;
		for (int skillIdx = 0; skillIdx < numSkills; ++skillIdx) {
			double points = ThreadLocalRandom.current().nextDouble(skillsMin, skillsMax);
			prof.skills[skillIdx] = points;
		}

		// Pick some silver bullets
		int numSilverBullets = ThreadLocalRandom.current().nextInt(0, maxSilverBullets+1);
		for (int i = 0; i < numSilverBullets; ++i) {
			int silverBulletedPersonId = ThreadLocalRandom.current().nextInt(0, totalNumPeople);
			prof.silverBullets.add(silverBulletedPersonId);
		}
		
		// Pick some preferred partners
		int numPreferredPartners = ThreadLocalRandom.current().nextInt(0, maxPreferredPartners+1);
		for (int i = 0; i < numPreferredPartners; ++i) {
			int preferredPartnerPersonId = ThreadLocalRandom.current().nextInt(0, totalNumPeople);
			prof.preferredPartners.add(preferredPartnerPersonId);
		}
		
		// Pick some project preferences
		for (int i = 0; i < projectPreferences; ++i) {
			int projectPreferenceID = ThreadLocalRandom.current().nextInt(0, numProjects);
			prof.preferredPartners.add(projectPreferenceID);
		}

		return prof;
	}

	public static Team[] randomTeams(PersonProfile[] profiles, int numTeams, int teamSize) {
		// Generate random teams to test against
		// Now keeping silver bullets in mind
		Team[] teams = generateTeamArray(numTeams, teamSize);
		HashSet<Integer> missingTeams = new HashSet<Integer>();
		ArrayList<Integer> missingPeople = new ArrayList<Integer>();
		int leftovers = profiles.length;
		HashSet<Integer> completeTeams = new HashSet<Integer>();

		// Go through and add every person to a team sequentially
		for (int i=0; i<numTeams; i++) {
			for (int j=0; j<teamSize; j++) {
				teams[i].memberIds[j] = profiles[teamSize*i + j].id;
				leftovers--;
			}
		}

		// Then check for silver bullets
		for (int i=0; i<numTeams; i++) {
			for (int j=0; j<teamSize; j++) {
				for (int k=0; k<teamSize; k++) {
					if (k!=j && teams[i].memberIds[j]!=-1 && teams[i].memberIds[k]!=-1 && (profiles[teams[i].memberIds[j]].silverBullets.contains(teams[i].memberIds[k]) || profiles[teams[i].memberIds[k]].silverBullets.contains(teams[i].memberIds[j]))) {

						// If we do find silver bullets, remove that person from the team and remember which team is missing a person, and which person is missing
						missingTeams.add(i);
						missingPeople.add(teams[i].memberIds[k]);
						teams[i].memberIds[k] = -1;
					}
				}
			}
		}

		// While there are teams missing people, continue trying to match people to teams
		int i=0;
		boolean leave = false;
		while (missingTeams.size() > 0) {
			if (!missingTeams.contains(i)) {

				// First try swapping out people in successful teams
				int silverBulletIndex = teamSize-1;
				int notSilverBulleted = 0;
				for (int j=0; j<teamSize; j++) {
					if (!profiles[teams[i].memberIds[j]].silverBullets.contains(missingPeople.get(0)) && !profiles[missingPeople.get(0)].silverBullets.contains(teams[i].memberIds[j])) {
						notSilverBulleted++;
					} else {
						silverBulletIndex = j;
					}

					if (notSilverBulleted == teamSize-1) {
						int tempMissing = missingPeople.get(0);
						missingPeople.set(0, teams[i].memberIds[silverBulletIndex]);
						teams[i].memberIds[silverBulletIndex] = tempMissing;
					}
				}

				// Then try every current missing person with every current team missing people
				for (int team : missingTeams) {
					leave = false;

					for (int person : missingPeople) {
						for (int j=0; j<teamSize; j++) {
							if (!profiles[teams[team].memberIds[j]].silverBullets.contains(person) && !profiles[person].silverBullets.contains(teams[i].memberIds[j])) {
								break;
							}
						}

						int alreadyHit = 0;
						for (int j=0; j<teamSize; j++) {
							if (teams[team].memberIds[j] == -1) {
								if (alreadyHit == 0) {
									teams[team].memberIds[j] = person;
									missingPeople.remove(Integer.valueOf(person));
								}

								alreadyHit++;
							}
						}

						leave = false;
						if (alreadyHit == 1) {
							missingTeams.remove(team);
							leave = true;
							break;
						}
					}

					if (leave) {
						break;
					}
				}
			}

			i++;
		}

		// Add any leftover people (basically create the teams of 5)
		for (int j=profiles.length-leftovers; j<profiles.length; j++) {
			for (int k=0; k<teams.length; k++) {
				if (!completeTeams.contains(k)) {
					boolean valid = true;

					for (int l=0; l<teams[k].memberIds.length; l++) {
						if (profiles[teams[k].memberIds[l]].silverBullets.contains(j)) {
							valid = false;
						}
					}

					if (valid) {
						teams[k].increaseSize(1);
						teams[k].memberIds[teamSize] = j;
						completeTeams.add(k);
						break;
					}
				}
			}
		}

		return teams;
	}

	/**
	 * Calculates the dot product of two vectors.
	 */
	public static double dotProduct(int[] v1, int[] v2) {
		double res = 0.0;
		for (int i = 0; i < v1.length; ++i) {
			res += v1[i] * v2[i];
		}
		return res;
	}

	/**
	 * Calculates the dot product of two vectors.
	 */
	public static double dotProduct(double[] v1, double[] v2) {
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
	
	public static double[] arrayElementWiseRange(double[] min, double[] max) {
		if (min.length != max.length)
			throw new IllegalArgumentException("Lengths of arrays to perform element-wise range on must be equal.");
		
		double[] res = new double[min.length];
		
		for (int i = 0; i < min.length; ++i)
			res[i] = max[i] - min[i];

		return res;
	}
	
	public static double averageSkillTotal(PersonProfile[] profiles) {
		double totSum = 0.0;
		int numSkills = profiles[0].skills.length;
		int numPeople = profiles.length;
		double avg = 0.0;
		
		for (PersonProfile peep : profiles) {
			for (int i = 0; i <numSkills; i++) {
				totSum += peep.skills[i];
			}
		}
		avg = totSum/(numPeople);
		
		return avg;
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

	public static Team[] generateTeamArray(int teamCount, int membersPerTeam) {
		Team[] res = new Team[teamCount];
		for (int i = 0; i < teamCount; ++i) {
			res[i] = new Team(membersPerTeam);
		}
		return res;
	}
	
	public static void arrayPrintDouble2D(double[][] array) {
		// Iterate through the 2D array and print every value in a way that looks nice (not horrible)
		for(int i=0; i<array.length; i++) {
			for(int j=0; j<array[i].length; j++) {
				System.out.print(" " + array[i][j]);
			}
			System.out.println("");
		}
	}
	
	public static void arrayPrintInt2D(int[][] array) {
		// Iterate through the 2D array and print every value in a way that looks nice (not horrible)
		for(int i=0; i<array.length; i++) {
			for(int j=0; j<array[i].length; j++) {
				System.out.print(" " + array[i][j]);
			}
			System.out.println("");
		}
	}
	
	public static void hashSetPrintInt(HashSet<Integer> hashset) {
		// Iterate over every value and print it
		for (int i : hashset) {
			System.out.print(i + " ");
		}
		System.out.println("");
	}
	
	public static void hashSetArrayPrintInt(ArrayList<HashSet<Integer>> array) {
		// Iterate over every element in the hash set and send it to be printed
		for (HashSet<Integer> element : array) {
			hashSetPrintInt(element);
		}
	}
	
	
	public static boolean searchArray(int[] list, int val) {
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
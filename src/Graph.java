import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

//import ResultScorer.TeamSetScore;

//import ResultScorer;

//import ResultScorer.TeamSetScore;

//import ResultScorer;

public class Graph {
	private Helper helper;
	private int cliqueSize;
	private int numTeams;
	private int numNodes;
	private double[][] adjacency;
	private HashSet<Integer> visitedNodes;
	private HashSet<Integer> coloredClique;
	private ArrayList<HashSet<Integer>> coloredCliques;
	private double avgScore;

	private ResultScorer scorer;

	public Graph() {
		final int maxSilverBullets = 2;
		final int maxPreferredPartners = 6;
		final double skillsWeight = 0.2; // Average dot product looks to be ~1.3
		final double preferenceWeight = 0.5;
		final int numSkills = 5;
		final int numProjects = numNodes * 2;
		final int projectPreferences = 3;
		final double projectWeight = 0.3; // Weight per same project in preferred projects
		
		scorer = new ResultScorer();
		
		cliqueSize = 4;

		helper = new Helper();

//		final PersonProfile[] profiles = helper.generateProfiles(numNodes, numSkills, maxSilverBullets, maxPreferredPartners, numProjects, projectPreferences);
		final PersonProfile[] profiles = CSVReader.readProfiles("Teaming_Anonymized.csv");

//		final PersonProfile[] profilesOld = CSVReader.readProfiles("Teaming_Anonymized.csv");
//		final PersonProfile[] profiles = new PersonProfile[40];
//		for(int x = 0; x < 40; x++) {
//			profiles[x] = profilesOld[x];
//		}

		numNodes = profiles.length;
		numTeams = (int) java.lang.Math.ceil((numNodes / cliqueSize));
		avgScore = helper.averageSkillTotal(profiles)*cliqueSize;


		adjacency = generateAdjacency(profiles, skillsWeight, preferenceWeight, projectWeight);
		adjacency = helper.normalize(adjacency);


		Team[] randomTeams = randomTeams(profiles);
		scorer.scoreTeams(randomTeams, profiles);
		System.out.println("Result of random teams:");
		ObjectPrinter.printTeamArray(randomTeams);
		System.out.println("\n\n");
		Team[] teamsFromGreedy1 = greedyCliques();
		ResultScorer.TeamSetScore score1 = scorer.scoreTeams(teamsFromGreedy1, profiles);
		System.out.println("Result of first greedy implementation:");
		ObjectPrinter.printTeamSetScore(score1);
		System.out.println("\n\n");
		Team[] teamsFromGreedy2 = greedyV2(0);
		ResultScorer.TeamSetScore score2 = scorer.scoreTeams(teamsFromGreedy2, profiles);
		System.out.println("Result of second greedy implementation:");
		ObjectPrinter.printTeamSetScore(score2);

		System.out.println("\n\n");
		Team[] teamsFromAllCliques = allCliques(profiles);
		scorer.scoreTeams(teamsFromAllCliques, profiles);
//		Team[] bruteTeams = bruteCliques(teamsFromAllCliques, scorer, profiles);
////		Team[] topTeams = topTeams(teamsFromAllCliques);
//		ObjectPrinter.printTeamArray(bruteTeams);
//		System.out.println("we good.");
		
		Team[] greedyCliqueTeams = greedyCliques(teamsFromAllCliques, scorer, profiles);
//		ObjectPrinter.printTeamArray(greedyCliqueTeams);
		ResultScorer.TeamSetScore score3 = scorer.scoreTeams(greedyCliqueTeams, profiles);
		System.out.println("Result of runnable allClique implementation:");
		ObjectPrinter.printTeamSetScore(score3);
		System.out.println("we good.");
		System.out.printf("Avgscore: %f", avgScore);
//		
		//getColoredCliques();
	}

	// Start of recursive colored clique finding
	public void getColoredCliques() {		
		// O(n^2) time complexity
		final double minWeight = 0.99;

		visitedNodes = new HashSet<Integer>();
		for (int i=0; i<numNodes; i++) {
			if (!visitedNodes.contains(i)) {
				coloredClique = getNodeColoredClique(i, new HashSet<Integer>(), minWeight);
				if (coloredClique.size() > 1) {
					coloredCliques.add(coloredClique);
					visitedNodes.addAll(coloredClique);
					helper.hashSetArrayPrintInt(coloredCliques);
				}
			}
		}
	}

	// Recursive strategy of finding cliques by traveling along edges that are above minWeight
	public HashSet<Integer> getNodeColoredClique(int node, HashSet<Integer> connected, double minWeight) {
		// Connected is a hashset of nodes belonging to this clique, add this node to the clique
		connected.add(node);
		
		// Loop through all of this node's edges and check for other nodes to add to the clique
		for (int i=0; i<adjacency[node].length; i++) {
			
			// If this node is not already in the clique and the edge is above minWeight
			if (!connected.contains(i) && adjacency[node][i] > minWeight) {
				
				// Travel to this node and continue to add nodes to the clique from there
				getNodeColoredClique(i, connected, minWeight);
			}
		}
		
		return connected;
	}
	
	public Team[] randomTeams(PersonProfile[] profiles) {
		// Generate random teams to test against (keeping silver bullets in mind)
		Team[] teams = helper.generateTeamArray(numTeams, cliqueSize);
		
		for (int i=0; i<numTeams; i++) {
			for (int j=0; j<cliqueSize; j++) {
				teams[i].memberIds[j] = profiles[4*i + j].id;
			}
		}
		return teams;
	}

	//Returns a matrix with rows showing different teams with the first column being a score out of 100
	public Team[] greedyCliques(){
		double[][] editableAdjacency = helper.arrayCopy(adjacency);
		int[] newAdditions = new int[2];
		int newNeighbor;
		Team[] proposedTeams = helper.generateTeamArray(numTeams, cliqueSize);
		
		for (int teamNum = 0; teamNum < numTeams; teamNum++) {
			for (int memNum = 0; memNum < cliqueSize; memNum++) {
				if(memNum == 0) { //If we need to find the highest edge (starting a new team)
					newAdditions = highestEdge(editableAdjacency);
					proposedTeams[teamNum].memberIds[0] = newAdditions[0];
					proposedTeams[teamNum].memberIds[1] = newAdditions[1];
					memNum++;
				}
				else { //If we want to find the node that best fits with our current nodes
					newNeighbor = highestNode(editableAdjacency, Arrays.copyOfRange(proposedTeams[teamNum].memberIds, 0, memNum));
					proposedTeams[teamNum].memberIds[memNum] = newNeighbor;
				}
			}
			for (int rowCol = 0; rowCol < adjacency.length; rowCol++) {
				for (int currMem = 0; currMem < cliqueSize; currMem++) { //Edit the editableadjacency to make sure we don't select 
					if (proposedTeams[teamNum].memberIds[currMem] >= 0) {
						editableAdjacency[proposedTeams[teamNum].memberIds[currMem]][rowCol] = -1;
						editableAdjacency[rowCol][proposedTeams[teamNum].memberIds[currMem]] = -1;
					}
				}
			}
		}
		
		return proposedTeams;
	}
	

	public Team[] greedyV2(int numToSkip){
		//Difference between greedy and greedy v2: Greedy v2 skips the top (numToSkip) edges when picking the next edge to start a clique with
		double[][] editableAdjacency = helper.arrayCopy(adjacency);
		int[] newAdditions = new int[2];
		int newNeighbor;
		Team[] proposedTeams = helper.generateTeamArray(numTeams, cliqueSize);
		//Iterating through the teams
		for (int teamNum = 0; teamNum < numTeams; teamNum++) {
			//Try to form a team
			for (int memNum = 0; memNum < cliqueSize; memNum++) {
				if(memNum == 0) { //If we need to find the highest edge (starting a new team)
					newAdditions = highestEdgeV2(editableAdjacency, numToSkip);
					proposedTeams[teamNum].memberIds[1] = newAdditions[1];
					proposedTeams[teamNum].memberIds[0] = newAdditions[0];
					memNum++;
				}
				else { //If we want to find the node that best fits with our current nodes
					newNeighbor = highestNode(editableAdjacency, Arrays.copyOfRange(proposedTeams[teamNum].memberIds, 0, memNum));
					proposedTeams[teamNum].memberIds[memNum] = newNeighbor;
				}
			}
			//Set values to -1 to not be confused in the future.
			for (int rowCol = 0; rowCol < adjacency.length; rowCol++) {
				for (int currMem = 0; currMem < cliqueSize; currMem++) { //Edit the editableadjacency to make sure we don't select 
					if(proposedTeams[teamNum].memberIds[currMem] >= 0) {
						editableAdjacency[proposedTeams[teamNum].memberIds[currMem]][rowCol] = -1;
						editableAdjacency[rowCol][proposedTeams[teamNum].memberIds[currMem]] = -1;
					}
				}
			}
		}
		return proposedTeams;
	}
	
	public Team[] allCliques(PersonProfile[] profiles){
		int numPeeps = adjacency.length;
		int numPossTeams = -1;
		if (numPeeps == 24) {
			numPossTeams = 10626; //24 People
		}
		else if (numPeeps == 40) {
			numPossTeams = 91390; //40 people
		}
		else if (numPeeps == 80){
			numPossTeams = 1581580; //80 people
		}
		else {
			numPossTeams = 3921225;
		}
		Team[] tempTeams = helper.generateTeamArray(numPossTeams, cliqueSize);
		
		int currTeam = 0;
		
		//numPeeps - 3 because there are 3 other members
		for (int firstMem = 0; firstMem < numPeeps - 3; firstMem++) {
			for (int secondMem = firstMem + 1; secondMem < numPeeps - 2; secondMem++) {
				if(adjacency[firstMem][secondMem] > 0) {
					for (int thirdMem = secondMem + 1; thirdMem < numPeeps - 1; thirdMem++) {
						if(adjacency[firstMem][thirdMem] > 0 && adjacency[secondMem][thirdMem] > 0) {
							for (int fourthMem = thirdMem + 1; fourthMem < numPeeps; fourthMem++) {
								if(adjacency[firstMem][fourthMem] > 0 && adjacency[secondMem][fourthMem] > 0 && adjacency[thirdMem][fourthMem] > 0 && currTeam < numPossTeams) {
									tempTeams[currTeam].memberIds[0] = firstMem;
									tempTeams[currTeam].memberIds[1] = secondMem;
									tempTeams[currTeam].memberIds[2] = thirdMem;
									tempTeams[currTeam].memberIds[3] = fourthMem;
									currTeam++;
								}
								else if (currTeam >= numPossTeams) {
									System.out.println("Ended up with more teams than possible??");
								}
							}
						}
					}
				}
			}
		}
		
		//Make sure all teams in returned array are populated
		Team[] finalTeams = helper.generateTeamArray(currTeam, cliqueSize);
		for (int i = 0; i < currTeam; i++) {
			finalTeams[i] = tempTeams[i];
		}

		return finalTeams;
	}
	
	//The leader into the brute force solution. To be updated so it uses the first 15 or so from the greedy clique algorithm.
	public Team[] bruteCliques(Team[] allTeams, ResultScorer scorer, PersonProfile[] profiles) {

		Team[] finalTeams = helper.generateTeamArray(numTeams, cliqueSize);
		boolean[] peepsUsed = new boolean[numNodes];
		
		Team[] newTeam = helper.generateTeamArray(0, cliqueSize);

		finalTeams = recursiveSolnV2(newTeam, allTeams, peepsUsed, scorer,profiles);
		
		return finalTeams;
		
	}
	
	//membersUsed of True means it's used
	public Team[] recursiveSoln(Team[] currTeam, Team[] allTeams, int maxUsed, boolean[] membersUsed, ResultScorer scorer, PersonProfile[] profiles) {
		//NOTE: this takes too long to feasibly run, thus the lacking comments and it not being used.
		Team[] tempTeam = helper.generateTeamArray(numTeams, cliqueSize);
		Team[] finTeam = helper.generateTeamArray(numTeams, cliqueSize);
		double score = 0.0;
		ResultScorer.TeamSetScore tempScorer;
		
		Team[] newCurrTeam = helper.generateTeamArray(currTeam.length + 1, cliqueSize);
		
		for(int x = 0; x < currTeam.length; x++) {
			newCurrTeam[x] = currTeam[x];
		}
		
		if(currTeam.length == 1) {
			System.out.printf("Top level of: %d\n", maxUsed);
		}
		else if (currTeam.length == numTeams/2) {
			System.out.println("At the halfway team mark.");
		}
		else if (numTeams - currTeam.length == 3) {
			System.out.println("3 left checkin.");
		}
		
		boolean finTeamGood = false;
		
		boolean validTeamSelect = true;
		for(int i = maxUsed; i < allTeams.length - numTeams + currTeam.length; i++) {
			
			for(int j : allTeams[i].memberIds) {
				if(membersUsed[j]) {
					validTeamSelect = false;
				}
			}
				
			if(validTeamSelect) {
				for(int j : allTeams[i].memberIds) {
					membersUsed[j] = true;
				}
				
				newCurrTeam[currTeam.length] = allTeams[i];
				
				tempTeam = recursiveSoln(newCurrTeam, allTeams, i, membersUsed, scorer, profiles);
				
				if(finTeamGood) { //If we have a final Team
					tempScorer = scorer.scoreTeams(tempTeam, profiles);
					
					if(tempScorer.pointsSD < score) {
						finTeam = tempTeam;
						score = tempScorer.pointsSD;
					}
				}
				else { 	//If we don't
					finTeam = tempTeam;
					tempScorer = scorer.scoreTeams(tempTeam, profiles);
					score = tempScorer.pointsSD;
					finTeamGood = true;
				}

				
				for(int j : allTeams[i].memberIds) {
					membersUsed[j] = false;
				}
			}
			validTeamSelect = true;
		}
		
		return finTeam;
	}
	
	public Team[] recursiveSolnV2(Team[] currTeam, Team[] allTeams, boolean[] membersUsed, ResultScorer scorer, PersonProfile[] profiles) {
		//NOTE: this takes too long to feasibly run, thus the lacking comments and it not being used.
		//If we have the right number of teams, return the teams
		if(currTeam.length == numTeams) {
//			System.out.println("We returned something!");
			return currTeam;
		}
		//Set up variables
		Team[] tempTeam = helper.generateTeamArray(numTeams, cliqueSize);
		Team[] finTeam = helper.generateTeamArray(numTeams, cliqueSize);
		double score = 0.0;
		ResultScorer.TeamSetScore tempScorer;
		int newAllTeamLen = 0;
		int[] goodTeams = new int[allTeams.length];
		boolean finTeamGood = false;
		boolean keepTeam;
		
		//Start generating the old team
		Team[] newCurrTeam = helper.generateTeamArray(currTeam.length + 1, cliqueSize);
		
		//Copy over the old team
		for(int x = 0; x < currTeam.length; x++) {
			newCurrTeam[x] = currTeam[x];
		}
		
		//Depth checking print statements
		if(currTeam.length == 1) {
			System.out.printf("Top level of: %d\n", currTeam.length);
		}
		else if (currTeam.length == numTeams/2) {
			System.out.println("At the halfway team mark.");
		}
		else if (numTeams - currTeam.length == 3) {
			System.out.println("3 left checkin.");
		}
//		else if (numTeams - currTeam.length == 2) {
//			System.out.println("2 left checkin.");
//		}
//		else if (numTeams - currTeam.length == 1) {
//			System.out.println("1 left checkin.");
//		}
		
		for(int i = 0; i < allTeams.length - numTeams + currTeam.length; i++) {
			//Update the newCurrTeam and corresponding membersUsed
			newCurrTeam[currTeam.length] = allTeams[i]; 
			//Reset how many people are in the length of it
			newAllTeamLen = 0;
			for(int j : allTeams[i].memberIds) {
				membersUsed[j] = true;
			}
			
			//Get all of the good teams in the allTeams
			for(int k = 1; k < allTeams.length; k++) {
				keepTeam = true;
				for(int j : allTeams[k].memberIds) {
					if(membersUsed[j]) {
						keepTeam = false;
					}
				}
				if(keepTeam) {
					goodTeams[newAllTeamLen] = k;
					newAllTeamLen++;
				}
			}
			
			//Generate the new team list
			Team[] newAllTeams = helper.generateTeamArray(newAllTeamLen, cliqueSize);
			//Copy the good teams over
			for(int k = 0; k < newAllTeamLen; k++) {
				newAllTeams[k] = allTeams[goodTeams[k]];
			}
			
			//Recursive bit
			tempTeam = recursiveSolnV2(newCurrTeam, newAllTeams, membersUsed, scorer, profiles);
			
			if(finTeamGood) { //If we have a final Team that's better
				tempScorer = scorer.scoreTeams(tempTeam, profiles);
//				System.out.println("We're comparing something!");
				if(tempScorer.pointsSD < score) {
					finTeam = tempTeam;
					score = tempScorer.pointsSD;
				}
			}
			else { 	//If we don't
				finTeam = tempTeam;
				tempScorer = scorer.scoreTeams(tempTeam, profiles);
				score = tempScorer.pointsSD;
				finTeamGood = true;
			}
			
			for(int j : allTeams[i].memberIds) { //reset which locs are used
				membersUsed[j] = false;
			}
		}
		
		return finTeam;
	}
	

	//Greedy Clique algorithm, where we found the best clique and worked our way down.
	public Team[] greedyCliques(Team[] allTeams, ResultScorer scorer, PersonProfile[] profiles) {
		/*
		 * allTeams: List of all cliques possible (silver bullets included)
		 * scorer: The result Scorer being used for all tests
		 * profiles: The list of personprofiles
		 * return value: Team[], showing all teams
		 */
		//Variable instantiation
		Team[] finalTeams = helper.generateTeamArray(numTeams, cliqueSize);
		boolean[] peepsUsed = new boolean[numNodes];
		boolean[] dontNeed = new boolean[allTeams.length];
		int teamNum = 0;
		boolean teamsDone = false;
		
		//variables whose initial value isn't needed
		double tempScore;
		double closestMatch;
		int teamPick;
		
		ResultScorer.TeamSetScore tempScorer;
		
		//True until we have all teams created 
		while(!teamsDone) {
			closestMatch = 100;
			teamPick = -1;
//			System.out.printf("Currently on team: %d\n", teamNum);
			
			//Loop through every team
			for(int i = 0; i < allTeams.length; i++) {
				if(!dontNeed[i]) { //Make sure that we need the team still
					if (closestMatch > Math.abs(avgScore - allTeams[i].score.skillPointTotal)) { //Check to see if they're the closest to the average. If so, save them
						closestMatch = Math.abs(avgScore - allTeams[i].score.skillPointTotal);
						teamPick = i;
					}
				}
			}
			
			//If we have a valid team we can pick
			if(teamPick > -1) {
				//Save the team
				finalTeams[teamNum] = allTeams[teamPick];
				teamNum++;
				
				//Note that the people are being used
				for(int j : allTeams[teamPick].memberIds) {
					peepsUsed[j] = true;
				}
				//If there are enough teams, exit
				if (teamNum == numTeams) {
					teamsDone = true;
				}
				//Otherwise, update which teams we don't need (if they include one of the ids of the used team, ignore).
				else {
					for (int j = 0; j < allTeams.length; j++) {
						if(!dontNeed[j]) {
							for(int x : allTeams[j].memberIds) {
								if (peepsUsed[x]) {
									dontNeed[j] = true;
								}
							}
						}
					}
				}
			}
			else {
				//Todo: implement backtracking system
				System.out.println("Issue detected in greedyCliques.");
				break;
			}
			
		}
		
		//Some more declaration
		double bestCurrFit;
		double tempFit;
		int teamAddition;
		
		//Handle overflow members (numPeeps % cliqueSize)
		//Add the additional members to the teams that best fit them
		if(numTeams*cliqueSize != numNodes) {
			for(int i = 0; i < peepsUsed.length; i++) {
				bestCurrFit = 0.0;
				teamAddition = -1;
				if(!peepsUsed[i]) {
					for(int j = 0; j < finalTeams.length; j++) { //Find the best team
						tempFit = getFit(finalTeams[j], i);
						if (tempFit > bestCurrFit && finalTeams[j].memberIds.length == cliqueSize) { //Determine if the team being looked at is better and is at the clique size
							bestCurrFit = tempFit;
							teamAddition = j;
						}
					}
					//Add the team
					finalTeams[teamAddition].increaseSize(1);
					finalTeams[teamAddition].memberIds[cliqueSize] = i;
				}
			}
		}		
		return finalTeams;
	}
	
	//Get the fit of a person to a team using the adjacency matrix
	public double getFit(Team team, int person) {
		/*
		 * team: The team being looked at
		 * person: the person's assigned number being added
		 * return val: a double indicating the strength made by multiplying hte different matrix locations.
		 */
		double fit = 1.0;
		for (int i : team.memberIds) {
			fit = fit *adjacency[person][i];
		}
		return fit;
	}

	public int[] highestEdge(double[][] adjacency) {
		/**
		 * Looking at the current adjacency matrix, find the edge with the highest value and return the corresponding nodes
		 * input: adjacency (double[][]) -- The adjacency matrix
		 * output: locs (int[2]) -- locations of nodes whose edge is highest. 
		 */
		int[] locs = new int[2];
		double currHigh = 0.0;
		
		for(int i = 0; i < adjacency.length; i ++) {
			for (int j = 0; j < adjacency[0].length; j++) {
				if (adjacency[i][j] > currHigh && i != j) { //Loop through, finding and updating highest edge
					currHigh = adjacency[i][j];
					locs[0] = i;
					locs[1] = j;
				}
			}
		}
		
		return locs;
	}
	
	
	/*
	 * Do the thing where we have a second Greedy algorithm that Skips the top (numToIgnore) edges for the first two members of a group
	 */
	public int[] highestEdgeV2(double[][] adjacency, int numToIgnore) {
		/**
		 * Looking at the current adjacency matrix, find the edge with the highest value and return the corresponding nodes. New fuctionality: numToIgnore, which allows ignoring of top x edges in an attempt to make a series of teams which work together
		 * input: adjacency (double[][]) -- The adjacency matrix
		 * output: locs (int[2]) -- locations of nodes whose edge is highest. 
		 */
		double currHigh = -1;
		int numIgnored = 0;
		double[] valsIgnored = new double[numToIgnore + 1];
		int[][] locsIgnored = new int[numToIgnore + 1][2];
		double minVal = 0;
		int minloc = 0;
		
		//Loop through every edge possible
		for(int i = 0; i < adjacency.length; i ++) {
			numIgnored = 0;
			for (int j = 0; j < adjacency[0].length; j++) { 
				if (adjacency[i][j] > currHigh && i != j) { //Loop through, finding and updating highest edge
					if(numIgnored < numToIgnore + 1) { //If we haven't ignored enough edges yet, add the current edge to the ignored list
						valsIgnored[numIgnored] = adjacency[i][j];
						locsIgnored[numIgnored][0] = i;
						locsIgnored[numIgnored][1] = j;
						numIgnored = numIgnored + 1;
						if(numIgnored == numToIgnore + 1) {
							minVal = helper.getMinVal(valsIgnored);
						}
					}
					else {
						if (adjacency[i][j] > minVal) {
							minloc = helper.getMinLoc(valsIgnored, minVal);
							
							valsIgnored[minloc] = adjacency[i][j];
							locsIgnored[minloc][0] = i;
							locsIgnored[minloc][1] = j;
							minVal = helper.getMinVal(valsIgnored);
						}
					}
				}
			}
		}
		
		return locsIgnored[minloc];
	}

	
	public int highestNode(double[][] adjacency, int[] neighbors) {
		/**
		 * Looking at the current adjacency matrix, find the edge with the highest value and return the corresponding nodes
		 * input: adjacency (double[][]) -- The adjacency matrix
		 * input: neighbors (int[]) -- A list of the current nodes in the group (or neighbors we are looking at)
		 * output: loc (int) -- location of node who fits best. 
		 */
		int loc = -1;
		double currHigh = 0.0;
		double tempVal;
		for (int x = 0; x < adjacency.length; x++) { //loop through other members to see how good they fit
			tempVal = 1.0;
			for (int j = 0; j < neighbors.length; j++) { //check how they fit with each other group member
				//  Make sure that x isn't a neighbor; make sure there is a neighbor in the slot being looked at; make sure there is a valid connection
				if (!helper.searchArray(neighbors, x) && neighbors[j] != -1 && adjacency[x][neighbors[j]] > 0) {//j isn't in neighbors{
					tempVal = tempVal * adjacency[x][neighbors[j]];
				}
				else {
					tempVal = 0.0;
				}
				
			}
			//If the new calculated value is higher than the old saved one
			if (tempVal > currHigh) {
				currHigh = tempVal;
				loc = x; 
			}
		}
		//Make sure it's a valid location before returning. If it isn't, there's a print statement about it.
		if(currHigh > 0.0) {
			return loc;
		}
		else {
			System.out.println("Impossible!");
			return loc;
		}
	}
	
	//Generates the adjacency matrix from the profiles using our secret formula.
	public double[][] generateAdjacency(PersonProfile[] profiles, double skillsWeight, double preferenceWeight, double projectWeight) {
		double[][] adjacencyArray = new double[profiles.length][profiles.length];
		
		for(int i=0; i<profiles.length; i++) {
			for(int j=0; j<profiles.length; j++) {
				adjacencyArray[i][j] = calculateEdgeWeight(profiles[i], profiles[j], skillsWeight, preferenceWeight, projectWeight);
			}
		}
		
		return adjacencyArray;
	}
	
	/**
	 * Calculates the edge weight from p1 to p2.
	 * @param p1 the profile of the starting node
	 * @param p2 the profile of the ending node
	 * @param skillsWeight how to weight the lack of skills overlap
	 * @param preferenceWeight how to weight the preference overlap
	 * @return the weight of the edge from p1 to p2, where a lower score is more desirable
	 */
	 public double calculateEdgeWeight(PersonProfile p1, PersonProfile p2, double skillsWeight, double preferenceWeight, double projectWeight) {
		 // Silver bullet makes weight 0.
		 int sameProjects = 0;
		 for (int element : p2.preferredProjects) {
			 if (p1.preferredProjects.contains(element)) {
				 sameProjects++;
			 }
		 }
		 
		 return (((p1.silverBullets.contains(p2.id) || p2.silverBullets.contains(p1.id))) ? 0 : skillsWeight * (1-(helper.dotProduct(p1.skills, p2.skills)/p1.skills.length)) + preferenceWeight * (p1.preferredPartners.contains(p2.id) ? 1 : 0) + projectWeight * sameProjects);
	}
	
	public static void main(String args[]) {
		new Graph();
	}
}

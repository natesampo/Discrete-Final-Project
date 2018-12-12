import java.util.Arrays;

public class GreedyCliques {

    private Graph graph;
    private int numTeams;
    private int teamSize;
    private double averageScore;

    public GreedyCliques(Graph graph, int numTeams, int teamSize, double averageScore) {
        this.graph = graph;
        this.numTeams = numTeams;
        this.teamSize = teamSize;
        this.averageScore = averageScore;
    }

    //Returns a matrix with rows showing different teams with the first column being a score out of 100
    public Team[] greedyCliques(PersonProfile[] profiles){
        /*
         * profiles: List of all PersonProfiles
         * return: Team[] of suggested teams
         */

        //Variable declaration
        Graph scratchGraph = new Graph(this.graph);
        int newNeighbor;
        Team[] proposedTeams = Helper.generateTeamArray(numTeams, teamSize);
        boolean[] peepsUsed = new boolean[scratchGraph.getNodeCount()];	//Tracks who is being used in order to add any stragglers

        //Iterate through all teams
        for (int teamNum = 0; teamNum < numTeams; teamNum++) {
            for (int memNum = 0; memNum < teamSize; memNum++) {
                if(memNum == 0) { //If we need to find the highest edge in the adjacency graph (starting a new team)
                    int[] newAdditions = scratchGraph.highestEdgeGreedy(0, true);
                    proposedTeams[teamNum].memberIds[0] = newAdditions[0];
                    proposedTeams[teamNum].memberIds[1] = newAdditions[1];
                    peepsUsed[newAdditions[1]] = true;
                    peepsUsed[newAdditions[0]] = true;
                    memNum++;
                }
                else { //If we want to find the node that best fits with our current team
                    newNeighbor = scratchGraph.highestNode(Arrays.copyOfRange(proposedTeams[teamNum].memberIds, 0, memNum));
                    proposedTeams[teamNum].memberIds[memNum] = newNeighbor;
                    peepsUsed[newNeighbor] = true;
                }
            }

            //Update the editableAdjacency so that people included in one team aren't included in multiple teams
            for (int rowCol = 0; rowCol < graph.getNodeCount(); rowCol++) {
                for (int currMem = 0; currMem < teamSize; currMem++) { //Edit the editableadjacency to make sure we don't select
                    if (proposedTeams[teamNum].memberIds[currMem] >= 0) {
                        scratchGraph.adjacency[proposedTeams[teamNum].memberIds[currMem]][rowCol] = -1;
                        scratchGraph.adjacency[rowCol][proposedTeams[teamNum].memberIds[currMem]] = -1;
                    }
                }
            }
        }
        //If the number of people don't line up, add the extras by finding their best team fit.
        if(numTeams*teamSize != scratchGraph.getNodeCount()) {
            proposedTeams = addExtras(proposedTeams, profiles, peepsUsed);
        }

        return proposedTeams;
    }


    public Team[] runV2(int numToSkip, PersonProfile[] profiles){
        /*
         * profiles: List of all PersonProfiles
         * numToSkip: The number of best connections to skip when forming a team
         * return: Team[] of suggested teams
         * Difference between greedy and greedy v2: Greedy v2 skips the top (numToSkip) edges when picking the next edge to start a clique with
         */

        //Variable Declaration
        Graph scratchGraph = new Graph(this.graph);
        int[] newAdditions;
        int newNeighbor;
        boolean[] peepsUsed = new boolean[scratchGraph.getNodeCount()];
        Team[] proposedTeams = Helper.generateTeamArray(numTeams, teamSize);


        //Iterating through the teams
        for (int teamNum = 0; teamNum < numTeams; teamNum++) {
            //Try to form a team
            for (int memNum = 0; memNum < teamSize; memNum++) {
                if(memNum == 0) { //If we need to find the highest edge (starting a new team)
                    newAdditions = scratchGraph.highestEdgeGreedy(numToSkip, false);
                    proposedTeams[teamNum].memberIds[1] = newAdditions[1];
                    proposedTeams[teamNum].memberIds[0] = newAdditions[0];
                    peepsUsed[newAdditions[1]] = true;
                    peepsUsed[newAdditions[0]] = true;
                    memNum++;
                }
                else { //If we want to find the node that best fits with our current nodes
                    newNeighbor = scratchGraph.highestNode(Arrays.copyOfRange(proposedTeams[teamNum].memberIds, 0, memNum));
                    proposedTeams[teamNum].memberIds[memNum] = newNeighbor;
                    peepsUsed[newNeighbor] = true;
                }
            }
            //Set values to -1 to not be confused in the future.
            for (int rowCol = 0; rowCol < graph.adjacency.length; rowCol++) {
                for (int currMem = 0; currMem < teamSize; currMem++) { //Edit the editableadjacency to make sure we don't select
                    if(proposedTeams[teamNum].memberIds[currMem] >= 0) {
                        scratchGraph.adjacency[proposedTeams[teamNum].memberIds[currMem]][rowCol] = -1;
                        scratchGraph.adjacency[rowCol][proposedTeams[teamNum].memberIds[currMem]] = -1;
                    }
                }
            }
        }

        //Add unused people to teams
        if(numTeams*teamSize != scratchGraph.getNodeCount()) {
            proposedTeams = addExtras(proposedTeams, profiles, peepsUsed);
        }
        return proposedTeams;
    }

    //Greedy Clique algorithm, where we found the best clique and worked our way down.
    public Team[] greedyCliques(Team[] allTeams, PersonProfile[] profiles) {
        /*
         * allTeams: List of all cliques possible (silver bullets included)
         * scorer: The result Scorer being used for all tests
         * profiles: The list of personprofiles
         * return value: Team[], showing all teams
         */
        //Variable instantiation
        Team[] finalTeams = Helper.generateTeamArray(numTeams, teamSize);
        boolean[] peepsUsed = new boolean[graph.getNodeCount()];
        boolean[] dontNeed = new boolean[allTeams.length];
        int teamNum = 0;
        boolean teamsDone = false;

        //variables whose initial value isn't needed
        double closestMatch;
        int teamPick;

        //True until we have all teams created
        while(!teamsDone) {
            closestMatch = 100;
            teamPick = -1;

            //Loop through every team
            for(int i = 0; i < allTeams.length; i++) {
                if(!dontNeed[i]) { //Make sure that we need the team still
                    if (closestMatch > Math.abs(averageScore - allTeams[i].score.skillPointTotal)) { //Check to see if they're the closest to the average. If so, save them
                        closestMatch = Math.abs(averageScore - allTeams[i].score.skillPointTotal);
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
                //TODO: implement backtracking system
                System.out.println("Issue detected in greedyCliques.");
                break;
            }

        }

        //Handle overflow members (numPeeps % teamSize)
        //Add the additional members to the teams that best fit them
        if(numTeams*teamSize != graph.getNodeCount()) {
            allTeams = addExtras(finalTeams, profiles, peepsUsed);

        }
        return finalTeams;
    }

    //Generates all valid cliques from a list of personProfiles
    public Team[] allCliques(PersonProfile[] profiles){
        /*
         * profiles - The list of all person profiles who can be put on teams
         * return: Team[] of all valid teams that can be formed (Mathematicall Choose(number of profiles, teamSize)
         */
        int numPeeps = graph.getNodeCount();
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
            numPossTeams = 3921225; //100 people, the maximum acceptable before it was arbitrarily deemed to be too compute intensive
        }
        Team[] tempTeams = Helper.generateTeamArray(numPossTeams, teamSize);

        int currTeam = 0;

        //Builds up all teams, starting from 1, 2, 3, 4 if valid then incrementing the the last member until it overflows and increments the next
        //The maximum number is the number of people - the slot to the left that the person is
        for (int firstMem = 0; firstMem < numPeeps - 3; firstMem++) {
            for (int secondMem = firstMem + 1; secondMem < numPeeps - 2; secondMem++) {
                if(graph.adjacency[firstMem][secondMem] > 0) {
                    for (int thirdMem = secondMem + 1; thirdMem < numPeeps - 1; thirdMem++) {
                        if(graph.adjacency[firstMem][thirdMem] > 0 && graph.adjacency[secondMem][thirdMem] > 0) {
                            for (int fourthMem = thirdMem + 1; fourthMem < numPeeps; fourthMem++) {
                                if(graph.adjacency[firstMem][fourthMem] > 0 && graph.adjacency[secondMem][fourthMem] > 0 && graph.adjacency[thirdMem][fourthMem] > 0 && currTeam < numPossTeams) {
                                    tempTeams[currTeam].memberIds[0] = firstMem;
                                    tempTeams[currTeam].memberIds[1] = secondMem;
                                    tempTeams[currTeam].memberIds[2] = thirdMem;
                                    tempTeams[currTeam].memberIds[3] = fourthMem;
                                    currTeam++; //Keeps track of the number of used teams
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
        //Number of teams were counted as they were generated.
        Team[] finalTeams = Helper.generateTeamArray(currTeam, teamSize);
        for (int i = 0; i < currTeam; i++) {
            finalTeams[i] = tempTeams[i];
        }

        Helper.shuffleArray(finalTeams, teamSize);
        return finalTeams;
    }

    public Team[] addExtras(Team[] finalTeams, PersonProfile[] profiles, boolean[] peepsUsed) {
        /*
         * finalTeams - The finalized teams withOOUT additional students added
         * profiles - list of person profiles
         * peepsUsed - List indicating which members have and haven't been used in a team
         * return - all teams suggested in the format of Team[]
         */
        //Variable declaration
        double bestCurrFit;
        double tempFit;
        int teamAddition;

        //Iterating through each member, checking if they were used
        for(int i = 0; i < peepsUsed.length; i++) {

            if(!peepsUsed[i]) { //If the person isn't in a team yet
                bestCurrFit = 0.0;
                teamAddition = -1;
                for(int j = 0; j < finalTeams.length; j++) { //Find the best team
                    tempFit = getFit(finalTeams[j], i);
                    if (tempFit > bestCurrFit && finalTeams[j].memberIds.length == teamSize) { //Determine if the team being looked at is better and is at the clique size
                        bestCurrFit = tempFit;
                        teamAddition = j;
                    }
                }
                //Add them to the best team
                finalTeams[teamAddition].increaseSize(1);
                finalTeams[teamAddition].memberIds[teamSize] = i;
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
            fit = fit * graph.adjacency[person][i];
        }
        return fit;
    }

}

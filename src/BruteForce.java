public class BruteForce {
    
    private PersonProfile[] profiles;
    private int teamSize;
    private int numTeams;
    
    public BruteForce(PersonProfile[] profiles, int teamSize) {
        this.profiles = profiles;
        this.teamSize = teamSize;
        this.numTeams = profiles.length / teamSize;
    }


    //The leader into the brute force solution. To be updated so it uses the first 15 or so from the greedy clique algorithm.
    public Team[] bruteCliques(Team[] allTeams, PersonProfile[] profiles, int teamSize) {
        /*
         * allTeams - List of all teams that can be formed from the person profiles
         * scorer - Gives a result scorer in order to know which teams are best
         * profiles - A list of all profiles of students.
         * return: Returns the suggested list of teams
         */

        boolean[] peepsUsed = new boolean[profiles.length];

        Team[] newTeam = Helper.generateTeamArray(0, teamSize);

        return recursiveSolnV2(newTeam, allTeams, peepsUsed, profiles);

    }

    //NOTE: this takes too long to feasibly run, thus the lack of it being run in hte main function.
    public Team[] recursiveSoln(Team[] currTeam, Team[] allTeams, int maxUsed, boolean[] membersUsed, ResultScorer scorer, PersonProfile[] profiles) {
        /*
         * currTeam - list of the current teams generated; ideal is to use the greedy algorithm to solve this part first
         * allTeams - List of all teams that can be formed from the person profiles
         * maxUsed - the maximum used team so far (used for recursion)
         * membersUsed - boolean[], indicating which students are on a team
         * scorer - Gives a result scorer in order to know which teams are best
         * profiles - A list of all profiles of students.
         * return: Returns the suggested list of teams
         */

        //Variable declaration
        Team[] tempTeam = Helper.generateTeamArray(numTeams, teamSize);
        Team[] finTeam = Helper.generateTeamArray(numTeams, teamSize);
        double score = 0.0;
        TeamSetScore tempScorer;
        Team[] newCurrTeam = Helper.generateTeamArray(currTeam.length + 1, teamSize);
        boolean finTeamGood = false;
        boolean validTeamSelect;

        //Copying over the old teams
        for(int x = 0; x < currTeam.length; x++) {
            newCurrTeam[x] = currTeam[x];
        }

        //Update print statements
        if(currTeam.length == 1) {
            System.out.printf("Top level of: %d\n", maxUsed);
        }
        else if (currTeam.length == numTeams/2) {
            System.out.println("At the halfway team mark.");
        }
        else if (numTeams - currTeam.length == 3) {
            System.out.println("3 left checkin.");
        }

        //Looping through the remainder of the teams
        for(int i = maxUsed; i < allTeams.length - numTeams + currTeam.length; i++) {
            validTeamSelect = true; //Reset team validity
            //Looking at the next team, update the list of which members are in a team and make sure all are valid members
            for(int j : allTeams[i].memberIds) {
                if(membersUsed[j]) {
                    validTeamSelect = false;
                }
            }
            //If the team was valid
            if(validTeamSelect) {
                for(int j : allTeams[i].memberIds) {
                    membersUsed[j] = true;
                }

                newCurrTeam[currTeam.length] = allTeams[i];

                tempTeam = recursiveSoln(newCurrTeam, allTeams, i, membersUsed, scorer, profiles);

                if(finTeamGood) { //If we have a final team yet
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
                //Reset member usage as they won't be used in the next set of teams
                for(int j : allTeams[i].memberIds) {
                    membersUsed[j] = false;
                }
            }
        }

        return finTeam;
    }

    //NOTE: this takes too long to feasibly run, thus the lack of it being run in hte main function.
    public Team[] recursiveSolnV2(Team[] currTeam, Team[] allTeams, boolean[] membersUsed, PersonProfile[] profiles) {
        /*
         * currTeam - list of the current teams generated; ideal is to use the greedy algorithm to solve this part first
         * allTeams - List of all teams that can be formed from the person profiles
         * membersUsed - boolean[], indicating which students are on a team
         * scorer - Gives a result scorer in order to know which teams are best
         * profiles - A list of all profiles of students.
         * return: Returns the suggested list of teams
         */
        //If we have the right number of teams, return the teams
        if(currTeam.length == numTeams) {
            return currTeam;
        }

        //Set up variables
        Team[] tempTeam = Helper.generateTeamArray(numTeams, teamSize);
        Team[] finTeam = Helper.generateTeamArray(numTeams, teamSize);
        double score = 0.0;
        TeamSetScore tempScorer;
        int newAllTeamLen = 0;
        int[] goodTeams = new int[allTeams.length];
        boolean finTeamGood = false; //Whether or not we have a final team yet
        boolean keepTeam; //Whether or not we should keep a team

        //Start generating the old team
        Team[] newCurrTeam = Helper.generateTeamArray(currTeam.length + 1, teamSize);

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

        //Loop through the rest of the teams that could be made
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
            Team[] newAllTeams = Helper.generateTeamArray(newAllTeamLen, teamSize);
            //Copy the good teams over
            for(int k = 0; k < newAllTeamLen; k++) {
                newAllTeams[k] = allTeams[goodTeams[k]];
            }

            //BruteForce bit
            tempTeam = recursiveSolnV2(newCurrTeam, newAllTeams, membersUsed, profiles);

            if(finTeamGood) { //If we have a final Team that's better
                tempScorer = ResultScorer.scoreTeams(tempTeam, profiles);
//				System.out.println("We're comparing something!");
                if(tempScorer.pointsSD < score) {
                    finTeam = tempTeam;
                    score = tempScorer.pointsSD;
                }
            }
            else { 	//If we don't
                finTeam = tempTeam;
                tempScorer = ResultScorer.scoreTeams(tempTeam, profiles);
                score = tempScorer.pointsSD;
                finTeamGood = true;
            }

            for(int j : allTeams[i].memberIds) { //reset which locs are used
                membersUsed[j] = false;
            }
        }

        return finTeam;
    }


}

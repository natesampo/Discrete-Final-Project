import java.util.Arrays;
import java.util.HashSet;

public class ColoredCliques {

    private Graph graph;
    private int numTeams;
    private int teamSize;
    private double colorWeight;

    public ColoredCliques(Graph graph, int numTeams, int teamSize, double colorWeight) {
        this.graph = graph;
        this.numTeams = numTeams;
        this.teamSize = teamSize;
        this.colorWeight = colorWeight;
    }

    // Start of colored clique finding by major
    public Team[] run(PersonProfile[] profiles) {
        /*
         * profiles - list of all personProfiles
         * return: Team[] of suggested teams
         */
        Team[] teams = Helper.generateTeamArray(numTeams, teamSize);
        int[] profileMajors = new int[graph.getNodeCount()];
        Graph scratchGraph = new Graph(graph);
        int[] edges;
        HashSet<Integer> completeTeams = new HashSet<Integer>();
        HashSet<Integer> notLeftOut = new HashSet<Integer>();

        // Assign every person a major based on their skills
        // This major will be the 'color' of their node
        for (int i=0; i<graph.getNodeCount(); i++) {

            // Find the largest of their skills, assign that as their major
            double max = 0;
            int maxIndex = -1;
            for (int j=0; j<profiles[i].skills.length; j++) {
                if (profiles[i].skills[j] > max) {
                    max = profiles[i].skills[j];
                    maxIndex = j;
                }
            }

            profileMajors[i] = maxIndex;
        }

        // Pretty much run greedy but take majors into consideration to form a rainbow graph
        for (int i=0; i < numTeams; i++) {
            for (int j=0; j < teamSize; j++) {
                if(j==0) {

                    // Create a new team with the current best pair if team is currently empty
                    edges = scratchGraph.highestEdge();
                    teams[i].memberIds[0] = edges[0];
                    teams[i].memberIds[1] = edges[1];
                    j++;
                    notLeftOut.add(edges[0]);
                    notLeftOut.add(edges[1]);

                    // Reduce the likelihood of choosing another teammate with that major
                    for (int k=0; k<profiles.length; k++) {
                        if (profileMajors[k] == profileMajors[edges[0]] || profileMajors[k] == profileMajors[edges[1]]) {
                            scratchGraph.adjacency[k][edges[0]] = Math.max(scratchGraph.adjacency[k][edges[0]]/colorWeight, 0.01);
                            scratchGraph.adjacency[edges[0]][k] = Math.max(scratchGraph.adjacency[edges[0]][k]/colorWeight, 0.01);
                            scratchGraph.adjacency[k][edges[1]] = Math.max(scratchGraph.adjacency[k][edges[1]]/colorWeight, 0.01);
                            scratchGraph.adjacency[edges[1]][k] = Math.max(scratchGraph.adjacency[edges[1]][k]/colorWeight, 0.01);
                        }
                    }
                }

                else {

                    // If team is not empty, add members to an existing team
                    // Keep trying new people until find one without being silver bulleted

                    boolean silverBulleted = true;
                    int potentialMember = -1;
                    while(silverBulleted) {
                        silverBulleted = false;

                        // See if we can add this person to the team
                        potentialMember = graph.highestNode(Arrays.copyOfRange(teams[i].memberIds, 0, j));

                        // Check every potential teammate for silver bullets to this person
                        for (int k=0; k<teams[i].memberIds.length; k++) {

                            // If someone has silver bulleted this person, go through every other team member and prevent them from picking this person
                            if (profiles[teams[i].memberIds[k]].silverBullets.contains(potentialMember)) {
                                for (int l=0; l<teams[i].memberIds.length; l++) {
                                    graph.adjacency[teams[i].memberIds[l]][potentialMember] = -1;
                                    graph.adjacency[potentialMember][teams[i].memberIds[l]] = -1;
                                }

                                silverBulleted = true;
                            }
                        }
                    }

                    // When we finally find someone who hasn't been silver bulleted, add them to the team
                    teams[i].memberIds[j] = potentialMember;
                    notLeftOut.add(teams[i].memberIds[j]);
                }
            }

            // Stop selecting the same person in the future
            for (int j=0; j<graph.getNodeCount(); j++) {
                for (int k=0; k<teamSize; k++) {
                    graph.adjacency[teams[i].memberIds[k]][j] = -1;
                    graph.adjacency[j][teams[i].memberIds[k]] = -1;
                }
            }
        }

        // Find any leftover people
        HashSet<Integer> leftOut = new HashSet<Integer>();
        for (int j=0; j<profiles.length; j++) {
            if (!notLeftOut.contains(j)) {
                leftOut.add(j);
            }
        }

        // Add any leftover people (basically create the teams of 5)
        for (int j : leftOut) {
            int minTeamIndex = 0;
            double minTeam = 100;
            for (int k=0; k<teams.length; k++) {
                double tempScore = ResultScorer.scoreTeam(teams[k], profiles).skillPointTotal;
                if (!completeTeams.contains(k) && tempScore < minTeam) {
                    minTeam = tempScore;
                    minTeamIndex = k;
                }
            }

            teams[minTeamIndex].increaseSize(1);
            teams[minTeamIndex].memberIds[teamSize] = j;
            completeTeams.add(minTeamIndex);
        }

        return teams;
    }


}

public class TeamFormer {

    private final int MAX_SILVER_BULLETS = 2;
    private final int MAX_PREFERRED_PARTNERS = 6;
    private final int MAX_PREFERRED_PROJECTS = 3;
    private final int TEAM_SIZE = 4;
    private final double SKILLS_WEIGHT = 0.2; // Average dot product looks to be ~1.3
    private final double PREFERENCE_WEIGHT = 0.8;
    private final double PROJECT_WEIGHT = 0.3; // Weight per same project in preferred projects
    //    private final int numProjects = numNodes * 2;
    //    private final int numSkills = 5;
    private int numTeams;
    private Graph graph;

    public TeamFormer() {

        double colorWeight = 1.1;

        //Used for random profile generation
//		final PersonProfile[] profiles = helper.generateProfiles(numPeople, numSkills, MAX_SILVER_BULLETS, MAX_PREFERRED_PARTNERS, numProjects, MAX_PREFERRED_PROJECTS);
        //Used for referencing the POE profiles.
        final PersonProfile[] profiles = CSVReader.readProfiles("Teaming_Anonymized.csv");

        int numPeople = profiles.length;
        int numTeams = (int) java.lang.Math.ceil((numPeople / TEAM_SIZE));
        double avgScore = Helper.averageSkillTotal(profiles) * TEAM_SIZE;


        graph = new Graph(profiles, SKILLS_WEIGHT, PREFERENCE_WEIGHT, PROJECT_WEIGHT);
        graph.adjacency = Helper.normalize(graph.adjacency);


        Team[] randomTeams = Helper.randomTeams(profiles, numTeams, TEAM_SIZE);
        TeamSetScore score0 = ResultScorer.scoreTeams(randomTeams, profiles);
        System.out.println("Result of random teams:");
        ObjectPrinter.printTeamSetScore(score0);
        System.out.println("\n\n");
        Team[] coloredCliques = new ColoredCliques(graph, numTeams, TEAM_SIZE, colorWeight).run(profiles);
        TeamSetScore score1 = ResultScorer.scoreTeams(coloredCliques, profiles);
        System.out.println("Result of colored graph:");
        ObjectPrinter.printTeamSetScore(score1);
        System.out.println("\n\n");
        GreedyCliques gc = new GreedyCliques(graph, numTeams, TEAM_SIZE, avgScore);
        Team[] teamsFromGreedy1 = gc.greedyCliques(profiles);
        TeamSetScore score2 = ResultScorer.scoreTeams(teamsFromGreedy1, profiles);
        System.out.println("Result of first greedy implementation:");
        ObjectPrinter.printTeamSetScore(score2);
        System.out.println("\n\n");
        Team[] teamsFromGreedy2 = gc.runV2(0, profiles);
        TeamSetScore score3 = ResultScorer.scoreTeams(teamsFromGreedy2, profiles);
        System.out.println("Result of second greedy implementation:");
        ObjectPrinter.printTeamSetScore(score3);

        System.out.println("\n\n");
        Team[] teamsFromAllCliques = gc.allCliques(profiles);
        ResultScorer.scoreTeams(teamsFromAllCliques, profiles);
//		Team[] bruteTeams = bruteCliques(teamsFromAllCliques, scorer, profiles);
////		Team[] topTeams = topTeams(teamsFromAllCliques);
//		ObjectPrinter.printTeamArray(bruteTeams);
//		System.out.println("we good.");

        Team[] greedyCliqueTeams = gc.greedyCliques(teamsFromAllCliques, profiles);
//		ObjectPrinter.printTeamArray(greedyCliqueTeams);
        TeamSetScore score4 = ResultScorer.scoreTeams(greedyCliqueTeams, profiles);
        System.out.println("Result of runnable allClique implementation:");
        ObjectPrinter.printTeamSetScore(score4);
        System.out.println("we good.");
        System.out.printf("Avgscore: %f", avgScore);
    }


    public static void main(String[] args) {
        new TeamFormer();
    }

    }

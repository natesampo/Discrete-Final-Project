import java.io.File;
import java.util.LinkedList;

public class TeamFormer {

    private final int MAX_SILVER_BULLETS = 2;
    private final int MAX_PREFERRED_PARTNERS = 6;
    private final int MAX_PREFERRED_PROJECTS = 3;
    private final int TEAM_SIZE = 4;
    private final double SKILLS_WEIGHT = 0.2; // Average dot product looks to be ~1.3
    private final double PARTNER_PREFERENCE_WEIGHT = 0.8;
    private final double PROJECT_PREFERENCE_WEIGHT = 0.3; // Weight per same project in preferred projects
    private Graph graph;

    public TeamFormer(String filename) {

        double[] skillsWeights = new double[]{0.2, 0.4, 0.6};
        double[] partnerWeights = new double[]{0.2, 0.5, 1};
        double[] projectWeights = new double[]{0.2, 0.5, 1};

        double colorWeight = 1.1;
        PersonProfile[] profiles;
        if (filename != null) {
            profiles = CSVReader.readProfiles(filename);
        } else {
            profiles = Helper.generateProfiles(TEAM_SIZE * 10, 3, MAX_SILVER_BULLETS,
                    MAX_PREFERRED_PARTNERS, MAX_PREFERRED_PROJECTS * 3, MAX_PREFERRED_PROJECTS);
        }

        int numPeople = profiles.length;
        int numTeams = (int) java.lang.Math.ceil((numPeople / TEAM_SIZE));
        double avgScore = Helper.averageSkillTotal(profiles) * TEAM_SIZE;

        Team[] randomTeams = Helper.randomTeams(profiles, numTeams, TEAM_SIZE);
        TeamSetScore score0 = ResultScorer.scoreTeams(randomTeams, profiles);
        System.out.printf("The average score of all teams for reference purposes: %f\n\n", avgScore);

        System.out.println("Result of random teams:");
        ObjectPrinter.printTeamSetScore(score0);

        System.out.println("Sweeping weights...");;
        graph = new Graph(profiles, SKILLS_WEIGHT, PARTNER_PREFERENCE_WEIGHT, PROJECT_PREFERENCE_WEIGHT);
        graph.adjacency = Helper.normalize(graph.adjacency);
        GreedyCliques gc = new GreedyCliques(graph, numTeams, TEAM_SIZE, avgScore);
        Team[] teamsFromAllCliques = gc.allCliques(profiles);
        ResultScorer.scoreTeams(teamsFromAllCliques, profiles);
        for (double skillsWeight : skillsWeights) {
            for (double partnerWeight : partnerWeights) {
                for (double projectWeight : projectWeights) {
                    System.out.println("Using skills weight: " + skillsWeight + ", partner weight: " + partnerWeight + ", and skill project weight: " + projectWeight);
                    LinkedList<TeamSetScore> scores = new LinkedList<>();

                    // Generate a graph using the current weights
                    graph = new Graph(profiles, skillsWeight, partnerWeight, projectWeight);
                    graph.adjacency = Helper.normalize(graph.adjacency);

                    // First greedy implementation
                    gc = new GreedyCliques(graph, numTeams, TEAM_SIZE, avgScore);
                    scores.add(ResultScorer.scoreTeams(gc.greedyCliques(profiles), profiles));
//                    TeamSetScore score2 = ResultScorer.scoreTeams(teamsFromGreedy1, profiles);
//                    System.out.println("Result of first greedy implementation:");
//                    ObjectPrinter.printTeamSetScore(score2);

                    // Second greedy implementation
                    scores.add(ResultScorer.scoreTeams(gc.runV2(0, profiles), profiles));
//                    TeamSetScore score3 = ResultScorer.scoreTeams(teamsFromGreedy2, profiles);
//                    System.out.println("Result of second greedy implementation:");
//                    ObjectPrinter.printTeamSetScore(score3);

                    scores.add(ResultScorer.scoreTeams(gc.greedyCliques(teamsFromAllCliques, profiles), profiles));
//		ObjectPrinter.printTeamArray(greedyCliqueTeams);
//                    TeamSetScore score4 = ResultScorer.scoreTeams(greedyCliqueTeams, profiles);
//                    System.out.println("Result of runnable allClique implementation:");
//                    ObjectPrinter.printTeamSetScore(score4);

                    ObjectPrinter.printTeamSetScoreList(scores);
                }
            }
        }

//        Team[] coloredCliques = new ColoredCliques(graph, numTeams, TEAM_SIZE, colorWeight).run(profiles);
//        TeamSetScore score1 = ResultScorer.scoreTeams(coloredCliques, profiles);
//        System.out.println("Result of colored graph:");
//        ObjectPrinter.printTeamSetScore(score1);
//        System.out.println("\n\n");
//        System.out.println("\n\n");
//        GreedyCliques gc = new GreedyCliques(graph, numTeams, TEAM_SIZE, avgScore);

        //If you ever have the compute power, uncomment the below to run the bruteforce clique selection
//		Team[] bruteTeams = bruteCliques(teamsFromAllCliques, scorer, profiles);
////		Team[] topTeams = topTeams(teamsFromAllCliques);
//		ObjectPrinter.printTeamArray(bruteTeams);
//		System.out.println("we good.");

    }


    public static void main(String[] args) {
        if (args.length > 0) {
            String filename = args[0];
            if (new File(filename).exists()) {
                System.out.println("Loading teaming survey data from file...");
                new TeamFormer(filename);
            } else {
                System.out.println("Could not find specified file. Using randomly generated data.");
                new TeamFormer(null);
            }
        } else {
            System.out.println("No teaming data file specified. Using randomly generated data.");
            new TeamFormer(null);
        }
    }

    }

import java.util.stream.IntStream;

public class ResultScorer {

    public static TeamSetScore scoreTeams(Team[] teams, PersonProfile[] profiles) {
        TeamSetScore score = new TeamSetScore(teams.length);
        for (int i = 0; i < teams.length; ++i) {
            Team team = teams[i];
            TeamScore teamScore = scoreTeam(team, profiles);
            score.teamScores[i] = teamScore;
        }
        return score;
    }

    public static TeamScore scoreTeam(Team team, PersonProfile[] profiles) {
        if (team.memberIds.length < 1) {
            throw new IllegalArgumentException("A team must consist of at least 1 member.");
        }

        TeamScore score = new TeamScore();

        int numSkills = profiles[0].skills.length;
        int numMembers = team.memberIds.length;

        // Tally up the total "points" for each skill across all members
        score.pointsBySkillSum = new int[numSkills];

        // Keep track of the min and max ratings for each skill
        score.pointsBySkillMin = new int[numSkills];
        score.pointsBySkillMax = new int[numSkills];


        for (int memberId : team.memberIds) {
            PersonProfile profile = profiles[memberId];
            int[] skills = profile.skills;
            score.pointsBySkillSum = Helper.sumArrays(score.pointsBySkillSum, skills);
            score.pointsBySkillMin = Helper.arrayElementWiseMin(score.pointsBySkillMin, skills);
            score.pointsBySkillMax = Helper.arrayElementWiseMax(score.pointsBySkillMax, skills);
        }

        // Calculate the mean points for each skill
        score.pointsBySkillMean = new double[numSkills];
        for (int i = 0; i < numSkills; ++i) {
            score.pointsBySkillMean[i] = (double)score.pointsBySkillSum[i] / numMembers;
        }

        // Determine the total number of skill "points" possessed by this team
        score.skillPointTotal = IntStream.of(score.pointsBySkillSum).sum();

        // Determine the average skill rating for all members across all skills
        score.meanSkillRating = (double)score.skillPointTotal / (numMembers * numSkills);

        return score;
    }

    public static class TeamSetScore {
        public TeamScore[] teamScores;
        // TODO: Implement overall stats

        public TeamSetScore(int numTeams) {
            teamScores = new TeamScore[numTeams];
        }
    }

    public static class TeamScore {

        // The total number of "points" for all skills across all members.
        public int skillPointTotal;

        // For each skill, the min, max, mean, and sum of the ratings of the members.
        // The lengths of these arrays are the number of skills being tracked.
        public int[] pointsBySkillMin;
        public int[] pointsBySkillMax;
        public double[] pointsBySkillMean;
        public int[] pointsBySkillSum;

        // The average (mean) rating across all skills and across all team members
        public double meanSkillRating;

    }

}

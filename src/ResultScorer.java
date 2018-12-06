import java.util.stream.DoubleStream;

public class ResultScorer {

    public TeamSetScore scoreTeams(Team[] teams, PersonProfile[] profiles) {
        TeamSetScore score = new TeamSetScore(teams.length);
        for (int i = 0; i < teams.length; ++i) {
            Team team = teams[i];
            TeamScore teamScore = scoreTeam(team, profiles);
            score.teamScores[i] = teamScore;
        }
        return score;
    }

    public TeamScore scoreTeam(Team team, PersonProfile[] profiles) {
        if (team.memberIds.length < 1) {
            throw new IllegalArgumentException("A team must consist of at least 1 member.");
        }

        TeamScore score = new TeamScore();

        int numSkills = profiles[0].skills.length;
        int numMembers = team.memberIds.length;

        // Tally up the total "points" for each skill across all members
        score.pointsBySkillSum = new double[numSkills];

        // Keep track of the min and max ratings for each skill
        score.pointsBySkillMin = new double[numSkills];
        score.pointsBySkillMax = new double[numSkills];


        for (int memberId : team.memberIds) {
            if (memberId == -1)
                continue;

            PersonProfile profile = profiles[memberId];
            double[] skills = profile.skills;
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
        score.skillPointTotal = DoubleStream.of(score.pointsBySkillSum).sum();

        // Determine the average skill rating for all members across all skills
        score.meanSkillRating = (double)score.skillPointTotal / (numMembers * numSkills);

        team.score = score;
        return score;
    }

    public class TeamSetScore {
        public TeamScore[] teamScores;
        // TODO: Implement overall stats

        public TeamSetScore(int numTeams) {
            teamScores = new TeamScore[numTeams];
        }
    }

    public class TeamScore {

        // The total number of "points" for all skills across all members.
        public double skillPointTotal;

        // For each skill, the min, max, mean, and sum of the ratings of the members.
        // The lengths of these arrays are the number of skills being tracked.
        public double[] pointsBySkillMin;
        public double[] pointsBySkillMax;
        public double[] pointsBySkillMean;
        public double[] pointsBySkillSum;

        // The average (mean) rating across all skills and across all team members
        public double meanSkillRating;

    }

}

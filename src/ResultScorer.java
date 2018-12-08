import java.util.HashSet;
import java.util.stream.DoubleStream;

public class ResultScorer {

    public TeamSetScore scoreTeams(Team[] teams, PersonProfile[] profiles) {
        TeamSetScore score = new TeamSetScore(teams);

        // First score all the teams and check their validity
        score.allTeamsValid = true;
        for (int i = 0; i < teams.length; ++i) {
            Team team = teams[i];
            TeamScore teamScore = scoreTeam(team, profiles);
            score.teamScores[i] = teamScore;
            score.allTeamsValid = score.allTeamsValid && team.score.isValid;
        }

        // Then compute some summary statistics
        score.totalSkillMin = score.teamScores[0].skillPointTotal; // Initialize this to the first team's total
        int numSkills = teams[0].score.pointsBySkillSum.length;
        double[][] skillPointTotals = new double[teams.length][numSkills];
        for (int i = 0; i < teams.length; ++i) {
            TeamScore teamScore = score.teamScores[i];
            score.totalSkillMin = Math.min(score.totalSkillMin, teamScore.skillPointTotal);
            score.totalSkillMax = Math.max(score.totalSkillMax, teamScore.skillPointTotal);
            System.arraycopy(teamScore.pointsBySkillSum, 0, skillPointTotals[i], 0, teamScore.pointsBySkillSum.length);
        }
        // Finish calculating skill point total SD
        score.pointsBySkillSD = computeStandardDevs(skillPointTotals);

        // TODO: Calculate preference meeting percentage


        return score;
    }

    public TeamScore scoreTeam(Team team, PersonProfile[] profiles) {
        if (team.memberIds.length < 1) {
            throw new IllegalArgumentException("A team must consist of at least 1 member.");
        }

        TeamScore score = new TeamScore();

        // Check for silver bullet violations
        HashSet<Integer> teamMembers = new HashSet<>();
        HashSet<Integer> silverBullets = new HashSet<>();
        for (int memberId : team.memberIds) {
            teamMembers.add(memberId);
            silverBullets.addAll(profiles[memberId].silverBullets);
        }
        teamMembers.removeAll(silverBullets);
        // If a member was removed because they were listed as a silver bullet, then
        // the following will be false
        score.isValid = teamMembers.size() == team.memberIds.length;
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

        score.pointsBySkillRange = Helper.arrayElementWiseRange(score.pointsBySkillMin, score.pointsBySkillMax);
        

        // Calculate the mean and standard deviation for each skill
        score.pointsBySkillMean = new double[numSkills];
        score.pointsBySkillSD = new double[numSkills];
        double SD = 0.0;
        for (int i = 0; i < numSkills; ++i) {
            score.pointsBySkillMean[i] = score.pointsBySkillSum[i] / numMembers;
            for (int memberId : team.memberIds) {
                if (memberId == -1)
                    continue;

                PersonProfile profile = profiles[memberId];
                double[] skills = profile.skills;
                SD += Math.pow(skills[i] - score.pointsBySkillMean[i], 2);
                
            }
            score.pointsBySkillSD[i] = Math.sqrt(SD/team.memberIds.length);
        }

        // Determine the total number of skill "points" possessed by this team
        score.skillPointTotal = DoubleStream.of(score.pointsBySkillSum).sum();

        double avgSkillTotal = score.skillPointTotal/team.memberIds.length;
        double totSD = 0;
        for (int memberId : team.memberIds) {
            if (memberId == -1)
                continue;

            PersonProfile profile = profiles[memberId];
            double[] skills = profile.skills;
            double tempsum = 0;
            for (double skill : skills) {
            	tempsum += skill;
            }
            totSD += Math.pow(avgSkillTotal - tempsum, 2);
            
        }
        score.pointsSD = totSD;

        // Determine the average skill rating for all members across all skills
        score.meanSkillRating = score.skillPointTotal / (numMembers * numSkills);

        team.score = score;
        return score;
    }

    private static double computeStandardDev(double[] arr) {
        double mean = computeMean(arr);

        double runningSum = 0;
        for (double d : arr)
            runningSum += Math.pow(d - mean, 2);

        return Math.sqrt(runningSum / arr.length);
    }

    private static double[] computeStandardDevs(double[][] arr) {
        double[] means = computeMeans(arr);

        // Compute the sum terms
        double[] runningSums = new double[means.length];
        for (double[] d : arr)
            for (int i = 0; i < means.length; ++i)
                runningSums[i] += Math.pow(d[i] - means[i], 2);

        // Divide by N and take the root
        for (int i = 0; i < runningSums.length; ++i)
            runningSums[i] = Math.sqrt(runningSums[i] / arr.length);

        return runningSums;
    }

    private static double[] computeMeans(double[][] arr) {
        double[] res = new double[arr[0].length];

        // Sum up the components
        for (double[] a : arr)
            for (int i = 0; i < a.length; ++i)
                res[i] += a[i];

        // Divide the sum by the size
        for (int i = 0; i < res.length; ++i)
            res[i] = res[i] / arr.length;

        return res;
    }

    private static double computeMean(double[] arr) {
        double tot = 0;
        for (double d : arr)
            tot += d;
        return tot / arr.length;
    }

    public class TeamSetScore {
        public Team[] teams;
        public boolean allTeamsValid;
        public TeamScore[] teamScores;
        public double[] pointsBySkillSD;
        public double totalSkillRange;
        public double totalSkillSD;
        public double totalSkillMin;
        public double totalSkillMax;

        // TODO: Implement overall stats

        public TeamSetScore(Team[] teams) {
            this.teams = teams;
            teamScores = new TeamScore[teams.length];
        }
    }

    public class TeamScore {

        // Keep track of if the team is invalid for any reason (e.g. silver bullets)
        public boolean isValid = true;

        // The total number of "points" for all skills across all members.
        public double skillPointTotal;

        // For each skill, the min, max, mean, and sum of the ratings of the members.
        // The lengths of these arrays are the number of skills being tracked.
        public double[] pointsBySkillMin;
        public double[] pointsBySkillMax;
        public double[] pointsBySkillMean;
        public double[] pointsBySkillSum;
        public double[] pointsBySkillRange;
        public double[] pointsBySkillSD;
        
        public double pointsRange;
        public double pointsSD;

        // The average (mean) rating across all skills and across all team members
        public double meanSkillRating;

    }

}

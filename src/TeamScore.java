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

    // The percentage of partner requests that were met
    public double partnerPreferenceMetPercentage;

}

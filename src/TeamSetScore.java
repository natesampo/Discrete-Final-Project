public class TeamSetScore {
    public Team[] teams;
    public boolean allTeamsValid;
    public TeamScore[] teamScores;
    public double[] pointsBySkillSD;
    public double totalSkillMin;
    public double totalSkillMax;
    public double meanPartPrefsSatisfied;
    public double meanFractionTeamMembersWithCommonProject;

    public double pointsSD;

    public TeamSetScore(Team[] teams) {
        this.teams = teams;
        teamScores = new TeamScore[teams.length];
    }
}

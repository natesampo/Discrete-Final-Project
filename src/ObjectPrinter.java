import java.lang.StringBuilder;

public class ObjectPrinter {

    private static final int DOUBLE_DECIMAL_PLACES = 2;

    public static void printTeamSetScore(TeamSetScore score) {
        printTeamArray(score.teams);

        // Print out the summary statistics
        StringBuilder sb = new StringBuilder();
        sb.append(score.allTeamsValid ? "All teams valid" : "Some teams invalid");
        sb.append("\tTotal skill minimum: ");
        appendDouble(sb, score.totalSkillMin);
        sb.append("\tTotal skill maximum: ");
        appendDouble(sb, score.totalSkillMax);
        sb.append("\tTotal skill range: ");
        appendDouble(sb, score.totalSkillMax - score.totalSkillMin);
        sb.append("\tPoints by skill total SD: ");
        appendArray(sb, score.pointsBySkillSD);
        sb.append('\t');
        sb.append("Total SD: ");
        appendDouble(sb, score.pointsSD);
        sb.append('\t');
        sb.append("Mean percentage partner preferences met: ");
        appendDouble(sb, score.meanPartPrefsSatisfied);

        System.out.println(sb.toString());
    }

    public static void printTeamArray(Team[] teams) {
        for (Team t : teams) {
            printTeam(t);
        }
    }

    public static void printTeam(Team t) {
        StringBuilder sb = new StringBuilder();

        sb.append("Members: ");
        for (int memberId : t.memberIds) {
            sb.append(memberId);
            sb.append('\t');
        }

        sb.append(t.score.isValid ? "Valid   " : "Invalid ");
        sb.append('\t');

        sb.append("Total points: ");
        appendDouble(sb, t.score.skillPointTotal);
        sb.append('\t');

        sb.append("By skill total: ");
        appendArray(sb, t.score.pointsBySkillSum);
        sb.append('\t');

        sb.append("By skill range: ");
        appendArray(sb, t.score.pointsBySkillRange);
        sb.append('\t');

        sb.append("By skill mean: ");
        appendArray(sb, t.score.pointsBySkillMean);
        sb.append('\t');

        sb.append("By skill SD: ");
        appendArray(sb, t.score.pointsBySkillSD);
        sb.append('\t');

        sb.append("Mean skill points: ");
        appendDouble(sb, t.score.meanSkillRating);
        sb.append('\t');

        sb.append("Partner preferences met: ");
        appendDouble(sb, t.score.partnerPreferenceMetPercentage * 100);
        sb.append("%");
        sb.append('\t');

        sb.append("Members with common project: ");
        appendDouble(sb, t.score.fractionTeamMembersWithCommonProject * 100);
        sb.append("%");

        System.out.println(sb.toString());
    }

    private static void appendDouble(StringBuilder sb, double num) {
        sb.append(String.format("%." + DOUBLE_DECIMAL_PLACES + "f", num));
    }

    public static void appendArray(StringBuilder sb, double[] arr) {
        sb.append('[');

        for (int i = 0; i < arr.length; ++i) {
            double n = arr[i];
            appendDouble(sb, n);
            if (i < arr.length - 1)
                sb.append(", ");
        }

        sb.append(']');
    }

}

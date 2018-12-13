import java.lang.StringBuilder;
import java.util.List;

public class ObjectPrinter {

    private static final int DOUBLE_DECIMAL_PLACES = 2;

    public static void printTeamSetScore(TeamSetScore score) {
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

    public static void printTeamSetScoreList(List<TeamSetScore> scores) {
        StringBuilder sb = new StringBuilder();
        sb.append("Valid?\tTot skill min\tTot skill max\tTot skill range\tPts by skill total SD\tTotal SD\tPart prefs met\tCommon proj\n");
        for (TeamSetScore score : scores) {
            sb.append(score.allTeamsValid ? "1" : "0");
            sb.append('\t');
            appendDouble(sb, score.totalSkillMin);
            sb.append('\t');
            appendDouble(sb, score.totalSkillMax);
            sb.append('\t');
            appendDouble(sb, score.totalSkillMax - score.totalSkillMin);
            sb.append('\t');
            appendArray(sb, score.pointsBySkillSD);
            sb.append('\t');
            appendDouble(sb, score.pointsSD);
            sb.append('\t');
            appendDouble(sb, score.meanPartPrefsSatisfied);

            sb.append('\n');
        }
        System.out.println(sb.toString());
    }

    public static void printTeamArray(Team[] teams) {
        System.out.println("Valid?\tTotal pts\tSkill total\tSkill range\tSkill mean\tSkill SD\tMean pts\tPart prefs\tCommon proj");
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

//        sb.append(t.score.isValid ? "Valid   " : "Invalid ");
        sb.append('\t');

//        sb.append("Total points: ");
        appendDouble(sb, t.score.skillPointTotal);
        sb.append('\t');

//        sb.append("By skill total: ");
        appendArray(sb, t.score.pointsBySkillSum);
        sb.append('\t');

//        sb.append("By skill range: ");
        appendArray(sb, t.score.pointsBySkillRange);
        sb.append('\t');

//        sb.append("By skill mean: ");
        appendArray(sb, t.score.pointsBySkillMean);
        sb.append('\t');

//        sb.append("By skill SD: ");
        appendArray(sb, t.score.pointsBySkillSD);
        sb.append('\t');

//        sb.append("Mean skill points: ");
        appendDouble(sb, t.score.meanSkillRating);
        sb.append('\t');

//        sb.append("Partner preferences met: ");
        appendDouble(sb, t.score.partnerPreferenceMetPercentage, DOUBLE_DECIMAL_PLACES * 2);
        sb.append("%");
        sb.append('\t');

//        sb.append("Members with common project: ");
        appendDouble(sb, t.score.fractionTeamMembersWithCommonProject, DOUBLE_DECIMAL_PLACES * 2);
        sb.append("%");

        System.out.println(sb.toString());
    }

    private static void appendDouble(StringBuilder sb, double num, int decimalPlaces) {
        sb.append(String.format("%." + decimalPlaces + "f", num));
    }

    private static void appendDouble(StringBuilder sb, double num) {
        appendDouble(sb, num, DOUBLE_DECIMAL_PLACES);
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

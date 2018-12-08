import java.lang.StringBuilder;

public class ObjectPrinter {

    public static void printTeamArray(Team[] teams) {
        for (Team t : teams) {
            printTeam(t);
        }
    }

    public static void printTeam(Team t) {
        StringBuilder sb = new StringBuilder();
        int doubleDecimalPlaces = 2;

        sb.append("Members: ");
        for (int memberId : t.memberIds) {
            sb.append(memberId);
            sb.append('\t');
        }

        sb.append("Connection strength: ");
        addFormattedDouble(sb, t.connectionStrength, doubleDecimalPlaces);
        sb.append('\t');

        sb.append("Total skill points: ");
        addFormattedDouble(sb, t.score.skillPointTotal, doubleDecimalPlaces);
        sb.append('\t');

        sb.append("Total points by skill: ");
        sb.append(arrayToString(t.score.pointsBySkillSum, doubleDecimalPlaces));
        sb.append('\t');

        sb.append("Mean skill points: ");
        addFormattedDouble(sb, t.score.meanSkillRating, doubleDecimalPlaces);

        System.out.println(sb.toString());
    }

    private static void addFormattedDouble(StringBuilder sb, double num, int decPlaces) {
        sb.append(String.format("%." + decPlaces + "f", num));
    }

    public static String arrayToString(double[] arr, int numDecimalPlaces) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');

        for (int i = 0; i < arr.length; ++i) {
            double n = arr[i];
            addFormattedDouble(sb, n, numDecimalPlaces);
            if (i < arr.length - 1)
                sb.append(", ");
        }

        sb.append(']');
        return sb.toString();
    }

}

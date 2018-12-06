import java.lang.StringBuilder;

public class ObjectPrinter {

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

        sb.append("Connection strength: ");
        sb.append(t.connectionStrength);
        sb.append('\t');

        sb.append("Total skill points: ");
        sb.append(t.score.skillPointTotal);
        sb.append('\t');

        sb.append("Total points by skill: ");
        sb.append(arrayToString(t.score.pointsBySkillSum));
        sb.append('\t');

        sb.append("Mean skill points: ");
        sb.append(t.score.meanSkillRating);

        System.out.println(sb.toString());
    }

    public static String arrayToString(double[] arr) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');

        for (int i = 0; i < arr.length; ++i) {
            double n = arr[i];
            sb.append(n);
            if (i < arr.length - 1)
                sb.append(", ");
        }

        sb.append(']');
        return sb.toString();
    }

}

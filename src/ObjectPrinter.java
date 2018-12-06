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

        System.out.println(sb.toString());
    }

}

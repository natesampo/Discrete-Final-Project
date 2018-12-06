import java.util.HashSet;

public class PersonProfile {
    public int id;
    public double[] skills;
    public HashSet<Integer> preferredPartners;
    public HashSet<Integer> silverBullets;

    public PersonProfile(int id, int numSkills) {
        this.id = id;
        skills = new double[numSkills];
        preferredPartners = new HashSet<>();
        silverBullets = new HashSet<>();
    }
}
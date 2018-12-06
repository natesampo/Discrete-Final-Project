import java.util.HashSet;

public class PersonProfile {
    public int id;
    public int[] skills;
    public HashSet<Integer> preferredPartners;
    public HashSet<Integer> silverBullets;

    public PersonProfile(int id, int numSkills) {
        this.id = id;
        skills = new int[numSkills];
        preferredPartners = new HashSet<>();
        silverBullets = new HashSet<>();
    }
}
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class CSVReader {

    // Delimiters used in the CSV file
    private static final String FIELD_DELIMITER = ";";
    private static final int MAX_NUM_PREFERRED_PROJECTS = 3;
    private static final int MAX_NUM_PREFERRED_PARTNERS = 6;
    private static final int MAX_NUM_SILVER_BULLETS = 2;
    private static final int NUM_SKILLS = 3;
    private static final int START_COL_PREFERRED_PROJECTS = 15;
    private static final int START_COL_PREFERRED_PARTNERS = START_COL_PREFERRED_PROJECTS + MAX_NUM_PREFERRED_PROJECTS;
    private static final int START_COL_SILVER_BULLETS = START_COL_PREFERRED_PARTNERS + MAX_NUM_PREFERRED_PARTNERS;
    private static final int START_COL_META_SKILLS = START_COL_SILVER_BULLETS + MAX_NUM_SILVER_BULLETS;

    public static PersonProfile[] readProfiles(String filename) {
        PersonProfile[] res = null;
        BufferedReader br = null;
        try
        {
            // Read the CSV file
            br = new BufferedReader(new FileReader(filename));

            LinkedList<PersonProfile> profiles = new LinkedList<>();

            // Read the column headers
            String[] headers = br.readLine().split(FIELD_DELIMITER);

            // Read the remaining lines
            String line = "";
            for (int id = 1; (line = br.readLine()) != null; ++id) {
                String[] cells = line.split(FIELD_DELIMITER);

                if (cells.length > 0 ) {
                    profiles.add(parsePerson(id, cells));
                }
            }

            // Convert the LinkedList to an array
            res = profiles.toArray(new PersonProfile[profiles.size()]);

        }
        catch(Exception ee) {
            ee.printStackTrace();
        }
        finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ie) {
                    System.out.println("Error occurred while closing the BufferedReader");
                    ie.printStackTrace();
                }
            }
        }
        return res;
    }

    private static PersonProfile parsePerson(int id, String[] row) {
        PersonProfile p = new PersonProfile(id, NUM_SKILLS);

        // Read preferred projects
        for (int i = 0; i < MAX_NUM_PREFERRED_PROJECTS; ++i) {
            int prefId = Integer.parseInt(row[START_COL_PREFERRED_PROJECTS+i]);
            if (prefId >= 0)
                p.preferredProjects.add(prefId);
        }

        // Read preferred partners
        for (int i = 0; i < MAX_NUM_PREFERRED_PARTNERS; ++i) {
            int prefId = Integer.parseInt(row[START_COL_PREFERRED_PARTNERS+i]);
            if (prefId >= 0)
                p.preferredPartners.add(prefId);
        }

        // Read silver bullets
        for (int i = 0; i < MAX_NUM_SILVER_BULLETS; ++i) {
            int sbId = Integer.parseInt(row[START_COL_SILVER_BULLETS+i]);
            if (sbId >= 0)
                p.silverBullets.add(sbId);
        }

        // Read meta-skill levels
        for (int i = 0; i < NUM_SKILLS; ++i) {
            p.skills[i] = Double.parseDouble(row[START_COL_META_SKILLS+i]);
        }

        return p;
    }
}
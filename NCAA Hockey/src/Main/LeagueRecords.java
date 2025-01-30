package Main;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to hold all-time league season records like Shots, Goals, etc. 
 * Adapted directly from the football LeagueRecords, preserving structure but renaming stats for hockey.
 */
public class LeagueRecords {

    /**
     * Inner class for a record (number, holder, year).
     * Same as original, just renamed the package/class references to hockey.
     */
    public class Record {
        private int number;
        private String holder;
        private int year;

        public Record(int n, String h, int y) {
            number = n;
            holder = h;
            year = y;
        }

        public int getNumber() {
            return number;
        }

        public String getHolder() {
            return holder;
        }

        public int getYear() {
            return year;
        }

        // Used if the team's abbreviation changes
        private void changeAbbr(String newAbbr) {
            String[] split = holder.split(" ");
            holder = newAbbr;
            for (int i = 1; i < split.length; ++i) {
                holder += " " + split[i];
            }
        }
    }

    private HashMap<String, Record> records;

    /**
     * Renamed list of record labels from football to hockey stats.
     * We keep them in the **same order** to preserve the original indexing.
     */
    public final String[] recordsList = {
        // “TEAM” group
        "TEAM",
        "Team GPG",           // formerly "Team PPG"
        "Team Opp GPG",       // formerly "Team Opp PPG"
        "Team Shots/G",       // formerly "Team YPG"
        "Team Opp Shots/G",   // formerly "Team Opp YPG"
        "Team TKW Diff",      // formerly "Team TO Diff"

        // “SEASON” group
        "SEASON",
        "Shots",              // formerly "Pass Yards"
        "Goals",              // formerly "Pass TDs"
        "Steals",             // formerly "Interceptions"
        "Shot Percent",       // formerly "Comp Percent"
        "Skate Dist",         // formerly "Rush Yards"
        "Skate Goals",        // formerly "Rush TDs"
        "Lost Puck",          // formerly "Rush Fumbles"
        "Assist Pts",         // formerly "Rec Yards"
        "Assist Gls",         // formerly "Rec TDs"
        "Assist Percent",     // formerly "Catch Percent"

        // “CAREER” group
        "CAREER",
        "Career Shots",        // formerly "Career Pass Yards"
        "Career Goals",        // formerly "Career Pass TDs"
        "Career Steals",       // formerly "Career Interceptions"
        "Career Skate Dist",   // formerly "Career Rush Yards"
        "Career Skate Goals",  // formerly "Career Rush TDs"
        "Career Lost Puck",    // formerly "Career Rush Fumbles"
        "Career Assist Pts",   // formerly "Career Rec Yards"
        "Career Assist Gls"    // formerly "Career Rec TDs"
    };

    /**
     * Constructor to initialize records from an existing list of CSV strings.
     * Same logic as the original football version.
     */
    public LeagueRecords(ArrayList<String> recordStrings) {
        records = new HashMap<>();
        String[] csv;
        for (String str : recordStrings) {
            csv = str.split(",");
            records.put(csv[0], new Record(Integer.parseInt(csv[1]), csv[2], Integer.parseInt(csv[3])));
        }
    }

    /**
     * Default constructor sets up the records HashMap with placeholder values.
     * Notice how “Team Opp GPG” or “Team Opp Shots/G” are set to 1000 to treat “lower = better.”
     */
    public LeagueRecords() {
        records = new HashMap<>();

        //TEAM group
        records.put("TEAM", null);
        records.put("Team GPG", new Record(0, "XXX", 0));
        records.put("Team Opp GPG", new Record(1000, "XXX", 0));
        records.put("Team Shots/G", new Record(0, "XXX", 0));
        records.put("Team Opp Shots/G", new Record(1000, "XXX", 0));
        records.put("Team TKW Diff", new Record(0, "XXX", 0));

        //SEASON group
        records.put("SEASON", null);
        records.put("Shots", new Record(0, "XXX", 0));
        records.put("Goals", new Record(0, "XXX", 0));
        records.put("Steals", new Record(0, "XXX", 0));
        records.put("Shot Percent", new Record(0, "XXX", 0));
        records.put("Skate Dist", new Record(0, "XXX", 0));
        records.put("Skate Goals", new Record(0, "XXX", 0));
        records.put("Lost Puck", new Record(0, "XXX", 0));
        records.put("Assist Pts", new Record(0, "XXX", 0));
        records.put("Assist Gls", new Record(0, "XXX", 0));
        records.put("Assist Percent", new Record(0, "XXX", 0));

        //CAREER group
        records.put("CAREER", null);
        records.put("Career Shots", new Record(0, "XXX", 0));
        records.put("Career Goals", new Record(0, "XXX", 0));
        records.put("Career Steals", new Record(0, "XXX", 0));
        records.put("Career Skate Dist", new Record(0, "XXX", 0));
        records.put("Career Skate Goals", new Record(0, "XXX", 0));
        records.put("Career Lost Puck", new Record(0, "XXX", 0));
        records.put("Career Assist Pts", new Record(0, "XXX", 0));
        records.put("Career Assist Gls", new Record(0, "XXX", 0));
    }

    /**
     * Checks a record to see if it was broken. “Team Opp GPG” or “Team Opp Shots/G”
     * treat lower as better; all others treat higher as better.
     */
    public void checkRecord(String record, int number, String holder, int year) {
        if (record.equals("Team Opp GPG") || record.equals("Team Opp Shots/G")) {
            // For these, lower = better
            if (records.containsKey(record) && number < records.get(record).getNumber()) {
                records.remove(record);
                records.put(record, new Record(number, holder, year));
            } else if (!records.containsKey(record)) {
                records.put(record, new Record(number, holder, year));
            }
        } else {
            // For all other records, higher = better
            if (records.containsKey(record) && number > records.get(record).getNumber()) {
                records.remove(record);
                records.put(record, new Record(number, holder, year));
            } else if (!records.containsKey(record)) {
                records.put(record, new Record(number, holder, year));
            }
        }
    }

    /**
     * Changes a team abbreviation in the record if the holder had the old abbr.
     */
    public void changeAbbrRecords(String oldAbbr, String newAbbr) {
        Record r;
        for (String s : recordsList) {
            r = records.get(s);
            if (r != null && r.getHolder().split(" ")[0].equals(oldAbbr)) {
                r.changeAbbr(newAbbr);
            }
        }
    }

    /**
     * Returns a CSV string of all records in the order of recordsList.
     */
    public String getRecordsStr() {
        StringBuilder sb = new StringBuilder();
        for (String s : recordsList) {
            sb.append(recordStrCSV(s) + "\n");
        }
        return sb.toString();
    }

    /**
     * Helper for getRecordsStr(), returns a single CSV line for that record.
     */
    private String recordStrCSV(String key) {
        if (records.containsKey(key)) {
            Record r = records.get(key);
            if (r == null) {
                return key + ",-1,-1,-1";
            }
            return key + "," + r.getNumber() + "," + r.getHolder() + "," + r.getYear();
        } else {
            return "ERROR,ERROR,ERROR,ERROR";
        }
    }

    /**
     * Builds a string of any records broken by the given team (abbr) in the given year
     * (ignores any “Career” records).
     */
    public String brokenRecordsStr(int year, String abbr) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Record> e : records.entrySet()) {
            Record rec = e.getValue();
            if (rec != null &&
                rec.getHolder().split(" ")[0].equals(abbr) &&
                rec.getYear() == year &&
                !e.getKey().split(" ")[0].equals("Career")) 
            {
                sb.append(rec.getHolder() + " broke the record for " +
                          e.getKey() + " with " + rec.getNumber() + "!\n");
            }
        }
        return sb.toString();
    }
}


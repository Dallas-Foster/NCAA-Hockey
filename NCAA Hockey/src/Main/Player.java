package Main;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Base hockey player class that others (positions) extend.
 * Has name, overall rating, potential, hockey IQ, durability, etc.
 */
public class Player {
    
    public TeamHockey team;
    public String name;
    public String position;      // e.g. "C", "LW", "RW", "D", "G"
    public int year;             // 1=Freshman, 2=Soph, etc.
    public int ratOvr;          // Overall rating
    public int ratPot;          // Potential
    public int ratHockeyIQ;     // was ratFootIQ in football
    public int ratDur;          // Durability
    public int ratImprovement;  // off-season improvement amount
    public int cost;            // cost for recruiting, etc.

    public int gamesPlayed;
    public int statsWins;
    
    // Reuse these as “won Hobey Baker,” “All-Hockey,” etc.
    public boolean wonHobey;        // was wonHeisman
    public boolean wonAllHockey;    // was wonAllAmerican
    public boolean wonAllConference;

    // Career totals
    public int careerGamesPlayed;
    public int careerHobeys;        // was careerHeismans
    public int careerAllHockey;     // was careerAllAmerican
    public int careerAllConference;
    public int careerWins;

    public boolean isRedshirt;

    public boolean isInjured;

    protected final String[] letterGrades = {
            "F", "F+", "D", "D+", "C", "C+", "B", "B+", "A", "A+"
    };

    public Vector ratingsVector; // For UI or debugging

    /**
     * Returns a year string: “Fr,” “So,” “Jr,” “Sr,” or “RS” for redshirt.
     */
    public String getYrStr() {
        if (year == 0) {
            return "RS";
        } else if (year == 1) {
            return "Fr";
        } else if (year == 2) {
            return "So";
        } else if (year == 3) {
            return "Jr";
        } else if (year == 4) {
            return "Sr";
        }
        return "ERR";
    }

    /**
     * Advance season (e.g., increment year).
     */
    public void advanceSeason() {
        year++;
    }

    /**
     * The “Heisman score” in football is replaced by a “Hobey Score,” 
     * but we keep the same logic: ratOvr * min(10, gamesPlayed).
     */
    public int getHeismanScore() {
        int adjGames = gamesPlayed;
        if (adjGames > 10) adjGames = 10;
        return ratOvr * adjGames;
    }

    /**
     * Returns a short name format: “J. Smith.”
     */
    public String getInitialName() {
        String[] names = name.split(" ");
        if (names.length > 1) {
            return names[0].substring(0,1) + ". " + names[1];
        } else {
            // fallback
            return name;
        }
    }

    /**
     * Returns a string with position, name, year, overall/potential, plus injury if present.
     */
    public String getPosNameYrOvrPot_Str() {
        return position + " " + name + " [" + getYrStr() + "]>" +
               "Ovr: " + ratOvr + ", Pot: " + getLetterGrade(ratPot);
    }

    public String getPosNameYrOvrPot_OneLine() {
        return position + " " + getInitialName() + " [" + getYrStr() + "] " +
               "Ovr: " + ratOvr + ", Pot: " + getLetterGrade(ratPot);
    }

    /**
     * Returns "Pos name [year] Ovr: X"
     */
    public String getPosNameYrOvr_Str() {
        return position + " " + name + " [" + getYrStr() + "] Ovr: " + ratOvr;
    }

    /**
     * Returns "[year] Ovr: X, Pot: Y"
     */
    public String getYrOvrPot_Str() {
        return "[" + getYrStr() + "] Ovr: " + ratOvr +
               ", Pot: " + getLetterGrade(ratPot);
    }

    public String getPosNameYrOvrPot_NoInjury() {
        return position + " " + getInitialName() +
               " [" + getYrStr() + "] Ovr: " + ratOvr +
               ", Pot: " + getLetterGrade(ratPot);
    }

    /**
     * For the “Mock Draft,” we can keep the same structure.
     */
    public String getMockDraftStr() {
        return position + " " + getInitialName() +
               " [" + getYrStr() + "]>" + team.strRep();
    }

    /**
     * Convert a rating to a letter grade (90 -> A, etc.).
     */
    protected String getLetterGrade(String num) {
        int val = Integer.parseInt(num);
        int ind = (val - 50) / 5;
        if (ind > 9) ind = 9;
        if (ind < 0) ind = 0;
        return letterGrades[ind];
    }

    protected String getLetterGradePot(String num) {
        int val = Integer.parseInt(num);
        int ind = val / 10;
        if (ind > 9) ind = 9;
        if (ind < 0) ind = 0;
        return letterGrades[ind];
    }

    /**
     * Overloaded for int rating.
     */
    protected String getLetterGrade(int num) {
        int ind = (num - 50) / 5;
        if (ind > 9) ind = 9;
        if (ind < 0) ind = 0;
        return letterGrades[ind];
    }

    protected String getLetterGradePot(int num) {
        int ind = num / 10;
        if (ind > 9) ind = 9;
        if (ind < 0) ind = 0;
        return letterGrades[ind];
    }

    /**
     * Detailed stats for UI. Overridden in position classes (e.g., PlayerC, PlayerG, etc.).
     */
    public ArrayList<String> getDetailStatsList(int games) {
        return null;
    }

    public ArrayList<String> getDetailAllStatsList(int games) {
        return null;
    }

    /**
     * “Career stats” summary, e.g. “games played, wins, awards.”
     */
    public ArrayList<String> getCareerStatsList() {
        ArrayList<String> pStats = new ArrayList<>();
        pStats.add("Games: " + (gamesPlayed + careerGamesPlayed) +
                " (" + (statsWins + careerWins) + "-" +
                ((gamesPlayed + careerGamesPlayed) - (statsWins + careerWins)) + ")"
                + ">Yrs: " + getYearsPlayed());
        pStats.add("Awards: " + getAwards() + "> ");
        return pStats;
    }

    /**
     * Returns the string of “startYear-endYear” based on player’s year.
     */
    public String getYearsPlayed() {
        int startYear = team.league.getYear() - year + 1;
        int endYear = team.league.getYear();
        return startYear + "-" + endYear;
    }

    /**
     * Summarize the awards: Hobey Baker, All-Hockey, All-Conference, etc.
     */
    public String getAwards() {
        ArrayList<String> awards = new ArrayList<>();
        int hobeyCount = careerHobeys + (wonHobey ? 1 : 0);
        int allH = careerAllHockey + (wonAllHockey ? 1 : 0);
        int ac = careerAllConference + (wonAllConference ? 1 : 0);

        if (hobeyCount > 0) awards.add(hobeyCount + "x POTY");    // “Player of the Year” → Hobey
        if (allH > 0) awards.add(allH + "x All-Hock");           // “All-Hockey”
        if (ac > 0) awards.add(ac + "x All-Conf");

        String awardsStr = "";
        for (int i = 0; i < awards.size(); i++) {
            awardsStr += awards.get(i);
            if (i != awards.size() - 1) awardsStr += ", ";
        }
        return awardsStr;
    }

    /**
     * Info for lineup (overridden in positions).
     */
    public String getInfoForLineup() {
        return null;
    }

    public String getInfoLineupInjury() {
        return getInitialName() + " [" + getYrStr() + "] " +
               "Ovr: " + ratOvr + ", Pot: " + getLetterGrade(ratPot);
    }

    /**
     * Return a “safe” gamesPlayed if it’s 0, to avoid division by zero in children classes.
     */
    public int getGamesPlayed() {
        if (gamesPlayed == 0) return 1;
        else return gamesPlayed;
    }

    /**
     * “getPosNumber” method from football adapted to hockey positions. 
     * You might later expand or remove it entirely.
     */
    public static int getPosNumber(String pos) {
        // Example hockey positions:
        switch (pos) {
            case "C": return 0;
            case "LW": return 1;
            case "RW": return 2;
            case "D": return 3;   // could differentiate LD vs RD if you wish
            case "G": return 4;
            default: return 5;    // fallback
        }
    }
}

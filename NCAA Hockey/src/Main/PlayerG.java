package Main;


import java.util.ArrayList;
import java.util.Vector;

/**
 * Class for the Goalie (G) player in hockey,
 * adapted from the original PlayerRB structure.
 */
public class PlayerG extends PlayerHockey {

    // Three goalie attributes
    public int ratGoaliePositioning;
    public int ratGoalieReflexes;
    public int ratGoalieHnd; // Handling / rebound control

    // Per-season stats
    public int statsShotsFaced;
    public int statsSaves;
    public int statsGoalsAllowed;
    public int statsShutouts;

    // Career totals
    public int careerShotsFaced;
    public int careerSaves;
    public int careerGoalsAllowed;
    public int careerShutouts;

    /**
     * Primary constructor for a new Goalie.
     */
    public PlayerG(String nm, TeamHockey tm, int yr, int pot, int iq,
                   int posn, int refl, int hnd, boolean rs, int dur) {
        team = tm;
        name = nm;
        year = yr;
        gamesPlayed = 0;
        isInjured = false;

        ratGoaliePositioning = posn;
        ratGoalieReflexes = refl;
        ratGoalieHnd = hnd;
        ratHockeyIQ = iq;
        ratDur = dur;
        ratPot = pot;
        ratOvr = (ratGoaliePositioning + ratGoalieReflexes + ratGoalieHnd) / 3;

        isRedshirt = rs;
        if (isRedshirt) {
            year = 0;
        }

        cost = (int)(Math.pow((float)ratOvr - 55,2)/2) + 70 + (int)(Math.random()*100) - 50;

        ratingsVector = new Vector();
        ratingsVector.addElement(name + " (" + getYrStr() + ")");
        ratingsVector.addElement(ratOvr + " (+" + ratImprovement + ")");
        ratingsVector.addElement(ratPot);
        ratingsVector.addElement(ratHockeyIQ);
        ratingsVector.addElement(ratGoaliePositioning);
        ratingsVector.addElement(ratGoalieReflexes);
        ratingsVector.addElement(ratGoalieHnd);

        // Season stats
        statsShotsFaced = 0;
        statsSaves = 0;
        statsGoalsAllowed = 0;
        statsShutouts = 0;
        wonHobey = false;
        wonAllHockey = false;
        wonAllConference = false;
        statsWins = 0;

        // Career stats
        careerShotsFaced = 0;
        careerSaves = 0;
        careerGoalsAllowed = 0;
        careerShutouts = 0;
        careerGamesPlayed = 0;
        careerHobeys = 0;
        careerAllHockey = 0;
        careerAllConference = 0;
        careerWins = 0;

        position = "G";
    }

    /**
     * Constructor with career stats.
     */
    public PlayerG(String nm, TeamHockey tm, int yr, int pot, int iq,
                   int posn, int refl, int hnd, boolean rs, int dur,
                   int cGamesPlayed, int cShotsFaced, int cSaves, 
                   int cGoalsAllowed, int cShutouts,
                   int cHobeys, int cAllHock, int cAC, int cWins) {
        team = tm;
        name = nm;
        year = yr;
        gamesPlayed = 0;
        isInjured = false;

        ratGoaliePositioning = posn;
        ratGoalieReflexes = refl;
        ratGoalieHnd = hnd;
        ratHockeyIQ = iq;
        ratDur = dur;
        ratPot = pot;
        ratOvr = (ratGoaliePositioning + ratGoalieReflexes + ratGoalieHnd) / 3;

        isRedshirt = rs;
        if (isRedshirt) {
            year = 0;
        }

        cost = (int)(Math.pow((float)ratOvr - 55,2)/2) + 70 + (int)(Math.random()*100) - 50;

        ratingsVector = new Vector();
        ratingsVector.addElement(name + " (" + getYrStr() + ")");
        ratingsVector.addElement(ratOvr + " (+" + ratImprovement + ")");
        ratingsVector.addElement(ratPot);
        ratingsVector.addElement(ratHockeyIQ);
        ratingsVector.addElement(ratGoaliePositioning);
        ratingsVector.addElement(ratGoalieReflexes);
        ratingsVector.addElement(ratGoalieHnd);

        statsShotsFaced = 0;
        statsSaves = 0;
        statsGoalsAllowed = 0;
        statsShutouts = 0;
        wonHobey = false;
        wonAllHockey = false;
        wonAllConference = false;
        statsWins = 0;

        careerGamesPlayed = cGamesPlayed;
        careerShotsFaced = cShotsFaced;
        careerSaves = cSaves;
        careerGoalsAllowed = cGoalsAllowed;
        careerShutouts = cShutouts;
        careerHobeys = cHobeys;
        careerAllHockey = cAllHock;
        careerAllConference = cAC;
        careerWins = cWins;

        position = "G";
    }

    /**
     * “Recruit” style constructor, using stars for randomization.
     */
    public PlayerG(String nm, int yr, int stars, TeamHockey tm) {
        name = nm;
        year = yr;
        team = tm;
        gamesPlayed = 0;
        isInjured = false;

        ratPot = (int)(50 + 50*Math.random());
        ratHockeyIQ = (int)(50 + 50*Math.random());
        ratDur = (int)(50 + 50*Math.random());

        ratGoaliePositioning = (int)(60 + yr*5 + stars*5 - 25*Math.random());
        ratGoalieReflexes = (int)(60 + yr*5 + stars*5 - 25*Math.random());
        ratGoalieHnd = (int)(60 + yr*5 + stars*5 - 25*Math.random());
        ratOvr = (ratGoaliePositioning + ratGoalieReflexes + ratGoalieHnd) / 3;

        cost = (int)(Math.pow((float)ratOvr - 55,2)/2) + 70 + (int)(Math.random()*100) - 50;

        ratingsVector = new Vector();
        ratingsVector.addElement(name + " (" + getYrStr() + ")");
        ratingsVector.addElement(ratOvr + " (+" + ratImprovement + ")");
        ratingsVector.addElement(ratPot);
        ratingsVector.addElement(ratHockeyIQ);
        ratingsVector.addElement(ratGoaliePositioning);
        ratingsVector.addElement(ratGoalieReflexes);
        ratingsVector.addElement(ratGoalieHnd);

        statsShotsFaced = 0;
        statsSaves = 0;
        statsGoalsAllowed = 0;
        statsShutouts = 0;
        wonHobey = false;
        wonAllHockey = false;
        wonAllConference = false;
        statsWins = 0;

        careerShotsFaced = 0;
        careerSaves = 0;
        careerGoalsAllowed = 0;
        careerShutouts = 0;
        careerGamesPlayed = 0;
        careerHobeys = 0;
        careerAllHockey = 0;
        careerAllConference = 0;
        careerWins = 0;

        position = "G";
    }

    /**
     * Return a Vector of the in-season goalie stats for UI.
     */
    public Vector getStatsVector() {
        Vector v = new Vector(5);
        v.add(statsShotsFaced);
        v.add(statsSaves);
        v.add(statsGoalsAllowed);
        v.add(statsShutouts);
        float savePct = (statsShotsFaced > 0) ? (float)statsSaves / statsShotsFaced : 0;
        savePct = (float)((int)(savePct * 1000)) / 1000; // e.g. .933
        v.add(savePct);
        return v;
    }

    /**
     * Return rating vector for UI.
     */
    public Vector getRatingsVector() {
        ratingsVector = new Vector();
        ratingsVector.addElement(name + " (" + getYrStr() + ")");
        ratingsVector.addElement(ratOvr + " (+" + ratImprovement + ")");
        ratingsVector.addElement(ratPot);
        ratingsVector.addElement(ratHockeyIQ);
        ratingsVector.addElement(ratGoaliePositioning);
        ratingsVector.addElement(ratGoalieReflexes);
        ratingsVector.addElement(ratGoalieHnd);
        return ratingsVector;
    }

    @Override
    public void advanceSeason() {
        year++;
        int oldOvr = ratOvr;

        ratHockeyIQ += (int)(Math.random()*(ratPot + gamesPlayed - 35))/10;
        ratGoaliePositioning += (int)(Math.random()*(ratPot + gamesPlayed - 35))/10;
        ratGoalieReflexes += (int)(Math.random()*(ratPot + gamesPlayed - 35))/10;
        ratGoalieHnd += (int)(Math.random()*(ratPot + gamesPlayed - 35))/10;

        if (Math.random()*100 < ratPot) {
            ratGoaliePositioning += (int)(Math.random()*(ratPot + gamesPlayed - 40))/10;
            ratGoalieReflexes += (int)(Math.random()*(ratPot + gamesPlayed - 40))/10;
            ratGoalieHnd += (int)(Math.random()*(ratPot + gamesPlayed - 40))/10;
        }

        ratOvr = (ratGoaliePositioning + ratGoalieReflexes + ratGoalieHnd) / 3;
        ratImprovement = ratOvr - oldOvr;

        // Move season stats to career
        careerShotsFaced += statsShotsFaced;
        careerSaves += statsSaves;
        careerGoalsAllowed += statsGoalsAllowed;
        careerShutouts += statsShutouts;
        careerGamesPlayed += gamesPlayed;
        careerWins += statsWins;

        if (wonHobey) careerHobeys++;
        if (wonAllHockey) careerAllHockey++;
        if (wonAllConference) careerAllConference++;

        statsShotsFaced = 0;
        statsSaves = 0;
        statsGoalsAllowed = 0;
        statsShutouts = 0;
    }

    @Override
    public int getHeismanScore() {
        // Weighted formula for a goalie’s “Hobey Score.”
        // Saves are good, goals allowed is bad, shutouts are a big bonus.
        return (int)(statsSaves * 1.5)
             - statsGoalsAllowed * 80
             + statsShutouts * 200;
    }

    @Override
    public ArrayList<String> getDetailStatsList(int games) {
        ArrayList<String> pStats = new ArrayList<>();
        pStats.add("Goals Allowed: " + statsGoalsAllowed + ">Shots Faced: " + statsShotsFaced);
        pStats.add("Saves: " + statsSaves + ">Shutouts: " + statsShutouts);
        float savePct = (statsShotsFaced > 0) ? (float)statsSaves / statsShotsFaced : 0;
        savePct = (float)((int)(savePct * 1000)) / 1000;
        pStats.add("Save%: " + savePct 
                   + ">Games: " + gamesPlayed + " (" + statsWins + "-" + (gamesPlayed - statsWins) + ")");
        pStats.add("Durability: " + getLetterGrade(ratDur) 
                   + ">Hockey IQ: " + getLetterGrade(ratHockeyIQ));
        pStats.add("Positioning: " + getLetterGrade(ratGoaliePositioning) 
                   + ">Reflexes: " + getLetterGrade(ratGoalieReflexes));
        pStats.add("Handling: " + getLetterGrade(ratGoalieHnd) + "> ");
        return pStats;
    }

    @Override
    public ArrayList<String> getDetailAllStatsList(int games) {
        // Use the same approach, then add career stats
        ArrayList<String> pStats = getDetailStatsList(games);
        pStats.add("[B]CAREER STATS:");
        pStats.addAll(getCareerStatsList());
        return pStats;
    }

    @Override
    public ArrayList<String> getCareerStatsList() {
        ArrayList<String> pStats = new ArrayList<>();
        pStats.add("Goals Allowed: " + (statsGoalsAllowed + careerGoalsAllowed) 
                   + ">Shots Faced: " + (statsShotsFaced + careerShotsFaced));
        pStats.add("Saves: " + (statsSaves + careerSaves) 
                   + ">Shutouts: " + (statsShutouts + careerShutouts));

        int totalShots = statsShotsFaced + careerShotsFaced;
        int totalSaves = statsSaves + careerSaves;
        float savePct = (totalShots > 0) ? (float)totalSaves / totalShots : 0;
        pStats.add("Career Save%: " + ((int)(savePct * 1000))/1000.0 + " > ");

        pStats.addAll(super.getCareerStatsList());
        return pStats;
    }

}

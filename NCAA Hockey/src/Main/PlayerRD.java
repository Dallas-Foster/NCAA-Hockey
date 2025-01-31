package Main;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Class for the Right Defense (RD) player in hockey,
 * very similar to PlayerLD, just with position = "RD".
 */
public class PlayerRD extends PlayerHockey {

    public int ratDefAware;
    public int ratDefCheck;
    public int ratDefPositioning;

    // Per-season stats
    public int statsShots;
    public int statsAssists;
    public int statsGoals;
    public int statsLostPuck;

    // Career totals
    public int careerShots;
    public int careerAssists;
    public int careerGoals;
    public int careerLostPuck;

    /**
     * Primary constructor.
     */
    public PlayerRD(String nm, TeamHockey tm, int yr, int pot, int iq,
                    int defAware, int defCheck, int defPos, boolean rs, int dur) {
        team = tm;
        name = nm;
        year = yr;
        gamesPlayed = 0;
        isInjured = false;

        ratDefAware = defAware;
        ratDefCheck = defCheck;
        ratDefPositioning = defPos;
        ratHockeyIQ = iq;
        ratDur = dur;
        ratPot = pot;
        ratOvr = (ratDefAware + ratDefCheck + ratDefPositioning) / 3;

        isRedshirt = rs;
        if (isRedshirt) year = 0;

        cost = (int)(Math.pow((float)ratOvr - 55,2)/2) + 70 + (int)(Math.random()*100) - 50;

        ratingsVector = new Vector();
        ratingsVector.addElement(name + " (" + getYrStr() + ")");
        ratingsVector.addElement(ratOvr + " (+" + ratImprovement + ")");
        ratingsVector.addElement(ratPot);
        ratingsVector.addElement(ratHockeyIQ);
        ratingsVector.addElement(ratDefAware);
        ratingsVector.addElement(ratDefCheck);
        ratingsVector.addElement(ratDefPositioning);

        statsShots = 0;
        statsAssists = 0;
        statsGoals = 0;
        statsLostPuck = 0;
        wonHobey = false;
        wonAllHockey = false;
        wonAllConference = false;
        statsWins = 0;

        careerShots = 0;
        careerAssists = 0;
        careerGoals = 0;
        careerLostPuck = 0;
        careerGamesPlayed = 0;
        careerHobeys = 0;
        careerAllHockey = 0;
        careerAllConference = 0;
        careerWins = 0;

        position = "RD";
    }

    /**
     * Constructor with career stats.
     */
    public PlayerRD(String nm, TeamHockey tm, int yr, int pot, int iq,
                    int defAware, int defCheck, int defPos, boolean rs, int dur,
                    int cGamesPlayed, int cShots, int cAssists, int cGoals, int cLostPuck,
                    int cHobeys, int cAllHock, int cAC, int cWins) {
        team = tm;
        name = nm;
        year = yr;
        gamesPlayed = 0;
        isInjured = false;

        ratDefAware = defAware;
        ratDefCheck = defCheck;
        ratDefPositioning = defPos;
        ratHockeyIQ = iq;
        ratDur = dur;
        ratPot = pot;
        ratOvr = (ratDefAware + ratDefCheck + ratDefPositioning) / 3;

        isRedshirt = rs;
        if (isRedshirt) year = 0;

        cost = (int)(Math.pow((float)ratOvr - 55,2)/2) + 70 + (int)(Math.random()*100) - 50;

        ratingsVector = new Vector();
        ratingsVector.addElement(name + " (" + getYrStr() + ")");
        ratingsVector.addElement(ratOvr + " (+" + ratImprovement + ")");
        ratingsVector.addElement(ratPot);
        ratingsVector.addElement(ratHockeyIQ);
        ratingsVector.addElement(ratDefAware);
        ratingsVector.addElement(ratDefCheck);
        ratingsVector.addElement(ratDefPositioning);

        statsShots = 0;
        statsAssists = 0;
        statsGoals = 0;
        statsLostPuck = 0;
        wonHobey = false;
        wonAllHockey = false;
        wonAllConference = false;
        statsWins = 0;

        careerGamesPlayed = cGamesPlayed;
        careerShots = cShots;
        careerAssists = cAssists;
        careerGoals = cGoals;
        careerLostPuck = cLostPuck;
        careerHobeys = cHobeys;
        careerAllHockey = cAllHock;
        careerAllConference = cAC;
        careerWins = cWins;

        position = "RD";
    }

    /**
     * “Recruit” style constructor.
     */
    public PlayerRD(String nm, int yr, int stars, TeamHockey tm) {
        name = nm;
        year = yr;
        team = tm;
        gamesPlayed = 0;
        isInjured = false;

        ratPot = (int)(50 + 50*Math.random());
        ratHockeyIQ = (int)(50 + 50*Math.random());
        ratDur = (int)(50 + 50*Math.random());

        ratDefAware = (int)(60 + yr*5 + stars*5 - 25*Math.random());
        ratDefCheck = (int)(60 + yr*5 + stars*5 - 25*Math.random());
        ratDefPositioning = (int)(60 + yr*5 + stars*5 - 25*Math.random());
        ratOvr = (ratDefAware + ratDefCheck + ratDefPositioning) / 3;

        cost = (int)(Math.pow((float)ratOvr - 55,2)/2) + 70 + (int)(Math.random()*100) - 50;

        ratingsVector = new Vector();
        ratingsVector.addElement(name + " (" + getYrStr() + ")");
        ratingsVector.addElement(ratOvr + " (+" + ratImprovement + ")");
        ratingsVector.addElement(ratPot);
        ratingsVector.addElement(ratHockeyIQ);
        ratingsVector.addElement(ratDefAware);
        ratingsVector.addElement(ratDefCheck);
        ratingsVector.addElement(ratDefPositioning);

        statsShots = 0;
        statsAssists = 0;
        statsGoals = 0;
        statsLostPuck = 0;
        wonHobey = false;
        wonAllHockey = false;
        wonAllConference = false;
        statsWins = 0;

        careerShots = 0;
        careerAssists = 0;
        careerGoals = 0;
        careerLostPuck = 0;
        careerGamesPlayed = 0;
        careerHobeys = 0;
        careerAllHockey = 0;
        careerAllConference = 0;
        careerWins = 0;

        position = "RD";
    }

    public Vector getStatsVector() {
        Vector v = new Vector(5);
        v.add(statsShots);
        v.add(statsAssists);
        v.add(statsGoals);
        v.add(statsLostPuck);
        float ratio = (statsShots > 0) ? (float)statsAssists / statsShots : 0;
        ratio = (float)((int)(ratio * 100)) / 100;
        v.add(ratio);
        return v;
    }

    public Vector getRatingsVector() {
        ratingsVector = new Vector();
        ratingsVector.addElement(name + " (" + getYrStr() + ")");
        ratingsVector.addElement(ratOvr + " (+" + ratImprovement + ")");
        ratingsVector.addElement(ratPot);
        ratingsVector.addElement(ratHockeyIQ);
        ratingsVector.addElement(ratDefAware);
        ratingsVector.addElement(ratDefCheck);
        ratingsVector.addElement(ratDefPositioning);
        return ratingsVector;
    }

    @Override
    public void advanceSeason() {
        year++;
        int oldOvr = ratOvr;

        ratHockeyIQ += (int)(Math.random()*(ratPot + gamesPlayed - 35))/10;
        ratDefAware += (int)(Math.random()*(ratPot + gamesPlayed - 35))/10;
        ratDefCheck += (int)(Math.random()*(ratPot + gamesPlayed - 35))/10;
        ratDefPositioning += (int)(Math.random()*(ratPot + gamesPlayed - 35))/10;

        if (Math.random() * 100 < ratPot) {
            ratDefAware += (int)(Math.random()*(ratPot + gamesPlayed - 40))/10;
            ratDefCheck += (int)(Math.random()*(ratPot + gamesPlayed - 40))/10;
            ratDefPositioning += (int)(Math.random()*(ratPot + gamesPlayed - 40))/10;
        }
        ratOvr = (ratDefAware + ratDefCheck + ratDefPositioning) / 3;
        ratImprovement = ratOvr - oldOvr;

        careerShots += statsShots;
        careerAssists += statsAssists;
        careerGoals += statsGoals;
        careerLostPuck += statsLostPuck;
        careerGamesPlayed += gamesPlayed;
        careerWins += statsWins;

        if (wonHobey) careerHobeys++;
        if (wonAllHockey) careerAllHockey++;
        if (wonAllConference) careerAllConference++;

        statsShots = 0;
        statsAssists = 0;
        statsGoals = 0;
        statsLostPuck = 0;
    }

    @Override
    public int getHeismanScore() {
        // Weighted formula for an RD in awarding
        return statsGoals * 90 
             + (int)(statsAssists * 2.0)
             - statsLostPuck * 60
             + ratDefAware;
    }

    @Override
    public ArrayList<String> getDetailStatsList(int games) {
        ArrayList<String> pStats = new ArrayList<>();
        pStats.add("Goals: " + statsGoals + ">Lost Puck: " + statsLostPuck);
        pStats.add("Shots: " + statsShots + ">Assists: " + statsAssists);
        pStats.add("Shots/Game: " + (statsShots / getGamesPlayed()) 
                   + ">Assists/Game: " + (statsAssists / getGamesPlayed()));
        pStats.add("Games: " + gamesPlayed 
                   + " (" + statsWins + "-" + (gamesPlayed - statsWins) + ")" 
                   + ">Durability: " + getLetterGrade(ratDur));
        pStats.add("Hockey IQ: " + getLetterGrade(ratHockeyIQ) 
                   + ">Def Aware: " + getLetterGrade(ratDefAware));
        pStats.add("Def Check: " + getLetterGrade(ratDefCheck) 
                   + ">Def Pos: " + getLetterGrade(ratDefPositioning));
        pStats.add(" > ");
        return pStats;
    }

    @Override
    public ArrayList<String> getDetailAllStatsList(int games) {
        ArrayList<String> pStats = getDetailStatsList(games);
        pStats.add("[B]CAREER STATS:");
        pStats.addAll(getCareerStatsList());
        return pStats;
    }

    @Override
    public ArrayList<String> getCareerStatsList() {
        ArrayList<String> pStats = new ArrayList<>();
        pStats.add("Goals: " + (statsGoals + careerGoals) 
                   + ">Lost Puck: " + (statsLostPuck + careerLostPuck));
        pStats.add("Shots: " + (statsShots + careerShots) 
                   + ">Assists: " + (statsAssists + careerAssists));
        int totalShots = statsShots + careerShots;
        int totalAssists = statsAssists + careerAssists;
        int totalGames = getGamesPlayed() + careerGamesPlayed;
        double shotsPerGame = (totalGames > 0) ? (double)totalShots / totalGames : 0.0;
        double assistsPerGame = (totalGames > 0) ? (double)totalAssists / totalGames : 0.0;
        pStats.add("Shots/Game: " + (int)(shotsPerGame*10)/10.0 
                   + ">Assists/Game: " + (int)(assistsPerGame*10)/10.0);
        pStats.addAll(super.getCareerStatsList());
        return pStats;
    }

}

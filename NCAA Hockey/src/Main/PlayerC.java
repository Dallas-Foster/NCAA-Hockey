package Main;


import java.util.ArrayList;
import java.util.Vector;

/**
 * Class for the Center (C) player in ice hockey,
 * adapted from the original PlayerRB class structure.
 */
public class PlayerC extends PlayerHockey {
    
    // Original "RB" attributes mapped to hockey attributes:
    // - ratRushPow -> ratShotPow
    // - ratRushSpd -> ratSpeed
    // - ratRushEva -> ratPuckControl
    public int ratShotPow;      // Ability to take powerful shots
    public int ratSpeed;        // Skating speed
    public int ratPuckControl;  // Ability to maintain control & deke

    // Stats (per-season)
    // Original: statsRushAtt, statsRushYards, statsTD, statsFumbles
    // For hockey: statsShots, statsAssists, statsGoals, statsLostPuck
    public int statsShots;
    public int statsAssists;
    public int statsGoals;
    public int statsLostPuck;

    // Career totals
    // Original: careerRushAtt, careerRushYards, careerTDs, careerFumbles
    public int careerShots;
    public int careerAssists;
    public int careerGoals;
    public int careerLostPuck;

    /**
     * Constructor for a new Center.
     */
    public PlayerC(String nm, TeamHockey tm, int yr, int pot, int iq, 
                   int sPow, int spd, int pc, boolean rs, int dur) {
        team = tm;
        name = nm;
        year = yr;
        gamesPlayed = 0;
        isInjured = false;

        // Overall rating is average of the 3 attributes
        ratOvr = (sPow + spd + pc) / 3;
        ratPot = pot;
        ratHockeyIQ = iq;
        ratDur = dur;
        ratShotPow = sPow;
        ratSpeed = spd;
        ratPuckControl = pc;

        isRedshirt = rs;
        if (isRedshirt) {
            year = 0;
        }

        // Cost formula from the old code 
        cost = (int)(Math.pow((float)ratOvr - 55,2)/2) + 70 + (int)(Math.random()*100) - 50;

        // Keep a Vector of ratings for UI
        ratingsVector = new Vector();
        ratingsVector.addElement(name + " (" + getYrStr() + ")");
        ratingsVector.addElement(ratOvr + " (+" + ratImprovement + ")");
        ratingsVector.addElement(ratPot);
        ratingsVector.addElement(ratHockeyIQ);
        ratingsVector.addElement(ratShotPow);
        ratingsVector.addElement(ratSpeed);
        ratingsVector.addElement(ratPuckControl);

        // Initialize per-season stats
        statsShots = 0;
        statsAssists = 0;
        statsGoals = 0;
        statsLostPuck = 0;
        wonHobey = false;       // was wonHeisman
        wonAllHockey = false;   // was wonAllAmerican
        wonAllConference = false;
        statsWins = 0;

        // Initialize career totals
        careerShots = 0;
        careerAssists = 0;
        careerGoals = 0;
        careerLostPuck = 0;
        careerGamesPlayed = 0;
        careerHobeys = 0;       // was careerHeismans
        careerAllHockey = 0;    // was careerAllAmerican
        careerAllConference = 0;
        careerWins = 0;

        // Position label for a Center
        position = "C";
    }

    /**
     * Constructor including some career stats. 
     */
    public PlayerC(String nm, TeamHockey tm, int yr, int pot, int iq, 
                   int sPow, int spd, int pc, boolean rs, int dur,
                   int cGamesPlayed, int cShots, int cAssists, int cGoals, int cLostPuck,
                   int cHobeys, int cAllHock, int cAC, int cWins) {
        team = tm;
        name = nm;
        year = yr;
        gamesPlayed = 0;
        isInjured = false;

        ratOvr = (sPow + spd + pc) / 3;
        ratPot = pot;
        ratHockeyIQ = iq;
        ratDur = dur;
        ratShotPow = sPow;
        ratSpeed = spd;
        ratPuckControl = pc;

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
        ratingsVector.addElement(ratShotPow);
        ratingsVector.addElement(ratSpeed);
        ratingsVector.addElement(ratPuckControl);

        // Current-season stats
        statsShots = 0;
        statsAssists = 0;
        statsGoals = 0;
        statsLostPuck = 0;
        wonHobey = false;
        wonAllHockey = false;
        wonAllConference = false;
        statsWins = 0;

        // Career stats
        careerShots = cShots;
        careerAssists = cAssists;
        careerGoals = cGoals;
        careerLostPuck = cLostPuck;
        careerGamesPlayed = cGamesPlayed;
        careerHobeys = cHobeys;
        careerAllHockey = cAllHock;
        careerAllConference = cAC;
        careerWins = cWins;

        position = "C";
    }

    /**
     * “Recruit” style constructor, using stars to randomize initial attributes.
     */
    public PlayerC(String nm, int yr, int stars, TeamHockey tm) {
        name = nm;
        year = yr;
        team = tm;
        gamesPlayed = 0;
        isInjured = false;

        ratPot = (int)(50 + 50*Math.random());
        ratHockeyIQ = (int)(50 + 50*Math.random());
        ratDur = (int)(50 + 50*Math.random());

        // Slight randomization around (60 + year*5 + stars*5)
        ratShotPow = (int)(60 + yr*5 + stars*5 - 25*Math.random());
        ratSpeed = (int)(60 + yr*5 + stars*5 - 25*Math.random());
        ratPuckControl = (int)(60 + yr*5 + stars*5 - 25*Math.random());

        ratOvr = (ratShotPow + ratSpeed + ratPuckControl) / 3;

        cost = (int)(Math.pow((float)ratOvr - 55,2)/2) + 70 + (int)(Math.random()*100) - 50;

        ratingsVector = new Vector();
        ratingsVector.addElement(name + " (" + getYrStr() + ")");
        ratingsVector.addElement(ratOvr + " (+" + ratImprovement + ")");
        ratingsVector.addElement(ratPot);
        ratingsVector.addElement(ratHockeyIQ);
        ratingsVector.addElement(ratShotPow);
        ratingsVector.addElement(ratSpeed);
        ratingsVector.addElement(ratPuckControl);

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

        position = "C";
    }

    /**
     * Return a small Vector of the player's in-season stats, for UI.
     */
    public Vector getStatsVector() {
        Vector v = new Vector(5);
        v.add(statsShots);
        v.add(statsAssists);
        v.add(statsGoals);
        v.add(statsLostPuck);
        // Example of “Shots per Game” or “Shot Accuracy,” etc.:
        // For football we had yards/attempt; here we might do assists/shots or something.
        float ratio = (statsShots > 0) ? (float)statsAssists / statsShots : 0;
        ratio = (float)((int)(ratio * 100)) / 100;
        v.add(ratio);
        return v;
    }

    /**
     * Return a fresh rating vector for display.
     */
    public Vector getRatingsVector() {
        ratingsVector = new Vector();
        ratingsVector.addElement(name + " (" + getYrStr() + ")");
        ratingsVector.addElement(ratOvr + " (+" + ratImprovement + ")");
        ratingsVector.addElement(ratPot);
        ratingsVector.addElement(ratHockeyIQ);
        ratingsVector.addElement(ratShotPow);
        ratingsVector.addElement(ratSpeed);
        ratingsVector.addElement(ratPuckControl);
        return ratingsVector;
    }

    /**
     * Advance the season, developing attributes, carrying over stats to career totals.
     */
    @Override
    public void advanceSeason() {
        year++;
        int oldOvr = ratOvr;

        // Each attribute gets a small boost based on potential & games played
        ratHockeyIQ += (int)(Math.random()*(ratPot + gamesPlayed - 35))/10;
        ratShotPow += (int)(Math.random()*(ratPot + gamesPlayed - 35))/10;
        ratSpeed += (int)(Math.random()*(ratPot + gamesPlayed - 35))/10;
        ratPuckControl += (int)(Math.random()*(ratPot + gamesPlayed - 35))/10;

        // Occasional “breakthrough”
        if (Math.random()*100 < ratPot) {
            ratShotPow += (int)(Math.random()*(ratPot + gamesPlayed - 40))/10;
            ratSpeed += (int)(Math.random()*(ratPot + gamesPlayed - 40))/10;
            ratPuckControl += (int)(Math.random()*(ratPot + gamesPlayed - 40))/10;
        }

        ratOvr = (ratShotPow + ratSpeed + ratPuckControl) / 3;
        ratImprovement = ratOvr - oldOvr;

        // Carry season stats over to career
        careerShots += statsShots;
        careerAssists += statsAssists;
        careerGoals += statsGoals;
        careerLostPuck += statsLostPuck;
        careerGamesPlayed += gamesPlayed;
        careerWins += statsWins;

        if (wonHobey) careerHobeys++;
        if (wonAllHockey) careerAllHockey++;
        if (wonAllConference) careerAllConference++;

        // Reset this year's stats
        statsShots = 0;
        statsAssists = 0;
        statsGoals = 0;
        statsLostPuck = 0;
    }

    /**
     * “Heisman Score” → “Hobey Score” logic. 
     * You might weigh goals, assists, lostPuck differently.
     */
    @Override
    public int getHeismanScore() {
        // Weighted formula: goals more valuable, lostPuck penalizes
        return statsGoals * 100 
             + (int)(statsAssists * 2.35)
             - statsLostPuck * 80;
    }

    /**
     * Detailed stats for UI.
     */
    @Override
    public ArrayList<String> getDetailStatsList(int games) {
        ArrayList<String> pStats = new ArrayList<>();
        // e.g. “Goals: X > LostPuck: Y”
        pStats.add("Goals: " + statsGoals + ">Lost Puck: " + statsLostPuck);
        pStats.add("Shots: " + statsShots + ">Assists: " + statsAssists);
        // Example “Shots/Game”
        pStats.add("Shots/Game: " + (statsShots / getGamesPlayed()) + ">Assists/Game: " + (statsAssists / getGamesPlayed()));
        // Games & Durability
        pStats.add("Games: " + gamesPlayed + " (" + statsWins + "-" + (gamesPlayed-statsWins) + ")" 
                   + ">Durability: " + getLetterGrade(ratDur));
        pStats.add("Hockey IQ: " + getLetterGrade(ratHockeyIQ) + ">Shot Pow: " + getLetterGrade(ratShotPow));
        pStats.add("Skate Spd: " + getLetterGrade(ratSpeed) + ">Puck Ctrl: " + getLetterGrade(ratPuckControl));
        pStats.add(" > ");
        return pStats;
    }

    /**
     * Detailed stats including career data.
     */
    @Override
    public ArrayList<String> getDetailAllStatsList(int games) {
        ArrayList<String> pStats = new ArrayList<>();
        pStats.add("Goals: " + statsGoals + ">Lost Puck: " + statsLostPuck);
        pStats.add("Shots: " + statsShots + ">Assists: " + statsAssists);
        pStats.add("Shots/Game: " + (statsShots / getGamesPlayed()) + ">Assists/Game: " + (statsAssists / getGamesPlayed()));
        pStats.add("Games: " + gamesPlayed + " (" + statsWins + "-" + (gamesPlayed-statsWins) + ")" 
                   + ">Durability: " + getLetterGrade(ratDur));
        pStats.add("Hockey IQ: " + getLetterGrade(ratHockeyIQ) + ">Shot Pow: " + getLetterGrade(ratShotPow));
        pStats.add("Skate Spd: " + getLetterGrade(ratSpeed) + ">Puck Ctrl: " + getLetterGrade(ratPuckControl));

        pStats.add("[B]CAREER STATS:");
        pStats.addAll(getCareerStatsList());
        return pStats;
    }

    /**
     * Gather career stats, then call super's careerStatsList for games, awards, etc.
     */
    @Override
    public ArrayList<String> getCareerStatsList() {
        ArrayList<String> pStats = new ArrayList<>();
        pStats.add("Goals: " + (statsGoals + careerGoals) + ">Lost Puck: " + (statsLostPuck + careerLostPuck));
        pStats.add("Shots: " + (statsShots + careerShots) + ">Assists: " + (statsAssists + careerAssists));
        // e.g. Shots/Game across entire career
        int totalShots = statsShots + careerShots;
        int totalAssists = statsAssists + careerAssists;
        int totalGames = getGamesPlayed() + careerGamesPlayed;
        double shotsPerGame = (totalGames > 0) ? (double)totalShots / totalGames : 0.0;
        double assistsPerGame = (totalGames > 0) ? (double)totalAssists / totalGames : 0.0;
        pStats.add("Shots/Game: " + (int)(shotsPerGame * 10)/10.0 + 
                   ">Assists/Game: " + (int)(assistsPerGame * 10)/10.0);

        // Add the base class's career stats info (games, wins, awards, etc.)
        pStats.addAll(super.getCareerStatsList());
        return pStats;
    }

}

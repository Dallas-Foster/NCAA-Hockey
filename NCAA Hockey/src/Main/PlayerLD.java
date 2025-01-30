package Main;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Class for the Left Defense (LD) player in hockey.
 */
public class PlayerLD extends PlayerHockey {

    // Defensive skill attributes
    public int ratDefAware;       // Defensive Awareness
    public int ratDefCheck;       // Checking / Hitting
    public int ratDefPositioning; // Positional defense

    // Per-season stats (we keep Shots, Assists, Goals, LostPuck for simplicity)
    public int statsShots;
    public int statsAssists;
    public int statsGoals;
    public int statsLostPuck;

    // Career totals
    public int careerShots;
    public int careerAssists;
    public int careerGoals;
    public int careerLostPuck;

    public PlayerLD(String nm, TeamHockey tm, int yr, int pot, int iq,
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
        // Overall rating
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

        // Stats
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

        position = "LD";
    }

    // Overloaded constructor with career stats...
    // “Recruit” constructor...
    // [Omitted for brevity, but same pattern as above classes]

    @Override
    public void advanceSeason() {
        year++;
        int oldOvr = ratOvr;

        ratHockeyIQ += (int)(Math.random()*(ratPot + gamesPlayed - 35))/10;
        ratDefAware += (int)(Math.random()*(ratPot + gamesPlayed - 35))/10;
        ratDefCheck += (int)(Math.random()*(ratPot + gamesPlayed - 35))/10;
        ratDefPositioning += (int)(Math.random()*(ratPot + gamesPlayed - 35))/10;

        if (Math.random()*100 < ratPot) {
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
        // For a defenseman, you might weigh fewer goals but reward “assists” or “defAware.” 
        // We'll do a simple formula:
        return statsGoals * 90
             + (int)(statsAssists * 2.0)
             - statsLostPuck * 60
             + ratDefAware * 2;
    }

    // The rest of the methods (getDetailStatsList, getDetailAllStatsList, getCareerStatsList, etc.)
    // are exactly the same structure, referencing statsShots, statsAssists, statsGoals, statsLostPuck
    // plus the defensive attribute letter grades.
}

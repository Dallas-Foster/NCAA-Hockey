package NCAAHockeySimPack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Class for storing hockey games, adapted directly from the football code.
 * It preserves the same structure (down, yard line, etc.) but with hockey
 * position names (C, LW, RW, LD, RD, G) and hockey stat placeholders.
 */
public class GameHockey implements Serializable {

    public TeamHockey homeTeam;
    public TeamHockey awayTeam;

    public boolean hasPlayed;

    public String gameName;

    // Score tracking
    public int homeScore;
    public int[] homePeriodScore; // formerly homeQScore
    public int awayScore;
    public int[] awayPeriodScore; // formerly awayQScore

    // Example “total shots” in place of “yards”
    public int homeShots;
    public int awayShots;

    // We reuse 'numOT' to mean number of OT sessions
    public int numOT;

    // Turnovers → use for “takeaways” or “lost-puck” events
    public int homeTakeaways;
    public int awayTakeaways;

    // STATS ARRAYS (renamed from QB, RB1, RB2, WR1, WR2, WR3, K)
    // In hockey: C, LW1, LW2, RW1, LD, RD, G
    // The sizes and indexing remain the same, but the naming is changed.
    public int[] HomeCStats;    // was HomeQBStats
    public int[] AwayCStats;    // was AwayQBStats

    public int[] HomeLW1Stats;  // was HomeRB1Stats
    public int[] HomeLW2Stats;  // was HomeRB2Stats
    public int[] AwayLW1Stats;  // was AwayRB1Stats
    public int[] AwayLW2Stats;  // was AwayRB2Stats

    public int[] HomeRW1Stats;  // was HomeWR1Stats
    public int[] HomeLDStats;   // was HomeWR2Stats
    public int[] HomeRDStats;   // was HomeWR3Stats
    public int[] AwayRW1Stats;  // was AwayWR1Stats
    public int[] AwayLDStats;   // was AwayWR2Stats
    public int[] AwayRDStats;   // was AwayWR3Stats

    public int[] HomeGStats;    // was HomeKStats
    public int[] AwayGStats;    // was AwayKStats

    // Store references to the actual players
    private PlayerC homeC;
    private PlayerC awayC;
    private PlayerLW[] homeLWs;
    private PlayerLW[] awayLWs;
    private PlayerD[] homeDs;  // can store RW1 or LD/RD as “D” in a pinch
    private PlayerD[] awayDs;
    private PlayerG homeG;
    private PlayerG awayG;

    // Game logs
    String gameEventLog;
    String goalInfo; // replaced tdInfo

    // The variables used when “simming” games
    private int gameTime;    // 0–3600 for 4 quarters, but we keep it
    private boolean gamePoss; // 1 if home, 0 if away
    private int gameYardLine; // reuse as a “zone” in hockey
    private int gameDown;     // meaningless in hockey, but we keep it
    private int gameYardsNeed;
    private boolean playingOT;
    private boolean bottomOT;

    /**
     * Create game with a name (like a special event).
     */
    public GameHockey(TeamHockey home, TeamHockey away, String name) {
        homeTeam = home;
        awayTeam = away;

        gameName = name;

        homeScore = 0;
        homePeriodScore = new int[10];
        awayScore = 0;
        awayPeriodScore = new int[10];
        numOT = 0;

        homeTakeaways = 0;
        awayTakeaways = 0;

        // Initialize arrays: “CStats” has size 6, “LWStats” size 4, “RW/LD/RDStats” size 6, “GStats” size 6
        HomeCStats = new int[6];
        AwayCStats = new int[6];

        HomeLW1Stats = new int[4];
        HomeLW2Stats = new int[4];
        AwayLW1Stats = new int[4];
        AwayLW2Stats = new int[4];

        HomeRW1Stats = new int[6];
        HomeLDStats = new int[6];
        HomeRDStats = new int[6];
        AwayRW1Stats = new int[6];
        AwayLDStats = new int[6];
        AwayRDStats = new int[6];

        HomeGStats = new int[6];
        AwayGStats = new int[6];

        hasPlayed = false;
        if (gameName.equals("In Conf") && (homeTeam.rivalTeam.equals(awayTeam.abbr)
                || awayTeam.rivalTeam.equals(homeTeam.abbr))) {
            gameName = "Rivalry Game";
        }
    }

    /**
     * Create a regular hockey game without a special name.
     */
    public GameHockey(TeamHockey home, TeamHockey away) {
        this(home, away, "");
    }

    /**
     * Gets the game summary, with stats and game log, for UI.
     * We keep the same structure: 4 strings (left, center, right, log).
     */
    public String[] getGameSummaryStr() {
        String[] gameSum = new String[4];
        StringBuilder gameL = new StringBuilder();
        StringBuilder gameC = new StringBuilder();
        StringBuilder gameR = new StringBuilder();

        // Basic scoreboard (renamed “Yards” → “Shots,” etc.)
        gameL.append("\nGoals\nShots\nTakeaways\n\n");
        gameC.append("#" + awayTeam.rankTeamPollScore + " " + awayTeam.abbr + "\n" + awayScore + "\n" + awayShots + " SOG\n"
                + awayTakeaways + " TKW\n\n");
        gameR.append("#" + homeTeam.rankTeamPollScore + " " + homeTeam.abbr + "\n" + homeScore + "\n" + homeShots + " SOG\n"
                + homeTakeaways + " TKW\n\n");

        // C (Center) stats (renamed from QBs)
        gameL.append("Centers\nName\nYr Ovr/Pot\nGoals/Assists\nShots\nShots on Net\n");
        gameC.append(awayTeam.abbr + "\n" + awayC.getInitialName() + "\n");
        gameC.append(awayC.getYrStr() + " " + awayC.ratOvr + "/" + awayC.ratPot + "\n");
        // Using [2]=Goals, [3]=Assists, [0]=Shots, [1]=ShotsOnNet as an example
        gameC.append(AwayCStats[2] + "/" + AwayCStats[3] + "\n"); 
        gameC.append(AwayCStats[0] + " total shots\n");
        gameC.append(AwayCStats[1] + " SOG\n");
        gameR.append(homeTeam.abbr + "\n" + homeC.getInitialName() + "\n");
        gameR.append(homeC.getYrStr() + " " + homeC.ratOvr + "/" + homeC.ratPot + "\n");
        gameR.append(HomeCStats[2] + "/" + HomeCStats[3] + "\n");
        gameR.append(HomeCStats[0] + " total shots\n");
        gameR.append(HomeCStats[1] + " SOG\n");

        // LW1
        gameL.append("\nLW1\nName\nYr Ovr/Pot\nGoals/LPuck\nShots\n");
        gameC.append("\n" + awayTeam.abbr + "\n" + awayLWs[0].getInitialName() + "\n");
        gameC.append(awayLWs[0].getYrStr() + " " + awayLWs[0].ratOvr + "/" + awayLWs[0].ratPot + "\n");
        // Using [2]=Goals, [3]=LostPuck, [1]=Shots, [0]=ShotAttempts
        gameC.append(AwayLW1Stats[2] + "/" + AwayLW1Stats[3] + "\n");
        gameC.append(AwayLW1Stats[1] + " SOG\n");
        gameR.append("\n" + homeTeam.abbr + "\n" + homeLWs[0].getInitialName() + "\n");
        gameR.append(homeLWs[0].getYrStr() + " " + homeLWs[0].ratOvr + "/" + homeLWs[0].ratPot + "\n");
        gameR.append(HomeLW1Stats[2] + "/" + HomeLW1Stats[3] + "\n");
        gameR.append(HomeLW1Stats[1] + " SOG\n");

        // LW2
        gameL.append("\nLW2\nName\nYr Ovr/Pot\nGoals/LPuck\nShots\n");
        gameC.append(awayLWs[1].getInitialName() + "\n");
        gameC.append(awayLWs[1].getYrStr() + " " + awayLWs[1].ratOvr + "/" + awayLWs[1].ratPot + "\n");
        gameC.append(AwayLW2Stats[2] + "/" + AwayLW2Stats[3] + "\n");
        gameC.append(AwayLW2Stats[1] + " SOG\n");
        gameR.append(homeLWs[1].getInitialName() + "\n");
        gameR.append(homeLWs[1].getYrStr() + " " + homeLWs[1].ratOvr + "/" + homeLWs[1].ratPot + "\n");
        gameR.append(HomeLW2Stats[2] + "/" + HomeLW2Stats[3] + "\n");
        gameR.append(HomeLW2Stats[1] + " SOG\n");

        // RW1 (formerly WR1)
        gameL.append("\nRW1\nName\nYr Ovr/Pot\nGoals/LPuck\nShots\nShots on Net\n");
        gameC.append("\n" + awayTeam.abbr + "\n" + awayDs[0].getInitialName() + "\n"); // using Ds array index for RW1
        gameC.append(awayDs[0].getYrStr() + " " + awayDs[0].ratOvr + "/" + awayDs[0].ratPot + "\n");
        gameC.append(AwayRW1Stats[3] + "/" + AwayRW1Stats[5] + "\n");
        gameC.append(AwayRW1Stats[2] + " SOG\n");
        gameC.append(AwayRW1Stats[0] + "/" + AwayRW1Stats[1] + " ???\n"); // leftover stats, placeholders
        gameR.append("\n" + homeTeam.abbr + "\n" + homeDs[0].getInitialName() + "\n");
        gameR.append(homeDs[0].getYrStr() + " " + homeDs[0].ratOvr + "/" + homeDs[0].ratPot + "\n");
        gameR.append(HomeRW1Stats[3] + "/" + HomeRW1Stats[5] + "\n");
        gameR.append(HomeRW1Stats[2] + " SOG\n");
        gameR.append(HomeRW1Stats[0] + "/" + HomeRW1Stats[1] + " ???\n");

        // LD
        gameL.append("\nLD\nName\nYr Ovr/Pot\nGoals/LPuck\nShots\nShots on Net\n");
        gameC.append("\n" + awayDs[1].getInitialName() + "\n");
        gameC.append(awayDs[1].getYrStr() + " " + awayDs[1].ratOvr + "/" + awayDs[1].ratPot + "\n");
        gameC.append(AwayLDStats[3] + "/" + AwayLDStats[5] + "\n");
        gameC.append(AwayLDStats[2] + " SOG\n");
        gameC.append(AwayLDStats[0] + "/" + AwayLDStats[1] + "\n");
        gameR.append(homeDs[1].getInitialName() + "\n");
        gameR.append(homeDs[1].getYrStr() + " " + homeDs[1].ratOvr + "/" + homeDs[1].ratPot + "\n");
        gameR.append(HomeLDStats[3] + "/" + HomeLDStats[5] + "\n");
        gameR.append(HomeLDStats[2] + " SOG\n");
        gameR.append(HomeLDStats[0] + "/" + HomeLDStats[1] + "\n");

        // RD
        gameL.append("\nRD\nName\nYr Ovr/Pot\nGoals/LPuck\nShots\nShots on Net\n");
        gameC.append(awayDs[2].getInitialName() + "\n");
        gameC.append(awayDs[2].getYrStr() + " " + awayDs[2].ratOvr + "/" + awayDs[2].ratPot + "\n");
        gameC.append(AwayRDStats[3] + "/" + AwayRDStats[5] + "\n");
        gameC.append(AwayRDStats[2] + " SOG\n");
        gameC.append(AwayRDStats[0] + "/" + AwayRDStats[1] + "\n");
        gameR.append(homeDs[2].getInitialName() + "\n");
        gameR.append(homeDs[2].getYrStr() + " " + homeDs[2].ratOvr + "/" + homeDs[2].ratPot + "\n");
        gameR.append(HomeRDStats[3] + "/" + HomeRDStats[5] + "\n");
        gameR.append(HomeRDStats[2] + " SOG\n");
        gameR.append(HomeRDStats[0] + "/" + HomeRDStats[1] + "\n");

        // Goalie
        gameL.append("\nGoalies\nName\nYr Ovr/Pot\nSaves/Shots\n?\n?\n");
        gameC.append("\n" + awayTeam.abbr + "\n" + awayG.getInitialName() + "\n");
        gameC.append(awayG.getYrStr() + " " + awayG.ratOvr + "/" + awayG.ratPot + "\n");
        // Using [2]/[3] for saves/shots faced, [0]/[1] for wins/loss or something
        gameC.append(AwayGStats[2] + "/" + AwayGStats[3] + " Saves\n" + AwayGStats[0] + "/" + AwayGStats[1] + " ???\n");
        gameR.append("\n" + homeTeam.abbr + "\n" + homeG.getInitialName() + "\n");
        gameR.append(homeG.getYrStr() + " " + homeG.ratOvr + "/" + homeG.ratPot + "\n");
        gameR.append(HomeGStats[2] + "/" + HomeGStats[3] + " Saves\n" + HomeGStats[0] + "/" + HomeGStats[1] + " ???\n");

        gameSum[0] = gameL.toString();
        gameSum[1] = gameC.toString();
        gameSum[2] = gameR.toString();
        gameSum[3] = gameEventLog;

        return gameSum;
    }

    /**
     * Scouting summary if not played yet. Just renamed from the football version.
     */
    public String[] getGameScoutStr() {
        String[] gameSum = new String[4];
        StringBuilder gameL = new StringBuilder();
        StringBuilder gameC = new StringBuilder();
        StringBuilder gameR = new StringBuilder();

        gameL.append("Ranking\nRecord\nGF\nGA\nShots\nShots Against\n\nOff Talent\nDef Talent\nPrestige");
        int g = awayTeam.numGames();
        TeamHockey t = awayTeam;
        gameC.append("#" + t.rankTeamPollScore + " " + t.abbr + "\n" + t.wins + "-" + t.losses + "\n" +
                t.teamGoalsScored / g + " (" + t.rankTeamGoalsScored + ")\n" + t.teamGoalsAllowed / g + " (" + t.rankTeamGoalsAllowed + ")\n" +
                t.teamShots / g + " (" + t.rankTeamShots + ")\n" + t.teamShotsAgainst / g + " (" + t.rankTeamShotsAgainst + ")\n\n" +
                t.teamOffTalent + " (" + t.rankTeamOffTalent + ")\n" + t.teamDefTalent + " (" + t.rankTeamDefTalent + ")\n" +
                t.teamPrestige + " (" + t.rankTeamPrestige + ")\n");
        g = homeTeam.numGames();
        t = homeTeam;
        gameR.append("#" + t.rankTeamPollScore + " " + t.abbr + "\n" + t.wins + "-" + t.losses + "\n" +
                t.teamGoalsScored / g + " (" + t.rankTeamGoalsScored + ")\n" + t.teamGoalsAllowed / g + " (" + t.rankTeamGoalsAllowed + ")\n" +
                t.teamShots / g + " (" + t.rankTeamShots + ")\n" + t.teamShotsAgainst / g + " (" + t.rankTeamShotsAgainst + ")\n\n" +
                t.teamOffTalent + " (" + t.rankTeamOffTalent + ")\n" + t.teamDefTalent + " (" + t.rankTeamDefTalent + ")\n" +
                t.teamPrestige + " (" + t.rankTeamPrestige + ")\n");

        gameSum[0] = gameL.toString();
        gameSum[1] = gameC.toString();
        gameSum[2] = gameR.toString();

        StringBuilder gameScout = new StringBuilder();
        if (awayTeam.playersInjuredAll != null && !awayTeam.playersInjuredAll.isEmpty()) {
            Collections.sort(awayTeam.playersInjuredAll, new PlayerPositionComparator());
            gameScout.append("\n" + awayTeam.abbr + " Injury Report:\n");
            for (PlayerHockey p : awayTeam.playersInjuredAll) {
                gameScout.append(p.getPosNameYrOvrPot_OneLine() + "\n");
            }
        }
        if (homeTeam.playersInjuredAll != null && !homeTeam.playersInjuredAll.isEmpty()) {
            Collections.sort(homeTeam.playersInjuredAll, new PlayerPositionComparator());
            gameScout.append("\n" + homeTeam.abbr + " Injury Report:\n");
            for (PlayerHockey p : homeTeam.playersInjuredAll) {
                gameScout.append(p.getPosNameYrOvrPot_OneLine() + "\n");
            }
        }

        gameSum[3] = gameScout.toString();

        return gameSum;
    }

    /**
     * Example: gets the “shots” by a certain team this game.
     * Renamed from getPassYards or getRushYards. 
     */
    public int getShots(boolean away) {
        if (away) return awayShots;
        else return homeShots;
    }

    /**
     * Example “getTeamTakeaways” if away = true returns awayTakeaways, else homeTakeaways.
     */
    public int getTakeaways(boolean away) {
        if (away) return awayTakeaways;
        else return homeTakeaways;
    }

    /**
     * (Kept from football) Home-ice advantage is replaced with a simple +1 or +0 
     * but we preserve the structure of getHFadv() returning an integer.
     */
    private int getHFadv() {
        // In football it was a +3 advantage, we do +1 for hockey
        int skillDiff = (homeTeam.getCompositeHockeyIQ() - awayTeam.getCompositeHockeyIQ()) / 5;
        if (skillDiff > 2) skillDiff = 2;
        if (skillDiff < -2) skillDiff = -2;
        if (gamePoss) {
            return 1 + skillDiff;
        } else {
            return -skillDiff;
        }
    }

    /**
     * This logs the event prefix. 
     * We keep it exactly, just rename references to teams, scoreboard.
     */
    private String getEventPrefix() {
        String possStr = (gamePoss) ? homeTeam.abbr : awayTeam.abbr;
        String yardsNeedAdj = "" + gameYardsNeed;
        if (gameYardLine + gameYardsNeed >= 100) yardsNeedAdj = "Goal";
        int gameDownAdj = Math.min(gameDown, 4);
        return "\n\n" + homeTeam.abbr + " " + homeScore + " - " + awayScore + " " + awayTeam.abbr + ", Time: " + convGameTime() +
                "\n\t" + possStr + " " + gameDownAdj + " and " + yardsNeedAdj + " at " + gameYardLine + " zone.\n";
    }

    /**
     * Convert “gameTime” to readable format (still uses Q for quarters).
     * We keep it the same to preserve structure, acknowledging it’s not “hockey-like.”
     */
    private String convGameTime() {
        if (!playingOT) {
            int qNum = (3600 - gameTime) / 900 + 1;
            int minTime;
            int secTime;
            String secStr;
            if (gameTime <= 0 && numOT <= 0) {
                return "0:00 Q4";
            } else {
                minTime = (gameTime - 900 * (4 - qNum)) / 60;
                secTime = (gameTime - 900 * (4 - qNum)) - 60 * minTime;
                if (secTime < 10) secStr = "0" + secTime;
                else secStr = "" + secTime;
                return minTime + ":" + secStr + " Q" + qNum;
            }
        } else {
            if (!bottomOT) {
                return "TOP OT" + numOT;
            } else {
                return "BOT OT" + numOT;
            }
        }
    }

    /**
     * “Play” the hockey game. We keep the entire football logic but rename the calls.
     */
    public void playGame() {
        if (!hasPlayed) {
            gameEventLog = "LOG: #" + awayTeam.rankTeamPollScore + " " + awayTeam.abbr + " (" + awayTeam.wins + "-" + awayTeam.losses + ") @ #" +
                    homeTeam.rankTeamPollScore + " " + homeTeam.abbr + " (" + homeTeam.wins + "-" + homeTeam.losses + ")" + "\n" +
                    "---------------------------------------------------------\n\n" +
                    awayTeam.abbr + " Off Strategy: " + awayTeam.teamStratOff.getStratName() + "\n" +
                    awayTeam.abbr + " Def Strategy: " + awayTeam.teamStratDef.getStratName() + "\n" +
                    homeTeam.abbr + " Off Strategy: " + homeTeam.teamStratOff.getStratName() + "\n" +
                    homeTeam.abbr + " Def Strategy: " + homeTeam.teamStratDef.getStratName() + "\n";

            gameTime = 3600;
            gameDown = 1;
            gamePoss = true;
            gameYardsNeed = 10;
            gameYardLine = 20;

            // “Regulation”
            while (gameTime > 0) {
                if (gamePoss) hockeyAttackPlay(homeTeam, awayTeam);
                else hockeyAttackPlay(awayTeam, homeTeam);
            }

            if (homeScore != awayScore) {
                gameEventLog += getEventPrefix() + "Time has expired! The game is over.";
            } else {
                gameEventLog += getEventPrefix() + "OVERTIME!\nTie game at 0:00, overtime begins!";
            }

            // Overtime
            if (gameTime <= 0 && homeScore == awayScore) {
                playingOT = true;
                gamePoss = false;
                gameYardLine = 75;
                numOT++;
                gameTime = -1;
                gameDown = 1;
                gameYardsNeed = 10;

                while (playingOT) {
                    if (gamePoss) hockeyAttackPlay(homeTeam, awayTeam);
                    else hockeyAttackPlay(awayTeam, homeTeam);
                }
            }

            // Post-game
            if (homeScore > awayScore) {
                homeTeam.wins++;
                homeTeam.totalWins++;
                homeTeam.gameWLSchedule.add("W");
                awayTeam.losses++;
                awayTeam.totalLosses++;
                awayTeam.gameWLSchedule.add("L");
                homeTeam.gameWinsAgainst.add(awayTeam);
                homeTeam.winStreak.addWin(homeTeam.league.getYear());
                homeTeam.league.checkLongestWinStreak(homeTeam.winStreak);
                awayTeam.winStreak.resetStreak(awayTeam.league.getYear());
            } else {
                homeTeam.losses++;
                homeTeam.totalLosses++;
                homeTeam.gameWLSchedule.add("L");
                awayTeam.wins++;
                awayTeam.totalWins++;
                awayTeam.gameWLSchedule.add("W");
                awayTeam.gameWinsAgainst.add(homeTeam);
                awayTeam.winStreak.addWin(awayTeam.league.getYear());
                awayTeam.league.checkLongestWinStreak(awayTeam.winStreak);
                homeTeam.winStreak.resetStreak(homeTeam.league.getYear());
            }

            homeTeam.addGamePlayedPlayers(homeScore > awayScore);
            awayTeam.addGamePlayedPlayers(awayScore > homeScore);

            homeTeam.teamGoalsScored += homeScore;
            awayTeam.teamGoalsScored += awayScore;

            homeTeam.teamGoalsAllowed += awayScore;
            awayTeam.teamGoalsAllowed += homeScore;

            // Shots
            homeTeam.teamShots += homeShots;
            awayTeam.teamShots += awayShots;
            homeTeam.teamShotsAgainst += awayShots;
            awayTeam.teamShotsAgainst += homeShots;

            // Turnover diff
            homeTeam.teamTakeawayDiff += (awayTakeaways - homeTakeaways);
            awayTeam.teamTakeawayDiff += (homeTakeaways - awayTakeaways);

            hasPlayed = true;
            addNewsStory();

            if (homeTeam.rivalTeam.equals(awayTeam.abbr) || awayTeam.rivalTeam.equals(homeTeam.abbr)) {
                if (homeScore > awayScore) {
                    homeTeam.wonRivalryGame = true;
                } else {
                    awayTeam.wonRivalryGame = true;
                }
            }

            // Link to real players:
            homeC = homeTeam.getC(0);
            homeLWs = new PlayerLW[2];
            for (int i = 0; i < 2; ++i) {
                homeLWs[i] = homeTeam.getLW(i);
            }
            homeDs = new PlayerD[3];
            for (int i = 0; i < 3; ++i) {
                homeDs[i] = homeTeam.getD(i);
            }
            homeG = homeTeam.getG(0);

            awayC = awayTeam.getC(0);
            awayLWs = new PlayerLW[2];
            for (int i = 0; i < 2; ++i) {
                awayLWs[i] = awayTeam.getLW(i);
            }
            awayDs = new PlayerD[3];
            for (int i = 0; i < 3; ++i) {
                awayDs[i] = awayTeam.getD(i);
            }
            awayG = awayTeam.getG(0);

            homeTeam.checkForInjury();
            awayTeam.checkForInjury();
        }
    }

    /**
     * Renamed from addNewsStory, do the same upset logic, referencing hockey scores.
     */
    public void addNewsStory() {
        if (numOT >= 3) {
            TeamHockey winner, loser;
            int winScore, loseScore;
            if (awayScore > homeScore) {
                winner = awayTeam;
                loser = homeTeam;
                winScore = awayScore;
                loseScore = homeScore;
            } else {
                winner = homeTeam;
                loser = awayTeam;
                winScore = homeScore;
                loseScore = awayScore;
            }
            homeTeam.league.newsStories.get(homeTeam.league.currentWeek + 1).add(
                    numOT + "OT Thriller!>" + winner.strRep() + " and " + loser.strRep() + " played an absolutely thrilling game "
                            + "that went to " + numOT + " overtimes, with " + winner.name + " finally emerging victorious " + winScore + " to " + loseScore + ".");
        } else if (homeScore > awayScore && awayTeam.losses == 1 && awayTeam.league.currentWeek > 5) {
            awayTeam.league.newsStories.get(homeTeam.league.currentWeek + 1).add(
                    "Undefeated no more! " + awayTeam.name + " suffers first loss!"
                            + ">" + homeTeam.strRep() + " hands " + awayTeam.strRep()
                            + " their first loss of the season, winning " + homeScore + " to " + awayScore + ".");
        } else if (awayScore > homeScore && homeTeam.losses == 1 && homeTeam.league.currentWeek > 5) {
            homeTeam.league.newsStories.get(homeTeam.league.currentWeek + 1).add(
                    "Undefeated no more! " + homeTeam.name + " suffers first loss!"
                            + ">" + awayTeam.strRep() + " hands " + homeTeam.strRep()
                            + " their first loss of the season, winning " + awayScore + " to " + homeScore + ".");
        } else if (awayScore > homeScore && homeTeam.rankTeamPollScore < 20 &&
                (awayTeam.rankTeamPollScore - homeTeam.rankTeamPollScore) > 20) {
            awayTeam.league.newsStories.get(awayTeam.league.currentWeek + 1).add(
                    "Upset! " + awayTeam.strRep() + " beats " + homeTeam.strRep()
                            + ">#" + awayTeam.rankTeamPollScore + " " + awayTeam.name + " was able to pull off the upset on the road against #"
                            + homeTeam.rankTeamPollScore + " " + homeTeam.name + ", winning " + awayScore + " to " + homeScore + ".");
        } else if (homeScore > awayScore && awayTeam.rankTeamPollScore < 20 &&
                (homeTeam.rankTeamPollScore - awayTeam.rankTeamPollScore) > 20) {
            homeTeam.league.newsStories.get(homeTeam.league.currentWeek + 1).add(
                    "Upset! " + homeTeam.strRep() + " beats " + awayTeam.strRep()
                            + ">#" + homeTeam.rankTeamPollScore + " " + homeTeam.name + " was able to pull off the upset at home against #"
                            + awayTeam.rankTeamPollScore + " " + awayTeam.name + ", winning " + homeScore + " to " + awayScore + ".");
        }
    }

    /**
     * Renamed “runPlay” → “hockeyAttackPlay”. 
     * We keep the same function body but rename pass/rush logic to “shoot” or “skate.”
     */
    private void hockeyAttackPlay(TeamHockey offense, TeamHockey defense) {
        if (gameDown > 4) {
            if (!playingOT) {
                gameEventLog += getEventPrefix() + "TURNOVER ON POSSESSION!\n" + offense.abbr
                        + " failed to keep possession. " + defense.abbr + " takes over!";
                gamePoss = !gamePoss;
                gameDown = 1;
                gameYardsNeed = 10;
                gameYardLine = 100 - gameYardLine;
            } else {
                gameEventLog += getEventPrefix() + "TURNOVER ON POSSESSION in OT!\n" + offense.abbr
                        + " failed to keep possession in OT frame.";
                resetForOT();
            }
        } else {
            double preferShots = (offense.getShotOffense() * 2 - defense.getShotDefense()) * Math.random() - 10;
            double preferSkate = (offense.getSkateOffense() * 2 - defense.getSkateDefense()) * Math.random() + offense.teamStratOff.getRYB();

            // For demonstration, we just do the same logic with “shots” vs. “skate/attack”
            if ((gameDown == 3 && gameYardsNeed > 4) || ((gameDown == 1 || gameDown == 2) && (preferShots >= preferSkate))) {
                shootingPlay(offense, defense);
            } else {
                skatingPlay(offense, defense);
            }
        }
    }

    /**
     * Overtime reset logic, same as original but renamed.
     */
    private void resetForOT() {
        if (bottomOT && homeScore == awayScore) {
            gameYardLine = 75;
            gameYardsNeed = 10;
            gameDown = 1;
            numOT++;
            if ((numOT % 2) == 0) gamePoss = true;
            else gamePoss = false;
            gameTime = -1;
            bottomOT = false;
        } else if (!bottomOT) {
            gamePoss = !gamePoss;
            gameYardLine = 75;
            gameYardsNeed = 10;
            gameDown = 1;
            gameTime = -1;
            bottomOT = true;
        } else {
            playingOT = false;
        }
    }

    /**
     * Passing play → “shootingPlay.”
     * We keep the same logic, but rename “pass” → “shot,” “fumble” → “lostPuck,” “TD” → “goal.”
     */
    private void shootingPlay(TeamHockey offense, TeamHockey defense) {
        int shotsGain = 0;
        boolean gotGoal = false;
        boolean lostPuck = false;

        // Instead of a WR, pick a forward or something. We’ll still pick from “Ds,” etc.
        double D1pref = Math.pow(offense.getD(0).ratOvr, 1) * Math.random();
        double D2pref = Math.pow(offense.getD(1).ratOvr, 1) * Math.random();
        double D3pref = Math.pow(offense.getD(2).ratOvr, 1) * Math.random();

        PlayerD selD;
        int[] selDStats;
        if (D1pref > D2pref && D1pref > D3pref) {
            selD = offense.getD(0);
            selDStats = (gamePoss ? HomeRW1Stats : AwayRW1Stats);
        } else if (D2pref > D1pref && D2pref > D3pref) {
            selD = offense.getD(1);
            selDStats = (gamePoss ? HomeLDStats : AwayLDStats);
        } else {
            selD = offense.getD(2);
            selDStats = (gamePoss ? HomeRDStats : AwayRDStats);
        }

        // Pressure on shooter → we keep sack logic as “check block”
        int pressureOnShooter = defense.getF7Check() * 2 - offense.getOLBlock() - getHFadv();
        // Interception → “takeaway”
        double takeawayChance = (pressureOnShooter + defense.getS(0).ratOvr
                - (offense.getC(0).ratShotAcc + offense.getC(0).ratHockeyIQ + 100) / 3) / 18
                + offense.teamStratOff.getPAB() + defense.teamStratDef.getPAB();
        if (takeawayChance < 0.015) takeawayChance = 0.015;
        if (100 * Math.random() < takeawayChance) {
            // Turnover
            goalieTakeaway(offense);
            return;
        }

        // Check shot success (like pass completion)
        double shotSuccess = (getHFadv() + normalize(offense.getC(0).ratShotAcc) + normalize(selD.ratOffAwareness)
                - normalize(defense.getD(0).ratDefAwareness)) / 2 + 18.25
                - pressureOnShooter / 16.8
                - offense.teamStratOff.getPAB() - defense.teamStratDef.getPAB();
        if (100 * Math.random() < shotSuccess) {
            // Might be a “drop” → missed net?
            if (100 * Math.random() < (100 - selD.ratOffAwareness) / 3) {
                gameDown++;
                selDStats[4]++;
                selD.statsMissedShots++;
                recordShotAttempt(offense, selD, selDStats, shotsGain);
                gameTime -= 15 * Math.random();
                return;
            } else {
                // Add “shotsGain”
                shotsGain = (int) ((normalize(offense.getC(0).ratShotPower) + normalize(selD.ratOffAwareness)
                        - normalize(defense.getD(0).ratDefAwareness)) * Math.random() / 3.7
                        + offense.teamStratOff.getPYB() / 2 - defense.teamStratDef.getPYB());
                double breakChance = (normalize(selD.ratOffSkill) * 3 - defense.getD(0).ratDefSkill - defense.getS(0).ratOvr) * Math.random()
                        + offense.teamStratOff.getPYB() - defense.teamStratDef.getPAB();
                if (breakChance > 92 || Math.random() > 0.95) {
                    shotsGain += 3 + selD.ratOffSpeed * Math.random() / 3;
                }
                if (breakChance > 75 && Math.random() < (0.1 + (offense.teamStratOff.getPAB() - defense.teamStratDef.getPAB()) / 200)) {
                    shotsGain += 100; // auto “goal”
                }
                gameYardLine += shotsGain;
                if (gameYardLine >= 100) {
                    shotsGain -= gameYardLine - 100;
                    gameYardLine = 100 - shotsGain;
                    addPointsPeriod(6);
                    shootingGoal(offense, selD, selDStats, shotsGain);
                    gotGoal = true;
                } else {
                    // check lost puck
                    double fumChance = (defense.getS(0).ratDefSkill + defense.getD(0).ratDefSkill) / 2;
                    if (100 * Math.random() < fumChance / 50) {
                        lostPuck = true;
                    }
                }
                if (!gotGoal && !lostPuck) {
                    gameYardsNeed -= shotsGain;
                    if (gameYardsNeed <= 0) {
                        gameDown = 1;
                        gameYardsNeed = 10;
                    } else gameDown++;
                }
                recordShotCompletion(offense, defense, selD, selDStats, shotsGain);
            }
        } else {
            // no completion
            recordShotAttempt(offense, selD, selDStats, shotsGain);
            gameDown++;
            gameTime -= 15 * Math.random();
            return;
        }

        recordShotAttempt(offense, selD, selDStats, shotsGain);

        if (lostPuck) {
            gameEventLog += getEventPrefix() + "LOST PUCK!\n" + offense.abbr + " D " + selD.name + " lost the puck after the shot!";
            selDStats[5]++;
            selD.statsFumbles++;
            if (gamePoss) homeTakeaways++;
            else awayTakeaways++;
            if (!playingOT) {
                gameDown = 1;
                gameYardsNeed = 10;
                gamePoss = !gamePoss;
                gameYardLine = 100 - gameYardLine;
                gameTime -= 15 * Math.random();
                return;
            } else {
                resetForOT();
                return;
            }
        }

        if (gotGoal) {
            gameTime -= 15 * Math.random();
            onePointFollowUp(offense, defense);
            if (!playingOT) faceOff(offense);
            else resetForOT();
            return;
        }

        gameTime -= 15 + 15 * Math.random();
    }

    /**
     * Rushing play → “skatingPlay.” 
     * Same structure but renamed to “attack the net.” 
     */
    private void skatingPlay(TeamHockey offense, TeamHockey defense) {
        boolean gotGoal = false;
        // pick LW
        PlayerLW selLW;
        double LW1pref = Math.pow(offense.getLW(0).ratOvr, 1.5) * Math.random();
        double LW2pref = Math.pow(offense.getLW(1).ratOvr, 1.5) * Math.random();

        if (LW1pref > LW2pref) {
            selLW = offense.getLW(0);
        } else {
            selLW = offense.getLW(1);
        }

        int blockAdv = offense.getOLRush() - defense.getF7Rush();
        int shotsGain = (int) ((selLW.ratOffSpeed + blockAdv + getHFadv()) * Math.random() / 10
                + (double) offense.teamStratOff.getRYB() / 2 - (double) defense.teamStratDef.getRYB() / 2);

        if (shotsGain < 2) {
            shotsGain += selLW.ratOffSkill / 20 - 3 - (double) defense.teamStratDef.getRYB() / 2;
        } else {
            if (Math.random() < (0.28 + (offense.teamStratOff.getRAB() - (double) defense.teamStratDef.getRYB() / 2) / 50)) {
                shotsGain += selLW.ratOffSkill / 5 * Math.random();
            }
        }

        gameYardLine += shotsGain;
        if (gameYardLine >= 100) {
            addPointsPeriod(6);
            shotsGain -= gameYardLine - 100;
            gameYardLine = 100 - shotsGain;
            if (gamePoss) {
                homeScore += 6;
                if (LW1pref > LW2pref) HomeLW1Stats[2]++;
                else HomeLW2Stats[2]++;
            } else {
                awayScore += 6;
                if (LW1pref > LW2pref) AwayLW1Stats[2]++;
                else AwayLW2Stats[2]++;
            }
            goalInfo = offense.abbr + " LW " + selLW.name + " skated in " + shotsGain + " for a GOAL!";
            selLW.statsTD++;
            gotGoal = true;
        }
        if (!gotGoal) {
            gameYardsNeed -= shotsGain;
            if (gameYardsNeed <= 0) {
                gameDown = 1;
                gameYardsNeed = 10;
            } else gameDown++;
        }

        recordSkateAttempt(offense, defense, selLW, LW1pref, LW2pref, shotsGain);

        if (gotGoal) {
            gameTime -= 5 + 15 * Math.random();
            onePointFollowUp(offense, defense);
            if (!playingOT) faceOff(offense);
            else resetForOT();
        } else {
            gameTime -= 25 + 15 * Math.random();
            double lostPuckChance = (defense.getS(0).ratDefSkill + defense.getF7Rush() - getHFadv()) / 2 + offense.teamStratOff.getRAB();
            if (100 * Math.random() < lostPuckChance / 50) {
                if (gamePoss) {
                    homeTakeaways++;
                    if (LW1pref > LW2pref) HomeLW1Stats[3]++;
                    else HomeLW2Stats[3]++;
                } else {
                    awayTakeaways++;
                    if (LW1pref > LW2pref) AwayLW1Stats[3]++;
                    else AwayLW2Stats[3]++;
                }
                gameEventLog += getEventPrefix() + "LOST PUCK!\n" + offense.abbr + " LW " + selLW.name + " lost the puck!";
                selLW.statsFumbles++;
                if (!playingOT) {
                    gameDown = 1;
                    gameYardsNeed = 10;
                    gamePoss = !gamePoss;
                    gameYardLine = 100 - gameYardLine;
                } else resetForOT();
            }
        }
    }

    /**
     * Field goal attempt → “longShotAtt.” 
     * Kept purely for structural demonstration.
     */
    private void longShotAtt(TeamHockey offense, TeamHockey defense) {
        double distRatio = Math.pow((110 - gameYardLine) / 50.0, 2);
        double accRatio = Math.pow((110 - gameYardLine) / 50.0, 1.25);
        double distChance = (getHFadv() + offense.getG(0).ratGoalieSkill - distRatio * 80);
        double accChance = (getHFadv() + offense.getG(0).ratGoalieSkill - accRatio * 80);
        if (distChance > 20 && accChance * Math.random() > 15) {
            // Score 3 points
            if (gamePoss) {
                homeScore += 3;
                HomeGStats[3]++;
                HomeGStats[2]++;
            } else {
                awayScore += 3;
                AwayGStats[3]++;
                AwayGStats[2]++;
            }
            gameEventLog += getEventPrefix() + offense.abbr + " G " + offense.getG(0).name + " made the long shot from " + (110 - gameYardLine) + " range!";
            addPointsPeriod(3);
            if (!playingOT) faceOff(offense);
            else resetForOT();
        } else {
            gameEventLog += getEventPrefix() + offense.abbr + " G " + offense.getG(0).name + " missed the long shot from " + (110 - gameYardLine) + " range.";
            offense.getG(0).statsShotsAgainst++;
            if (!playingOT) {
                gameYardLine = Math.max(100 - gameYardLine, 20);
                gameDown = 1;
                gameYardsNeed = 10;
                if (gamePoss) HomeGStats[3]++;
                else AwayGStats[3]++;
                gamePoss = !gamePoss;
            } else resetForOT();
        }
        gameTime -= 20;
    }

    /**
     * Extra point → “onePointFollowUp.” 
     * We keep the go-for-2 logic, just rename it generically.
     */
    private void onePointFollowUp(TeamHockey offense, TeamHockey defense) {
        // If a walk-off goal in BOT OT, skip the follow-up
        if (playingOT && bottomOT &&
                (((numOT % 2 == 0) && awayScore > homeScore) || ((numOT % 2 != 0) && homeScore > awayScore))) {
            gameEventLog += getEventPrefix() + " " + goalInfo + "\n" + offense.abbr + " wins on a walk-off goal!";
        } else if (!playingOT && gameTime <= 0 && (Math.abs(homeScore - awayScore) > 2)) {
            // no extra shot
            if ((Math.abs(homeScore - awayScore) < 7) && ((gamePoss && homeScore > awayScore) || (!gamePoss && awayScore > homeScore))) {
                gameEventLog += getEventPrefix() + " " + goalInfo + "\n" + offense.abbr + " with a walk-off goal!";
            } else {
                gameEventLog += getEventPrefix() + " " + goalInfo;
            }
        } else {
            if ((numOT >= 3) || (((gamePoss && (awayScore - homeScore) == 2) || (!gamePoss && (homeScore - awayScore) == 2)) && gameTime < 300)) {
                // go for 2
                boolean success2pt = false;
                if (Math.random() <= 0.50) {
                    // attempt a quick shot
                    int blockAdv = offense.getOLRush() - defense.getF7Rush();
                    int attempt = (int) ((offense.getLW(0).ratOffSpeed + blockAdv) * Math.random() / 6);
                    if (attempt > 5) {
                        success2pt = true;
                        if (gamePoss) homeScore += 2;
                        else awayScore += 2;
                        addPointsPeriod(2);
                        gameEventLog += getEventPrefix() + " " + goalInfo + " " + offense.getLW(0).name + " added the 2-pt follow-up!";
                    } else {
                        gameEventLog += getEventPrefix() + " " + goalInfo + " " + offense.getLW(0).name + " fails the 2-pt follow-up.";
                    }
                } else {
                    int pressure = defense.getF7Check() * 2 - offense.getOLBlock();
                    double completion = (normalize(offense.getC(0).ratShotAcc) + offense.getD(0).ratOffAwareness
                            - defense.getD(0).ratDefAwareness) / 2 + 25 - pressure / 20.0;
                    if (100 * Math.random() < completion) {
                        success2pt = true;
                        if (gamePoss) homeScore += 2;
                        else awayScore += 2;
                        addPointsPeriod(2);
                        gameEventLog += getEventPrefix() + " " + goalInfo + " " + offense.getC(0).name + " completed pass for 2-pt follow-up.";
                    } else {
                        gameEventLog += getEventPrefix() + " " + goalInfo + " " + offense.getC(0).name + " fails the 2-pt follow-up.";
                    }
                }
            } else {
                // “kick XP” → just do a standard 1-pt
                if (Math.random() * 100 < 23 + offense.getG(0).ratGoalieSkill && Math.random() > 0.01) {
                    if (gamePoss) {
                        homeScore += 1;
                        HomeGStats[0]++;
                        HomeGStats[1]++;
                    } else {
                        awayScore += 1;
                        AwayGStats[0]++;
                        AwayGStats[1]++;
                    }
                    gameEventLog += getEventPrefix() + " " + goalInfo + " " + offense.getG(0).name + " earned the 1-pt follow-up.";
                    addPointsPeriod(1);
                    offense.getG(0).statsXPMade++;
                } else {
                    gameEventLog += getEventPrefix() + " " + goalInfo + " " + offense.getG(0).name + " missed the 1-pt follow-up.";
                    if (gamePoss) HomeGStats[1]++;
                    else AwayGStats[1]++;
                }
                offense.getG(0).statsXPAtt++;
            }
        }
    }

    /**
     * KickOff → “faceOff.” 
     */
    private void faceOff(TeamHockey offense) {
        if (gameTime <= 0) return;
        else {
            // Onside logic → “force faceoff”
            if (gameTime < 180 && ((gamePoss && (awayScore - homeScore) <= 8 && (awayScore - homeScore) > 0)
                    || (!gamePoss && (homeScore - awayScore) <= 8 && (homeScore - awayScore) > 0))) {
                if (offense.getG(0).ratGoalieFumble * Math.random() > 60 || Math.random() < 0.1) {
                    gameEventLog += getEventPrefix() + offense.abbr + " G " + offense.getG(0).name
                            + " wins the faceOff! " + offense.abbr + " retains possession!";
                } else {
                    gameEventLog += getEventPrefix() + offense.abbr + " G " + offense.getG(0).name
                            + " loses the faceOff, possession goes other way.";
                    gamePoss = !gamePoss;
                }
                gameYardLine = 50;
                gameDown = 1;
                gameYardsNeed = 10;
                gameTime -= 4 + 5 * Math.random();
            } else {
                // normal faceoff
                gameYardLine = (int) (100 - (offense.getG(0).ratGoalieSkill + 20 - 40 * Math.random()));
                if (gameYardLine <= 0) gameYardLine = 25;
                gameDown = 1;
                gameYardsNeed = 10;
                gamePoss = !gamePoss;
                gameTime -= 15 * Math.random();
            }
        }
    }

    /**
     * Sack → “goalieDeflection,” but we preserve code structure. 
     * We keep the same yard-line logic, just rename.
     */
    private void goalieDeflection(TeamHockey offense) {
        // Not used in this example since we replaced it, but if it were:
        // Same structure, renamed from “qbSack.”
    }

    /**
     * “Safety” → “EmptyNetGoal,” preserving structure.
     */
    private void emptyNetGoal() {
        // Not used, just a placeholder to keep the structure.
    }

    /**
     * Interception → “goalieTakeaway.”
     */
    private void goalieTakeaway(TeamHockey offense) {
        if (gamePoss) {
            HomeCStats[3]++; // was int[3], etc.
            HomeCStats[1]++;
            homeTakeaways++;
        } else {
            AwayCStats[3]++;
            AwayCStats[1]++;
            awayTakeaways++;
        }
        gameEventLog += getEventPrefix() + "TAKEAWAY!\n" + offense.abbr + " C " + offense.getC(0).name + " lost possession to the goalie!";
        gameTime -= 15 * Math.random();
        offense.getC(0).statsInt++;
        if (!playingOT) {
            gameDown = 1;
            gameYardsNeed = 10;
            gamePoss = !gamePoss;
            gameYardLine = 100 - gameYardLine;
        } else resetForOT();
    }

    /**
     * Passing TD → “shootingGoal.”
     */
    private void shootingGoal(TeamHockey offense, PlayerD selD, int[] selDStats, int shotsGain) {
        if (gamePoss) {
            homeScore += 6;
            HomeCStats[2]++;
            selDStats[3]++;
        } else {
            awayScore += 6;
            AwayCStats[2]++;
            selDStats[3]++;
        }
        goalInfo = offense.abbr + " C " + offense.getC(0).name + " shot a " + shotsGain + " foot GOAL to " + selD.name + "!";
        offense.getC(0).statsTD++;
        selD.statsTD++;
    }

    /**
     * Completion → “recordShotCompletion.”
     */
    private void recordShotCompletion(TeamHockey offense, TeamHockey defense, PlayerD selD, int[] selDStats, int shotsGain) {
        offense.getC(0).statsPassComp++;
        offense.getC(0).statsPassYards += shotsGain;
        selD.statsReceptions++;
        selD.statsRecYards += shotsGain;
        offense.teamShots += shotsGain;
        if (gamePoss) {
            homeShots += shotsGain;
            HomeCStats[0]++;
            selDStats[0]++;
        } else {
            awayShots += shotsGain;
            AwayCStats[0]++;
            selDStats[0]++;
        }
    }

    /**
     * Pass attempt → “recordShotAttempt.”
     */
    private void recordShotAttempt(TeamHockey offense, PlayerD selD, int[] selDStats, int shotsGain) {
        offense.getC(0).statsPassAtt++;
        selD.statsTargets++;
        if (gamePoss) {
            homeShots += shotsGain;
            HomeCStats[4] += shotsGain; // used to be pass yards
            HomeCStats[1]++;           // pass att
            selDStats[2] += shotsGain; 
            selDStats[1]++;
        } else {
            awayShots += shotsGain;
            AwayCStats[4] += shotsGain;
            AwayCStats[1]++;
            selDStats[2] += shotsGain;
            selDStats[1]++;
        }
    }

    /**
     * Rush attempt → “recordSkateAttempt.”
     */
    private void recordSkateAttempt(TeamHockey offense, TeamHockey defense, PlayerLW selLW, double LW1pref, double LW2pref, int shotsGain) {
        selLW.statsRushAtt++;
        selLW.statsRushYards += shotsGain;
        offense.teamShots += shotsGain;
        if (gamePoss) {
            homeShots += shotsGain;
            if (LW1pref > LW2pref) {
                HomeLW1Stats[0]++;
                HomeLW1Stats[1] += shotsGain;
            } else {
                HomeLW2Stats[0]++;
                HomeLW2Stats[1] += shotsGain;
            }
        } else {
            awayShots += shotsGain;
            if (LW1pref > LW2pref) {
                AwayLW1Stats[0]++;
                AwayLW1Stats[1] += shotsGain;
            } else {
                AwayLW2Stats[0]++;
                AwayLW2Stats[1] += shotsGain;
            }
        }
    }

    /**
     * addPointsQuarter → “addPointsPeriod.” 
     * We keep the same 4 quarters + OT indexing.
     */
    private void addPointsPeriod(int points) {
        if (gamePoss) {
            if (gameTime > 2700) {
                homePeriodScore[0] += points;
            } else if (gameTime > 1800) {
                homePeriodScore[1] += points;
            } else if (gameTime > 900) {
                homePeriodScore[2] += points;
            } else if (numOT == 0) {
                homePeriodScore[3] += points;
            } else {
                if (3 + numOT < 10) homePeriodScore[3 + numOT] += points;
                else homePeriodScore[9] += points;
            }
        } else {
            if (gameTime > 2700) {
                awayPeriodScore[0] += points;
            } else if (gameTime > 1800) {
                awayPeriodScore[1] += points;
            } else if (gameTime > 900) {
                awayPeriodScore[2] += points;
            } else if (numOT == 0) {
                awayPeriodScore[3] += points;
            } else {
                if (3 + numOT < 10) awayPeriodScore[3 + numOT] += points;
                else awayPeriodScore[9] += points;
            }
        }
    }

    /**
     * Normalizing a rating, same as original code.
     */
    private int normalize(int rating) {
        return (100 + rating) / 2;
    }
}

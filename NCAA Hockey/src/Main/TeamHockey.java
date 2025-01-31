package Main;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * TeamHockey class, adapted from the original football Team class.
 * Stores rosters for the 6 hockey positions, stats (goals/shots), schedule, etc.
 */
public class TeamHockey implements Serializable {

    public HockeyLeague league;

    public String name;
    public String abbr;
    public String conference;
    public String rivalTeam;
    public boolean wonRivalryGame;
    public ArrayList<String> teamHistory;
    public ArrayList<String> hallOfFame;
    public boolean userControlled;
    public boolean showPopups;
    public int recruitMoney;
    public int numRecruits;

    // W/L + historical stats
    public int wins;
    public int losses;
    public int totalWins;
    public int totalLosses;
    public int totalCCs;        // total Conf Champs
    public int totalNCs;        // total National Champs
    public int totalCCLosses;
    public int totalNCLosses;
    public int totalCups;       // replaced 'totalBowls'
    public int totalCupLosses;  // replaced 'totalBowlLosses'
    public String evenYearHomeOpp; // if you still do home/away rotations


    // Schedule, games, results
    public ArrayList<GameHockey> gameSchedule; 
    public GameHockey gameOOCSchedule0;  // optional if you use OOC
    public GameHockey gameOOCSchedule4;
    public GameHockey gameOOCSchedule9;
    public ArrayList<String> gameWLSchedule;
    public ArrayList<TeamHockey> gameWinsAgainst;
    public String confChampion; // "CC"
    public String semiFinalWL;  // "SFW" or "SFL"
    public String natChampWL;   // "NCW" or "NCL"

    // Team stats for hockey
    public int teamGoals;       // total goals scored
    public int teamOppGoals;    // total goals allowed
    public int teamShots;       // total shots
    public int teamOppShots;    // total shots allowed
    public int teamTODiff;      // “takeaway-giveaway diff”
    public int teamOffTalent;   // aggregated rating from forwards
    public int teamDefTalent;   // aggregated rating from defense & goalies
    public int teamPrestige;
    public int teamPollScore;   // e.g. used in “ranking”
    public int teamStrengthOfWins;

    // Rankings in categories
    public int rankTeamGoals;
    public int rankTeamOppGoals;
    public int rankTeamShots;
    public int rankTeamOppShots;
    public int rankTeamTODiff;
    public int rankTeamOffTalent;
    public int rankTeamDefTalent;
    public int rankTeamPrestige;
    public int rankTeamRecruitClass;
    public int rankTeamPollScore;
    public int rankTeamStrengthOfWins;

    // improvements after the season
    public int diffPrestige;
    public int diffOffTalent;
    public int diffDefTalent;

    // The six hockey rosters
    public ArrayList<PlayerC>  teamCenters;
    public ArrayList<PlayerLW> teamLeftWings;
    public ArrayList<PlayerRW> teamRightWings;
    public ArrayList<PlayerLD> teamLD;
    public ArrayList<PlayerRD> teamRD;
    public ArrayList<PlayerG>  teamGoalies;

    // By year 
    public ArrayList<PlayerHockey> teamRSs; 
    public ArrayList<PlayerHockey> teamFRs;
    public ArrayList<PlayerHockey> teamSOs;
    public ArrayList<PlayerHockey> teamJRs;
    public ArrayList<PlayerHockey> teamSRs;

    public ArrayList<PlayerHockey> playersLeaving;
    public ArrayList<PlayerHockey> playersInjured;
    public ArrayList<PlayerHockey> playersRecovered;
    public ArrayList<PlayerHockey> playersInjuredAll;


    // If rating is above this, might leave early
    private static final int PRO_OVR = 90;
    private static final double PRO_CHANCE = 0.5;

    /**
     * Create new hockey team, recruit initial players, set stats to 0.
     */
    public TeamHockey(String name, String abbr, String conference,
                      HockeyLeague league, int prestige, String rivalTeamAbbr) {
        this.league = league;
        userControlled = false;
        showPopups = true;
        teamHistory = new ArrayList<>();
        hallOfFame = new ArrayList<>();
        playersInjuredAll = new ArrayList<>();

        teamCenters = new ArrayList<>();
        teamLeftWings = new ArrayList<>();
        teamRightWings = new ArrayList<>();
        teamLD = new ArrayList<>();
        teamRD = new ArrayList<>();
        teamGoalies = new ArrayList<>();

        teamRSs = new ArrayList<>();
        teamFRs = new ArrayList<>();
        teamSOs = new ArrayList<>();
        teamJRs = new ArrayList<>();
        teamSRs = new ArrayList<>();

        gameSchedule = new ArrayList<>();
        gameOOCSchedule0 = null;
        gameOOCSchedule4 = null;
        gameOOCSchedule9 = null;
        gameWinsAgainst = new ArrayList<>();
        gameWLSchedule = new ArrayList<>();
        confChampion = "";
        semiFinalWL = "";
        natChampWL = "";

        teamPrestige = prestige;
        // recruit initial rosters
        recruitInitialPlayers(4, 4, 4, 3, 3, 2); // example: 4 C, 4 LW, 4 RW, 3 LD, 3 RD, 2 G

        totalWins = 0;
        totalLosses = 0;
        totalCCs = 0;
        totalNCs = 0;
        totalCCLosses = 0;
        totalNCLosses = 0;
        totalCups = 0;
        totalCupLosses = 0;
        this.name = name;
        this.abbr = abbr;
        this.conference = conference;
        rivalTeam = rivalTeamAbbr;
        wonRivalryGame = false;

        // set stats
        teamGoals = 0;
        teamOppGoals = 0;
        teamShots = 0;
        teamOppShots = 0;
        teamTODiff = 0;
        teamOffTalent = getOffTalent();
        teamDefTalent = getDefTalent();
        teamPollScore = teamPrestige + teamOffTalent + teamDefTalent;

        numRecruits = 20; // e.g. # of prospects
        playersLeaving = new ArrayList<>();
    }

    /**
     * Private helper to recruit a base roster for new teams.
     */
    private void recruitInitialPlayers(int cNeeds, int lwNeeds, int rwNeeds,
                                       int ldNeeds, int rdNeeds, int gNeeds) {
        int stars = teamPrestige / 20 + 1;
        int chance = 20 - (teamPrestige - 20*(teamPrestige/20)); 

        for (int i = 0; i < cNeeds; i++) {
            int s = stars;
            if (100*Math.random() < 5*chance) s = s - 1;
            if (s < 1) s = 1;
            teamCenters.add(new PlayerC(league.getRandName(), (int)(4*Math.random()+1), s, this));
        }
        for (int i = 0; i < lwNeeds; i++) {
            int s = stars;
            if (100*Math.random() < 5*chance) s = s - 1;
            if (s < 1) s = 1;
            teamLeftWings.add(new PlayerLW(league.getRandName(), (int)(4*Math.random()+1), s, this));
        }
        for (int i = 0; i < rwNeeds; i++) {
            int s = stars;
            if (100*Math.random() < 5*chance) s = s - 1;
            if (s < 1) s = 1;
            teamRightWings.add(new PlayerRW(league.getRandName(), (int)(4*Math.random()+1), s, this));
        }
        for (int i = 0; i < ldNeeds; i++) {
            int s = stars;
            if (100*Math.random() < 5*chance) s = s - 1;
            if (s < 1) s = 1;
            teamLD.add(new PlayerLD(league.getRandName(), (int)(4*Math.random()+1), s, this));
        }
        for (int i = 0; i < rdNeeds; i++) {
            int s = stars;
            if (100*Math.random() < 5*chance) s = s - 1;
            if (s < 1) s = 1;
            teamRD.add(new PlayerRD(league.getRandName(), (int)(4*Math.random()+1), s, this));
        }
        for (int i = 0; i < gNeeds; i++) {
            int s = stars;
            if (100*Math.random() < 5*chance) s = s - 1;
            if (s < 1) s = 1;
            teamGoalies.add(new PlayerG(league.getRandName(), (int)(4*Math.random()+1), s, this));
        }

        sortPlayers();
    }

    /**
     * If loading from file, you’d parse lines of CSV. 
     * For brevity, omitted here. 
     * Make sure to store them in the correct hockey ArrayLists.
     */
    public TeamHockey(String loadStr, HockeyLeague league) {
        // parse loadStr, fill rosters, stats, etc.
        // ... 
    }

    /**
     * Update team’s offense/defense ratings & poll score.
     */
    public void updateTalentRatings() {
        teamOffTalent = getOffTalent();
        teamDefTalent = getDefTalent();
        teamPollScore = teamPrestige + teamOffTalent + teamDefTalent;
    }

    /**
     * Advance season, adjust prestige, remove seniors, etc.
     */
    public void advanceSeason() {
        int oldPrestige = teamPrestige;
        if (this != league.saveBless && this != league.saveCurse) {
            // if you beat your rival and the difference is not huge
            if (wonRivalryGame && (teamPrestige - league.findTeamAbbr(rivalTeam).teamPrestige < 20)) {
                teamPrestige += 2;
            } else if (!wonRivalryGame && (league.findTeamAbbr(rivalTeam).teamPrestige - teamPrestige < 20)) {
                teamPrestige -= 2;
            }

            // compare final rank to expected
            int expectedFinish = 100 - teamPrestige;
            int diffExpected = expectedFinish - rankTeamPollScore;
            if (teamPrestige > 45 || diffExpected > 0) {
                teamPrestige = (int)Math.pow(teamPrestige, 1 + (float)diffExpected/1500);
            }

            if ("NCW".equals(natChampWL)) {
                // national champs
                teamPrestige += 3;
            }
        }

        if (teamPrestige > 95) teamPrestige = 95;
        if (teamPrestige < 45) teamPrestige = 45;

        diffPrestige = teamPrestige - oldPrestige;

        if (userControlled) checkHallofFame();
        checkCareerRecords(league.leagueRecords);
        if (league.userTeam == this) {
            checkCareerRecords(league.userTeamRecords);
        }

        advanceSeasonPlayers();
    }

    /**
     * Check for any HoF inductees among players leaving.
     */
    public void checkHallofFame() {
        for (PlayerHockey p : playersLeaving) {
            int gms = p.gamesPlayed + p.careerGamesPlayed;
            int conf = p.careerAllConference + (p.wonAllConference ? 1 : 0);
            int allH = p.careerAllHockey + (p.wonAllHockey ? 1 : 0);
            int hobey = p.careerHobeys + (p.wonHobey ? 1 : 0);

            // random formula example
            if (gms/2 + 5*conf + 15*allH + 50*hobey > 50) {
                ArrayList<String> cStats = p.getCareerStatsList();
                StringBuilder sb = new StringBuilder();
                sb.append(p.getPosNameYrOvr_Str() + "&");
                for (String s : cStats) {
                    sb.append(s + "&");
                }
                hallOfFame.add(sb.toString());
            }
        }
    }

    /**
     * Check if any league records were broken by this team.
     * E.g. "Team GPG", "Shots," "Goals," etc.
     */
    public void checkLeagueRecords(LeagueRecords records) {
        records.checkRecord("Team GPG", teamGoals / numGames(), abbr, league.getYear());
        records.checkRecord("Team Opp GPG", teamOppGoals / numGames(), abbr, league.getYear());
        records.checkRecord("Team Shots/G", teamShots / numGames(), abbr, league.getYear());
        records.checkRecord("Team Opp Shots/G", teamOppShots / numGames(), abbr, league.getYear());
        records.checkRecord("Team TKW Diff", teamTODiff, abbr, league.getYear());

        // check for individual players if you want
        // e.g. top scorers, etc.
    }

    /**
     * Checks the career records for all leaving players.
     */
    public void checkCareerRecords(LeagueRecords records) {
        // if a PlayerC had e.g. careerShots, careerGoals
        // call records.checkRecord("Career Shots", c.statsShots + c.careerShots, abbr + " " + c.getInitialName(), league.getYear()-1)
        // similarly for other positions
    }

    /**
     * Determine who is leaving (year 4, or year 3 if OVR>PRO_OVR).
     */
    public void getPlayersLeaving() {
        if (!playersLeaving.isEmpty()) return; // already done
        double addChance = 0.0;
        if ("NCW".equals(natChampWL)) addChance += 0.2;

        // centers
        for (PlayerC c : teamCenters) {
            if (c.year == 4 || (c.year == 3 && c.ratOvr > PRO_OVR && Math.random() < PRO_CHANCE + addChance)) {
                playersLeaving.add(c);
            }
        }
        // same for LW, RW, LD, RD, G
        for (PlayerLW lw : teamLeftWings) {
            if (lw.year == 4 || (lw.year == 3 && lw.ratOvr > PRO_OVR && Math.random() < PRO_CHANCE + addChance)) {
                playersLeaving.add(lw);
            }
        }
        for (PlayerRW rw : teamRightWings) {
            if (rw.year == 4 || (rw.year == 3 && rw.ratOvr > PRO_OVR && Math.random() < PRO_CHANCE + addChance)) {
                playersLeaving.add(rw);
            }
        }
        for (PlayerLD ld : teamLD) {
            if (ld.year == 4 || (ld.year == 3 && ld.ratOvr > PRO_OVR && Math.random() < PRO_CHANCE + addChance)) {
                playersLeaving.add(ld);
            }
        }
        for (PlayerRD rd : teamRD) {
            if (rd.year == 4 || (rd.year == 3 && rd.ratOvr > PRO_OVR && Math.random() < PRO_CHANCE + addChance)) {
                playersLeaving.add(rd);
            }
        }
        for (PlayerG g : teamGoalies) {
            if (g.year == 4 || (g.year == 3 && g.ratOvr > PRO_OVR && Math.random() < PRO_CHANCE + addChance)) {
                playersLeaving.add(g);
            }
        }
    }

    /**
     * Advance season for players, removing seniors or early departures, then optionally
     * recruit new ones if not user-controlled.
     */
    public void advanceSeasonPlayers() {
        getPlayersLeaving(); // if not already done
        int cNeeds=0, lwNeeds=0, rwNeeds=0, ldNeeds=0, rdNeeds=0, gNeeds=0;

        // remove from rosters
        // centers
        int i = 0;
        while (i < teamCenters.size()) {
            PlayerC c = teamCenters.get(i);
            if (playersLeaving.contains(c)) {
                teamCenters.remove(i);
                cNeeds++;
            } else {
                c.advanceSeason();
                i++;
            }
        }

        // same approach for LW, RW, LD, RD, G
        i = 0;
        while (i < teamLeftWings.size()) {
            PlayerLW lw = teamLeftWings.get(i);
            if (playersLeaving.contains(lw)) {
                teamLeftWings.remove(i);
                lwNeeds++;
            } else {
                lw.advanceSeason();
                i++;
            }
        }
        // ... similarly for teamRightWings, teamLD, teamRD, teamGoalies

        if (!userControlled) {
            recruitPlayersFreshman(cNeeds, lwNeeds, rwNeeds, ldNeeds, rdNeeds, gNeeds);
            resetStats();
        }
    }

    /**
     * Recruit freshman players to fill needs for next season (if not user controlled).
     */
    public void recruitPlayersFreshman(int cNeeds, int lwNeeds, int rwNeeds,
                                       int ldNeeds, int rdNeeds, int gNeeds) {
        int starsBase = teamPrestige/20 + 1;
        int chance = 20 - (teamPrestige - 20*(teamPrestige/20));
        double starBonusChance = 0.15;
        double starBonusDouble = 0.05;

        for (int i = 0; i < cNeeds; i++) {
            int s = starsBase;
            if (100*Math.random() < 5*chance) s--;
            if (Math.random() < starBonusChance) s++;
            else if (Math.random() < starBonusDouble) s+=2;
            if (s < 1) s=1; if (s>5) s=5;
            teamCenters.add(new PlayerC(league.getRandName(), 1, s, this));
        }
        // similarly for LW, RW, LD, RD, G

        sortPlayers();
    }

    /**
     * Sort each roster by overall rating.
     */
    public void sortPlayers() {
        Collections.sort(teamCenters, new PlayerComparator());
        Collections.sort(teamLeftWings, new PlayerComparator());
        Collections.sort(teamRightWings, new PlayerComparator());
        Collections.sort(teamLD, new PlayerComparator());
        Collections.sort(teamRD, new PlayerComparator());
        Collections.sort(teamGoalies, new PlayerComparator());

        Collections.sort(teamRSs, new PlayerComparator());
        Collections.sort(teamFRs, new PlayerComparator());
        Collections.sort(teamSOs, new PlayerComparator());
        Collections.sort(teamJRs, new PlayerComparator());
        Collections.sort(teamSRs, new PlayerComparator());
    }

    /**
     * Offense talent example: top 2 lines of forwards (Centers, LWs, RWs).
     */
    public int getOffTalent() {
        // just an example
        int cTop = (teamCenters.get(0).ratOvr + teamCenters.get(1).ratOvr)/2;
        int lwTop = (teamLeftWings.get(0).ratOvr + teamLeftWings.get(1).ratOvr)/2;
        int rwTop = (teamRightWings.get(0).ratOvr + teamRightWings.get(1).ratOvr)/2;
        return (cTop + lwTop + rwTop)/3;
    }

    /**
     * Defense talent example: top 2 defense pairs plus top goalie?
     */
    public int getDefTalent() {
        int ldTop = (teamLD.get(0).ratOvr + teamLD.get(1).ratOvr)/2;
        int rdTop = (teamRD.get(0).ratOvr + teamRD.get(1).ratOvr)/2;
        int g = teamGoalies.get(0).ratOvr;
        return (ldTop + rdTop + g)/3;
    }

    /**
     * Example: get composite "HockeyIQ" if needed
     */
    public int getCompositeHockeyIQ() {
        // sum from top lines
        int iq = 0;
        iq += teamCenters.get(0).ratHockeyIQ + teamCenters.get(1).ratHockeyIQ;
        iq += teamLeftWings.get(0).ratHockeyIQ + teamLeftWings.get(1).ratHockeyIQ;
        iq += teamRightWings.get(0).ratHockeyIQ + teamRightWings.get(1).ratHockeyIQ;
        iq += teamLD.get(0).ratHockeyIQ + teamLD.get(1).ratHockeyIQ;
        iq += teamRD.get(0).ratHockeyIQ + teamRD.get(1).ratHockeyIQ;
        iq += teamGoalies.get(0).ratHockeyIQ;
        return iq/8;
    }

    /**
     * Example: get recruiting class rating
     * sums OVR of all true freshmen above 65, as a rough measure
     */
    public int getRecruitingClassRat() {
        int classStrength = 0;
        int numFreshman = 0;
        for (PlayerHockey p : getAllPlayers()) {
            if (p.year == 1 && p.ratOvr > 65) {
                classStrength += (p.ratOvr - 30);
                numFreshman++;
            }
        }
        if (numFreshman > 0) {
            return classStrength*(classStrength/numFreshman)/100;
        } else {
            return 0;
        }
    }

    /**
     * Returns a list of all players on this team (C,LW,RW,LD,RD,G).
     */
    public ArrayList<PlayerHockey> getAllPlayers() {
        ArrayList<PlayerHockey> list = new ArrayList<>();
        list.addAll(teamCenters);
        list.addAll(teamLeftWings);
        list.addAll(teamRightWings);
        list.addAll(teamLD);
        list.addAll(teamRD);
        list.addAll(teamGoalies);
        return list;
    }

    /**
     * Returns how many games so far
     */
    public int numGames() {
        if (wins + losses == 0) return 1;
        return (wins + losses);
    }

    public String getStrAbbrWL() {
        return abbr + " (" + wins + "-" + losses + ")";
    }

    public String strRep() {
        return "#" + rankTeamPollScore + " " + abbr + " (" + wins + "-" + losses + ")";
    }

    public String strRepWithBowlResults() {
        // if you rename "bowl" to "cup," do similarly
        return "#" + rankTeamPollScore + " " + abbr + " (" + wins + "-" + losses + ") " 
               + confChampion + " " + semiFinalWL + natChampWL;
    }

    public String strRepWithPrestige() {
        return "#" + rankTeamPollScore + " " + abbr 
             + " (Pres: " + teamPrestige + ")";
    }

    /**
     * Summarize your team’s last game of the week
     */
    public String weekSummaryStr() {
        int i = wins + losses - 1;
        GameHockey g = gameSchedule.get(i);
        String gameSummary = gameWLSchedule.get(i) + " " + gameSummaryStr(g);
        String rivalryGameStr = "";
        if (g.gameName.equals("Rivalry Game")) {
            if ( gameWLSchedule.get(i).equals("W") ) rivalryGameStr = "Won Rivalry!\n";
            else rivalryGameStr = "Lost Rivalry!\n";
        }
        return rivalryGameStr + name + " " + gameSummary 
               + "\nNew poll rank: #" + rankTeamPollScore 
               + " " + abbr + " (" + wins + "-" + losses + ")";
    }

    /**
     * One-line game summary e.g. "5 - 2 vs ABC #12"
     */
    public String gameSummaryStr(GameHockey g) {
        if (g.homeTeam == this) {
            return g.homeScore + " - " + g.awayScore 
                 + " vs " + g.awayTeam.abbr + " #" + g.awayTeam.rankTeamPollScore;
        } else {
            return g.awayScore + " - " + g.homeScore 
                 + " @ " + g.homeTeam.abbr + " #" + g.homeTeam.rankTeamPollScore;
        }
    }

    /**
     * Score-only summary e.g. "5 - 2"
     */
    public String gameSummaryStrScore(GameHockey g) {
        if (g.homeTeam == this) {
            return g.homeScore + " - " + g.awayScore;
        } else {
            return g.awayScore + " - " + g.homeScore;
        }
    }

    /**
     * "vs ABC #12" or "@ XYZ #45"
     */
    public String gameSummaryStrOpponent(GameHockey g) {
        if (g.homeTeam == this) {
            return "vs " + g.awayTeam.abbr + " #" + g.awayTeam.rankTeamPollScore;
        } else {
            return "@ " + g.homeTeam.abbr + " #" + g.homeTeam.rankTeamPollScore;
        }
    }

    /**
     * Resets stats for the upcoming season
     */
    public void resetStats() {
        gameSchedule = new ArrayList<>();
        gameOOCSchedule0 = null;
        gameOOCSchedule4 = null;
        gameOOCSchedule9 = null;
        gameWinsAgainst = new ArrayList<>();
        gameWLSchedule = new ArrayList<>();
        confChampion = "";
        semiFinalWL = "";
        natChampWL = "";
        wins = 0;
        losses = 0;

        teamGoals = 0;
        teamOppGoals = 0;
        teamShots = 0;
        teamOppShots = 0;
        teamTODiff = 0;
    }

    /**
     * Recompute poll score based on performance
     */
    public void updatePollScore() {
        updateStrengthOfWins();
        int preseasonBias = 8 - (wins + losses);
        if (preseasonBias < 0) preseasonBias = 0;

        // example formula
        teamPollScore = (wins*200 
                        + 3*(teamGoals - teamOppGoals)
                        + (teamShots - teamOppShots)/40
                        + 3*(preseasonBias)*(teamPrestige + getOffTalent() + getDefTalent())
                        + teamStrengthOfWins) / 10;

        if ("CC".equals(confChampion)) {
            teamPollScore += 50;
        }
        if ("NCW".equals(natChampWL)) {
            teamPollScore += 100;
        }
        if (losses == 0) {
            teamPollScore += 30;
        } else if (losses == 1) {
            teamPollScore += 15;
        }
        teamOffTalent = getOffTalent();
        teamDefTalent = getDefTalent();
    }

    /**
     * Weighted measure of your SoS or SOW
     */
    public void updateStrengthOfWins() {
        int strWins = 0;
        for (int i = 0; i < 12 && i < gameSchedule.size(); i++) {
            GameHockey g = gameSchedule.get(i);
            if (g.homeTeam == this) {
                strWins += Math.pow(60 - g.awayTeam.rankTeamPollScore,2);
            } else {
                strWins += Math.pow(60 - g.homeTeam.rankTeamPollScore,2);
            }
        }
        teamStrengthOfWins = strWins/50;
        for (TeamHockey t : gameWinsAgainst) {
            teamStrengthOfWins += Math.pow(t.wins,2);
        }
    }

    /**
     * store season info
     */
    public void updateTeamHistory() {
        String histYear = league.getYear() + ": #" + rankTeamPollScore + " " + abbr 
                        + " (" + wins + "-" + losses + ") " 
                        + confChampion + " " + semiFinalWL + natChampWL;
        for (int i = 12; i < gameSchedule.size(); i++) {
            GameHockey g = gameSchedule.get(i);
            histYear += ">" + g.gameName + ": ";
            String[] sum = getGameSummaryStr(i);
            histYear += sum[1] + " " + sum[2];
        }
        teamHistory.add(histYear);
    }

    /**
     * Return a one-liner for each game in schedule
     */
    public String[] getGameSummaryStr(int gameNumber) {
        String[] gs = new String[3];
        GameHockey g = gameSchedule.get(gameNumber);
        gs[0] = g.gameName;  // e.g. "In Conf," "Rivalry Game," "NCG"
        if (gameNumber < gameWLSchedule.size()) {
            gs[1] = gameWLSchedule.get(gameNumber) + " " + gameSummaryStr(g);
            if (g.numOT > 0) gs[1] += " (" + g.numOT + "OT)";
        } else {
            gs[1] = "---";
        }
        gs[2] = gameSummaryStrOpponent(g);
        return gs;
    }

    /**
     * Provide a final summary of your season’s performance
     */
    public String seasonSummaryStr() {
        String summary = "Your team, " + name + ", finished the season ranked #" 
                         + rankTeamPollScore + " with " + wins + " wins and " 
                         + losses + " losses.";
        // Could compare final rank to expected, if you want
        // Could mention you won the National Championship, etc.
        return summary;
    }

    /**
     * Turn the team’s entire history into an array of strings
     */
    public String[] getTeamHistoryList() {
        String[] hist = new String[teamHistory.size() + 5];
        hist[0] = "Overall W-L: " + totalWins + "-" + totalLosses;
        hist[1] = "Conf Champ Record: " + totalCCs + "-" + totalCCLosses;
        hist[2] = "Cup Game Record: " + totalCups + "-" + totalCupLosses;
        hist[3] = "National Champ Record: " + totalNCs + "-" + totalNCLosses;
        hist[4] = " ";
        for (int i = 0; i < teamHistory.size(); i++) {
            hist[i+5] = teamHistory.get(i);
        }
        return hist;
    }

    public String getTeamHistoryStr() {
        StringBuilder sb = new StringBuilder();
        sb.append("Overall W-L: " + totalWins + "-" + totalLosses + "\n");
        sb.append("Conf Champ Record: " + totalCCs + "-" + totalCCLosses + "\n");
        sb.append("Cup Game Record: " + totalCups + "-" + totalCupLosses + "\n");
        sb.append("National Champ Record: " + totalNCs + "-" + totalNCLosses + "\n");
        sb.append("\nYear by year summary:\n");
        for (String s : teamHistory) {
            sb.append(s + "\n");
        }
        return sb.toString();
    }

    /**
     * Add +1 to gamesPlayed for the “starters,” so they can get off-season improvement.
     */
    public void addGamePlayedPlayers(boolean wonGame) {
        // Forwards
        addGamePlayedList(teamCenters, 4, wonGame);
        addGamePlayedList(teamLeftWings, 4, wonGame);
        addGamePlayedList(teamRightWings, 4, wonGame);
        // Defense
        addGamePlayedList(teamLD, 3, wonGame);
        addGamePlayedList(teamRD, 3, wonGame);
        // Goalie
        addGamePlayedList(teamGoalies, 1, wonGame);
    }

    private void addGamePlayedList(ArrayList<? extends PlayerHockey> arr, int starters, boolean wonGame) {
        for (int i = 0; i < starters && i < arr.size(); i++) {
            arr.get(i).gamesPlayed++;
            if (wonGame) arr.get(i).statsWins++;
        }
    }

    /**
     * Provide a full list of players leaving 
     */
    public String getGraduatingPlayersStr() {
        StringBuilder sb = new StringBuilder();
        for (PlayerHockey p : playersLeaving) {
            sb.append(p.getPosNameYrOvrPot_OneLine() + "\n");
        }
        return sb.toString();
    }

    public String[] getGradPlayersList() {
        String[] arr = new String[playersLeaving.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = playersLeaving.get(i).getPosNameYrOvrPot_Str();
        }
        return arr;
    }

    /**
     * Provide a final "Team Needs" if you do AI recruiting logic
     */
    public String getTeamNeeds() {
        // Example: if you want 4 centers, 4 lw, 4 rw, 3 ld, 3 rd, 2 goalies minimum
        StringBuilder sb = new StringBuilder();
        sb.append( (4 - teamCenters.size()) + "C, ");
        sb.append( (4 - teamLeftWings.size()) + "LW, ");
        sb.append( (4 - teamRightWings.size()) + "RW, ");
        sb.append( (3 - teamLD.size()) + "LD, ");
        sb.append( (3 - teamRD.size()) + "RD, ");
        sb.append( (2 - teamGoalies.size()) + "G");
        return sb.toString();
    }


}

/**
 * Compare players by overall rating, then potential, also considering injury status.
 */
class PlayerComparator implements Comparator<PlayerHockey> {
    @Override
    public int compare(PlayerHockey a, PlayerHockey b) {
        if (!a.isInjured && !b.isInjured) {
            // both healthy
            if (a.year > 0 && b.year > 0) {
                // neither is redshirt
                if (a.ratOvr > b.ratOvr) return -1;
                else if (a.ratOvr < b.ratOvr) return 1;
                else {
                    return a.ratPot > b.ratPot ? -1 : a.ratPot == b.ratPot ? 0 : 1;
                }
            } else if (a.year > 0) {
                return -1;
            } else if (b.year > 0) {
                return 1;
            } else {
                // both RS
                return a.ratOvr > b.ratOvr ? -1 
                     : a.ratOvr == b.ratOvr ? 0 : 1;
            }
        } else if (!a.isInjured) {
            return -1;
        } else if (!b.isInjured) {
            return 1;
        } else {
            // both injured
            return a.ratOvr > b.ratOvr ? -1 
                 : a.ratOvr == b.ratOvr ? 0 : 1;
        }
    }
}

/**
 * Comparator used if you want to sort by position in some order: 
 * C=0, LW=1, RW=2, LD=3, RD=4, G=5, etc.
 */
class PlayerPositionComparator implements Comparator<PlayerHockey> {
    @Override
    public int compare(PlayerHockey a, PlayerHockey b) {
        int aPos = PlayerHockey.getPosNumber(a.position);
        int bPos = PlayerHockey.getPosNumber(b.position);
        return Integer.compare(aPos, bPos);
    }
}

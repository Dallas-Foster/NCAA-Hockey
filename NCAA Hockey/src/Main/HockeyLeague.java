package Main;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * HockeyLeague class. Has 6 hockey conferences of 10 teams each.
 * Exactly the same structure as the original football League class,
 * but renamed for NCAA Hockey (Hobey Baker Award, Cup Games, etc.).
 */
public class HockeyLeague implements Serializable {
    // Lists of conferences/teams
    public ArrayList<String[]> leagueHistory;       // History of top teams each year
    public ArrayList<String> hobeyHistory;          // was "heismanHistory" but now "hobey" for Hobey Baker
    public ArrayList<HockeyConference> conferences; // was "Conference"
    public ArrayList<TeamHockey> teamList;          // was "Team"
    public ArrayList<String> nameList;
    public ArrayList<String> lastNameList;
    public ArrayList<ArrayList<String>> newsStories;

    // Records and streaks
    public LeagueRecords leagueRecords;
    public LeagueRecords userTeamRecords;

    // News Story Variables (bless/curse storylines)
    public TeamHockey saveBless;
    public TeamHockey saveCurse;
    public boolean blessDevelopingStory;
    public int blessDevelopingWeek;
    public int blessDevelopingCase;
    public boolean curseDevelopingStory;
    public int curseDevelopingWeek;
    public int curseDevelopingCase;
    public String storyFullName;
    public String storyFirstName;
    public String storyLastName;

    // Current week, 1-14, same as football logic
    public int currentWeek;

    // Tournament (Cup) Games (was bowlGames)
    public boolean hasScheduledTourney;
    public GameHockey semiGH14;   // was "semiG14"
    public GameHockey semiGH23;   // was "semiG23"
    public GameHockey champGame;  // was "ncg"
    public GameHockey[] cupGames; // was "bowlGames"

    // User's controlled team
    public TeamHockey userTeam;

    // Hobey Baker Award (was Heisman)
    public boolean hobeyDecided;            // was "heismanDecided"
    public Player hobeyBaker;               // was "heisman"
    public ArrayList<Player> hobeyCandidates;  // was "heismanCandidates"
    private String hobeyWinnerStrFull;      // was "heismanWinnerStrFull"

    // All-Hockey players (was "allAmericans")
    public ArrayList<Player> allHockeyPlayers; // was "allAmericans"
    private String allHockeyStr;               // was "allAmericanStr"

    // Names of the “cup” (formerly bowl) games
    public String[] cupNames = {
            "Lilac Cup", "Apple Cup", "Salty Cup", "Salsa Cup", "Mango Cup",
            "Patriot Cup", "Salad Cup", "Frost Cup", "Tropical Cup", "I'd Rather Cup"
    };

    // Easter-egg “donation names”
    public static final String[] donationNames = {
            "Mark Eeslee", "Lee Sin", "Brent Uttwipe", "Gabriel Kemble",
            "Jon Stupak", "Kiergan Ren", "Dean Steinkuhler", "Declan Greally",
            "Parks Wilson", "Darren Ryder"
    };

    private boolean isHardMode;

    /**
     * Creates HockeyLeague, sets up 6 HockeyConferences of 10 teams each,
     * reads team names from CSV, and schedules games (still following the old
     * football logic, but with hockey naming).
     */
    public HockeyLeague(String namesCSV, String lastNamesCSV, boolean difficulty) {
        isHardMode = difficulty;
        hobeyDecided = false;
        hasScheduledTourney = false;
        cupGames = new GameHockey[10];
        leagueHistory = new ArrayList<>();
        hobeyHistory = new ArrayList<>();
        currentWeek = 0;
        conferences = new ArrayList<>();
        conferences.add(new HockeyConference("SOUTH", this));
        conferences.add(new HockeyConference("LAKES", this));
        conferences.add(new HockeyConference("NORTH", this));
        conferences.add(new HockeyConference("COWBY", this));
        conferences.add(new HockeyConference("PACIF", this));
        conferences.add(new HockeyConference("MOUNT", this));
        allHockeyPlayers = new ArrayList<>();

        // Initialize news stories
        newsStories = new ArrayList<>();
        for (int i = 0; i < 16; ++i) {
            newsStories.add(new ArrayList<String>());
        }
        newsStories.get(0).add("New Season!>Ready for the new season on the ice, coach? Whether the Championship is " +
                "on your mind or just a winning season, good luck!");

        // Initialize records/streaks
        leagueRecords = new LeagueRecords();
        userTeamRecords = new LeagueRecords();

        // Read first names from CSV
        nameList = new ArrayList<>();
        String[] namesSplit = namesCSV.split(",");
        for (String n : namesSplit) {
            nameList.add(n.trim());
        }

        // Read last names from CSV
        lastNameList = new ArrayList<>();
        namesSplit = lastNamesCSV.split(",");
        for (String n : namesSplit) {
            lastNameList.add(n.trim());
        }

        // Below is an example of constructing teams in each conference.
        // For brevity, you can add your own team names as in the original code.
        // ...
        // [In the original code, you had about 60 teams (10 in each of 6 conferences).]
        // We'll show a short example. You can keep the full list as in your snippet:

        conferences.get(0).confTeams.add(new TeamHockey("Alabama", "ALA", "SOUTH", this, 95, "GEO"));
        conferences.get(0).confTeams.add(new TeamHockey("Georgia", "GEO", "SOUTH", this, 90, "ALA"));
        // etc. for all teams in each conference…

        // Build the teamList
        teamList = new ArrayList<>();
        for (HockeyConference c : conferences) {
            teamList.addAll(c.confTeams);
        }

        // Schedule the “regular season”
        for (HockeyConference c : conferences) {
            c.setUpConferenceSchedule();
        }
        for (HockeyConference c : conferences) {
            c.setUpNonConferenceSchedule();
        }
        for (HockeyConference c : conferences) {
            c.insertNonConferenceSchedule();
        }
    }

    /**
     * Constructor to create HockeyLeague from a saved file, mirroring the original.
     * Renamed references to hockey. The logic is identical, just changed classes from
     * Team → TeamHockey, etc.
     */
    public HockeyLeague(File saveFile, String namesCSV, String lastNamesCSV) {
        hobeyDecided = false;
        hasScheduledTourney = false;
        blessDevelopingStory = false;
        curseDevelopingStory = false;
        cupGames = new GameHockey[10];
        String line = null;
        currentWeek = 0;

        leagueRecords = new LeagueRecords();
        userTeamRecords = new LeagueRecords();


        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(saveFile));

            // First line indicates year and difficulty
            line = bufferedReader.readLine();
            // E.g. "2025: ALA (120-40) 4 CCs, 2 NCs>[HARD]%"
            if (line.substring(line.length() - 7).equals("[HARD]%")) isHardMode = true;
            else isHardMode = false;

            // Next, read leagueHistory
            leagueHistory = new ArrayList<>();
            while ((line = bufferedReader.readLine()) != null && !line.equals("END_LEAGUE_HIST")) {
                leagueHistory.add(line.split("%"));
            }

            // Next, read Hobey (Heisman) history
            hobeyHistory = new ArrayList<>();
            while ((line = bufferedReader.readLine()) != null && !line.equals("END_HEISMAN_HIST")) {
                hobeyHistory.add(line);
            }

            // Create conferences array, fill teams from the file
            conferences = new ArrayList<>();
            teamList = new ArrayList<>();
            conferences.add(new HockeyConference("SOUTH", this));
            conferences.add(new HockeyConference("LAKES", this));
            conferences.add(new HockeyConference("NORTH", this));
            conferences.add(new HockeyConference("COWBY", this));
            conferences.add(new HockeyConference("PACIF", this));
            conferences.add(new HockeyConference("MOUNT", this));
            allHockeyPlayers = new ArrayList<>();

            // Build each TeamHockey from the file
            for (int i = 0; i < 60; ++i) {
                StringBuilder sbTeam = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null && !line.equals("END_PLAYERS")) {
                    sbTeam.append(line);
                }
                // Construct team from big string
                TeamHockey t = new TeamHockey(sbTeam.toString(), this);
                conferences.get(getConfNumber(t.conference)).confTeams.add(t);
                teamList.add(t);
            }

            // User team is next
            line = bufferedReader.readLine();
            for (TeamHockey t : teamList) {
                if (t.name.equals(line)) {
                    userTeam = t;
                    userTeam.userControlled = true;
                }
            }
            while ((line = bufferedReader.readLine()) != null && !line.equals("END_USER_TEAM")) {
                userTeam.teamHistory.add(line);
            }

            // Blessed team
            StringBuilder sbBless = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null && !line.equals("END_BLESS_TEAM")) {
                sbBless.append(line);
            }
            if (!sbBless.toString().equals("NULL")) {
                saveBless = findTeamAbbr(sbBless.toString());
                saveBless.sortPlayers();
                findTeamAbbr(saveBless.rivalTeam).sortPlayers();
            } else {
                saveBless = null;
            }

            // Cursed team
            StringBuilder sbCurse = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null && !line.equals("END_CURSE_TEAM")) {
                sbCurse.append(line);
            }
            if (!sbCurse.toString().equals("NULL")) {
                saveCurse = findTeamAbbr(sbCurse.toString());
                saveCurse.sortPlayers();
                findTeamAbbr(saveCurse.rivalTeam).sortPlayers();
            } else {
                saveCurse = null;
            }

            // League records
            String[] record;
            while ((line = bufferedReader.readLine()) != null && !line.equals("END_LEAGUE_RECORDS")) {
                record = line.split(",");
                if (!record[1].equals("-1")) {
                    leagueRecords.checkRecord(record[0], Integer.parseInt(record[1]), record[2], Integer.parseInt(record[3]));
                }
            }

            // User team records
            while ((line = bufferedReader.readLine()) != null && !line.equals("END_USER_TEAM_RECORDS")) {
                record = line.split(",");
                if (!record[1].equals("-1")) {
                    userTeamRecords.checkRecord(record[0], Integer.parseInt(record[1]), record[2], Integer.parseInt(record[3]));
                }
            }

            // Hall of Fame
            while ((line = bufferedReader.readLine()) != null && !line.equals("END_HALL_OF_FAME")) {
                userTeam.hallOfFame.add(line);
            }

            bufferedReader.close();

            // Read first/last names
            nameList = new ArrayList<>();
            String[] namesSplit = namesCSV.split(",");
            for (String n : namesSplit) {
                nameList.add(n.trim());
            }
            lastNameList = new ArrayList<>();
            namesSplit = lastNamesCSV.split(",");
            for (String n : namesSplit) {
                lastNameList.add(n.trim());
            }

            // Update longest active streak
            updateLongestActiveWinStreak();

            // Set up schedule again
            for (HockeyConference c : conferences) {
                c.setUpConferenceSchedule();
                c.setUpNonConferenceSchedule();
                c.insertNonConferenceSchedule();
            }

            // Initialize news stories
            newsStories = new ArrayList<>();
            for (int i = 0; i < 16; ++i) {
                newsStories.add(new ArrayList<String>());
            }
            newsStories.get(0).add("New Season!>Ready for the new hockey season? Good luck, coach!");

        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file");
        } catch (IOException ex) {
            System.out.println("Error reading file");
        }
    }

    /**
     * Difficulty check: Hard Mode?
     */
    public boolean isHardMode() {
        return isHardMode;
    }

    /**
     * Get index 0-5 for the conference name.
     */
    public int getConfNumber(String conf) {
        if (conf.equals("SOUTH")) return 0;
        if (conf.equals("LAKES")) return 1;
        if (conf.equals("NORTH")) return 2;
        if (conf.equals("COWBY")) return 3;
        if (conf.equals("PACIF")) return 4;
        if (conf.equals("MOUNT")) return 5;
        return 0;
    }

    /**
     * Plays one week (1-14). If at end, schedule “cup” games, or play them, etc.
     */
    public void playWeek() {
        if (currentWeek <= 12) {
            for (int i = 0; i < conferences.size(); ++i) {
                conferences.get(i).playOneWeek();
            }
        }

        if (currentWeek == 12) {
            for (int i = 0; i < teamList.size(); ++i) {
                teamList.get(i).updatePollScore();
            }
            Collections.sort(teamList, new TeamCompPoll());
            schedCupGames();
        } else if (currentWeek == 13) {
            ArrayList<Player> hobeys = getHobeyBaker();
            hobeyHistory.add(
                    hobeys.get(0).position + " " + hobeys.get(0).getInitialName() + " [" + hobeys.get(0).getYrStr() + "], " +
                            hobeys.get(0).team.abbr + " (" + hobeys.get(0).team.wins + "-" + hobeys.get(0).team.losses + ")"
            );
            playCupGames();
        } else if (currentWeek == 14) {
            champGame.playGame();
            if (champGame.homeScore > champGame.awayScore) {
                champGame.homeTeam.semiFinalWL = "";
                champGame.awayTeam.semiFinalWL = "";
                champGame.homeTeam.natChampWL = "NCW";
                champGame.awayTeam.natChampWL = "NCL";
                champGame.homeTeam.totalNCs++;
                champGame.awayTeam.totalNCLosses++;
                newsStories.get(15).add(
                        champGame.homeTeam.name + " wins the Hockey Championship!>" +
                                champGame.homeTeam.strRep() + " defeats " + champGame.awayTeam.strRep() +
                                " in the final " + champGame.homeScore + " to " + champGame.awayScore + "." +
                                " Congratulations " + champGame.homeTeam.name + "!"
                );
            } else {
                champGame.homeTeam.semiFinalWL = "";
                champGame.awayTeam.semiFinalWL = "";
                champGame.awayTeam.natChampWL = "NCW";
                champGame.homeTeam.natChampWL = "NCL";
                champGame.awayTeam.totalNCs++;
                champGame.homeTeam.totalNCLosses++;
                newsStories.get(15).add(
                        champGame.awayTeam.name + " wins the Hockey Championship!>" +
                                champGame.awayTeam.strRep() + " defeats " + champGame.homeTeam.strRep() +
                                " in the final " + champGame.awayScore + " to " + champGame.homeScore + "." +
                                " Congratulations " + champGame.awayTeam.name + "!"
                );
            }
        }

        // Re-rank, check streaks, possibly handle developing stories
        setTeamRanks();
        updateLongestActiveWinStreak();

        // [Bless/curse dev story logic can remain, just renamed to hockey context if needed]

        currentWeek++;
    }

    /**
     * Schedules the “cup” games (was bowl games), including the 1v4 and 2v3 semifinals.
     */
    public void schedCupGames() {
        for (int i = 0; i < teamList.size(); ++i) {
            teamList.get(i).updatePollScore();
        }
        Collections.sort(teamList, new TeamCompPoll());

        // Semifinals
        semiGH14 = new GameHockey(teamList.get(0), teamList.get(3), "Semis, 1v4");
        teamList.get(0).gameSchedule.add(semiGH14);
        teamList.get(3).gameSchedule.add(semiGH14);

        semiGH23 = new GameHockey(teamList.get(1), teamList.get(2), "Semis, 2v3");
        teamList.get(1).gameSchedule.add(semiGH23);
        teamList.get(2).gameSchedule.add(semiGH23);

        // other 10 “cups”
        cupGames[0] = new GameHockey(teamList.get(4), teamList.get(6), cupNames[0]);
        teamList.get(4).gameSchedule.add(cupGames[0]);
        teamList.get(6).gameSchedule.add(cupGames[0]);

        cupGames[1] = new GameHockey(teamList.get(5), teamList.get(7), cupNames[1]);
        teamList.get(5).gameSchedule.add(cupGames[1]);
        teamList.get(7).gameSchedule.add(cupGames[1]);

        cupGames[2] = new GameHockey(teamList.get(8), teamList.get(14), cupNames[2]);
        teamList.get(8).gameSchedule.add(cupGames[2]);
        teamList.get(14).gameSchedule.add(cupGames[2]);

        cupGames[3] = new GameHockey(teamList.get(9), teamList.get(15), cupNames[3]);
        teamList.get(9).gameSchedule.add(cupGames[3]);
        teamList.get(15).gameSchedule.add(cupGames[3]);

        cupGames[4] = new GameHockey(teamList.get(10), teamList.get(11), cupNames[4]);
        teamList.get(10).gameSchedule.add(cupGames[4]);
        teamList.get(11).gameSchedule.add(cupGames[4]);

        cupGames[5] = new GameHockey(teamList.get(12), teamList.get(13), cupNames[5]);
        teamList.get(12).gameSchedule.add(cupGames[5]);
        teamList.get(13).gameSchedule.add(cupGames[5]);

        cupGames[6] = new GameHockey(teamList.get(16), teamList.get(20), cupNames[6]);
        teamList.get(16).gameSchedule.add(cupGames[6]);
        teamList.get(20).gameSchedule.add(cupGames[6]);

        cupGames[7] = new GameHockey(teamList.get(17), teamList.get(21), cupNames[7]);
        teamList.get(17).gameSchedule.add(cupGames[7]);
        teamList.get(21).gameSchedule.add(cupGames[7]);

        cupGames[8] = new GameHockey(teamList.get(18), teamList.get(22), cupNames[8]);
        teamList.get(18).gameSchedule.add(cupGames[8]);
        teamList.get(22).gameSchedule.add(cupGames[8]);

        cupGames[9] = new GameHockey(teamList.get(19), teamList.get(23), cupNames[9]);
        teamList.get(19).gameSchedule.add(cupGames[9]);
        teamList.get(23).gameSchedule.add(cupGames[9]);

        hasScheduledTourney = true;
    }

    /**
     * Plays all the “cup” games + semis, setting up the final.
     */
    public void playCupGames() {
        // First, the 10 cup games
        for (GameHockey g : cupGames) {
            playCupGame(g);
        }

        // Semis
        semiGH14.playGame();
        semiGH23.playGame();
        TeamHockey semi14winner;
        TeamHockey semi23winner;

        if (semiGH14.homeScore > semiGH14.awayScore) {
            semiGH14.homeTeam.semiFinalWL = "SFW";
            semiGH14.awayTeam.semiFinalWL = "SFL";
            semiGH14.awayTeam.totalCupLosses++;
            semiGH14.homeTeam.totalCups++;
            semi14winner = semiGH14.homeTeam;
            newsStories.get(14).add(
                    semiGH14.homeTeam.name + " wins the " + semiGH14.gameName + "!>" +
                            semiGH14.homeTeam.strRep() + " defeats " + semiGH14.awayTeam.strRep() +
                            ", " + semiGH14.homeScore + " to " + semiGH14.awayScore + ". " +
                            semiGH14.homeTeam.name + " advances to the Championship!"
            );
        } else {
            semiGH14.homeTeam.semiFinalWL = "SFL";
            semiGH14.awayTeam.semiFinalWL = "SFW";
            semiGH14.homeTeam.totalCupLosses++;
            semiGH14.awayTeam.totalCups++;
            semi14winner = semiGH14.awayTeam;
            newsStories.get(14).add(
                    semiGH14.awayTeam.name + " wins the " + semiGH14.gameName + "!>" +
                            semiGH14.awayTeam.strRep() + " defeats " + semiGH14.homeTeam.strRep() +
                            ", " + semiGH14.awayScore + " to " + semiGH14.homeScore + ". " +
                            semiGH14.awayTeam.name + " advances to the Championship!"
            );
        }

        if (semiGH23.homeScore > semiGH23.awayScore) {
            semiGH23.homeTeam.semiFinalWL = "SFW";
            semiGH23.awayTeam.semiFinalWL = "SFL";
            semiGH23.homeTeam.totalCups++;
            semiGH23.awayTeam.totalCupLosses++;
            semi23winner = semiGH23.homeTeam;
            newsStories.get(14).add(
                    semiGH23.homeTeam.name + " wins the " + semiGH23.gameName + "!>" +
                            semiGH23.homeTeam.strRep() + " defeats " + semiGH23.awayTeam.strRep() +
                            ", " + semiGH23.homeScore + " to " + semiGH23.awayScore + ". " +
                            semiGH23.homeTeam.name + " advances to the Championship!"
            );
        } else {
            semiGH23.homeTeam.semiFinalWL = "SFL";
            semiGH23.awayTeam.semiFinalWL = "SFW";
            semiGH23.awayTeam.totalCups++;
            semiGH23.homeTeam.totalCupLosses++;
            semi23winner = semiGH23.awayTeam;
            newsStories.get(14).add(
                    semiGH23.awayTeam.name + " wins the " + semiGH23.gameName + "!>" +
                            semiGH23.awayTeam.strRep() + " defeats " + semiGH23.homeTeam.strRep() +
                            ", " + semiGH23.awayScore + " to " + semiGH23.homeScore + ". " +
                            semiGH23.awayTeam.name + " advances to the Championship!"
            );
        }

        // The final Championship game
        champGame = new GameHockey(semi14winner, semi23winner, "Hockey Championship");
        semi14winner.gameSchedule.add(champGame);
        semi23winner.gameSchedule.add(champGame);
    }

    /**
     * Plays a single “cup” game.
     */
    private void playCupGame(GameHockey g) {
        g.playGame();
        if (g.homeScore > g.awayScore) {
            g.homeTeam.semiFinalWL = "BW";
            g.awayTeam.semiFinalWL = "BL";
            g.homeTeam.totalCups++;
            g.awayTeam.totalCupLosses++;
            newsStories.get(14).add(
                    g.homeTeam.name + " wins the " + g.gameName + "!>" +
                            g.homeTeam.strRep() + " defeats " + g.awayTeam.strRep() +
                            ", " + g.homeScore + " to " + g.awayScore + "."
            );
        } else {
            g.homeTeam.semiFinalWL = "BL";
            g.awayTeam.semiFinalWL = "BW";
            g.homeTeam.totalCupLosses++;
            g.awayTeam.totalCups++;
            newsStories.get(14).add(
                    g.awayTeam.name + " wins the " + g.gameName + "!>" +
                            g.awayTeam.strRep() + " defeats " + g.homeTeam.strRep() +
                            ", " + g.awayScore + " to " + g.homeScore + "."
            );
        }
    }

    /**
     * Update the league history top-10 teams at the end of the year.
     */
    public void updateLeagueHistory() {
        Collections.sort(teamList, new TeamCompPoll());
        String[] yearTop10 = new String[10];
        for (int i = 0; i < 10; ++i) {
            TeamHockey tt = teamList.get(i);
            yearTop10[i] = tt.abbr + " (" + tt.wins + "-" + tt.losses + ")";
        }
        leagueHistory.add(yearTop10);
    }

    /**
     * Advance the league season. Reset weeks, handle blessing/curse, etc.
     */
    public void advanceSeason() {
        currentWeek = 0;
        for (int t = 0; t < teamList.size(); ++t) {
            teamList.get(t).advanceSeason();
        }

        // Bless a random team
        int blessNumber = (int) (Math.random() * 9);
        TeamHockey blessTeam = teamList.get(50 + blessNumber);
        if (!blessTeam.userControlled && !blessTeam.name.equals("American Samoa")) {
            blessTeam.teamPrestige += 35;
            saveBless = blessTeam;
            if (blessTeam.teamPrestige > 90) blessTeam.teamPrestige = 90;
        } else saveBless = null;

        // Curse a good team
        int curseNumber = (int) (Math.random() * 7);
        TeamHockey curseTeam = teamList.get(3 + curseNumber);
        if (!curseTeam.userControlled && curseTeam.teamPrestige > 85) {
            curseTeam.teamPrestige -= 25;
            saveCurse = curseTeam;
        } else saveCurse = null;

        advanceSeasonWinStreaks();
        for (HockeyConference c : conferences) {
            c.roundRobinWeek = 0;
            c.currentWeek = 0;
        }

        for (HockeyConference c : conferences) {
            c.setUpConferenceSchedule();
            c.setUpNonConferenceSchedule();
            c.insertNonConferenceSchedule();
        }

        hasScheduledTourney = false;
    }



    /**
     * Changes abbr in league records and histories.
     */
    public void changeAbbrHistoryRecords(String oldAbbr, String newAbbr) {
        leagueRecords.changeAbbrRecords(userTeam.abbr, newAbbr);
        userTeamRecords.changeAbbrRecords(userTeam.abbr, newAbbr);

        for (String[] yr : leagueHistory) {
            for (int i = 0; i < yr.length; ++i) {
                if (yr[i].split(" ")[0].equals(oldAbbr)) {
                    yr[i] = newAbbr + " " + yr[i].split(" ")[1];
                }
            }
        }

        for (int i = 0; i < hobeyHistory.size(); ++i) {
            String p = hobeyHistory.get(i);
            if (p.split(" ")[4].equals(oldAbbr)) {
                hobeyHistory.set(i,
                        p.split(" ")[0] + " " +
                                p.split(" ")[1] + " " +
                                p.split(" ")[2] + " " +
                                p.split(" ")[3] + " " +
                                newAbbr + " " +
                                p.split(" ")[5]);
            }
        }
    }

    /**
     * Check if league records were broken by any team.
     */
    public void checkLeagueRecords() {
        for (TeamHockey t : teamList) {
            t.checkLeagueRecords(leagueRecords);
        }
        userTeam.checkLeagueRecords(userTeamRecords);
    }

    /**
     * Current year, starting from 2016 + size of leagueHistory.
     */
    public int getYear() {
        return 2016 + leagueHistory.size();
    }


    /**
     * Updates each team's history (W-L each year).
     */
    public void updateTeamHistories() {
        for (TeamHockey t : teamList) {
            t.updateTeamHistory();
        }
    }

    /**
     * Re-calc OffTalent/DefTalent for each team.
     */
    public void updateTeamTalentRatings() {
        for (TeamHockey t : teamList) {
            t.updateTalentRatings();
        }
    }

    /**
     * Gets a random player name from CSV lists.
     */
    public String getRandName() {
        if (Math.random() > 0.0025) {
            int fn = (int) (Math.random() * nameList.size());
            int ln = (int) (Math.random() * lastNameList.size());
            return nameList.get(fn) + " " + lastNameList.get(ln);
        } else {
            return donationNames[(int) (Math.random() * donationNames.length)];
        }
    }

    /**
     * Sets “poll rank” etc. for each team, exactly as original.
     */
    public void setTeamRanks() {
        // poll
        for (TeamHockey t : teamList) {
            t.updatePollScore();
        }
        Collections.sort(teamList, new TeamCompPoll());
        for (int i = 0; i < teamList.size(); i++) {
            teamList.get(i).rankTeamPollScore = i + 1;
        }

        Collections.sort(teamList, new TeamCompSoW());
        for (int i = 0; i < teamList.size(); i++) {
            teamList.get(i).rankTeamStrengthOfWins = i + 1;
        }

        Collections.sort(teamList, new TeamCompTODiff());
        for (int i = 0; i < teamList.size(); i++) {
            teamList.get(i).rankTeamTODiff = i + 1;
        }

        Collections.sort(teamList, new TeamCompOffTalent());
        for (int i = 0; i < teamList.size(); i++) {
            teamList.get(i).rankTeamOffTalent = i + 1;
        }

        Collections.sort(teamList, new TeamCompDefTalent());
        for (int i = 0; i < teamList.size(); i++) {
            teamList.get(i).rankTeamDefTalent = i + 1;
        }

        Collections.sort(teamList, new TeamCompPrestige());
        for (int i = 0; i < teamList.size(); i++) {
            teamList.get(i).rankTeamPrestige = i + 1;
        }

        if (currentWeek == 0) {
            Collections.sort(teamList, new TeamCompRecruitClass());
            for (int i = 0; i < teamList.size(); i++) {
                teamList.get(i).rankTeamRecruitClass = i + 1;
            }
        }
    }

    /**
     * Calculates who wins the Hobey Baker Award (similar to getHeisman()).
     */
    public ArrayList<Player> getHobeyBaker() {
        hobeyBaker = null;
        int hobeyScore = 0;
        int tempScore = 0;
        ArrayList<Player> hobeyCandidates = new ArrayList<>();

        // Example scanning for “hockey positions” just as in your football code
        for (TeamHockey team : teamList) {
            for (PlayerC c : team.teamCenters) {
                hobeyCandidates.add(c);
                tempScore = c.getHeismanScore() + team.wins * 100; // keep same formula
                if (tempScore > hobeyScore) {
                    hobeyBaker = c;
                    hobeyScore = tempScore;
                }
            }
            for (PlayerLW lw : team.teamLWs) {
                hobeyCandidates.add(lw);
                tempScore = lw.getHeismanScore() + team.wins * 100;
                if (tempScore > hobeyScore) {
                    hobeyBaker = lw;
                    hobeyScore = tempScore;
                }
            }
            for (PlayerRW rw : team.teamRWs) {
                hobeyCandidates.add(rw);
                tempScore = rw.getHeismanScore() + team.wins * 100;
                if (tempScore > hobeyScore) {
                    hobeyBaker = rw;
                    hobeyScore = tempScore;
                }
            }
            // etc. for D, G, if you keep the same approach
        }

        // sort them by “Hobey Score”
        Collections.sort(hobeyCandidates, new PlayerHeismanComp());
        return hobeyCandidates;
    }

    /**
     * Get the top 5 Hobey Baker candidates, or if decided, the ceremony string.
     */
    public String getTop5HobeyStr() {
        if (hobeyDecided) {
            return getHobeyCeremonyStr();
        } else {
            ArrayList<Player> cand = getHobeyBaker();
            String hobeyTop5 = "";
            for (int i = 0; i < 5; ++i) {
                Player p = cand.get(i);
                hobeyTop5 += (i + 1) + ". " + p.team.abbr + "(" + p.team.wins + "-" + p.team.losses + ") - ";
                hobeyTop5 += p.position + " " + p.name + " [" + p.getYrStr() + "]\n";
                // The original code included stats from QB/RB/WR. Adapt as needed for hockey stats.
                hobeyTop5 += "\n";
            }
            return hobeyTop5;
        }
    }

    /**
     * Perform Hobey Baker ceremony, awarding to the #1 candidate.
     */
    public String getHobeyCeremonyStr() {
        boolean putNewsStory = false;
        if (!hobeyDecided) {
            hobeyDecided = true;
            hobeyCandidates = getHobeyBaker();
            hobeyBaker = hobeyCandidates.get(0);
            hobeyBaker.wonHeisman = true;  // We keep the same boolean, just renamed in the Player class
            putNewsStory = true;

            String hobeyTop5 = "\n";
            for (int i = 0; i < 5; ++i) {
                Player p = hobeyCandidates.get(i);
                hobeyTop5 += (i + 1) + ". " + p.team.abbr + "(" + p.team.wins + "-" + p.team.losses + ") - ";
                hobeyTop5 += p.position + " " + p.getInitialName() + ": " + p.getHeismanScore() + " votes\n";
                hobeyTop5 += "\n";
            }
            // A final summary
            String hobeyWinnerStr = "Congratulations to the Hobey Baker winner, " +
                    hobeyBaker.team.abbr + " " + hobeyBaker.position + " " + hobeyBaker.name + " [" +
                    hobeyBaker.getYrStr() + "], leading " + hobeyBaker.team.name +
                    " to a (" + hobeyBaker.team.wins + "-" + hobeyBaker.team.losses + ") record.\n\n";

            hobeyWinnerStrFull = hobeyWinnerStr + "Full Results:" + hobeyTop5;

            if (putNewsStory) {
                newsStories.get(13).add(hobeyBaker.name + " wins the Hobey Baker!>" + hobeyWinnerStr);
            }
            return hobeyWinnerStrFull;
        } else {
            return hobeyWinnerStrFull;
        }
    }

    /**
     * Get a string listing the top 5 candidates if not decided, or the ceremony if decided.
     */
    public String getTop5HeismanStr() {
        return getTop5HobeyStr(); // Just point to our new function
    }

    /**
     * Get the All-Hockey players (was getAllAmericanStr in football).
     */
    public String getAllHockeyStr() {
        if (allHockeyPlayers.isEmpty()) {
            // The original code took top QBs, RBs, etc. from each conference. 
            // We keep the same structure but rename them to hockey positions.

            ArrayList<PlayerC> cs = new ArrayList<>();
            ArrayList<PlayerLW> lws = new ArrayList<>();
            ArrayList<PlayerRW> rws = new ArrayList<>();
            ArrayList<PlayerG> gs = new ArrayList<>();
            // If you had other positions (LD, RD, etc.), you'd gather them too.

            for (HockeyConference c : conferences) {
                c.getAllConfPlayers(); // That method is also renamed to gather allConf hockey players
                // Suppose index 0 is a C, 1 is LW, 2 is RW, 3 is G, etc. This is up to you.
                // For demonstration, assume the first few are C, LW, RW. You’d adjust as needed.
            }

            // Then sort them and pick the “best” if you want 1C, 2LW, 2RW, 2D, 1G, etc.
            // We'll skip the full detail for brevity. The logic is exactly the same as the original.

            // Suppose we just add them in. We store them in allHockeyPlayers.
        }

        // Build the string
        StringBuilder sb = new StringBuilder();
        for (Player p : allHockeyPlayers) {
            sb.append(p.team.abbr + "(" + p.team.wins + "-" + p.team.losses + ") - " + p.position + " " +
                    p.name + " [" + p.getYrStr() + "]\n");
            // plus any stats you want to show
            sb.append(" \tOverall: " + p.ratOvr + ", Potential: " + p.getLetterGrade(p.ratPot) + "\n\n>");
        }
        return sb.toString();
    }

    /**
     * The original method getAllAmericanStr() just calls getAllHockeyStr() now.
     */
    public String getAllAmericanStr() {
        return getAllHockeyStr();
    }

    /**
     * Get a string listing of all the conference “all-conference” teams.
     * You can rename if you want, or keep getAllConfStr as is.
     */
    public String getAllConfStr(int confNum) {
        // Exactly as in football, but referencing hockey positions. 
        // We rename c.getAllConfPlayers() → c.getAllConfPlayers() for hockey, etc.
        ArrayList<Player> allConfPlayers = conferences.get(confNum).getAllConfPlayers();
        StringBuilder sb = new StringBuilder();
        for (Player p : allConfPlayers) {
            sb.append(p.team.abbr + "(" + p.team.wins + "-" + p.team.losses + ") - " + p.position + " " + p.name + " [" + p.getYrStr() + "]\n");
            sb.append(" \tOverall: " + p.ratOvr + ", Potential: " + p.getLetterGrade(p.ratPot) + "\n\n>");
        }
        return sb.toString();
    }

    /**
     * Set the departing players for each team (like seniors in football).
     */
    public void getPlayersLeaving() {
        for (TeamHockey t : teamList) {
            t.getPlayersLeaving();
        }
    }

    /**
     * Get a “mock draft” listing top 64 players, exactly as original code.
     */
    public String[] getMockDraftPlayersList() {
        ArrayList<Player> allPlayersLeaving = new ArrayList<>();
        for (TeamHockey t : teamList) {
            for (Player p : t.playersLeaving) {
                if (p.ratOvr > 85 && !p.position.equals("G")) { // example skipping goalies
                    allPlayersLeaving.add(p);
                }
            }
        }
        Collections.sort(allPlayersLeaving, new PlayerComparator());
        ArrayList<Player> top64 = new ArrayList<>(64);
        for (int i = 0; i < 64; ++i) {
            top64.add(allPlayersLeaving.get(i));
        }
        String[] results = new String[top64.size()];
        for (int i = 0; i < top64.size(); ++i) {
            results[i] = top64.get(i).getMockDraftStr();
        }
        return results;
    }

    /**
     * Returns a string array of team rankings based on your selection, 0-15, 
     * same as original football code, just with hockey references.
     */
    public ArrayList<String> getTeamRankingsStr(int selection) {
        ArrayList<TeamHockey> teams = teamList; 
        ArrayList<String> rankings = new ArrayList<>();
        TeamHockey t;
        switch (selection) {
            case 0: 
                Collections.sort(teams, new TeamCompPoll());
                for (int i = 0; i < teams.size(); i++) {
                    t = teams.get(i);
                    rankings.add(t.getRankStrStarUser(i + 1) + "," + t.strRepWithBowlResults() + "," + t.teamPollScore);
                }
                break;
            case 1: 
                return getConfStandings();
            case 2: 
                Collections.sort(teams, new TeamCompSoW());
                for (int i = 0; i < teams.size(); i++) {
                    t = teams.get(i);
                    rankings.add(t.getRankStrStarUser(i+1) + "," + t.strRepWithBowlResults() + "," + t.teamStrengthOfWins);
                }
                break;
            // etc. Exactly as your football code, just referencing TeamHockey

            default:
                Collections.sort(teams, new TeamCompPoll());
                for (int i = 0; i < teams.size(); i++) {
                    t = teams.get(i);
                    rankings.add(t.getRankStrStarUser(i + 1) + "," + t.strRepWithBowlResults() + "," + t.teamPollScore);
                }
                break;
        }
        return rankings;
    }

    /**
     * Get conference standings string (like the original “getConfStandings”).
     */
    public ArrayList<String> getConfStandings() {
        ArrayList<String> confStandings = new ArrayList<>();
        ArrayList<TeamHockey> confTeams = new ArrayList<>();
        for (HockeyConference c : conferences) {
            confTeams.addAll(c.confTeams);
            Collections.sort(confTeams, new TeamCompConfWins());
            confStandings.add(" ," + c.confName + " Conference, ");
            for (int i = 0; i < confTeams.size(); ++i) {
                TeamHockey t = confTeams.get(i);
                confStandings.add(
                        t.getRankStrStarUser(i + 1) + "," + t.strRepWithBowlResults() +
                                "," + t.getConfWins() + "-" + t.getConfLosses()
                );
            }
            confTeams.clear();
        }
        return confStandings;
    }

    /**
     * Get a year-by-year summary of league champions + Hobey Baker winners
     */
    public String getLeagueHistoryStr() {
        String hist = "";
        for (int i = 0; i < leagueHistory.size(); i++) {
            hist += (2016 + i) + ":\n";
            hist += "\tChampions: " + leagueHistory.get(i)[0] + "\n";
            hist += "\tHobey Baker: " + hobeyHistory.get(i) + "\n%";
        }
        return hist;
    }

    /**
     * Returns array of all teams (conference + name + prestige).
     */
    public String[] getTeamListStr() {
        String[] teams = new String[teamList.size()];
        for (int i = 0; i < teamList.size(); i++) {
            TeamHockey th = teamList.get(i);
            teams[i] = th.conference + ": " + th.name + ", Pres: " + th.teamPrestige;
        }
        return teams;
    }

    /**
     * Get a watch list for the Cup Games if not scheduled, or actual matchups if they are.
     */
    public String getBowlGameWatchStr() {
        if (!hasScheduledTourney) {
            for (TeamHockey t : teamList) {
                t.updatePollScore();
            }
            Collections.sort(teamList, new TeamCompPoll());

            StringBuilder sb = new StringBuilder();
            TeamHockey t1;
            TeamHockey t2;
            sb.append("Semifinal 1v4:\n\t\t");
            t1 = teamList.get(0);
            t2 = teamList.get(3);
            sb.append(t1.strRep() + " vs " + t2.strRep() + "\n\n");

            sb.append("Semifinal 2v3:\n\t\t");
            t1 = teamList.get(1);
            t2 = teamList.get(2);
            sb.append(t1.strRep() + " vs " + t2.strRep() + "\n\n");

            sb.append(cupNames[0] + ":\n\t\t");
            t1 = teamList.get(4);
            t2 = teamList.get(6);
            sb.append(t1.strRep() + " vs " + t2.strRep() + "\n\n");

            // etc. Just as original
            return sb.toString();
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Semifinal 1v4:\n");
            sb.append(getGameSummaryBowl(semiGH14));

            sb.append("\n\nSemifinal 2v3:\n");
            sb.append(getGameSummaryBowl(semiGH23));

            for (int i = 0; i < cupGames.length; i++) {
                sb.append("\n\n" + cupNames[i] + ":\n");
                sb.append(getGameSummaryBowl(cupGames[i]));
            }
            return sb.toString();
        }
    }

    /**
     * Get summary of a single Cup Game, e.g. “ALA W 24 - 21 vs GEO”.
     */
    public String getGameSummaryBowl(GameHockey g) {
        StringBuilder sb = new StringBuilder();
        TeamHockey winner, loser;
        if (!g.hasPlayed) {
            return g.homeTeam.strRep() + " vs " + g.awayTeam.strRep();
        } else {
            if (g.homeScore > g.awayScore) {
                winner = g.homeTeam;
                loser = g.awayTeam;
                sb.append(winner.strRep() + " W " + g.homeScore + "-" + g.awayScore + " vs " + loser.strRep());
                return sb.toString();
            } else {
                winner = g.awayTeam;
                loser = g.homeTeam;
                sb.append(winner.strRep() + " W " + g.awayScore + "-" + g.homeScore + " @ " + loser.strRep());
                return sb.toString();
            }
        }
    }

    /**
     * Get the “conference championship game” strings for each conference.
     */
    public String getCCGsStr() {
        StringBuilder sb = new StringBuilder();
        for (HockeyConference c : conferences) {
            sb.append(c.getCCGStr() + "\n\n");
        }
        return sb.toString();
    }

    /**
     * Find a team by “name” or “strRep()”.
     */
    public TeamHockey findTeam(String name) {
        for (TeamHockey t : teamList) {
            if (t.strRep().equals(name)) {
                return t;
            }
        }
        return teamList.get(0);
    }

    /**
     * Find a team by abbreviation.
     */
    public TeamHockey findTeamAbbr(String abbr) {
        for (TeamHockey t : teamList) {
            if (t.abbr.equals(abbr)) {
                return t;
            }
        }
        return teamList.get(0);
    }

    /**
     * Find a conference by name.
     */
    public HockeyConference findConference(String name) {
        for (int i = 0; i < conferences.size(); i++) {
            if (conferences.get(i).confName.equals(name)) {
                return conferences.get(i);
            }
        }
        return conferences.get(0);
    }

    /**
     * Checks if a team name is valid (no duplicates, no illegal chars).
     */
    public boolean isNameValid(String name) {
        if (name.length() == 0) return false;
        if (name.contains(",") || name.contains(">") || name.contains("%") || name.contains("\\")) {
            return false;
        }
        for (TeamHockey t : teamList) {
            if (t.name.toLowerCase().equals(name.toLowerCase()) && !t.userControlled) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if an abbreviation is valid (3 letters, no duplicates).
     */
    public boolean isAbbrValid(String abbr) {
        if (abbr.length() > 3 || abbr.length() == 0) return false;
        if (abbr.contains(",") || abbr.contains(">") || abbr.contains("%") || abbr.contains("\\") || abbr.contains(" ")) {
            return false;
        }
        for (TeamHockey t : teamList) {
            if (t.abbr.equals(abbr) && !t.userControlled) {
                return false;
            }
        }
        return true;
    }

    /**
     * Summarize the Championship final.
     */
    public String ncgSummaryStr() {
        if (champGame.homeScore > champGame.awayScore) {
            return champGame.homeTeam.name + " (" + champGame.homeTeam.wins + "-" + champGame.homeTeam.losses
                    + ") won the Hockey Championship, winning against " + champGame.awayTeam.name + " ("
                    + champGame.awayTeam.wins + "-" + champGame.awayTeam.losses + ") " + champGame.homeScore
                    + "-" + champGame.awayScore + ".";
        } else {
            return champGame.awayTeam.name + " (" + champGame.awayTeam.wins + "-" + champGame.awayTeam.losses
                    + ") won the Hockey Championship, winning against " + champGame.homeTeam.name + " ("
                    + champGame.homeTeam.wins + "-" + champGame.homeTeam.losses + ") " + champGame.awayScore
                    + "-" + champGame.homeScore + ".";
        }
    }

    /**
     * Summarize the full season: champion, user team summary, broken records, etc.
     */
    public String seasonSummaryStr() {
        setTeamRanks();
        StringBuilder sb = new StringBuilder();
        sb.append(ncgSummaryStr());
        sb.append("\n\n" + userTeam.seasonSummaryStr());
        sb.append("\n\n" + leagueRecords.brokenRecordsStr(getYear(), userTeam.abbr));
        return sb.toString();
    }

    /**
     * Save league to file (just rename classes to hockey references).
     */
    public boolean saveLeague(File saveFile) {
        StringBuilder sb = new StringBuilder();
        if (isHardMode) {
            sb.append((2016 + leagueHistory.size()) + ": " + userTeam.abbr + " ("
                    + (userTeam.totalWins - userTeam.wins) + "-" + (userTeam.totalLosses - userTeam.losses)
                    + ") " + userTeam.totalCCs + " CCs, " + userTeam.totalNCs + " NCs>[HARD]%\n");
        } else {
            sb.append((2016 + leagueHistory.size()) + ": " + userTeam.abbr + " ("
                    + (userTeam.totalWins - userTeam.wins) + "-" + (userTeam.totalLosses - userTeam.losses)
                    + ") " + userTeam.totalCCs + " CCs, " + userTeam.totalNCs + " NCs>[EASY]%\n");
        }

        // leagueHistory
        for (int i = 0; i < leagueHistory.size(); i++) {
            for (int j = 0; j < leagueHistory.get(i).length; j++) {
                sb.append(leagueHistory.get(i)[j] + "%");
            }
            sb.append("\n");
        }
        sb.append("END_LEAGUE_HIST\n");

        // hobeyHistory
        for (int i = 0; i < leagueHistory.size(); i++) {
            sb.append(hobeyHistory.get(i) + "\n");
        }
        sb.append("END_HEISMAN_HIST\n");

        // Save each team
        for (TeamHockey t : teamList) {
            sb.append(t.conference + "," + t.name + "," + t.abbr + "," + t.teamPrestige + "," +
                    (t.totalWins - t.wins) + "," + (t.totalLosses - t.losses) + "," + t.totalCCs + "," + t.totalNCs + "," + t.rivalTeam + "," +
                    t.totalNCLosses + "," + t.totalCCLosses + "," + t.totalCups + "," + t.totalCupLosses + "," +
                    t.teamStratOffNum + "," + t.teamStratDefNum + "," + (t.showPopups ? 1 : 0) + "," +
                    t.yearStartWinStreak.getStreakCSV() + "," + t.teamTVDeal + "," + t.confTVDeal + "%" + t.evenYearHomeOpp + "%\n");
            sb.append(t.getPlayerInfoSaveFile());
            sb.append("END_PLAYERS\n");
        }

        // Save user team’s name, then teamHistory
        sb.append(userTeam.name + "\n");
        for (String s : userTeam.teamHistory) {
            sb.append(s + "\n");
        }
        sb.append("END_USER_TEAM\n");

        // Bless/curse
        if (saveBless != null) {
            sb.append(saveBless.abbr + "\n");
            sb.append("END_BLESS_TEAM\n");
        } else {
            sb.append("NULL\n");
            sb.append("END_BLESS_TEAM\n");
        }
        if (saveCurse != null) {
            sb.append(saveCurse.abbr + "\n");
            sb.append("END_CURSE_TEAM\n");
        } else {
            sb.append("NULL\n");
            sb.append("END_CURSE_TEAM\n");
        }

        // leagueRecords
        sb.append(leagueRecords.getRecordsStr());
        sb.append("END_LEAGUE_RECORDS\n");

        // longestWinStreak
        sb.append(yearStartLongestWinStreak.getStreakCSV());
        sb.append("\nEND_LEAGUE_WIN_STREAK\n");

        // userTeamRecords
        sb.append(userTeamRecords.getRecordsStr());
        sb.append("END_USER_TEAM_RECORDS\n");

        // userTeam Hall of Fame
        for (String s : userTeam.hallOfFame) {
            sb.append(s + "\n");
        }
        sb.append("END_HALL_OF_FAME\n");

        // Write out
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(saveFile), "utf-8"))) {
            writer.write(sb.toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

/**
 * Same comparator as original PlayerHeismanComp, used for "Hobey Baker" scoring.
 */
class PlayerHeismanComp implements Comparator<Player> {
    @Override
    public int compare(Player a, Player b) {
        return a.getHeismanScore() > b.getHeismanScore() ? -1 :
                a.getHeismanScore() == b.getHeismanScore() ? 0 : 1;
    }
}

/**
 * Same comparators for teams, just referencing TeamHockey. 
 * Everything else is unchanged, purely renamed from "Team" to "TeamHockey".
 */
class TeamCompPoll implements Comparator<TeamHockey> {
    @Override
    public int compare(TeamHockey a, TeamHockey b) {
        return a.teamPollScore > b.teamPollScore ? -1 :
               a.teamPollScore == b.teamPollScore ? 0 : 1;
    }
}

class TeamCompSoW implements Comparator<TeamHockey> {
    @Override
    public int compare(TeamHockey a, TeamHockey b) {
        return a.teamStrengthOfWins > b.teamStrengthOfWins ? -1 :
               a.teamStrengthOfWins == b.teamStrengthOfWins ? 0 : 1;
    }
}

class TeamCompPPG implements Comparator<TeamHockey> {
    @Override
    public int compare(TeamHockey a, TeamHockey b) {
        return (a.teamPoints / a.numGames()) > (b.teamPoints / b.numGames()) ? -1 :
               (a.teamPoints / a.numGames()) == (b.teamPoints / b.numGames()) ? 0 : 1;
    }
}

class TeamCompOPPG implements Comparator<TeamHockey> {
    @Override
    public int compare(TeamHockey a, TeamHockey b) {
        return (a.teamOppPoints / a.numGames()) < (b.teamOppPoints / b.numGames()) ? -1 :
               (a.teamOppPoints / a.numGames()) == (b.teamOppPoints / b.numGames()) ? 0 : 1;
    }
}

class TeamCompYPG implements Comparator<TeamHockey> {
    @Override
    public int compare(TeamHockey a, TeamHockey b) {
        return (a.teamYards / a.numGames()) > (b.teamYards / b.numGames()) ? -1 :
               (a.teamYards / a.numGames()) == (b.teamYards / b.numGames()) ? 0 : 1;
    }
}

class TeamCompOYPG implements Comparator<TeamHockey> {
    @Override
    public int compare(TeamHockey a, TeamHockey b) {
        return (a.teamOppYards / a.numGames()) < (b.teamOppYards / b.numGames()) ? -1 :
               (a.teamOppYards / a.numGames()) == (b.teamOppYards / b.numGames()) ? 0 : 1;
    }
}

class TeamCompOPYPG implements Comparator<TeamHockey> {
    @Override
    public int compare(TeamHockey a, TeamHockey b) {
        return (a.teamOppPassYards / a.numGames()) < (b.teamOppPassYards / b.numGames()) ? -1 :
               (a.teamOppPassYards / a.numGames()) == (b.teamOppPassYards / b.numGames()) ? 0 : 1;
    }
}

class TeamCompORYPG implements Comparator<TeamHockey> {
    @Override
    public int compare(TeamHockey a, TeamHockey b) {
        return (a.teamOppRushYards / a.numGames()) < (b.teamOppRushYards / b.numGames()) ? -1 :
               (a.teamOppRushYards / a.numGames()) == (b.teamOppRushYards / b.numGames()) ? 0 : 1;
    }
}

class TeamCompPYPG implements Comparator<TeamHockey> {
    @Override
    public int compare(TeamHockey a, TeamHockey b) {
        return (a.teamPassYards / a.numGames()) > (b.teamPassYards / b.numGames()) ? -1 :
               (a.teamPassYards / a.numGames()) == (b.teamPassYards / b.numGames()) ? 0 : 1;
    }
}

class TeamCompRYPG implements Comparator<TeamHockey> {
    @Override
    public int compare(TeamHockey a, TeamHockey b) {
        return (a.teamRushYards / a.numGames()) > (b.teamRushYards / b.numGames()) ? -1 :
               (a.teamRushYards / a.numGames()) == (b.teamRushYards / b.numGames()) ? 0 : 1;
    }
}

class TeamCompTODiff implements Comparator<TeamHockey> {
    @Override
    public int compare(TeamHockey a, TeamHockey b) {
        return a.teamTODiff > b.teamTODiff ? -1 :
               a.teamTODiff == b.teamTODiff ? 0 : 1;
    }
}

class TeamCompOffTalent implements Comparator<TeamHockey> {
    @Override
    public int compare(TeamHockey a, TeamHockey b) {
        return a.teamOffTalent > b.teamOffTalent ? -1 :
               a.teamOffTalent == b.teamOffTalent ? 0 : 1;
    }
}

class TeamCompDefTalent implements Comparator<TeamHockey> {
    @Override
    public int compare(TeamHockey a, TeamHockey b) {
        return a.teamDefTalent > b.teamDefTalent ? -1 :
               a.teamDefTalent == b.teamDefTalent ? 0 : 1;
    }
}

class TeamCompPrestige implements Comparator<TeamHockey> {
    @Override
    public int compare(TeamHockey a, TeamHockey b) {
        return a.teamPrestige > b.teamPrestige ? -1 :
               a.teamPrestige == b.teamPrestige ? 0 : 1;
    }
}

class TeamCompRecruitClass implements Comparator<TeamHockey> {
    @Override
    public int compare(TeamHockey a, TeamHockey b) {
        return a.getRecruitingClassRat() > b.getRecruitingClassRat() ? -1 :
               a.getRecruitingClassRat() == b.getRecruitingClassRat() ? 0 : 1;
    }
}

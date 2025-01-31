package Main;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.lang.StringBuilder;

/**
 * Class representing a Hockey Conference in NCAA Hockey.
 * Example assumes 10 teams and a simplified scheduling approach,
 * but you may adjust the structure (number of teams, number of games,
 * tournament format, etc.) to fit an actual NCAA hockey conference.
 *
 * @author
 */
public class HockeyConference implements Serializable {

    /** Name of the conference, e.g. "NCHC", "Big10", "HockeyEast", etc. */
    public String confName;

    /** A simple integer representation of conference strength. */
    public int confPrestige;

    /** The list of teams in this hockey conference. */
    public ArrayList<TeamHockey> confTeams;

    /** Used to determine which year’s home/away pattern to use (if you still track alternating years). */
    public boolean evenYear;

    /** Reference to the larger league or association managing multiple conferences. */
    public HockeyLeague league;

    /** Conference Championship “final” game (or final series) placeholder. */
    private GameHockey confChampGame;

    /**
     * Tracks the current "week" or "round" of conference play. In NCAA Hockey,
     * you might call these weekends/series rather than "weeks."
     */
    public int currentWeek;

    /** Tracks scheduling iteration used for round-robin or partial round-robin. */
    public int roundRobinWeek;

    /** String summary for All-Conference announcements, if desired. */
    public String allConferenceStr;

    /** List of all-conference players, e.g. for end-of-season awards. */
    public ArrayList<Player> allConferencePlayers;

    /**
     * Constructs a HockeyConference with a given name, linked to a larger league.
     * Example assumes an initial prestige value of 75.
     */
    public HockeyConference(String name, HockeyLeague league) {
        this.confName = name;
        this.confPrestige = 75;
        this.confTeams = new ArrayList<>();
        this.league = league;
        this.currentWeek = 0;
        this.roundRobinWeek = 0;
        this.allConferencePlayers = new ArrayList<>();
    }

    /**
     * Sets up an in-conference schedule (round-robin or partial round-robin).
     * For simplicity, this example still uses 10 teams and a “roundRobinWeek”
     * approach similar to the football version.
     *
     * Adjust as needed for actual NCAA hockey scheduling (often weekend series).
     */
    public void setUpConferenceSchedule() {
        roundRobinWeek = 0;
        evenYear = (league.leagueHistory.size() % 2 == 0);

        // For example, if you had logic to alternate home/away each season,
        // you can adapt or remove it here. Below uses placeholders similar to the original code.
        if (league.leagueHistory.size() == 0 || confTeams.get(0).evenYearHomeOpp == null) {
            int[][] evenHomeGames = new int[10][];
            // Example pattern for hockey: totally up to you how you define these
            evenHomeGames[0] = new int[]{7, 4, 8, 3};
            evenHomeGames[1] = new int[]{8, 9, 5, 0, 4};
            evenHomeGames[2] = new int[]{5, 0, 6, 1};
            evenHomeGames[3] = new int[]{6, 1, 9, 7, 2};
            evenHomeGames[4] = new int[]{3, 7, 2, 8};
            evenHomeGames[5] = new int[]{4, 8, 3, 9, 0};
            evenHomeGames[6] = new int[]{4, 0, 1, 5};
            evenHomeGames[7] = new int[]{2, 6, 1, 5, 9};
            evenHomeGames[8] = new int[]{9, 3, 7, 2, 6};
            evenHomeGames[9] = new int[]{0, 2, 4, 6};

            for (int x = 0; x < evenHomeGames.length; x++) {
                StringBuilder sb = new StringBuilder();
                for (int y = 0; y < evenHomeGames[x].length; y++) {
                    sb.append(confTeams.get(evenHomeGames[x][y]).abbr).append(",");
                }
                confTeams.get(x).evenYearHomeOpp = sb.toString();
            }
        }

        // Scheduling each round: simplistic example
        for (int r = 0; r < 9; ++r) {
            for (int g = 0; g < 5; ++g) {
                TeamHockey homeTeam = confTeams.get((roundRobinWeek + g) % 9);
                TeamHockey awayTeam;
                if (g == 0) {
                    awayTeam = confTeams.get(9);
                } else {
                    awayTeam = confTeams.get((9 - g + roundRobinWeek) % 9);
                }

                GameHockey gm;
                // Check whether it's an even year and if 'awayTeam' is in homeTeam's
                // home list, etc. Logic is exactly as in the original code:
                if ((evenYear && homeTeam.evenYearHomeOpp.contains(awayTeam.abbr))
                     || (evenYear && !awayTeam.evenYearHomeOpp.contains(homeTeam.abbr))
                     || (!evenYear && !homeTeam.evenYearHomeOpp.contains(awayTeam.abbr))
                     || (!evenYear && awayTeam.evenYearHomeOpp.contains(homeTeam.abbr))) {
                    // homeTeam is home
                    gm = new GameHockey(homeTeam, awayTeam, "In Conference");
                } else {
                    // awayTeam is home
                    gm = new GameHockey(awayTeam, homeTeam, "In Conference");
                }

                homeTeam.gameSchedule.add(gm);
                awayTeam.gameSchedule.add(gm);
            }
            roundRobinWeek++;
        }
    }

    /**
     * Sets up non-conference games (similar to OOC in football).
     * This example uses the same 3 "out-of-conference" slots. In hockey,
     * you would typically schedule far more non-conference games, or series.
     */
    public void setUpNonConferenceSchedule() {
        // Just an example of dividing conferences by index or name.
        // Adjust to match your actual league structure.
        int confNum = -1;
        switch (confName) {
            case "SOUTH": confNum = 0; break;
            case "LAKES": confNum = 1; break;
            case "NORTH": confNum = 2; break;
            default: break;
        }

        if (confNum != -1) {
            // Arbitrarily picking 3 "non-conference" matchups
            for (int offset = 3; offset < 6; ++offset) {
                ArrayList<TeamHockey> availableTeams = new ArrayList<>();
                int selectedConfIndex = confNum + offset;
                if (selectedConfIndex == 6) selectedConfIndex = 3;
                if (selectedConfIndex == 7) selectedConfIndex = 4;
                if (selectedConfIndex == 8) selectedConfIndex = 5;

                // Grab teams from another conference
                for (int i = 0; i < 10; ++i) {
                    availableTeams.add(league.conferences
                                          .get(selectedConfIndex).confTeams.get(i));
                }

                // Pair up each team in this conference with a random from the other
                for (int i = 0; i < 10; ++i) {
                    int selIndex = (int) (availableTeams.size() * Math.random());
                    TeamHockey teamA = confTeams.get(i);
                    TeamHockey teamB = availableTeams.get(selIndex);

                    GameHockey gm;
                    if (Math.random() > 0.5) {
                        // Team A hosts Team B
                        gm = new GameHockey(teamA, teamB,
                                teamA.conference.substring(0, 3) + " vs " + teamB.conference.substring(0, 3));
                    } else {
                        // Team B hosts Team A
                        gm = new GameHockey(teamB, teamA,
                                teamB.conference.substring(0, 3) + " vs " + teamA.conference.substring(0, 3));
                    }

                    // Insert the game into the correct "slot"
                    if (offset == 3) {
                        teamA.gameOOCSchedule0 = gm;
                        teamB.gameOOCSchedule0 = gm;
                    } else if (offset == 4) {
                        teamA.gameOOCSchedule4 = gm;
                        teamB.gameOOCSchedule4 = gm;
                    } else if (offset == 5) {
                        teamA.gameOOCSchedule9 = gm;
                        teamB.gameOOCSchedule9 = gm;
                    }
                    availableTeams.remove(selIndex);
                }
            }
        }
    }

    /**
     * Inserts the non-conference games into the early/mid/late season schedule.
     * In this example, they are inserted at “weeks” 1, 5, and 10. 
     * Adjust for hockey’s typical schedule as needed.
     */
    public void insertNonConferenceSchedule() {
        for (TeamHockey team : confTeams) {
            // Insert them into the team’s schedule at specific indexes
            team.gameSchedule.add(0, team.gameOOCSchedule0);
            team.gameSchedule.add(4, team.gameOOCSchedule4);
            team.gameSchedule.add(9, team.gameOOCSchedule9);
        }
    }

    /**
     * Simulates one “week” of games (in hockey, possibly one weekend series).
     * If we've reached the end, schedule/play the conference championship.
     */
    public void playOneWeek() {
        // Example: maybe the regular season is 12 weeks in this simplified approach
        if (currentWeek == 12) {
            playConferenceChampionship();
        } else {
            // Play all scheduled games for each team this week
            for (TeamHockey team : confTeams) {
                team.gameSchedule.get(currentWeek).playGame();
            }
            if (currentWeek == 11) scheduleConferenceChampionship();
            currentWeek++;
        }
    }

    /**
     * Determine which two teams make the conference championship game (often
     * there's an entire conference tournament in real NCAA hockey, but this is a simple 2-team final).
     */
    public void scheduleConferenceChampionship() {
        // Update poll/score for each team
        for (TeamHockey t : confTeams) {
            t.updatePollScore();
        }

        // Sort by conference points or wins
        Collections.sort(confTeams, new TeamHockeyCompConfWins());


        // The top two teams get placed into the “conference championship”
        confChampGame = new GameHockey(confTeams.get(0), confTeams.get(1), confName + " Conference Championship");
        confTeams.get(0).gameSchedule.add(confChampGame);
        confTeams.get(1).gameSchedule.add(confChampGame);
    }



    /**
     * Plays the conference championship game.
     * Winner is crowned “Conference Champion.”
     */
    public void playConferenceChampionship() {
        confChampGame.playGame();
        TeamHockey home = confChampGame.homeTeam;
        TeamHockey away = confChampGame.awayTeam;

        if (confChampGame.homeScore > confChampGame.awayScore) {
            home.confChampion = "CONF-CHAMP";
            home.totalCCs++;
            away.totalCCLosses++;
            league.newsStories.get(13).add(
                    home.name + " wins the " + confName + " Hockey Championship!>" +
                    home.strRep() + " defended home ice against " + away.strRep() +
                    ", with a final score of " + confChampGame.homeScore + " to " + confChampGame.awayScore + "."
            );
        } else {
            away.confChampion = "CONF-CHAMP";
            away.totalCCs++;
            home.totalCCLosses++;
            league.newsStories.get(13).add(
                    away.name + " wins the " + confName + " Hockey Championship!>" +
                    away.strRep() + " pulled off the upset on the road against " + home.strRep() +
                    ", winning by a score of " + confChampGame.awayScore + " to " + confChampGame.homeScore + "."
            );
        }

        // Re-sort teams by poll or final ranking
        Collections.sort(confTeams, new TeamHockeyCompPoll());
    }

    /**
     * Returns a short summary of the upcoming or final conference championship matchup.
     */
    public String getConferenceChampionshipStr() {
        if (confChampGame == null) {
            // Predict the top 2 teams
            TeamHockey t1 = null, t2 = null;
            int bestWins = -1, secondBestWins = -1;
            // Simple iteration to find top 2 by wins
            for (int i = confTeams.size() - 1; i >= 0; --i) {
                TeamHockey t = confTeams.get(i);
                if (t.getWins() >= bestWins) {
                    secondBestWins = bestWins;
                    bestWins = t.getConfWins();
                    t2 = t1;
                    t1 = t;
                } else if (t.getConfWins() >= secondBestWins) {
                    secondBestWins = t.getConfWins();
                    t2 = t;
                }
            }
            return confName + " Conference Championship (Predicted):\n\t" +
                    t1.strRep() + " vs " + t2.strRep();
        } else {
            if (!confChampGame.hasPlayed) {
                return confName + " Conference Championship:\n\t" +
                        confChampGame.homeTeam.strRep() + " vs " +
                        confChampGame.awayTeam.strRep();
            } else {
                // Return final result
                StringBuilder sb = new StringBuilder();
                TeamHockey winner, loser;
                sb.append(confName).append(" Conference Championship:\n");
                if (confChampGame.homeScore > confChampGame.awayScore) {
                    winner = confChampGame.homeTeam;
                    loser = confChampGame.awayTeam;
                    sb.append(winner.strRep()).append(" W ")
                      .append(confChampGame.homeScore).append("-").append(confChampGame.awayScore)
                      .append(" vs ").append(loser.strRep());
                } else {
                    winner = confChampGame.awayTeam;
                    loser = confChampGame.homeTeam;
                    sb.append(winner.strRep()).append(" W ")
                      .append(confChampGame.awayScore).append("-").append(confChampGame.homeScore)
                      .append(" @ ").append(loser.strRep());
                }
                return sb.toString();
            }
        }
    }

    /**
     * Returns the All-Conference players (e.g., for end-of-season awards).
     * In actual NCAA hockey, you might choose, for instance:
     * 3 forwards, 2 defensemen, 1 goalie (First Team).
     * For demonstration, we’ll do something small and simple.
     */
    public ArrayList<Player> getAllConferencePlayers() {
        if (allConferencePlayers.isEmpty()) {
            // Separate out players by position
            ArrayList<PlayerC> Centers = new ArrayList<>();
            ArrayList<PlayerLW> Leftwings = new ArrayList<>();
            ArrayList<PlayerRW> Rightwings = new ArrayList<>();
            ArrayList<PlayerLD> leftdefensemen = new ArrayList<>();
            ArrayList<PlayerRD> rightdefensemen = new ArrayList<>();
            ArrayList<PlayerG> goalies = new ArrayList<>();

            // Gather all players from all teams
            for (TeamHockey t : confTeams) {
                forwards.addAll(t.teamForwards);
                defensemen.addAll(t.teamDefense);
                goalies.addAll(t.teamGoalies);
            }

            // Sort by "Hobey Baker" ranking logic or however you measure
            Collections.sort(forwards, new PlayerHobeyBakerComp());
            Collections.sort(defensemen, new PlayerHobeyBakerComp());
            Collections.sort(goalies, new PlayerHobeyBakerComp());

            // Pick a small first-team example: 3F, 2D, 1G
            for (int i = 0; i < 3 && i < forwards.size(); i++) {
                allConferencePlayers.add(forwards.get(i));
                forwards.get(i).wonAllConference = true;
            }
            for (int i = 0; i < 2 && i < defensemen.size(); i++) {
                allConferencePlayers.add(defensemen.get(i));
                defensemen.get(i).wonAllConference = true;
            }
            if (!goalies.isEmpty()) {
                allConferencePlayers.add(goalies.get(0));
                goalies.get(0).wonAllConference = true;
            }
        }

        return allConferencePlayers;
    }
}


/**
 * Comparator for sorting teams by conference wins/points. You might also
 * incorporate points (2 for a win, 1 for an OT/SO loss), but this is just
 * an example parallel to the football version.
 */
class TeamHockeyCompConfWins implements Comparator<TeamHockey> {
    @Override
    public int compare(TeamHockey a, TeamHockey b) {
        // First, check if either is already champion
        if ("CONF-CHAMP".equals(a.confChampion)) return -1;
        if ("CONF-CHAMP".equals(b.confChampion)) return 1;

        // Compare conference wins (or points)
        if (a.getConfWins() > b.getConfWins()) {
            return -1;
        } else if (a.getConfWins() < b.getConfWins()) {
            return 1;
        } else {
            // If tied, check head-to-head or other tiebreakers
            if (a.gameWinsAgainst.contains(b)) {
                return -1;
            } else if (b.gameWinsAgainst.contains(a)) {
                return 1;
            }
            return 0;
        }
    }
}

/**
 * Example comparator for overall poll/rank sorting.
 */
class TeamHockeyCompPoll implements Comparator<TeamHockey> {
    @Override
    public int compare(TeamHockey a, TeamHockey b) {
        // If you track pollScore or some rating, compare here:
        return Double.compare(b.getPollScore(), a.getPollScore());
    }
}

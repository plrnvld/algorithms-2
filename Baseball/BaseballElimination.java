import java.util.stream.StreamSupport;

import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.ST;

class BaseballElimination {

    private ST<String, Integer> teamsTable;
    private int w[];
    private int l[];
    private int r[];
    private int g[][];
    private int currMaxWins;
    private FordFulkerson ff;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        teamsTable = new ST<>();

        // ############### Read teams, add indexes, create FlowNetwork, run
        // FordFulkerson

        ff = new FordFulkerson(null, -1, -2);

        for (var team : teams()) {
            int wins = wins(team);
            currMaxWins = Math.max(wins, currMaxWins);
        }
    }

    private int teamIndex(String team) {
        if (teamsTable.contains(team))
            return teamsTable.get(team);

        throw new IllegalArgumentException("Invalid team: " + team);
    }

    // number of teams
    public int numberOfTeams() {
        return teamsTable.size();
    }

    // all teams
    public Iterable<String> teams() {
        return teamsTable.keys();
    }

    // number of wins for given team
    public int wins(String team) {
        return w[teamIndex(team)];
    }

    // number of losses for given team
    public int losses(String team) {
        return l[teamIndex(team)];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        return r[teamIndex(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        int index1 = teamIndex(team1);
        int index2 = teamIndex(team2);
        return g[index1][index2];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        if (wins(team) + remaining(team) < currMaxWins) // Trivial elimination
            return true;

        return false;
    }

    // subset R of teams that eliminates given team; null
    // if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (!isEliminated(team))
            return null;

        return () -> StreamSupport.stream(teams().spliterator(), false)
                .filter(t -> ff.inCut(teamIndex(t)))
                .iterator();
    }

    public static void main(String[] args) {
        // some code
    }
}
import java.util.Arrays;
import java.util.stream.StreamSupport;

import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.StdOut;

class BaseballElimination {

    private ST<String, Integer> teamsTable;
    private int w[];
    private int l[];
    private int r[];
    private int g[][];
    private int currMaxWins;
    private long allGamesRemaining;
    private FordFulkerson ff;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        readScenario(filename);

        // ############### Read teams, add indexes, create FlowNetwork, run
        // FordFulkerson

        ff = new FordFulkerson(null, -1, -2);

        for (var team : teams()) {
            int wins = wins(team);
            currMaxWins = Math.max(wins, currMaxWins);
        }

        allGamesRemaining = StreamSupport.stream(teams().spliterator(), false)
                .map(t -> (long) remaining(t))
                .reduce(0l, Long::sum);
    }

    private void readScenario(String filename) {
        String[] lines = new In(filename).readAllLines();

        var numTeams = Integer.valueOf(lines[0]);
        g = new int[numTeams][numTeams];

        String[] teamLines = Arrays.copyOfRange(lines, 1, lines.length);

        teamsTable = new ST<>();

        int index = 0;
        for (var line : teamLines) {
            String[] words = line.split("\\s+");
            teamsTable.put(words[0], index);

            // ###################
            for (var word : words) {
                System.out.println("*" + word + "*");
            }

            System.out.println();

            index++;
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

        long maxFlow = Math.round(ff.value());
        return allGamesRemaining == maxFlow;
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
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            } else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
import java.util.Arrays;
import java.util.stream.StreamSupport;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.StdOut;

public class BaseballElimination {

    private ST<String, Integer> teamsTable;
    private int[] wins;
    private int[] losses;
    private int[] remaining;
    private FordFulkerson[] ffs;
    private int[][] gamesAgainst;
    private int currMaxWins;
    private final long allGamesRemaining;

    private class MatchUp {
        public int team1Index;
        public int team2Index;

        public MatchUp(int team1Index, int team2Index) {
            this.team1Index = team1Index;
            this.team2Index = team2Index;
        }

        public boolean involves(int teamIndex) {
            return team1Index == teamIndex || team2Index == teamIndex;
        }
    }

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        readScenario(filename);

        for (var team : teams()) {
            int wins = wins(team);
            currMaxWins = Math.max(wins, currMaxWins);
        }

        allGamesRemaining = StreamSupport.stream(teams().spliterator(), false)
                .map(t -> (long) remaining(t))
                .reduce(0L, Long::sum);
    }

    private FordFulkerson getOrCreateFordFulkerson(String team) {
        int index = teamIndex(team);
        if (ffs[index] != null)
            return ffs[index];

        FlowNetwork network = buildFlowNetwork(index);
        int endVertex = network.V() - 1; // The last vertex is the endVertex
        int startVertex = network.V() - 2; // The vertex before the last vertex is the start vertex

        FordFulkerson ff = new FordFulkerson(network, startVertex, endVertex);
        ffs[index] = ff;
        return ff;
    }

    private FlowNetwork buildFlowNetwork(int forTeamIndex) {
        MatchUp[] matchUps = createMatchupsForOtherTeams(forTeamIndex, numberOfTeams());

        int numVertices = 2 + numberOfTeams() - 1 + matchUps.length;
        FlowNetwork flowNetwork = new FlowNetwork(numVertices);
        int endVertex = flowNetwork.V() - 1;
        int startVertex = flowNetwork.V() - 2;

        // Add team vertices
        for (int curr = 0; curr < numberOfTeams(); curr++) {
            if (curr != forTeamIndex) {
                int currVertex = vertexIndex(curr, forTeamIndex);
                double capacity = Math.max(wins[forTeamIndex] + remaining[forTeamIndex] - wins[curr], 0);
                FlowEdge edge = new FlowEdge(currVertex, endVertex, capacity);
                flowNetwork.addEdge(edge);
            }
        }

        // Add matchup vertices
        int index = numberOfTeams() - 1; // Indices already used by adding other teams
        for (var matchUp : matchUps) {
            FlowEdge toMatchupEdge = new FlowEdge(startVertex, index, gamesAgainst[matchUp.team1Index][matchUp.team2Index]);
            flowNetwork.addEdge(toMatchupEdge);

            FlowEdge toTeam1 = new FlowEdge(index, vertexIndex(matchUp.team1Index, forTeamIndex), Double.POSITIVE_INFINITY);
            flowNetwork.addEdge(toTeam1);

            FlowEdge toTeam2 = new FlowEdge(index, vertexIndex(matchUp.team2Index, forTeamIndex), Double.POSITIVE_INFINITY);
            flowNetwork.addEdge(toTeam2);

            index++;
        }

        return flowNetwork;
    }

    private int vertexIndex(int teamNum, int forTeam) {
        if (teamNum < forTeam)
            return teamNum;
        if (teamNum > forTeam)
            return teamNum - 1;

        throw new IllegalArgumentException("Not allowed");
    }

    private void readScenario(String filename) {

        String[] lines = new In(filename).readAllLines();

        var numTeams = Integer.valueOf(lines[0]);
        wins = new int[numTeams];
        losses = new int[numTeams];
        remaining = new int[numTeams];

        gamesAgainst = new int[numTeams][numTeams];

        ffs = new FordFulkerson[numTeams];

        String[] teamLines = Arrays.copyOfRange(lines, 1, lines.length);

        teamsTable = new ST<>();

        int index = 0;
        for (var line : teamLines) {
            String[] words = line.trim().split("\\s+");
            addTeam(index, numTeams, words);

            index++;
        }
    }

    private void addTeam(int index, int numTeams, String[] words) {
        teamsTable.put(words[0], index);
        wins[index] = Integer.parseInt(words[1]);
        losses[index] = Integer.parseInt(words[2]);
        remaining[index] = Integer.parseInt(words[3]);

        for (int i = 0; i < numTeams; i++) {
            gamesAgainst[index][i] = Integer.parseInt(words[i + 4]);
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
        return wins[teamIndex(team)];
    }

    // number of losses for given team
    public int losses(String team) {
        return losses[teamIndex(team)];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        return remaining[teamIndex(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        int index1 = teamIndex(team1);
        int index2 = teamIndex(team2);
        return gamesAgainst[index1][index2];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        FordFulkerson ff = getOrCreateFordFulkerson(team);

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

        FordFulkerson ff = getOrCreateFordFulkerson(team);

        return () -> StreamSupport.stream(teams().spliterator(), false)
                .filter(t -> ff.inCut(teamIndex(t)))
                .iterator();
    }

    private MatchUp[] createMatchupsForOtherTeams(int currTeamIndex, int numTeams) {
        if (numTeams < 2)
            return new MatchUp[0];

        int count = 0;
        MatchUp[] combinations = new MatchUp[((numTeams - 1) * numTeams) / 2 - (numTeams - 1)];

        for (var i = 0; i < numTeams; i++) {
            for (var j = i + 1; j < numTeams; j++) {
                MatchUp matchUp = new MatchUp(i, j);
                if (!matchUp.involves(currTeamIndex)) {
                    combinations[count] = matchUp;
                    count++;
                }
            }
        }

        return combinations;
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
import java.util.Arrays;
import java.util.stream.StreamSupport;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.LinkedQueue;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.StdOut;

public class BaseballElimination {

    private ST<String, Integer> teamsTable;
    private int[] winCount;
    private int[] lossCount;
    private int[] remaining;
    private FFWithStartEdges[] ffs;
    private int[][] gamesAgainst;
    private int currMaxWins;

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

    private class FFWithStartEdges {
        public FordFulkerson ff;
        public FlowNetwork flowNetwork;
        public Queue<FlowEdge> startEdges;

        public FFWithStartEdges(FordFulkerson ff, FlowNetwork flowNetwork, Queue<FlowEdge> startEdges) {
            this.ff = ff;
            this.flowNetwork = flowNetwork;
            this.startEdges = startEdges;
        }
    }

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        readScenario(filename);

        for (var team : teams()) {
            int wins = wins(team);
            currMaxWins = Math.max(wins, currMaxWins);
        }
    }

    private FFWithStartEdges getOrCreateFordFulkerson(String team) {
        int index = teamIndex(team);
        if (ffs[index] != null)
            return ffs[index];

        FlowNetwork network = buildFlowNetwork(index);
        int endVertex = network.V() - 1; // The last vertex is the endVertex
        int startVertex = network.V() - 2; // The vertex before the last vertex is the start vertex

        Queue<FlowEdge> startEdges = new Queue<>();
        for (var flowEdge : network.edges()) {
            if (flowEdge.from() == startVertex || flowEdge.to() == startVertex) {
                startEdges.enqueue(flowEdge);
            }
        }

        FordFulkerson ff = new FordFulkerson(network, startVertex, endVertex);
        FFWithStartEdges ffWithStartEdges = new FFWithStartEdges(ff, network, startEdges);
        ffs[index] = ffWithStartEdges;
        return ffWithStartEdges;
    }

    private FlowNetwork buildFlowNetwork(int forTeamIndex) {
        LinkedQueue<MatchUp> matchUps = createMatchupsForOtherTeams(forTeamIndex, numberOfTeams());

        int numVertices = 2 + numberOfTeams() - 1 + matchUps.size();
        FlowNetwork flowNetwork = new FlowNetwork(numVertices);
        int endVertex = flowNetwork.V() - 1;
        int startVertex = flowNetwork.V() - 2;

        // Add team vertices
        for (int curr = 0; curr < numberOfTeams(); curr++) {
            if (curr != forTeamIndex) {
                int teamIndexInNetwork = teamIndexInNetwork(curr, forTeamIndex);
                int capacity = Math.max(winCount[forTeamIndex] + remaining[forTeamIndex] - winCount[curr], 0);
                FlowEdge edge = new FlowEdge(teamIndexInNetwork, endVertex, capacity);
                flowNetwork.addEdge(edge);
            }
        }

        // Add matchup vertices
        int index = numberOfTeams() - 1; // Indices already used by adding other teams
        for (var matchUp : matchUps) {
            FlowEdge toMatchupEdge = new FlowEdge(startVertex, index,
                    gamesAgainst[matchUp.team1Index][matchUp.team2Index]);
            flowNetwork.addEdge(toMatchupEdge);

            FlowEdge toTeam1 = new FlowEdge(index, teamIndexInNetwork(matchUp.team1Index, forTeamIndex),
                    Double.POSITIVE_INFINITY);
            flowNetwork.addEdge(toTeam1);

            FlowEdge toTeam2 = new FlowEdge(index, teamIndexInNetwork(matchUp.team2Index, forTeamIndex),
                    Double.POSITIVE_INFINITY);
            flowNetwork.addEdge(toTeam2);

            index++;
        }

        return flowNetwork;
    }

    private int teamIndexInNetwork(int teamNum, int forTeam) {
        if (teamNum < forTeam)
            return teamNum;
        if (teamNum > forTeam)
            return teamNum - 1;

        throw new IllegalArgumentException("Not allowed");
    }

    private void readScenario(String filename) {

        String[] lines = new In(filename).readAllLines();

        var numTeams = Integer.parseInt(lines[0]);
        winCount = new int[numTeams];
        lossCount = new int[numTeams];
        remaining = new int[numTeams];

        gamesAgainst = new int[numTeams][numTeams];

        ffs = new FFWithStartEdges[numTeams];

        String[] teamLines = Arrays.copyOfRange(lines, 1, lines.length);

        teamsTable = new ST<>();

        int index = 0;
        for (var line : teamLines) {
            String[] words = line.trim().split("\\s+");
            addTeamInfo(index, numTeams, words);

            index++;
        }
    }

    private void addTeamInfo(int teamIndex, int numTeams, String[] words) {
        int pos = 0;
        teamsTable.put(words[pos++], teamIndex);
        winCount[teamIndex] = Integer.parseInt(words[pos++]);
        lossCount[teamIndex] = Integer.parseInt(words[pos++]);
        remaining[teamIndex] = Integer.parseInt(words[pos++]);

        for (int i = 0; i < numTeams; i++) {
            gamesAgainst[teamIndex][i] = Integer.parseInt(words[i + pos]);
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
        return winCount[teamIndex(team)];
    }

    // number of losses for given team
    public int losses(String team) {
        return lossCount[teamIndex(team)];
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
        if (wins(team) + remaining(team) < currMaxWins) // Trivial elimination
            return true;

        FFWithStartEdges ffWithStartEdges = getOrCreateFordFulkerson(team);

        int startVertex = ffWithStartEdges.flowNetwork.V() - 2;
        for (var flowEdge : ffWithStartEdges.startEdges) {
            int otherVertex = flowEdge.other(startVertex);
            if (flowEdge.residualCapacityTo(otherVertex) > 0.0001)
                return true;
        }

        return false;
    }

    // subset R of teams that eliminates given team; null
    // if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (!isEliminated(team))
            return null;

        FFWithStartEdges ffWithStartEdges = getOrCreateFordFulkerson(team);
        return () -> StreamSupport.stream(teams().spliterator(), false)
                .filter(t -> ffWithStartEdges.ff.inCut(teamIndex(t)))
                .iterator();
    }

    private LinkedQueue<MatchUp> createMatchupsForOtherTeams(int currTeamIndex, int numTeams) {
        LinkedQueue<MatchUp> combinations = new LinkedQueue<>();

        if (numTeams < 2)
            return combinations;

        for (var i = 0; i < numTeams; i++) {
            for (var j = i + 1; j < numTeams; j++) {
                MatchUp matchUp = new MatchUp(i, j);
                if (!matchUp.involves(currTeamIndex)) {
                    combinations.enqueue(matchUp);
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
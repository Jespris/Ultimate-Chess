package Game.Players.AI;

import Game.Board.Board;
import Game.Board.BoardUtils;
import Game.Board.FenUtilities;
import Game.Moves.Move;
import Game.Moves.MoveTransition;
import Game.Players.Alliance;
import Game.Players.Player;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class AlphaBetaMoveSorted extends Observable implements MoveStrategy {
    private final BoardEvaluator evaluator;
    private final long minSearchTime; // in seconds
    private final MoveSorter moveSorter;
    private long totalBoardsEvaluated;
    private long boardsEvaluated;
    private int cutOffsProduced;
    private int transpositionsSkipped;
    private final Map<String, Integer> transpositionTable;
    private Instant startInstant;

    private enum MoveSorter {

        SORT {
            @Override
            List<Move> sort(final Board board, final List<Move> moves, final Move bestMove) {
                return getSortedMoves(board, moves, bestMove);
            }
        },
        CAPTURE_SORT{
            @Override
            List<Move> sort(final Board board, final List<Move> moves, final Move bestMove) {
                return getCaptureMoves(board, moves);
            }

            private List<Move> getCaptureMoves(final Board board, final List<Move> moves) {
                final List<Move> captureMoves= new ArrayList<>();
                for (final Move move : moves){
                    final MoveTransition t = board.currentPlayer().makeMove(move);
                    if (t.getMoveStatus().isDone()) {
                        if (move.isAttackMove()) {
                            captureMoves.add(move);
                        }
                    }
                }
                return List.copyOf(captureMoves);
            }
        };

        private static List<Move> getSortedMoves(final Board board, final List<Move> moves, final Move bestMove){
            final List<Move> sortedMoves = new ArrayList<>();
            if (bestMove != null){
                sortedMoves.add(bestMove);
            }
            for (final Move move : moves){
                if (move != bestMove){
                    final MoveTransition t = board.currentPlayer().makeMove(move);
                    if (t.getMoveStatus().isDone()){
                        if (move.isAttackMove() || move.isCastlingMove()){
                            sortedMoves.add(move);
                        }
                    }
                }
            }
            for (final Move move : moves){
                if (!sortedMoves.contains(move)){
                    final MoveTransition t = board.currentPlayer().makeMove(move);
                    if (t.getMoveStatus().isDone()){
                        sortedMoves.add(move);
                    }
                }
            }

            return List.copyOf(sortedMoves);
        }

        abstract List<Move> sort(Board board, List<Move> moves, Move bestMove);
    }

    public AlphaBetaMoveSorted(final int minSearchTime) {
        this.evaluator = StandardBoardEvaluator.get();
        this.minSearchTime = minSearchTime;
        this.moveSorter = MoveSorter.SORT;
        this.totalBoardsEvaluated = 0;
        this.boardsEvaluated = 0;
        this.cutOffsProduced = 0;
        this.transpositionsSkipped = 0;
        this.transpositionTable = new HashMap<>();
        this.startInstant = null;
    }

    @Override
    public String toString() {
        return "AB+MO";
    }

    @Override
    public long getNumBoardsEvaluated() {
        return this.totalBoardsEvaluated;
    }

    @Override
    public Move execute(final Board board) {
        List<Move> sortedMoves = this.moveSorter.sort(board, board.currentPlayer().getLegalMoves(), null);
        this.startInstant = Instant.now();
        Move bestMove = getTheOnlyLegalMove(board, sortedMoves);
        if (bestMove != null){
            System.out.println(bestMove + "is the only legal move, play it!");
            return bestMove;
        }
        int depth = 0;
        Duration duration;
        do {
            depth++;
            // System.out.println("INCREASING DEPTH TO " + depth);
            sortedMoves = this.moveSorter.sort(board, board.currentPlayer().getLegalMoves(), bestMove);
            bestMove = findBestMoveAlphaBetaMinMax(board, sortedMoves, depth);
            // System.out.println("Current time spent on move: " + executionTime + " ms");
            Instant endTime = Instant.now();
            duration = Duration.between(this.startInstant, endTime);
        } while (duration.toSeconds() < minSearchTime);
        // estimate how long next search is going to take, e.g. depth 7 => 7^3 * 33 = 11319 ms
        return bestMove;
    }

    private Move findBestMoveAlphaBetaMinMax(final Board board, final List<Move> sortedMoves, final int depth){
        Move bestMove = Move.MoveFactory.getNullMove();
        this.transpositionTable.clear();
        this.transpositionsSkipped = 0;
        final Player currentPlayer = board.currentPlayer();
        final Alliance alliance = currentPlayer.getAlliance();
        this.boardsEvaluated = 0;
        this.cutOffsProduced = 0;
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;
        int evaluation = 0;
        int moveCounter = 0;
        final int numMoves = sortedMoves.size();
        System.out.println(board.currentPlayer() + " THINKING with depth = " + depth);
        System.out.println("Previous best move found: " + sortedMoves.getFirst().toString());
        // System.out.println("\tOrdered moves! : " + this.moveSorter.sort(board.currentPlayer().getLegalMoves()));
        for (final Move move : sortedMoves) {

            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            final String s;
            // if (moveTransition.getMoveStatus().isDone()) {  // the move sorter checks if the move is valid
            final Instant startTime = Instant.now();
            currentValue = alliance.isWhite() ?
                    min(moveTransition.getToBoard(), depth, highestSeenValue, lowestSeenValue) :
                    max(moveTransition.getToBoard(), depth, highestSeenValue, lowestSeenValue);
            if (alliance.isWhite() && currentValue > highestSeenValue) {
                highestSeenValue = currentValue;
                // evaluation = currentValue;
                bestMove = move;
            }
            else if (alliance.isBlack() && currentValue < lowestSeenValue) {
                lowestSeenValue = currentValue;
                // evaluation = currentValue;
                bestMove = move;
            }
            final Instant endTime = Instant.now();
            final Duration totalMoveDuration = Duration.between(this.startInstant, endTime);
            if (totalMoveDuration.toSeconds() > this.minSearchTime){
                break;
            }
            // System.out.printf("(Depth %d) move: (%d/%d) %s, best: %s [evaluation: %.2f], time: %d ms\n", depth, moveCounter, numMoves, move, bestMove, (double) evaluation / 100, moveDuration.toMillis());
            moveCounter++;
        }
        System.out.printf("%s's BEST MOVE %s [#boards evaluated = %d, moves at depth %d calculated = (%d / %d), cutoffCount = %d transpositions percent = %.2f prune percent = %.2f]\n",
                board.currentPlayer(),
                bestMove, this.boardsEvaluated,
                depth, moveCounter, numMoves,
                this.cutOffsProduced, (double)this.transpositionsSkipped/this.boardsEvaluated, 100 * ((double)this.cutOffsProduced/this.boardsEvaluated));
        return bestMove;
    }

    private Move getTheOnlyLegalMove(final Board board, final List<Move> sortedMoves) {
        Move onlyMove = null;
        for (final Move move : sortedMoves){
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                if (onlyMove == null) {
                    onlyMove = move;
                } else {
                    return null;
                }
            }
        }
        return onlyMove;
    }

    public int max(final Board board,
                   final int depth,
                   final int highest,
                   final int lowest) {
        if (depth == 0 || BoardUtils.isEndGameScenario(board)) {
            final String FEN = FenUtilities.createFENFromGame(board);
            this.boardsEvaluated++;
            this.totalBoardsEvaluated++;
            return transpositionEval(board, depth, FEN);
        }
        int currentHighest = highest;
        for (final Move move : this.moveSorter.sort(board, board.currentPlayer().getLegalMoves(), null)) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                currentHighest = Math.max(currentHighest, min(moveTransition.getToBoard(),
                        calculateQuiescenceDepth(board, move, depth), currentHighest, lowest));
                if (lowest <= currentHighest) {
                    this.cutOffsProduced++;
                    break;
                }
            }
        }
        return currentHighest;
    }

    public int min(final Board board,
                   final int depth,
                   final int highest,
                   final int lowest) {
        if (depth == 0 || BoardUtils.isEndGameScenario(board)) {
            final String FEN = FenUtilities.createFENFromGame(board);
            this.totalBoardsEvaluated++;
            this.boardsEvaluated++;
            return transpositionEval(board, depth, FEN);
        }
        int currentLowest = lowest;
        for (final Move move : this.moveSorter.sort(board, board.currentPlayer().getLegalMoves(), null)) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                currentLowest = Math.min(currentLowest, max(moveTransition.getToBoard(),
                        calculateQuiescenceDepth(board, move, depth), highest, currentLowest));
                if (currentLowest <= highest) {
                    this.cutOffsProduced++;
                    break;
                }
            }
        }
        return currentLowest;
    }

    private int transpositionEval(Board board, int depth, String FEN) {
        if (this.transpositionTable.containsKey(FEN)){
            // System.out.println("Found a transposition!");
            this.transpositionsSkipped++;
            return this.transpositionTable.get(FEN);
        } else {
            final int evaluation;

            evaluation = this.evaluator.evaluate(board, depth);

            this.transpositionTable.put(FEN, evaluation);
            return evaluation;
        }
    }

    private int calculateQuiescenceDepth(final Board board,
                                         final Move move,
                                         final int depth) {
        // TODO: implement this
        return depth - 1;
    }
}

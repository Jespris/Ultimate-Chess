package Game.Players.AI;

import Game.Players.Alliance;
import Game.Board.Board;
import Game.Board.BoardUtils;
import Game.Pieces.King;
import Game.Pieces.Piece;
import Game.Players.Player;

import java.time.Duration;
import java.time.Instant;

public final class StandardBoardEvaluator implements BoardEvaluator{
    private static final int PIECE_MOBILITY_FACTOR = 2;  // 20 moves = 40 centipawns
    private static final int CHECK_BONUS = 50;  // centipawns
    private static final int CHECKMATE_BONUS = 100000;
    private static final int DEPTH_BONUS = 50;  // checkmate now is 50 centipawns better than checkmate next turn
    private static final int CASTLE_BONUS = 80;  // 80 centipawns for castling
    private static final int KING_SAFETY_FACTOR = 2;  // -2 centipawns for each enemy move that attacks the squares around the king

    private static final StandardBoardEvaluator INSTANCE = new StandardBoardEvaluator();

    private StandardBoardEvaluator(){
    }

    public static StandardBoardEvaluator get(){
        return INSTANCE;
    }

    @Override
    public int evaluate(final Board board, final int depth) {
        if (board.currentPlayer().isInStaleMate()){
            return 0;
        } else if (board.currentPlayer().isInCheckMate()){
            if (board.currentPlayer().getAlliance() == Alliance.WHITE){
                return -10000;
            } else {
                return 10000;
            }
        } else {
            return scorePlayer(board, board.whitePlayer(), depth) - scorePlayer(board, board.blackPlayer(), depth);
        }
    }

    private int scorePlayer(final Board board, final Player player, final int depth) {
        Instant start = Instant.now();
        final int score = materialValue(player) +
                piecePlacement(player) +
                pieceMobility(player) +
                check(player) +
                checkMate(player, depth) +
                castled(player) +
                kingSafety(player);
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        System.out.println("Board eval time: " + duration.toMillis() + "ms");
        return score;
    }

    private int kingSafety(Player player) {
        int kingAttacks = 0;
        // checks how many pieces attacks each of the squares around the king
        final int[] directions = {-9, -8, -7, -1, 1, 7, 8, 9};
        final King king = player.getPlayerKing();
        for (final int direction : directions){
            if (player.getPlayerKing().isFirstColumnExclusion(king.getPiecePosition(), direction) ||
                    player.getPlayerKing().isEightColumnExclusion(king.getPiecePosition(), direction)){
                continue;
            }
            final int destination = king.getPiecePosition() + direction;
            if (BoardUtils.isValidTileCoordinate(destination)){
                kingAttacks += Player.calculateAttacksOnTile(destination, player.getOpponent().getLegalMoves()).size();
            }
        }
        return -(kingAttacks * KING_SAFETY_FACTOR);
    }

    private int castled(Player player) {
        return player.hasCastled() ? CASTLE_BONUS : 0;
    }

    private int checkMate(final Player player, final int depth) {
        return player.getOpponent().isInCheckMate() ? CHECKMATE_BONUS * depthBonus(depth) : 0;
    }

    private static int depthBonus(int depth) {
        return depth == 0 ? 1 : DEPTH_BONUS * depth;
    }

    private static int check(final Player player) {
        return player.getOpponent().isInCheck() ? CHECK_BONUS : 0;
    }

    private static int pieceMobility(final Player player) {
        // how many legal moves
        return player.getLegalMoves().size() * PIECE_MOBILITY_FACTOR;
    }

    private static int piecePlacement(final Player player) {
        int piecePlacementScore = 0;
        for (final Piece piece : player.getActivePieces()){
            piecePlacementScore += piece.locationBonus();
        }
        return piecePlacementScore;
    }


    private static int materialValue(final Player player){
        int materialScore = 0;
        for (final Piece piece : player.getActivePieces()){
            materialScore += piece.getPieceValue();
        }
        return materialScore;
    }
}


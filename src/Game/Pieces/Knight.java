package Game.Pieces;

import Game.Board.Board;
import Game.Board.BoardUtils;
import Game.Moves.Move;
import Game.Players.Alliance;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {
    private final static int[] knightJumps = {-17, -15, -10, -6, 6, 10, 15, 17};

    public Knight(final Alliance pieceAlliance, final int piecePosition) {
        super(PieceType.KNIGHT, pieceAlliance, piecePosition, true);
    }

    public Knight(final Alliance pieceAlliance, final int piecePosition, final boolean isFirstMove){
        super(PieceType.KNIGHT, pieceAlliance, piecePosition, isFirstMove);

    }

    @Override
    public List<Move> calculateLegalMoves(Board board) {

        int destination;
        final List<Move> legalMoves = new ArrayList<>();

        for(final int knightJump : knightJumps){
            destination = this.piecePosition + knightJump;
            if (BoardUtils.isValidTileCoordinate(destination)){

                if (isFirstColumnExclusion(this.piecePosition, knightJump) ||
                        isSecondColumnExclusion(this.piecePosition, knightJump) ||
                        isSeventhColumnExclusion(this.piecePosition, knightJump) ||
                        isEightColumnExclusion(this.piecePosition, knightJump)){
                    // knight jump is wrong at the edge of the board
                    continue;
                }
                final Piece pieceAtDestination = board.getPiece(destination);
                if (pieceAtDestination == null) {
                    legalMoves.add(new Move.StandardMove(board, destination, this));
                } else {
                    final Alliance pieceOnTileAlliance = pieceAtDestination.getPieceAlliance();

                    if (this.pieceAlliance != pieceOnTileAlliance) {
                        legalMoves.add(new Move.MajorAttackMove(board, this, destination, pieceAtDestination));
                    }
                }
            }
        }
        return List.copyOf(legalMoves);
    }

    @Override
    public String toString(){
        return PieceType.KNIGHT.toString();
    }

    @Override
    public Knight movePiece(final Move move) {
        return new Knight(move.getPieceMoved().getPieceAlliance(), move.getDestination());
    }

    @Override
    public int locationBonus() {
        return this.pieceAlliance.knightBonus(this.piecePosition);
    }

    // Some knight-jump offsets are wrong at the edge of the boards, exclude those when adding legal moves
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition] && ((candidateOffset == -17) || (candidateOffset == -10) ||
                (candidateOffset == 6) || (candidateOffset == 15));
    }

    private static boolean isSecondColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.SECOND_COLUMN[currentPosition] && ((candidateOffset == -10) || (candidateOffset == 6));
    }

    private static boolean isSeventhColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.SEVENTH_COLUMN[currentPosition] && ((candidateOffset == 10) || (candidateOffset == -6));
    }

    private static boolean isEightColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.EIGHT_COLUMN[currentPosition] && ((candidateOffset == 17) || (candidateOffset == 10) ||
                (candidateOffset == -6) || (candidateOffset == -15));
    }
}

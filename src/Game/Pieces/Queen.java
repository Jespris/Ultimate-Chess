package Game.Pieces;

import Game.Board.Board;
import Game.Board.BoardUtils;
import Game.Moves.Move;
import Game.Players.Alliance;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {
    private final static int[] QUEEN_VECTORS = {-9, -8, -7, -1, 1, 7, 8, 9};

    public Queen(final Alliance pieceAlliance, final int piecePosition) {
        super(PieceType.QUEEN, pieceAlliance, piecePosition, true);
    }

    public Queen(final Alliance pieceAlliance, final int piecePosition, final boolean isFirstMove){
        super(PieceType.QUEEN, pieceAlliance, piecePosition, isFirstMove);
    }

    @Override
    public List<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int queenVector : QUEEN_VECTORS) {
            int candidateDestinationCoordinate = this.piecePosition;
            while (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {

                if (isFirstColumnExclusion(candidateDestinationCoordinate, queenVector) || isEightColumnExclusion(candidateDestinationCoordinate, queenVector)) {
                    break;
                }
                candidateDestinationCoordinate += queenVector;

                if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {

                    final Piece pieceAtDestination = board.getPiece(candidateDestinationCoordinate);

                    if (pieceAtDestination == null) {
                        legalMoves.add(new Move.StandardMove(board, candidateDestinationCoordinate, this));
                    } else {

                        final Alliance pieceOnTileAlliance = pieceAtDestination.getPieceAlliance();

                        if (this.pieceAlliance != pieceOnTileAlliance) {
                            legalMoves.add(new Move.MajorAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
                        }
                        break;
                    }
                }
            }
        }
        return List.copyOf(legalMoves);
    }

    @Override
    public String toString(){
        return PieceType.QUEEN.toString();
    }

    @Override
    public Queen movePiece(final Move move) {
        return new Queen(move.getPieceMoved().getPieceAlliance(), move.getDestination());
    }

    @Override
    public int locationBonus() {
        return this.pieceAlliance.queenBonus(this.piecePosition);
    }

    // Some queen vectors are wrong at the edge of the boards, exclude those when adding legal moves
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -1 || candidateOffset == -9 || candidateOffset == 7);
    }

    private static boolean isEightColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.EIGHT_COLUMN[currentPosition] && (candidateOffset == 1 || candidateOffset == 9 || candidateOffset == -7);
    }
}

package Game.Pieces;

import Game.Board.Board;
import Game.Board.BoardUtils;
import Game.Moves.Move;
import Game.Players.Alliance;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
    private final static int[] KING_VECTORS = {-9, -8, -7, -1, 1, 7, 8, 9};
    private final boolean isCastled;
    private final boolean kingSideCastleCapable;
    private final boolean queenSideCastleCapable;

    public King(final Alliance pieceAlliance, final int piecePosition,
                final boolean kingSideCastleCapable, final boolean queenSideCastleCapable) {
        super(PieceType.KING, pieceAlliance, piecePosition, true);
        this.isCastled = false;
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;
    }

    public King(final Alliance pieceAlliance, final int piecePosition, final boolean isFirstMove,
                final boolean isCastled, final boolean kingSideCastleCapable, final boolean queenSideCastleCapable){
        super(PieceType.KING, pieceAlliance, piecePosition, isFirstMove);
        this.isCastled = isCastled;
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;
    }

    public boolean hasCastled(){
        return this.isCastled;
    }

    public boolean isKingSideCastleCapable() {
        return this.kingSideCastleCapable;
    }

    public boolean isQueenSideCastleCapable() {
        return this.queenSideCastleCapable;
    }

    @Override
    public List<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for (final int kingVector: KING_VECTORS){
            if (isFirstColumnExclusion(this.piecePosition, kingVector) ||
                    isEightColumnExclusion(this.piecePosition, kingVector)){
                continue;
            }
            final int destination = this.piecePosition + kingVector;
            if (BoardUtils.isValidTileCoordinate(destination)) {
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
        return PieceType.KING.toString();
    }

    @Override
    public King movePiece(final Move move) {
        return new King(move.getPieceMoved().getPieceAlliance(), move.getDestination(), false, move.isCastlingMove(), false, false);
    }

    @Override
    public int locationBonus() {
        return this.pieceAlliance.kingBonus(this.piecePosition);
    }

    // Some king vectors are wrong at the edge of the boards, exclude those when adding legal moves
    public boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -1 || candidateOffset == -9 || candidateOffset == 7);
    }

    public boolean isEightColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.EIGHT_COLUMN[currentPosition] && (candidateOffset == 1 || candidateOffset == 9 || candidateOffset == -7);
    }
}

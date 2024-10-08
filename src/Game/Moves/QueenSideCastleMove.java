package Game.Moves;

import Game.Pieces.Piece;

public class QueenSideCastleMove extends Move {
    public QueenSideCastleMove(int fromSquare, int toSquare, Piece pieceMoved) {
        super(fromSquare, toSquare, pieceMoved, null);
    }

    @Override
    public boolean isCastleMove() {
        return true;
    }
}

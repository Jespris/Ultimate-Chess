package Game.Moves;

import Game.Pieces.Piece;

public class KingSideCastleMove extends Move {
    public KingSideCastleMove(int fromSquare, int toSquare, Piece pieceMoved) {
        super(fromSquare, toSquare, pieceMoved, null);
    }

    @Override
    public boolean isCastleMove() {
        return true;
    }

    @Override
    public boolean isKingSideCastleMove() {
        return true;
    }
}

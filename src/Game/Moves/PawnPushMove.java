package Game.Moves;

import Game.Pieces.Piece;

public class PawnPushMove extends Move {
    public PawnPushMove(int fromSquare, int toSquare, Piece pieceMoved) {
        super(fromSquare, toSquare, pieceMoved, null);
    }

    @Override
    public boolean isPawnPushMove() {
        return true;
    }
}

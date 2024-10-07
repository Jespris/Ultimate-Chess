package Game;

import Game.Pieces.Piece;

public class DoublePawnMove extends Move {
    public DoublePawnMove(int fromSquare, int toSquare, Piece pieceMoved) {
        super(fromSquare, toSquare, pieceMoved, null);
    }

    @Override
    public boolean isPawnDoubleMove() {
        return true;
    }
}

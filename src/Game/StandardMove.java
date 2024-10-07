package Game;

import Game.Pieces.Piece;

public class StandardMove extends Move {
    public StandardMove(int fromSquare, int toSquare, Piece pieceMoved) {
        super(fromSquare, toSquare, pieceMoved, null);
    }
}

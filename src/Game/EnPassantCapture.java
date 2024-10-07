package Game;

import Game.Pieces.Piece;

public class EnPassantCapture extends Move {
    public EnPassantCapture(int fromSquare, int toSquare, Piece pieceMoved) {
        super(fromSquare, toSquare, pieceMoved, null);
    }

    @Override
    public boolean isEnPassantCaptureMove() {
        return true;
    }
}

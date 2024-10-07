package Game.Moves;

import Game.Pieces.Piece;

public class CaptureMove extends Move{
    public CaptureMove(int fromSquare, int toSquare, Piece pieceMoved, Piece pieceCaptured) {
        super(fromSquare, toSquare, pieceMoved, pieceCaptured);
    }
}

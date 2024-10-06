package Game;

import Game.Pieces.Piece;

public class Move {
    private int fromSquare;
    private int toSquare;
    private Piece pieceMoved;
    private Piece pieceCaptured;

    public Move(int fromSquare, int toSquare, Piece pieceMoved, Piece pieceCaptured) {
        this.fromSquare = fromSquare;
        this.toSquare = toSquare;
        this.pieceMoved = pieceMoved;
        this.pieceCaptured = pieceCaptured;
    }
}

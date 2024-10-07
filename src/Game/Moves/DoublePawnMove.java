package Game.Moves;

import Game.Pieces.Piece;

public class DoublePawnMove extends Move {
    private int enPassantSquare;
    public DoublePawnMove(int fromSquare, int toSquare, Piece pieceMoved, int enPassantSquare) {
        super(fromSquare, toSquare, pieceMoved, null);
        this.enPassantSquare = enPassantSquare;
    }

    @Override
    public boolean isPawnDoubleMove() {
        return true;
    }

    public int getEnPassantSquare() {
        return enPassantSquare;
    }
}

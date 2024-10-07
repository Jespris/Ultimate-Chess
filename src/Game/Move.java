package Game;

import Game.Pieces.Piece;

public abstract class Move {
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

    public boolean isCaptureMove(){
        return pieceCaptured != null;
    }

    public boolean isCastleMove(){
        return false;
    }

    public boolean isPromotionMove(){
        return false;
    }

    public boolean isPawnDoubleMove(){
        return false;
    }

    public boolean isEnPassantCaptureMove(){
        return false;
    }

    public int getFromSquare() {
        return fromSquare;
    }

    public int getToSquare() {
        return toSquare;
    }

    public Piece getPieceCaptured() {
        return pieceCaptured;
    }

    public Piece getPieceMoved() {
        return pieceMoved;
    }
}

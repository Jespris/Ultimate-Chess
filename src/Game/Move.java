package Game;

public class Move {
    private int fromSquare;
    private int toSquare;
    private int pieceMoved;
    private int pieceCaptured;

    public Move(int fromSquare, int toSquare, int pieceMoved, int pieceCaptured) {
        this.fromSquare = fromSquare;
        this.toSquare = toSquare;
        this.pieceMoved = pieceMoved;
        this.pieceCaptured = pieceCaptured;
    }
}

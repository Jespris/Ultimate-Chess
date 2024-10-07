package Game.Pieces;

import Game.Board;
import Game.CaptureMove;
import Game.Move;
import Game.StandardMove;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class Piece {

    private final String pieceName;
    private final int pieceDesignator;
    private final boolean isWhite;
    private final int initSquareIndex;
    private int currentSquare;

    public Piece(final char pieceName, final boolean isWhite, final int squareIndex) {
        this.pieceName = setPieceName(isWhite, pieceName);
        this.isWhite = isWhite;
        this.pieceDesignator = getDesignator();
        this.initSquareIndex = squareIndex;
        this.currentSquare = squareIndex;
    }

    private String setPieceName(boolean isWhite, char pieceName) {
        if (isWhite) {
            return 'w' + String.valueOf(pieceName);
        } else {
            return 'b' + String.valueOf(pieceName);
        }
    }

    private int getDesignator() {
        HashMap<Character, Integer> pieces = new HashMap<>();
        pieces.put('P', 1);
        pieces.put('K', 2);
        pieces.put('Q', 3);
        pieces.put('B', 4);
        pieces.put('N', 5);
        pieces.put('R', 6);

        if (this.isWhite){
            return pieces.get(this.pieceName.charAt(1));
        } else {
            return pieces.get(this.pieceName.charAt(1)) * -1;
        }
    }

    public int getInitSquareIndex(){
        return this.initSquareIndex;
    }

    public String getPieceName() {
        return pieceName;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public int getPieceDesignator() {
        return pieceDesignator;
    }

    public abstract List<Move> getMoves(Board board);

    public int getCurrentSquare() {
        return this.currentSquare;
    }

    public void setCurrentSquare(int currentSquare) {
        this.currentSquare = currentSquare;
    }

    public boolean contains(int[] arr, int target) {
        return Arrays.stream(arr).anyMatch(i -> i == target);
    }

    public List<Move> linePieceMove(int current, int direction, Board board){
        List<Move> moves = new ArrayList<>();
        for (int i = 1; i < 8; i++){
            int destination = current + direction * i;
            if (0 <= destination && destination < 64){
                // inside the board
                // System.out.println("The destination square " + destination + " is inside the board");
                Piece pieceOnSquare = board.getPieceOnSquare(destination);
                if (pieceOnSquare != null) {
                    // System.out.println("There is a piece on the destination square.");
                    if (pieceOnSquare.isWhite() != this.isWhite()){
                        // Can capture but then break loop
                        // System.out.println("There is a capturable piece on the destination square.");
                        moves.add(new CaptureMove(current, destination, this, pieceOnSquare));
                    }
                    break;
                } else {
                    moves.add(new StandardMove(current, destination, this));
                }
                // Check if we are on the edge of the board
                if (contains(board.getFileIndexes("A"), destination) && ((direction < 0 && direction != -8) || direction == 7)){
                    // System.out.println("The destination square is on the left edge of the board and we are going to the left");
                    break;
                }
                if (contains(board.getFileIndexes("H"), destination) && ((direction > 0 && direction != 8) || direction == -7)){
                    // System.out.println("The destination square is on the right edge of the board and we are going to the right");
                    break;
                }
            } else {
                // System.out.println("The destination is outside the board");
                break;
            }
        }
        return moves;
    }
}

package Game.Pieces;

import Game.Board;
import Game.Moves.CaptureMove;
import Game.Moves.Move;
import Game.Moves.StandardMove;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
    public King(final boolean isWhite, final int squareIndex) {
        super('K', isWhite, squareIndex);
    }

    @Override
    public List<Move> getMoves(Board board) {
        List<Move> moves = new ArrayList<>();
        int[] directions = {-9, -8, -7, -1, 1, 7, 8, 9};
        int current = getCurrentSquare();
        for (int direction : directions) {
            int destination = current + direction;
            if (validSquare(current, direction, board)){
                Piece pieceOnSquare = board.getPieceOnSquare(destination);
                if (pieceOnSquare != null) {
                    // System.out.println("There is a piece on the destination square.");
                    if (pieceOnSquare.isWhite() != this.isWhite()){
                        // Can capture but then break loop
                        // System.out.println("There is a capturable piece on the destination square.");
                        moves.add(new CaptureMove(current, destination, this, pieceOnSquare));
                    }
                } else {
                    moves.add(new StandardMove(current, destination, this));
                }
            }
        }
        return moves;
    }

    private boolean validSquare(int current, int direction, Board board) {
        int destination = current + direction;
        if (0 <= destination && destination < 64) {
            // Check if we are on the edge of the board
            if (contains(board.getFileIndexes("A"), destination) && (direction == -1 || direction == -9 || direction == 7)) {
                // System.out.println("The destination square is on the left edge of the board and we are going to the left");
                return false;
            }
            if (contains(board.getFileIndexes("H"), destination) && (direction == 1 || direction == 9 || direction == -7)) {
                // System.out.println("The destination square is on the right edge of the board and we are going to the right");
                return false;
            }
            return true;
        }
        // Outside the board
        return false;
    }

    @Override
    public boolean isKing() {
        return true;
    }
}

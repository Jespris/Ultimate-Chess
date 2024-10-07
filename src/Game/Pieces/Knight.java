package Game.Pieces;

import Game.Board;
import Game.CaptureMove;
import Game.Move;
import Game.StandardMove;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {
    public Knight(boolean isWhite, int squareIndex) {
        super('N', isWhite, squareIndex);
    }

    @Override
    public List<Move> getMoves(Board board) {
        int[] directions = {15, 17, -15, -17, -6, 10, -10, 6};
        List<Move> moves = new ArrayList<>();
        int current = this.getCurrentSquare();
        for (int direction : directions) {
            int destination = current + direction;
            if (isEdgeException(current, direction, board)) {
                continue;
            } else {
                Piece pieceOnSquare = board.getPieceOnSquare(destination);
                if (pieceOnSquare != null) {
                    if (pieceOnSquare.isWhite() != this.isWhite()){
                        // Capture move
                        moves.add(new CaptureMove(current, destination, this, pieceOnSquare));
                    }
                } else {
                    // No piece on destination tile
                    moves.add(new StandardMove(current, destination, this));
                }
            }
        }
        return moves;
    }

    private boolean isEdgeException(int current, int direction, Board board) {
        return (contains(board.getFileIndexes("A"), current) && (direction == 15 || direction == -17 || direction == -10 || direction == 6)) ||
                (contains(board.getFileIndexes("H"), current) && (direction == 17 || direction == -15 || direction == -6 || direction == 10)) ||
                (contains(board.getRowIndexes(1), current) && direction > 0) ||
                (contains(board.getRowIndexes(8), current) && direction < 0) ||
                (contains(board.getFileIndexes("B"), current) && (direction == -10 || direction == 6)) ||
                (contains(board.getFileIndexes("G"), current) && (direction == -6 || direction == 10)) ||
                (contains(board.getRowIndexes(2), current) && (direction == 15 || direction == 17)) ||
                (contains(board.getRowIndexes(7), current) && (direction == -15 || direction == -17));
    }
}

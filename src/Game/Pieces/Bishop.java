package Game.Pieces;

import Game.Board;
import Game.Moves.Move;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {
    public Bishop(boolean isWhite, int squareIndex) {
        super('B', isWhite, squareIndex);
    }

    @Override
    public List<Move> getMoves(Board board) {
        List<Move> moves = new ArrayList<>();
        // Up/Down moves
        int[] directions = {-9, -7, 7, 9};
        int current = this.getCurrentSquare();
        for (int direction : directions){
            // Check if we are on the A or H file
            if (((direction == -9 || direction == 7) && contains(board.getFileIndexes("A"), current)) ||
                    ((direction == -7 || direction == 9) && contains(board.getFileIndexes("H"), current))) {
                continue;
            }
            moves.addAll(linePieceMove(current, direction, board));
        }
        return moves;
    }
}

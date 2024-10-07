package Game.Pieces;

import Game.Board;
import Game.Moves.Move;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {
    public Rook(boolean isWhite, int squareIndex) {
        super('R', isWhite, squareIndex);
    }

    @Override
    public List<Move> getMoves(Board board) {
        List<Move> moves = new ArrayList<>();
        // Up/Down moves
        int[] directions = {-8, -1, 1, 8};
        int current = this.getCurrentSquare();
        for (int direction : directions){
            // Check if we are on the A or H file
            if ((direction == -1 && contains(board.getFileIndexes("A"), current)) ||
                    (direction == 1 && contains(board.getFileIndexes("H"), current))) {
                continue;
            }
            moves.addAll(linePieceMove(current, direction, board));
        }
        return moves;
    }
}

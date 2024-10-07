package Game.Pieces;

import Game.Board;
import Game.Moves.Move;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {
    public Queen(boolean isWhite, int squareIndex) {
        super('Q', isWhite, squareIndex);
    }

    @Override
    public List<Move> getMoves(Board board) {
        List<Move> moves = new ArrayList<>();
        // Up/Down moves
        int[] directions = {-9, -8, -7, -1, 1, 7, 8, 9};
        int current = this.getCurrentSquare();
        for (int direction : directions){
            // Check if we are on the A or H file
            if (((direction == -1 || direction == -9 || direction == 7) && contains(board.getFileIndexes("A"), current)) ||
                    ((direction == 1 || direction == 9 || direction == -7)  && contains(board.getFileIndexes("H"), current))) {
                System.out.println("This Queen is on an edge case scenario, continuing with next direction");
                continue;
            }
            moves.addAll(linePieceMove(current, direction, board));
        }
        return moves;
    }
}

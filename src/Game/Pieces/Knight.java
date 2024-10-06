package Game.Pieces;

import Game.Board;
import Game.Move;

import java.util.List;

public class Knight extends Piece {
    public Knight(boolean isWhite, int squareIndex) {
        super('N', isWhite, squareIndex);
    }

    @Override
    public List<Move> getMoves(Board board) {
        return List.of();
    }
}

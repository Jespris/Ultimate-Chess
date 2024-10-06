package Game.Pieces;

import Game.Board;
import Game.Move;

import java.util.List;

public class King extends Piece {
    public King(final boolean isWhite, final int squareIndex) {
        super('K', isWhite, squareIndex);
    }

    @Override
    public List<Move> getMoves(Board board) {
        return List.of();
    }
}

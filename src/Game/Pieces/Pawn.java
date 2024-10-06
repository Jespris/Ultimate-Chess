package Game.Pieces;

import Game.Board;
import Game.Move;

import java.util.List;

public class Pawn extends Piece {
    public Pawn(boolean isWhite, int squareIndex) {
        super('P', isWhite, squareIndex);
    }

    @Override
    public List<Move> getMoves(Board board) {
        return List.of();
    }
}

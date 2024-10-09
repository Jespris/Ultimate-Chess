package Game.Players.AI;

import Game.Board.Board;
import Game.Moves.Move;

public class RandomMove implements MoveStrategy{
    @Override
    public long getNumBoardsEvaluated() {
        return 0;
    }

    @Override
    public Move execute(Board board) {
        return null;
    }
}

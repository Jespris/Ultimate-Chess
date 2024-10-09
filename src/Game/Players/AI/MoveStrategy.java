package Game.Players.AI;

import Game.Board.Board;
import Game.Moves.Move;

public interface MoveStrategy {
    long getNumBoardsEvaluated();
    Move execute(Board board);
}

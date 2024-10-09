package Game.Players.AI;

import Game.Board.Board;

public interface BoardEvaluator {
    int evaluate(Board board, int depth);
}

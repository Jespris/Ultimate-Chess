import Game.Board;
import Game.ChessGameLayout;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        System.out.println("Hello world!");

        Board board = new Board();
        board.createStandardBoard();
        board.printBoard();

        ChessGameLayout gameLayout = new ChessGameLayout(board);
        System.out.println(board.getPieceOnSquare(0));
    }
}
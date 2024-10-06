import Game.Board;
import Game.ChessGameLayout;
import Game.Pieces.Queen;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        System.out.println("Hello world!");

        Board board = new Board();
        // board.createStandardBoard();
        board.setPiece(new Queen(true, board.getSquareIndex("E4")));
        board.printBoard();

        ChessGameLayout gameLayout = new ChessGameLayout(board);
        System.out.println(board.getPieceOnSquare(0));
    }
}
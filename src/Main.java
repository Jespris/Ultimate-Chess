import Game.Board;
import Game.ChessGameLayout;
import Game.Pieces.Knight;
import Game.Pieces.Queen;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        System.out.println("Hello world!");

        Board board = new Board();
        // board.createStandardBoard();
        board.setPiece(new Queen(true, board.getSquareIndex("E4")));
        board.setPiece(new Queen(true, board.getSquareIndex("D1")));
        board.setPiece(new Knight(true, board.getSquareIndex("G7")));
        board.setPiece(new Knight(true, board.getSquareIndex("A1")));
        board.setPiece(new Knight(true, board.getSquareIndex("C6")));

        board.printBoard();

        ChessGameLayout gameLayout = new ChessGameLayout(board);
        System.out.println(board.getPieceOnSquare(0));
    }
}
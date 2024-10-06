package Testing;

import Game.Board;
import Game.Pieces.Bishop;
import Game.Pieces.Queen;
import Game.Pieces.Rook;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BoardTests {
    @Test
    public void testBoardSquares() {
        Board board = new Board();
        int a8 = board.getSquareIndex("A8");
        assertEquals(0, a8);

        int h8 = board.getSquareIndex("H8");
        assertEquals(7, h8);

        int h1 = board.getSquareIndex("H1");
        assertEquals(63, h1);

        int e4 = board.getSquareIndex("E4");
        assertEquals(36, e4);
    }
    @Test
    public void TestQueenMoves(){
        Board board = new Board();
        board.setPiece(new Queen(true, board.getSquareIndex("E4")));
        int nrLegalMoves = board.getLegalMoves().size();
        assertEquals(27, nrLegalMoves);

        board = new Board();
        board.setPiece(new Queen(true, board.getSquareIndex("G7")));
        nrLegalMoves = board.getLegalMoves().size();
        assertEquals(23, nrLegalMoves);

        board = new Board();
        board.setPiece(new Queen(true, board.getSquareIndex("D1")));
        nrLegalMoves = board.getLegalMoves().size();
        assertEquals(21, nrLegalMoves);
    }

    @Test
    public void TestRookMoves(){
        Board board = new Board();
        board.setPiece(new Rook(true, board.getSquareIndex("E4")));
        int nrLegalMoves = board.getLegalMoves().size();
        assertEquals(14, nrLegalMoves);

        board = new Board();
        board.setPiece(new Rook(true, board.getSquareIndex("H7")));
        nrLegalMoves = board.getLegalMoves().size();
        assertEquals(14, nrLegalMoves);

        board = new Board();
        board.setPiece(new Rook(true, board.getSquareIndex("D1")));
        nrLegalMoves = board.getLegalMoves().size();
        assertEquals(14, nrLegalMoves);
    }

    @Test
    public void TestBishopMoves(){
        Board board = new Board();
        board.setPiece(new Bishop(true, board.getSquareIndex("E4")));
        int nrLegalMoves = board.getLegalMoves().size();
        assertEquals(13, nrLegalMoves);

        board = new Board();
        board.setPiece(new Bishop(true, board.getSquareIndex("G7")));
        nrLegalMoves = board.getLegalMoves().size();
        assertEquals(9, nrLegalMoves);

        board = new Board();
        board.setPiece(new Bishop(true, board.getSquareIndex("D1")));
        nrLegalMoves = board.getLegalMoves().size();
        assertEquals(7, nrLegalMoves);
    }
}

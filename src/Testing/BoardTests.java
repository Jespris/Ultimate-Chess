package Testing;

import Game.Board;
import Game.Moves.Move;
import Game.Pieces.*;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        assertEquals(26+5, nrLegalMoves);

        board = new Board();
        board.setPiece(new Queen(true, board.getSquareIndex("G7")));
        nrLegalMoves = board.getLegalMoves().size();
        assertEquals(23+5, nrLegalMoves);

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
        assertEquals(13+5, nrLegalMoves);

        board = new Board();
        board.setPiece(new Rook(true, board.getSquareIndex("H7")));
        nrLegalMoves = board.getLegalMoves().size();
        assertEquals(14+5, nrLegalMoves);

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
        assertEquals(13+5, nrLegalMoves);

        board = new Board();
        board.setPiece(new Bishop(true, board.getSquareIndex("G7")));
        nrLegalMoves = board.getLegalMoves().size();
        assertEquals(9+5, nrLegalMoves);

        board = new Board();
        board.setPiece(new Bishop(true, board.getSquareIndex("D1")));
        nrLegalMoves = board.getLegalMoves().size();
        assertEquals(7+4, nrLegalMoves);
    }

    @Test
    public void TestKnightMoves(){
        Board board = new Board();
        board.setPiece(new Knight(true, board.getSquareIndex("E4")));
        int nrLegalMoves = board.getLegalMoves().size();
        assertEquals(8+5, nrLegalMoves);

        board = new Board();
        board.setPiece(new Knight(true, board.getSquareIndex("G4")));
        nrLegalMoves = board.getLegalMoves().size();
        assertEquals(6+5, nrLegalMoves);

        board = new Board();
        board.setPiece(new Knight(true, board.getSquareIndex("D1")));
        nrLegalMoves = board.getLegalMoves().size();
        assertEquals(4+4, nrLegalMoves);

        board = new Board();
        board.setPiece(new Knight(true, board.getSquareIndex("A8")));
        nrLegalMoves = board.getLegalMoves().size();
        assertEquals(2+5, nrLegalMoves);

        board = new Board();
        board.setPiece(new Knight(true, board.getSquareIndex("H1")));
        nrLegalMoves = board.getLegalMoves().size();
        assertEquals(2+5, nrLegalMoves);
    }

    @Test
    public void testStandardBoard(){
        // TODO
    }

    @Test
    public void testCastlingMoves(){
        Board board = new Board();
        board.setPiece(new Rook(true, board.getSquareIndex("H1")));
        board.setPiece(new Rook(true, board.getSquareIndex("A1")));
        int castleMovesFound = findNrOfCastlingMoves(board);
        assertEquals(2, castleMovesFound);

        Move kingcastleMove = board.moveSelector(board.getSquareIndex("E1"), board.getSquareIndex("G1"));
        assert kingcastleMove != null;
        assertTrue(kingcastleMove.isCastleMove());

        Move queenCastleMove = board.moveSelector(board.getSquareIndex("E1"), board.getSquareIndex("C1"));
        assert queenCastleMove != null;
        assertTrue(queenCastleMove.isCastleMove());

        // Make a queenside rook move and find one castling move
        Move rookMove = board.moveSelector(board.getSquareIndex("A1"), board.getSquareIndex("A4"));
        assert rookMove != null;
        board = board.makeMove(rookMove);
        board.switchTurn();
        assertTrue(board.getWhiteToMove());
        castleMovesFound = findNrOfCastlingMoves(board);
        assertEquals(1, castleMovesFound);

        // add a queen to prevent castling
        board.setPiece(new Queen(false, board.getSquareIndex("F8")));
        castleMovesFound = findNrOfCastlingMoves(board);
        assertEquals(0, castleMovesFound);
        // Add a pawn to block attacking queen
        board.setPiece(new Pawn(true, board.getSquareIndex("F2")));
        castleMovesFound = findNrOfCastlingMoves(board);
        assertEquals(1, castleMovesFound);
        // Add an own knight to block castling
        board.setPiece(new Knight(true, board.getSquareIndex("G1")));
        castleMovesFound = findNrOfCastlingMoves(board);
        assertEquals(0, castleMovesFound);

    }

    private int findNrOfCastlingMoves(Board board){
        List<Move> legalMoves = board.getLegalMoves();
        int castleMovesFound = 0;
        for (Move move : legalMoves) {
            if (move.isCastleMove()){
                castleMovesFound++;
            }
        }
        return castleMovesFound;
    }
}

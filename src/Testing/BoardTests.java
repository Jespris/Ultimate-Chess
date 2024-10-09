package Testing;

import Game.Board.Board;
import Game.Board.BoardUtils;
import Game.Moves.Move;
import Game.Pieces.*;
import Game.Players.Alliance;
import org.junit.Test;

import java.util.List;

import static Game.Board.Board.*;
import static org.junit.Assert.*;

public class BoardTests {
    @Test
    public void testBoardSquares() {
        String topLeft = BoardUtils.getChessNotationAtCoordinate(0);
        assertEquals("a8", topLeft);

        String bottomRight = BoardUtils.getChessNotationAtCoordinate(63);
        assertEquals("h1", bottomRight);

        String e4 = BoardUtils.getChessNotationAtCoordinate(36);
        assertEquals("e4", e4);
    }
    @Test
    public void TestQueenMoves(){
        Builder builder = new Builder();
        builder.setPiece(new Queen(Alliance.WHITE, 0));
        Board board = builder.build();
        int nrLegalMoves = board.getAllLegalMoves().size();
        assertEquals(21, nrLegalMoves);

        builder = new Builder();
        builder.setPiece(new Queen(Alliance.WHITE, BoardUtils.getCoordinateAtPosition("e4")));
        board = builder.build();
        List<Move> moves = board.getAllLegalMoves();
        nrLegalMoves = moves.size();
        assertEquals(27, nrLegalMoves);
    }

    @Test
    public void TestRookMoves(){
        Builder builder = new Builder();
        builder.setPiece(new Rook(Alliance.WHITE, 0));
        Board board = builder.build();
        int nrLegalMoves = board.getAllLegalMoves().size();
        assertEquals(14, nrLegalMoves);

        builder = new Builder();
        builder.setPiece(new Rook(Alliance.WHITE, BoardUtils.getCoordinateAtPosition("e4")));
        board = builder.build();
        List<Move> moves = board.getAllLegalMoves();
        nrLegalMoves = moves.size();
        assertEquals(14, nrLegalMoves);
    }

    @Test
    public void TestBishopMoves(){
        Builder builder = new Builder();
        builder.setPiece(new Bishop(Alliance.WHITE, 0));
        Board board = builder.build();
        int nrLegalMoves = board.getAllLegalMoves().size();
        assertEquals(7, nrLegalMoves);

        builder = new Builder();
        builder.setPiece(new Bishop(Alliance.WHITE, BoardUtils.getCoordinateAtPosition("e4")));
        board = builder.build();
        List<Move> moves = board.getAllLegalMoves();
        nrLegalMoves = moves.size();
        assertEquals(13, nrLegalMoves);

        builder = new Builder();
        builder.setPiece(new Bishop(Alliance.WHITE, BoardUtils.getCoordinateAtPosition("g7")));
        board = builder.build();
        nrLegalMoves = board.getAllLegalMoves().size();
        assertEquals(9, nrLegalMoves);
    }

    @Test
    public void TestKnightMoves(){
        Builder builder = new Builder();
        builder.setPiece(new Knight(Alliance.WHITE, 0));
        Board board = builder.build();
        int nrLegalMoves = board.getAllLegalMoves().size();
        assertEquals(2, nrLegalMoves);

        builder = new Builder();
        builder.setPiece(new Knight(Alliance.WHITE, BoardUtils.getCoordinateAtPosition("e4")));
        board = builder.build();
        List<Move> moves = board.getAllLegalMoves();
        nrLegalMoves = moves.size();
        assertEquals(8, nrLegalMoves);

        builder = new Builder();
        builder.setPiece(new Knight(Alliance.WHITE, BoardUtils.getCoordinateAtPosition("g7")));
        board = builder.build();
        nrLegalMoves = board.getAllLegalMoves().size();
        assertEquals(4, nrLegalMoves);

        builder = new Builder();
        builder.setPiece(new Knight(Alliance.WHITE, BoardUtils.getCoordinateAtPosition("g5")));
        board = builder.build();
        nrLegalMoves = board.getAllLegalMoves().size();
        assertEquals(6, nrLegalMoves);
    }

    @Test
    public void testStandardBoard(){
        Board board = Board.createStandardBoard();
        List<Move> moves = board.whitePlayer().getLegalMoves();
        int nrLegalMoves = moves.size();
        assertEquals(20, nrLegalMoves);  // white has 20 legal moves
        board = moves.getFirst().execute();  // execute a random move

        assertTrue(board.currentPlayer().getAlliance().isBlack());  // it is black to move
        moves = board.blackPlayer().getLegalMoves();
        nrLegalMoves = moves.size();
        assertEquals(20, nrLegalMoves); // black also has 20 legal moves
    }

    @Test
    public void testCastlingMoves(){
        Builder builder = new Builder();
        builder.setPiece(new King(Alliance.WHITE, BoardUtils.getCoordinateAtPosition("e1"), true, true));
        builder.setPiece(new Rook(Alliance.WHITE, BoardUtils.getCoordinateAtPosition("h1")));
        builder.setPiece(new Rook(Alliance.WHITE, BoardUtils.getCoordinateAtPosition("a1")));
        builder.setPiece(new Pawn(Alliance.WHITE, BoardUtils.getCoordinateAtPosition("h2")));
        builder.setPiece(new Pawn(Alliance.WHITE, BoardUtils.getCoordinateAtPosition("a2")));
        Board board = builder.build();
        int nrLegalMoves = board.getAllLegalMoves().size();
        // Both pawns have 2 legal moves, king has 5, kings rook 2, queens rook 3 and 2 castle moves
        assertEquals(2+2+5+2+3+2, nrLegalMoves);

        // Add a knight to block queenside castling
        builder.setPiece(new Knight(Alliance.WHITE, BoardUtils.getCoordinateAtPosition("b1")));
        board = builder.build();
        nrLegalMoves = board.getAllLegalMoves().size();
        assertEquals(2+2+5+2+1+3, nrLegalMoves);
    }

    private int findNrOfCastlingMoves(final Board board){
        return 0;
    }
}

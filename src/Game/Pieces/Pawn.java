package Game.Pieces;

import Game.Board;
import Game.Move;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    public Pawn(boolean isWhite, int squareIndex) {
        super('P', isWhite, squareIndex);
    }

    @Override
    public List<Move> getMoves(Board board) {
        List<Move> moves = new ArrayList<>();
        int direction = isWhite() ? -1 : 1;
        int[] captureDirections = {7*direction, 9*direction};
        int current = getCurrentSquare();
        int[] startingRow = isWhite() ? board.getRowIndexes(2) : board.getRowIndexes(7);
        boolean firstMove = contains(startingRow, current);

        // move one square
        int destination = current + direction * 8;
        Piece pieceOnSquare = board.getPieceOnSquare(destination);
        if (pieceOnSquare == null) {
            moves.add(new Move(current, destination, this, null));
            if (firstMove) {
                int doubleMoveDestination = destination + direction * 8;
                pieceOnSquare = board.getPieceOnSquare(doubleMoveDestination);
                if (pieceOnSquare == null) {
                    // TODO: add subclasses to Move to easily identify the enpassant square
                    moves.add(new Move(current, doubleMoveDestination, this, null));
                }
            }
        }
        for (int captureDirection : captureDirections) {
            if (contains(board.getFileIndexes("A"), current) && (captureDirection == -9 || captureDirection == 7)) {
                // Left edge of the board capturing to the left
                continue;
            }
            if (contains(board.getFileIndexes("H"), current) && (captureDirection == -7 || captureDirection == 9)) {
                // Right edge of the board capturing to the right
                continue;
            }
            int captureDestination = current + captureDirection;
            pieceOnSquare = board.getPieceOnSquare(captureDestination);
            if (pieceOnSquare != null) {
                if (pieceOnSquare.isWhite() != isWhite()) {
                    // opponent capturable piece
                    moves.add(new Move(current, captureDestination, this, pieceOnSquare));
                }
            } else {
                if (captureDestination == board.getEnPassantSquare()) {
                    Pawn enPassantPawn = board.getEnPassantPawn();
                    assert enPassantPawn != null;
                    moves.add(new Move(current, captureDestination, this, enPassantPawn));
                }
            }
        }
        // TODO: add promotion moves
        return moves;
    }
}

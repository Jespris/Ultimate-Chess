package Game.Pieces;

import Game.*;

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
        int[] promotionRow = isWhite() ? board.getRowIndexes(8) : board.getRowIndexes(1);
        boolean firstMove = contains(startingRow, current);

        // move one square
        int destination = current + direction * 8;
        Piece pieceOnSquare = board.getPieceOnSquare(destination);
        if (pieceOnSquare == null) {
            moves.add(new StandardMove(current, destination, this));
            if (firstMove) {
                int doubleMoveDestination = destination + direction * 8;
                pieceOnSquare = board.getPieceOnSquare(doubleMoveDestination);
                if (pieceOnSquare == null) {
                    moves.add(new DoublePawnMove(current, doubleMoveDestination, this));
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
                    moves.add(new CaptureMove(current, captureDestination, this, pieceOnSquare));
                }
            } else {
                if (captureDestination == board.getEnPassantSquare()) {
                    Pawn enPassantPawn = board.getEnPassantPawn();
                    assert enPassantPawn != null; // TODO: remove redundant check that the board has stored the pawn
                    moves.add(new EnPassantCapture(current, captureDestination, this));
                }
            }
        }
        // TODO: add promotion moves
        return moves;
    }
}
